package com.adl.isms.configuration;

import com.adl.isms.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class GradeDataInitializer {

    private final EnrolmentRepository enrolmentRepository;
    private final GradeRepository gradeRepository;
    private final Random random = new Random(99); // different seed from attendance

    public GradeDataInitializer(EnrolmentRepository enrolmentRepository,
                                GradeRepository gradeRepository) {
        this.enrolmentRepository = enrolmentRepository;
        this.gradeRepository = gradeRepository;
    }

    @Scheduled(initialDelay = 6000, fixedDelay = 5000)
    @Transactional
    public void seedGrades() {
        if (gradeRepository.count() > 0) {
            // Already seeded — silently skip all future ticks
            return;
        }

        List<EnrolmentEntity> allEnrolments = enrolmentRepository.findAll();

        if (allEnrolments.isEmpty()) {
            System.out.println("Enrolments not ready yet for grade seeding, retrying in 5 seconds...");
            return;
        }

        List<GradeEntity> gradeList = new ArrayList<>();

        for (EnrolmentEntity enrolment : allEnrolments) {
            StudentEntity student = enrolment.getStudent();
            CourseEntity course = enrolment.getCourse();

            // Guard unique (student_id, course_id) — GradeEntity uses @OneToOne on course
            boolean alreadyExists = gradeRepository
                    .findByStudent_IdAndCourse_Id(student.getId(), course.getId())
                    .isPresent();

            if (alreadyExists) continue;

            double marks = generateRealisticMarks();
            double gradePoint = marksToGradePoint(marks);
            gradeList.add(new GradeEntity(student, course, marks, gradePoint));
        }

        gradeRepository.saveAllAndFlush(gradeList);
        System.out.println("Initialized " + gradeList.size() + " grade records.");
    }

    private double generateRealisticMarks() {
        int bucket = random.nextInt(100);
        double raw;
        if (bucket < 5) {
            raw = random.nextDouble() * 39.0;            // 0 – 39
        } else if (bucket < 20) {
            raw = 40.0 + random.nextDouble() * 19.0;    // 40 – 59
        } else if (bucket < 70) {
            raw = 60.0 + random.nextDouble() * 19.0;    // 60 – 79
        } else {
            raw = 80.0 + random.nextDouble() * 20.0;    // 80 – 100
        }
        return Math.round(raw * 10.0) / 10.0;
    }

    private double marksToGradePoint(double marks) {
        if (marks >= 90) return 10.0;
        if (marks >= 80) return 9.0;
        if (marks >= 70) return 8.0;
        if (marks >= 60) return 7.0;
        if (marks >= 50) return 6.0;
        if (marks >= 40) return 5.0;
        return 0.0;
    }
}
