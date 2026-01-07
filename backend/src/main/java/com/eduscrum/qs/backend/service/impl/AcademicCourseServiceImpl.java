package com.eduscrum.qs.backend.service.impl;

import com.eduscrum.qs.backend.domain.model.AcademicCourse;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AcademicCourseRepository;
import com.eduscrum.qs.backend.service.AcademicCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AcademicCourseServiceImpl implements AcademicCourseService {

    private final AcademicCourseRepository repo;

    public AcademicCourseServiceImpl(AcademicCourseRepository repo) {
        this.repo = repo;
    }
    @Override
    @Transactional(readOnly = true)
    public List<AcademicCourse> listByTeacher(Long teacherId) {
        return repo.findByTeacher_Id(teacherId);
    }


    @Override
    public AcademicCourse create(AcademicCourse course) {
        return repo.save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicCourse getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicCourse not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicCourse> listAll() {
        return repo.findAll();
    }

    @Override
    public AcademicCourse update(Long id, AcademicCourse updated) {
        AcademicCourse existing = getById(id);
        BeanUtils.copyProperties(updated, existing, "id");
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.delete(getById(id));
    }
}
