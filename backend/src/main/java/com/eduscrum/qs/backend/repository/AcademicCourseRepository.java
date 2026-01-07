package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.AcademicCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcademicCourseRepository extends JpaRepository<AcademicCourse, Long> {
    List<AcademicCourse> findByTeacher_Id(Long teacherId);
}
