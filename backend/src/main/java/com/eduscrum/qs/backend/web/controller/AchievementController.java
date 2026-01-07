package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.model.*;
import com.eduscrum.qs.backend.exception.ConflictException;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.*;
import com.eduscrum.qs.backend.service.AchievementService;
import com.eduscrum.qs.backend.service.AccountAchievementService;
import com.eduscrum.qs.backend.web.dto.request.AchievementRequest;
import com.eduscrum.qs.backend.web.dto.request.GrantAchievementRequest;
import com.eduscrum.qs.backend.web.dto.response.LeaderboardEntryResponse;
import com.eduscrum.qs.backend.web.dto.response.TeamLeaderboardEntryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;
    private final AccountAchievementService accountAchievementService;

    private final AchievementRepository achievementRepo;
    private final AccountRepository accountRepo;
    private final AccountAchievementRepository accountAchievementRepo;
    private final TeamAssignmentRepository teamAssignmentRepo;

    public AchievementController(
            AchievementService achievementService,
            AccountAchievementService accountAchievementService,
            AchievementRepository achievementRepo,
            AccountRepository accountRepo,
            AccountAchievementRepository accountAchievementRepo,
            TeamAssignmentRepository teamAssignmentRepo
    ) {
        this.achievementService = achievementService;
        this.accountAchievementService = accountAchievementService;
        this.achievementRepo = achievementRepo;
        this.accountRepo = accountRepo;
        this.accountAchievementRepo = accountAchievementRepo;
        this.teamAssignmentRepo = teamAssignmentRepo;
    }

    // ---- CRUD ----

    @GetMapping
    public List<Achievement> listAll() {
        return achievementService.listAll();
    }

    @GetMapping("/{id}")
    public Achievement getById(@PathVariable Long id) {
        return achievementService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Achievement create(@Valid @RequestBody AchievementRequest req) {
        Achievement a = new Achievement();
        a.setName(req.name());
        a.setDescription(req.description());
        a.setPoints(req.points());
        return achievementService.create(a);
    }

    @PutMapping("/{id}")
    public Achievement update(@PathVariable Long id, @Valid @RequestBody AchievementRequest req) {
        Achievement a = achievementService.getById(id);
        a.setName(req.name());
        a.setDescription(req.description());
        a.setPoints(req.points());
        return achievementService.update(id, a);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        achievementService.delete(id);
    }

    // ---- GRANT ----

    @PostMapping("/grant")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountAchievement grant(@Valid @RequestBody GrantAchievementRequest req) {
        Account acc = accountRepo.findById(req.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.accountId()));

        Achievement ach = achievementRepo.findById(req.achievementId())
                .orElseThrow(() -> new ResourceNotFoundException("Achievement not found: " + req.achievementId()));

        if (accountAchievementRepo.existsByAccountIdAndAchievementId(req.accountId(), req.achievementId())) {
            throw new ConflictException("Achievement already granted to this account.");
        }

        AccountAchievement aa = new AccountAchievement();
        aa.setAccount(acc);
        aa.setAchievement(ach);
        aa.setAssignedAt(LocalDateTime.now());

        return accountAchievementService.create(aa);
    }

    // ---- LEADERBOARDS ----

    @GetMapping("/leaderboard")
    public List<LeaderboardEntryResponse> leaderboard() {
        List<AccountAchievement> all = accountAchievementRepo.findAll();

        Map<Long, List<AccountAchievement>> byAccount = all.stream()
                .filter(x -> x.getAccount() != null)
                .collect(Collectors.groupingBy(x -> x.getAccount().getId()));

        List<LeaderboardEntryResponse> out = new ArrayList<>();

        for (var entry : byAccount.entrySet()) {
            Long accountId = entry.getKey();
            List<AccountAchievement> awards = entry.getValue();

            String name = awards.get(0).getAccount().getName();
            long totalAchievements = awards.size();
            int totalPoints = awards.stream()
                    .map(AccountAchievement::getAchievement)
                    .filter(Objects::nonNull)
                    .mapToInt(a -> a.getPoints() == null ? 0 : a.getPoints())
                    .sum();

            out.add(new LeaderboardEntryResponse(accountId, name, totalAchievements, totalPoints));
        }

        out.sort(Comparator.comparingInt(LeaderboardEntryResponse::totalPoints).reversed());
        return out;
    }

    @GetMapping("/team-leaderboard/{teamId}")
    public List<TeamLeaderboardEntryResponse> teamLeaderboard(@PathVariable Long teamId) {
        List<TeamAssignment> members = teamAssignmentRepo.findByTeamId(teamId);
        if (members.isEmpty()) return List.of();

        Set<Long> memberIds = members.stream()
                .map(m -> m.getAccount().getId())
                .collect(Collectors.toSet());

        List<AccountAchievement> allAwards = accountAchievementRepo.findAll().stream()
                .filter(x -> x.getAccount() != null && memberIds.contains(x.getAccount().getId()))
                .toList();

        Map<Long, List<AccountAchievement>> byAccount = allAwards.stream()
                .collect(Collectors.groupingBy(x -> x.getAccount().getId()));

        List<TeamLeaderboardEntryResponse> out = new ArrayList<>();

        for (TeamAssignment m : members) {
            Long accountId = m.getAccount().getId();
            String name = m.getAccount().getName();

            List<AccountAchievement> awards = byAccount.getOrDefault(accountId, List.of());
            long totalAchievements = awards.size();
            int totalPoints = awards.stream()
                    .map(AccountAchievement::getAchievement)
                    .filter(Objects::nonNull)
                    .mapToInt(a -> a.getPoints() == null ? 0 : a.getPoints())
                    .sum();

            out.add(new TeamLeaderboardEntryResponse(
                    teamId, accountId, name, m.getScrumRole(), totalAchievements, totalPoints
            ));
        }

        out.sort(Comparator.comparingInt(TeamLeaderboardEntryResponse::totalPoints).reversed());
        return out;
    }
}
