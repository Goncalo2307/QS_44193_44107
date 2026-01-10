package com.eduscrum.qs.backend;

import com.eduscrum.qs.backend.domain.model.Sprint;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.SprintRepository;
import com.eduscrum.qs.backend.service.impl.SprintServiceImpl;
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
class SprintServiceTest {

    @Mock
    private SprintRepository repo;

    @InjectMocks
    private SprintServiceImpl service;

    private Sprint sprint;

    @BeforeEach
    void setup() {
        sprint = new Sprint();
        sprint.setId(1L);
        sprint.setName("Sprint 1");
    }

    @Test
    void create_savesSprint() {
        when(repo.save(sprint)).thenReturn(sprint);

        Sprint result = service.create(sprint);

        assertSame(sprint, result);
        verify(repo).save(sprint);
    }

    @Test
    void getById_exists_returns() {
        when(repo.findById(1L)).thenReturn(Optional.of(sprint));

        Sprint result = service.getById(1L);

        assertSame(sprint, result);
        verify(repo).findById(1L);
    }

    @Test
    void getById_missing_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void listAll_returns() {
        when(repo.findAll()).thenReturn(List.of(sprint));

        List<Sprint> result = service.listAll();

        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void listByProjectWorkspace_delegates() {
        when(repo.findByProjectWorkspaceId(10L)).thenReturn(List.of(sprint));

        List<Sprint> result = service.listByProjectWorkspace(10L);

        assertEquals(1, result.size());
        verify(repo).findByProjectWorkspaceId(10L);
    }

    @Test
    void update_copiesPropsExceptId_andSaves() {
        when(repo.findById(1L)).thenReturn(Optional.of(sprint));
        when(repo.save(any(Sprint.class))).thenAnswer(inv -> inv.getArgument(0));

        Sprint updated = new Sprint();
        updated.setId(999L);
        updated.setName("Sprint 1 (Updated)");

        Sprint result = service.update(1L, updated);

        assertEquals(1L, result.getId());
        assertEquals("Sprint 1 (Updated)", result.getName());

        ArgumentCaptor<Sprint> captor = ArgumentCaptor.forClass(Sprint.class);
        verify(repo).save(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @Test
    void delete_deletesExisting() {
        when(repo.findById(1L)).thenReturn(Optional.of(sprint));

        service.delete(1L);

        verify(repo).delete(sprint);
    }
}
