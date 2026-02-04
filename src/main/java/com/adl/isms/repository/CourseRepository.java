package com.adl.isms.repository;

import com.adl.isms.dto.CourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    boolean existsByCourseCode(String courseCode);
    Optional<CourseEntity> findByCourseCode(String courseCode);
    Page<CourseDTO> findAllProjectedBy(Pageable pageable);

    List<CourseEntity> findAllBySemester(int semester);
}
