package com.eduscrum.qs.backend;

import com.eduscrum.qs.backend.domain.model.ProjectWorkspace;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.ProjectWorkspaceRepository;
import com.eduscrum.qs.backend.service.impl.ProjectWorkspaceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectWorkspaceRepository repo;

    @InjectMocks
    private ProjectWorkspaceServiceImpl service;

    private ProjectWorkspace project;

    @BeforeEach
    void setup() {
        project = new ProjectWorkspace();
        project.setId(1L);
        project.setName("Projeto A");
        project.setDescription("Desc A");
    }

    @Test
    void create_savesProject() {
        when(repo.save(project)).thenReturn(project);

        ProjectWorkspace result = service.create(project);

        assertSame(project, result);
        verify(repo).save(project);
    }

    @Test
    void getById_exists_returns() {
        when(repo.findById(1L)).thenReturn(Optional.of(project));

        ProjectWorkspace result = service.getById(1L);

        assertSame(project, result);
        verify(repo).findById(1L);
    }

    @Test
    void getById_missing_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
        verify(repo).findById(99L);
    }

    @Test
    void listAll_returnsRepoResults() {
        when(repo.findAll()).thenReturn(List.of(project));

        List<ProjectWorkspace> result = service.listAll();

        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void listByCourse_delegates() {
        when(repo.findByCourseId(10L)).thenReturn(List.of(project));

        List<ProjectWorkspace> result = service.listByCourse(10L);

        assertEquals(1, result.size());
        verify(repo).findByCourseId(10L);
    }

    @Test
    void update_copiesPropsExceptId_andSaves() {
        when(repo.findById(1L)).thenReturn(Optional.of(project));
        when(repo.save(any(ProjectWorkspace.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjectWorkspace updated = new ProjectWorkspace();
        updated.setId(999L);
        updated.setName("Projeto A (Updated)");
        updated.setDescription("Nova desc");

        ProjectWorkspace result = service.update(1L, updated);

        assertEquals(1L, result.getId());
        assertEquals("Projeto A (Updated)", result.getName());
        assertEquals("Nova desc", result.getDescription());

        ArgumentCaptor<ProjectWorkspace> captor = ArgumentCaptor.forClass(ProjectWorkspace.class);
        verify(repo).save(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @Test
    void delete_deletesExisting() {
        when(repo.findById(1L)).thenReturn(Optional.of(project));

        service.delete(1L);

        verify(repo).delete(project);
    }
}
