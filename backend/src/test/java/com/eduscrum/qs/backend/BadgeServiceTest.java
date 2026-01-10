package com.eduscrum.qs.backend;

import com.eduscrum.qs.backend.domain.model.Achievement;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AchievementRepository;
import com.eduscrum.qs.backend.service.impl.AchievementServiceImpl;
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
class BadgeServiceTest {

    @Mock
    private AchievementRepository repo;

    @InjectMocks
    private AchievementServiceImpl service;

    private Achievement achievement;

    @BeforeEach
    void setup() {
        achievement = new Achievement();
        achievement.setId(1L);
        achievement.setName("Top Performer");
        achievement.setDescription("Excelente desempenho");
        achievement.setPoints(100);
    }

    @Test
    void create_savesAchievement() {
        when(repo.save(achievement)).thenReturn(achievement);

        Achievement result = service.create(achievement);

        assertSame(achievement, result);
        verify(repo).save(achievement);
    }

    @Test
    void getById_exists_returns() {
        when(repo.findById(1L)).thenReturn(Optional.of(achievement));

        Achievement result = service.getById(1L);

        assertSame(achievement, result);
        verify(repo).findById(1L);
    }

    @Test
    void getById_missing_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void listAll_returns() {
        when(repo.findAll()).thenReturn(List.of(achievement));

        List<Achievement> result = service.listAll();

        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void update_copiesPropsExceptId_andSaves() {
        when(repo.findById(1L)).thenReturn(Optional.of(achievement));
        when(repo.save(any(Achievement.class))).thenAnswer(inv -> inv.getArgument(0));

        Achievement updated = new Achievement();
        updated.setId(999L);
        updated.setName("Top Performer (Updated)");
        updated.setDescription("Nova descrição");
        updated.setPoints(150);

        Achievement result = service.update(1L, updated);

        assertEquals(1L, result.getId());
        assertEquals("Top Performer (Updated)", result.getName());
        assertEquals("Nova descrição", result.getDescription());
        assertEquals(150, result.getPoints());

        ArgumentCaptor<Achievement> captor = ArgumentCaptor.forClass(Achievement.class);
        verify(repo).save(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @Test
    void delete_deletesExisting() {
        when(repo.findById(1L)).thenReturn(Optional.of(achievement));

        service.delete(1L);

        verify(repo).delete(achievement);
    }
}
