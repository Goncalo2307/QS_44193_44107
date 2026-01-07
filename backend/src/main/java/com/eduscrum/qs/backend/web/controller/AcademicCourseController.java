package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.AcademicCourse;
import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.service.AcademicCourseService;
import com.eduscrum.qs.backend.web.dto.request.CourseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class AcademicCourseController {

    private final AcademicCourseService courseService;
    private final AccountRepository accountRepo;

    public AcademicCourseController(AcademicCourseService courseService, AccountRepository accountRepo) {
        this.courseService = courseService;
        this.accountRepo = accountRepo;
    }

    @GetMapping
    public List<AcademicCourse> listAll(@RequestParam(required = false) Long teacherId) {
        List<AcademicCourse> all = courseService.listAll();

        if (teacherId == null) {
            return all;
        }

        return all.stream()
                .filter(c -> c.getTeacher() != null && c.getTeacher().getId() != null)
                .filter(c -> teacherId.equals(c.getTeacher().getId()))
                .toList();
    }

    @GetMapping("/{id}")
    public AcademicCourse getById(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcademicCourse create(@Valid @RequestBody CourseRequest req) {
        AcademicCourse c = new AcademicCourse();
        c.setName(req.name());
        c.setDescription(req.description());

        if (req.teacherAccountId() != null) {
            Account teacher = accountRepo.findById(req.teacherAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.teacherAccountId()));
            c.setTeacher(teacher);
        }

        return courseService.create(c);
    }

    @PutMapping("/{id}")
    public AcademicCourse update(@PathVariable Long id, @Valid @RequestBody CourseRequest req) {
        AcademicCourse existing = courseService.getById(id);

        existing.setName(req.name());
        existing.setDescription(req.description());

        if (req.teacherAccountId() != null) {
            Account teacher = accountRepo.findById(req.teacherAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.teacherAccountId()));
            existing.setTeacher(teacher);
        } else {
            existing.setTeacher(null);
        }

        return courseService.update(id, existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}
