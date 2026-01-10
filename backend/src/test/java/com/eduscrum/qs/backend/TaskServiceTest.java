package com.eduscrum.qs.backend;

import com.eduscrum.qs.backend.domain.enums.TaskStatus;
import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.domain.model.Task;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.repository.TaskRepository;
import com.eduscrum.qs.backend.service.impl.TaskServiceImpl;
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
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepo;

    @Mock
    private AccountRepository accountRepo;

    @InjectMocks
    private TaskServiceImpl service;

    private Task task;

    @BeforeEach
    void setup() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Tarefa 1");
        task.setStatus(TaskStatus.TO_DO);
    }

    @Test
    void create_savesTask() {
        when(taskRepo.save(task)).thenReturn(task);

        Task result = service.create(task);

        assertSame(task, result);
        verify(taskRepo).save(task);
    }

    @Test
    void getById_exists_returns() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        Task result = service.getById(1L);

        assertSame(task, result);
        verify(taskRepo).findById(1L);
    }

    @Test
    void getById_missing_throws() {
        when(taskRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void listAll_returns() {
        when(taskRepo.findAll()).thenReturn(List.of(task));

        List<Task> result = service.listAll();

        assertEquals(1, result.size());
        verify(taskRepo).findAll();
    }

    @Test
    void listBySprint_delegates() {
        when(taskRepo.findBySprintId(10L)).thenReturn(List.of(task));

        List<Task> result = service.listBySprint(10L);

        assertEquals(1, result.size());
        verify(taskRepo).findBySprintId(10L);
    }

    @Test
    void listByAssignedUser_delegates() {
        when(taskRepo.findByAssignedUserId(5L)).thenReturn(List.of(task));

        List<Task> result = service.listByAssignedUser(5L);

        assertEquals(1, result.size());
        verify(taskRepo).findByAssignedUserId(5L);
    }

    @Test
    void update_copiesPropsExceptId_andSaves() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepo.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task updated = new Task();
        updated.setId(999L);
        updated.setTitle("Tarefa 1 (Updated)");
        updated.setStatus(TaskStatus.DONE);

        Task result = service.update(1L, updated);

        assertEquals(1L, result.getId());
        assertEquals("Tarefa 1 (Updated)", result.getTitle());
        assertEquals(TaskStatus.DONE, result.getStatus());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepo).save(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @Test
    void updateStatus_updatesAndSaves() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepo.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.updateStatus(1L, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepo).save(task);
    }

    @Test
    void assignTaskToUser_assignsAndSaves() {
        Account user = new Account();
        user.setId(7L);

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(accountRepo.findById(7L)).thenReturn(Optional.of(user));
        when(taskRepo.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = service.assignTaskToUser(1L, 7L);

        assertNotNull(result.getAssignedUser());
        assertEquals(7L, result.getAssignedUser().getId());
        verify(taskRepo).save(task);
    }

    @Test
    void assignTaskToUser_whenUserMissing_throws() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(accountRepo.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.assignTaskToUser(1L, 7L));
        verify(taskRepo, never()).save(any());
    }

    @Test
    void delete_deletesExisting() {
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        service.delete(1L);

        verify(taskRepo).delete(task);
    }
}
