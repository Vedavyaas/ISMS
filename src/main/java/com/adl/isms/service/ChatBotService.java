package com.adl.isms.service;

import com.adl.isms.repository.*;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatBotService {
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final FinanceRepository financeRepository;
    private final OllamaChatModel chatModel;

    public ChatBotService(StudentRepository studentRepository,
                          AttendanceRepository attendanceRepository,
                          GradeRepository gradeRepository,
                          FinanceRepository financeRepository,
                          OllamaChatModel chatModel) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.gradeRepository = gradeRepository;
        this.financeRepository = financeRepository;
        this.chatModel = chatModel;
    }

    public ResponseEntity<String> getSuggestion(String question, String username) {
        Optional<StudentEntity> studentOpt = studentRepository.findByUserId_UserName(username);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Student profile not found.");
        }

        StudentEntity student = studentOpt.get();

        // --- Build context string ---
        StringBuilder context = new StringBuilder();
        context.append("You are an academic advisor assistant for the ISMS (Institutional Student Management System). ")
               .append("Answer questions helpfully and concisely based on the student's data provided below.\n\n");

        // Student profile
        context.append("=== Student Profile ===\n");
        context.append("Name: ").append(student.getName()).append("\n");
        context.append("Email: ").append(student.getEmail()).append("\n");
        context.append("Department: ").append(student.getDepartment()).append("\n");
        context.append("Semester: ").append(student.getCurrentSemester()).append("\n");
        context.append("Enrolment Status: ").append(student.getEnrolmentStatus()).append("\n\n");

        // Attendance
        List<AttendanceEntity> attendanceList = attendanceRepository.findAllByStudent_UserId_UserName(username);
        if (!attendanceList.isEmpty()) {
            context.append("=== Attendance Records ===\n");
            attendanceList.forEach(a ->
                context.append("- ").append(a.getCourse().getCourseName())
                       .append(" (").append(a.getCourse().getCourseCode()).append("): ")
                       .append(a.getAttendancePercentage()).append("%")
                       .append(a.getAttendancePercentage() < 75.0 ? " [WARNING: Below 75%]" : " [OK]")
                       .append("\n")
            );
            double avg = attendanceList.stream()
                    .mapToDouble(AttendanceEntity::getAttendancePercentage)
                    .average().orElse(0.0);
            context.append("Overall Average Attendance: ").append(String.format("%.1f", avg)).append("%\n\n");
        } else {
            context.append("=== Attendance Records ===\nNo attendance data available.\n\n");
        }

        // Grades
        List<GradeEntity> grades = gradeRepository.findAllByStudent_UserId_UserName(username);
        if (!grades.isEmpty()) {
            context.append("=== Grade Records ===\n");
            grades.forEach(g ->
                context.append("- ").append(g.getCourse().getCourseName())
                       .append(" (").append(g.getCourse().getCourseCode()).append("): ")
                       .append("Marks=").append(g.getMarksObtained())
                       .append(", Grade Point=").append(g.getGradePoint())
                       .append("\n")
            );
            double cgpa = grades.stream()
                    .mapToDouble(GradeEntity::getGradePoint)
                    .average().orElse(0.0);
            context.append("CGPA (average grade point): ").append(String.format("%.2f", cgpa)).append("\n\n");
        } else {
            context.append("=== Grade Records ===\nNo grade data available yet.\n\n");
        }

        // Finance
        financeRepository.findByStudent_UserId_UserName(username).ifPresentOrElse(f -> {
            context.append("=== Finance Status ===\n");
            context.append("Amount Due: ₹").append(f.getAmountDue()).append("\n");
            context.append("Amount Paid: ₹").append(f.getAmountPaid()).append("\n");
            context.append("Balance: ₹").append(f.getAmountDue() - f.getAmountPaid()).append("\n");
            context.append("Payment Status: ").append(f.getPaymentStatus()).append("\n\n");
        }, () -> context.append("=== Finance Status ===\nNo finance record found.\n\n"));

        // Build prompt
        String systemText = context.toString();
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemText),
                new UserMessage(question)
        ));

        String answer = chatModel.call(prompt).getResult().getOutput().getText();
        return ResponseEntity.ok(answer);
    }
}

