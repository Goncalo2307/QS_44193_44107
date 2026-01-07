package com.eduscrum.qs.backend.service;

import com.eduscrum.qs.backend.domain.model.AcademicCourse;
import java.util.List;

public interface AcademicCourseService {
    AcademicCourse create(AcademicCourse course);

    AcademicCourse getById(Long id);

    List<AcademicCourse> listAll();

    List<AcademicCourse> listByTeacher(Long teacherId);

    AcademicCourse update(Long id, AcademicCourse course);

    void delete(Long id);
}
