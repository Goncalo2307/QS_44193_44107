package com.eduscrum.qs.backend;

import com.eduscrum.qs.backend.domain.model.ScrumTeam;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.ScrumTeamRepository;
import com.eduscrum.qs.backend.service.impl.ScrumTeamServiceImpl;
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
class TeamServiceTest {

    @Mock
    private ScrumTeamRepository repo;

    @InjectMocks
    private ScrumTeamServiceImpl service;

    private ScrumTeam team;

    @BeforeEach
    void setup() {
        team = new ScrumTeam();
        team.setId(1L);
        team.setName("Equipa 1");
    }

    @Test
    void create_savesTeam() {
        when(repo.save(team)).thenReturn(team);

        ScrumTeam result = service.create(team);

        assertSame(team, result);
        verify(repo).save(team);
    }

    @Test
    void getById_exists_returns() {
        when(repo.findById(1L)).thenReturn(Optional.of(team));

        ScrumTeam result = service.getById(1L);

        assertSame(team, result);
        verify(repo).findById(1L);
    }

    @Test
    void getById_missing_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void listAll_returns() {
        when(repo.findAll()).thenReturn(List.of(team));

        List<ScrumTeam> result = service.listAll();

        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void listByProjectWorkspace_delegates() {
        when(repo.findByProjectWorkspaceId(10L)).thenReturn(List.of(team));

        List<ScrumTeam> result = service.listByProjectWorkspace(10L);

        assertEquals(1, result.size());
        verify(repo).findByProjectWorkspaceId(10L);
    }

    @Test
    void update_copiesPropsExceptId_andSaves() {
        when(repo.findById(1L)).thenReturn(Optional.of(team));
        when(repo.save(any(ScrumTeam.class))).thenAnswer(inv -> inv.getArgument(0));

        ScrumTeam updated = new ScrumTeam();
        updated.setId(999L);
        updated.setName("Equipa 1 (Updated)");

        ScrumTeam result = service.update(1L, updated);

        assertEquals(1L, result.getId());
        assertEquals("Equipa 1 (Updated)", result.getName());

        ArgumentCaptor<ScrumTeam> captor = ArgumentCaptor.forClass(ScrumTeam.class);
        verify(repo).save(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @Test
    void delete_deletesExisting() {
        when(repo.findById(1L)).thenReturn(Optional.of(team));

        service.delete(1L);

        verify(repo).delete(team);
    }
}
