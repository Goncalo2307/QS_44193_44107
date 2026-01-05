package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.AcademicCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicCourseRepository extends JpaRepository<AcademicCourse, Long> {
}
