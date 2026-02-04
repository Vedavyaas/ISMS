package com.adl.isms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrolmentRepository extends JpaRepository<EnrolmentEntity, Long> {
    List<EnrolmentEntity> findAllByStudent_UserId_UserName(String username);
    List<EnrolmentEntity> findAllByFacultyEntity_User_UserName(String username);
}
