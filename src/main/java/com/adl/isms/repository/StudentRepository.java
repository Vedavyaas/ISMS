package com.adl.isms.repository;

import com.adl.isms.assests.EnrolmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    boolean existsByEmail(String email);
    Page<StudentEntity> findAllByEnrolmentStatus(EnrolmentStatus enrolmentStatus, Pageable pageable);
}
