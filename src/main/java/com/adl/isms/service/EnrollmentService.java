package com.adl.isms.service;

import com.adl.isms.assests.Department;
import com.adl.isms.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class EnrollmentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final EnrolmentRepository enrolmentRepository;
    private final Random random = new Random();

    public EnrollmentService(StudentRepository studentRepository, CourseRepository courseRepository, FacultyRepository facultyRepository, EnrolmentRepository enrolmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.enrolmentRepository = enrolmentRepository;
    }

    @Scheduled(fixedDelay = 100_000)
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void scheduleAllStudents() {
        System.out.println("Starting async scheduling for all students...");
        List<StudentEntity> students = studentRepository.findAll();
        List<FacultyEntity> allFaculty = facultyRepository.findAll();

        if (allFaculty.isEmpty()) {
            return;
        }

        for (StudentEntity student : students) {
            Integer semester = student.getCurrentSemester();
            if (semester == null) continue;

            // Fetch courses for the student's semester
            List<CourseEntity> courses = courseRepository.findAllBySemester(semester);

            for (CourseEntity course : courses) {
                // Check if already enrolled
                // Note: ideally we should have a method in repository existsByStudentAndCourse but we can skip if assuming fresh init
                
                // Assign a random faculty for now (or logic could be improved to match department)
                FacultyEntity assignedFaculty = assignFaculty(allFaculty, student.getDepartment());
                
                EnrolmentEntity enrolment = new EnrolmentEntity();
                enrolment.setStudent(student);
                enrolment.setCourse(course);
                enrolment.setFacultyEntity(assignedFaculty);
                enrolment.setSemesterID("SEM_" + semester + "_" + java.time.Year.now().getValue()); // e.g. SEM_3_2026
                
                enrolmentRepository.save(enrolment);
            }
        }
        System.out.println("Finished async scheduling for " + students.size() + " students.");
    }

    private FacultyEntity assignFaculty(List<FacultyEntity> facultyList, Department department) {
        // Try to find faculty in same department
        List<FacultyEntity> sameDeptFaculty = facultyList.stream()
                .filter(f -> f.getDepartment() == department)
                .toList();

        if (!sameDeptFaculty.isEmpty()) {
            return sameDeptFaculty.get(random.nextInt(sameDeptFaculty.size()));
        }
        // Fallback to random faculty if none in department
        return facultyList.get(random.nextInt(facultyList.size()));
    }

    public List<com.adl.isms.dto.StudentCourseViewDTO> getStudentCourses(String username) {
        List<EnrolmentEntity> enrolments = enrolmentRepository.findAllByStudent_UserId_UserName(username);
        return enrolments.stream()
                .map(e -> new com.adl.isms.dto.StudentCourseViewDTO(
                        e.getCourse().getCourseName(),
                        e.getCourse().getCourseCode(),
                        e.getFacultyEntity().getName(),
                        e.getSemesterID()
                ))
                .toList();
    }

    public List<com.adl.isms.dto.FacultyCourseViewDTO> getFacultyCourses(String username) {
        List<EnrolmentEntity> enrolments = enrolmentRepository.findAllByFacultyEntity_User_UserName(username);
        
        // Group by course code to count students and get unique courses
        return enrolments.stream()
                .collect(java.util.stream.Collectors.groupingBy(e -> e.getCourse()))
                .entrySet().stream()
                .map(entry -> {
                    CourseEntity course = entry.getKey();
                    long count = entry.getValue().size();
                    // Assuming semesterID is same for all in this context or picking first
                    String semesterId = entry.getValue().get(0).getSemesterID(); 
                    return new com.adl.isms.dto.FacultyCourseViewDTO(
                            course.getCourseName(),
                            course.getCourseCode(),
                            semesterId,
                            count
                    );
                })
                .toList();
    }
}
