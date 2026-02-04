package com.adl.isms.repository;

import com.adl.isms.assests.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<FacultyEntity, Long> {
    boolean existsByEmail(String email);

    List<FacultyEntity> findAllByDepartment(Department department);
}
