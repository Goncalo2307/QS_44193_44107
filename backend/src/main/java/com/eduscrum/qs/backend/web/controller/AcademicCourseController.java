package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.AcademicCourse;
import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.service.AcademicCourseService;
import com.eduscrum.qs.backend.security.services.AccountDetails;
import com.eduscrum.qs.backend.web.dto.request.CourseRequest;
import com.eduscrum.qs.backend.web.dto.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // Compatibilidade: se teacherId vier, filtra (útil para testes/Postman)
        if (teacherId != null) {
            return courseService.listAll().stream()
                    .filter(c -> c.getTeacher() != null && teacherId.equals(c.getTeacher().getId()))
                    .toList();
        }

        // Comportamento do projeto base: o professor só vê os seus cursos.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AccountDetails details) {
            boolean isTeacher = details.getAuthorities().stream().anyMatch(a -> "ROLE_TEACHER".equals(a.getAuthority()));
            boolean isAdmin = details.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

            if (isAdmin) {
                return courseService.listAll();
            }
            if (isTeacher) {
                Long teacherAccId = details.getId();
                return courseService.listAll().stream()
                        .filter(c -> c.getTeacher() != null && teacherAccId.equals(c.getTeacher().getId()))
                        .toList();
            }
        }

        // Aluno: devolve todos (pode ser refinado mais tarde)
        return courseService.listAll();
    }

    @GetMapping("/{id}")
    public AcademicCourse getById(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<MessageResponse> create(@Valid @RequestBody CourseRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AccountDetails details)) {
            throw new ResourceNotFoundException("Authenticated user not found.");
        }

        boolean isAdmin = details.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        Account teacher;
        if (isAdmin && req.teacherAccountId() != null) {
            teacher = accountRepo.findById(req.teacherAccountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.teacherAccountId()));
        } else {
            teacher = accountRepo.findById(details.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + details.getId()));
        }

        AcademicCourse c = new AcademicCourse();
        c.setName(req.name());
        c.setDescription(req.description());
        c.setTeacher(teacher);

        courseService.create(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Curso criado com sucesso!"));
    }

    // Extra (não existe no projeto base) - mantido, mas sem permitir teacher=null (o model exige teacher).
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public AcademicCourse update(@PathVariable Long id, @Valid @RequestBody CourseRequest req) {
        AcademicCourse existing = courseService.getById(id);
        existing.setName(req.name());
        existing.setDescription(req.description());

        // Só ADMIN pode reatribuir teacher explicitamente
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AccountDetails details) {
            boolean isAdmin = details.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (isAdmin && req.teacherAccountId() != null) {
                Account teacher = accountRepo.findById(req.teacherAccountId())
                        .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.teacherAccountId()));
                existing.setTeacher(teacher);
            }
        }

        if (existing.getTeacher() == null) {
            throw new ResourceNotFoundException("Teacher is required.");
        }

        return courseService.update(id, existing);
    }

    // Extra (não existe no projeto base) - mantido
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}
