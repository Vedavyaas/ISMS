package com.adl.isms.configuration;

import com.adl.isms.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeds sample attendance data after startup.
 * Polls every 5 seconds (initial delay 5s) until the async EnrollmentService
 * has populated enrolments, then seeds one attendance record per enrolled
 * (student, course) pair and silently skips all future ticks.
 */
@Component
public class AttendanceDataInitializer {

    private final EnrolmentRepository enrolmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final Random random = new Random(42);

    public AttendanceDataInitializer(EnrolmentRepository enrolmentRepository,
                                     AttendanceRepository attendanceRepository) {
        this.enrolmentRepository = enrolmentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    @Transactional
    public void seedAttendance() {
        if (attendanceRepository.count() > 0) {
            // Already seeded — silently skip all future ticks
            return;
        }

        List<EnrolmentEntity> allEnrolments = enrolmentRepository.findAll();

        if (allEnrolments.isEmpty()) {
            System.out.println("Enrolments not ready yet, retrying attendance seeding in 5 seconds...");
            return;
        }

        List<AttendanceEntity> attendanceList = new ArrayList<>();

        for (EnrolmentEntity enrolment : allEnrolments) {
            StudentEntity student = enrolment.getStudent();
            CourseEntity  course  = enrolment.getCourse();

            // Guard against the unique (student_id, course_id) constraint
            boolean alreadyExists = attendanceRepository
                    .findByStudent_IdAndCourse_Id(student.getId(), course.getId())
                    .isPresent();

            if (alreadyExists) continue;

            attendanceList.add(new AttendanceEntity(student, course, generateRealisticAttendance()));
        }

        attendanceRepository.saveAllAndFlush(attendanceList);
        System.out.println("Initialized " + attendanceList.size()
                + " attendance records based on " + allEnrolments.size() + " enrolments.");
    }

    /**
     * ~10% chance of low attendance (30–59%), ~90% chance of normal (60–100%).
     */
    private double generateRealisticAttendance() {
        if (random.nextInt(10) == 0) {
            double raw = 30.0 + random.nextDouble() * 29.0;
            return Math.round(raw * 10.0) / 10.0;
        } else {
            double raw = 60.0 + random.nextDouble() * 40.0;
            return Math.round(raw * 10.0) / 10.0;
        }
    }
}
