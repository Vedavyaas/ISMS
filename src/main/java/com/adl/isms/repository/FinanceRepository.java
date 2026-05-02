package com.adl.isms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinanceRepository extends JpaRepository<FinanceEntity, Long> {
    Optional<FinanceEntity> findByStudent_Id(Long studentId);
    Optional<FinanceEntity> findByStudent_UserId_UserName(String username);
}
