package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.domain.model.AccountAchievement;
import com.eduscrum.qs.backend.domain.model.Achievement;
import com.eduscrum.qs.backend.exception.ConflictException;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountAchievementRepository;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.repository.AchievementRepository;
import com.eduscrum.qs.backend.service.AchievementService;
import com.eduscrum.qs.backend.service.AccountAchievementService;
import com.eduscrum.qs.backend.web.dto.request.AssignAchievementRequest;
import com.eduscrum.qs.backend.web.dto.response.LeaderboardResponse;
import com.eduscrum.qs.backend.web.dto.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller de compatibilidade com o projeto base (antigo).
 *
 * Nota: Neste projeto, "Badge" -> {@link Achievement} e "UserBadge" -> {@link AccountAchievement}.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    private final AchievementService achievementService;
    private final AccountAchievementService accountAchievementService;
    private final AchievementRepository achievementRepo;
    private final AccountRepository accountRepo;
    private final AccountAchievementRepository accountAchievementRepo;

    public BadgeController(
            AchievementService achievementService,
            AccountAchievementService accountAchievementService,
            AchievementRepository achievementRepo,
            AccountRepository accountRepo,
            AccountAchievementRepository accountAchievementRepo
    ) {
        this.achievementService = achievementService;
        this.accountAchievementService = accountAchievementService;
        this.achievementRepo = achievementRepo;
        this.accountRepo = accountRepo;
        this.accountAchievementRepo = accountAchievementRepo;
    }

    @GetMapping
    public List<Achievement> getAllBadges() {
        return achievementService.listAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MessageResponse> createBadge(@RequestBody Achievement achievement) {
        achievement.setId(null);
        achievementService.create(achievement);
        return ResponseEntity.ok(new MessageResponse("Prémio criado com sucesso!"));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MessageResponse> assignBadgeToStudent(@Valid @RequestBody AssignAchievementRequest request) {
        Account student = accountRepo.findByEmail(request.studentEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Erro: Aluno não encontrado com este email: " + request.studentEmail()
                ));

        Achievement achievement = achievementRepo.findById(request.achievementId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Erro: Prémio não encontrado com o ID: " + request.achievementId()
                ));

        if (accountAchievementRepo.existsByAccount_IdAndAchievement_Id(student.getId(), achievement.getId())) {
            throw new ConflictException("Achievement already granted to this account.");
        }

        AccountAchievement aa = new AccountAchievement();
        aa.setAccount(student);
        aa.setAchievement(achievement);
        aa.setAssignedAt(LocalDateTime.now());
        accountAchievementService.create(aa);

        return ResponseEntity.ok(new MessageResponse("Medalha atribuída com sucesso!"));
    }

    @GetMapping("/student/{studentId}")
    public List<AccountAchievement> getStudentBadges(@PathVariable Long studentId) {
        return accountAchievementRepo.findByAccount_IdOrderByAssignedAtDesc(studentId);
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardResponse> getLeaderboard() {
        return accountAchievementRepo.getLeaderboard();
    }

    @GetMapping("/leaderboard/export")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<byte[]> exportLeaderboardToCsv() {
        List<LeaderboardResponse> leaderboard = getLeaderboard();

        StringBuilder csv = new StringBuilder();
        csv.append("Nome do Aluno;Total XP\n");
        for (LeaderboardResponse row : leaderboard) {
            csv.append(row.studentName()).append(';').append(row.totalPoints()).append('\n');
        }

        byte[] csvData = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"leaderboard.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/stats/average")
    public ResponseEntity<Double> getAverageScore() {
        List<LeaderboardResponse> leaderboard = getLeaderboard();
        if (leaderboard.isEmpty()) {
            return ResponseEntity.ok(0.0);
        }

        double total = leaderboard.stream().mapToLong(LeaderboardResponse::totalPoints).sum();
        return ResponseEntity.ok(total / leaderboard.size());
    }
}
