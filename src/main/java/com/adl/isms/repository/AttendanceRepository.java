package com.adl.isms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    List<AttendanceEntity> findAllByStudent_UserId_UserName(String username);

    List<AttendanceEntity> findAllByCourse_Id(Long courseId);

    Optional<AttendanceEntity> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    List<AttendanceEntity> findAll();
}
