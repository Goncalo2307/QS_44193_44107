package com.eduscrum.qs.backend.web.controller;

import com.eduscrum.qs.backend.domain.enums.ScrumRoleType;
import com.eduscrum.qs.backend.domain.model.Account;
import com.eduscrum.qs.backend.domain.model.ProjectWorkspace;
import com.eduscrum.qs.backend.domain.model.ScrumTeam;
import com.eduscrum.qs.backend.domain.model.TeamAssignment;
import com.eduscrum.qs.backend.exception.ConflictException;
import com.eduscrum.qs.backend.exception.ResourceNotFoundException;
import com.eduscrum.qs.backend.repository.AccountRepository;
import com.eduscrum.qs.backend.repository.ProjectWorkspaceRepository;
import com.eduscrum.qs.backend.repository.TeamAssignmentRepository;
import com.eduscrum.qs.backend.repository.ScrumTeamRepository;
import com.eduscrum.qs.backend.service.ScrumTeamService;
import com.eduscrum.qs.backend.web.dto.request.TeamMemberRequest;
import com.eduscrum.qs.backend.web.dto.request.TeamRequest;
import com.eduscrum.qs.backend.web.dto.response.TeamLeaderboardResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class ScrumTeamController {

    private final ScrumTeamService teamService;
    private final ScrumTeamRepository teamRepo;
    private final ProjectWorkspaceRepository workspaceRepo;
    private final AccountRepository accountRepo;
    private final TeamAssignmentRepository assignmentRepo;

    public ScrumTeamController(
            ScrumTeamService teamService,
            ScrumTeamRepository teamRepo,
            ProjectWorkspaceRepository workspaceRepo,
            AccountRepository accountRepo,
            TeamAssignmentRepository assignmentRepo
    ) {
        this.teamService = teamService;
        this.teamRepo = teamRepo;
        this.workspaceRepo = workspaceRepo;
        this.accountRepo = accountRepo;
        this.assignmentRepo = assignmentRepo;
    }

    // -----------------------------
    // ENDPOINTS ALINHADOS COM O PROJETO BASE (antigo)
    // -----------------------------

    @PostMapping("/project/{projectId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ScrumTeam> createTeamForProject(
            @PathVariable Long projectId,
            @RequestParam(name = "teamName") String teamName
    ) {
        ProjectWorkspace project = workspaceRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (teamRepo.existsByProjectWorkspaceId(projectId)) {
            throw new ConflictException("This project already has a team.");
        }

        ScrumTeam team = new ScrumTeam();
        team.setName(teamName);
        team.setProjectWorkspace(project);

        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(team));
    }

    @GetMapping("/project/{projectId}")
    public List<ScrumTeam> getTeamsByProject(@PathVariable Long projectId) {
        return teamService.listByProjectWorkspace(projectId);
    }

    @PostMapping("/{teamId}/member")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TeamAssignment> addMemberLegacy(
            @PathVariable Long teamId,
            @RequestParam Long userId,
            @RequestParam String role
    ) {
        ScrumTeam team = teamService.getById(teamId);
        Account account = accountRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + userId));

        if (assignmentRepo.findByTeamIdAndAccountId(teamId, userId).isPresent()) {
            throw new ConflictException("Account already assigned to this team.");
        }

        ScrumRoleType scrumRole;
        try { scrumRole = ScrumRoleType.valueOf(role.trim().toUpperCase()); }
        catch (Exception ex) { scrumRole = ScrumRoleType.DEVELOPER; }

        TeamAssignment a = new TeamAssignment();
        a.setTeam(team);
        a.setAccount(account);
        a.setScrumRole(scrumRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentRepo.save(a));
    }

    @DeleteMapping("/member/{teamMemberId}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMemberLegacy(@PathVariable Long teamMemberId) {
        TeamAssignment a = assignmentRepo.findById(teamMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found: " + teamMemberId));
        assignmentRepo.delete(a);
    }

    @GetMapping("/leaderboard")
    public List<TeamLeaderboardResponse> getTeamLeaderboard() {
        return assignmentRepo.getTeamLeaderboard();
    }

    @GetMapping
    public List<ScrumTeam> listAll(@RequestParam(required = false) Long workspaceId) {
        if (workspaceId == null) return teamService.listAll();
        return teamService.listByProjectWorkspace(workspaceId);
    }

    @GetMapping("/{id}")
    public ScrumTeam getById(@PathVariable Long id) {
        return teamService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ScrumTeam create(@Valid @RequestBody TeamRequest req) {
        ProjectWorkspace ws = workspaceRepo.findById(req.workspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectWorkspace not found: " + req.workspaceId()));

        if (teamRepo.existsByProjectWorkspaceId(req.workspaceId())) {
            throw new ConflictException("This project already has a team.");
        }

        ScrumTeam team = new ScrumTeam();
        team.setName(req.name());
        team.setProjectWorkspace(ws);

        return teamService.create(team);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ScrumTeam update(@PathVariable Long id, @Valid @RequestBody TeamRequest req) {
        ScrumTeam team = teamService.getById(id);

        ProjectWorkspace ws = workspaceRepo.findById(req.workspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectWorkspace not found: " + req.workspaceId()));

        team.setName(req.name());
        team.setProjectWorkspace(ws);

        return teamService.update(id, team);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        teamService.delete(id);
    }

    // ---- Members (TeamAssignment) ----

    @GetMapping("/{teamId}/members")
    public List<TeamAssignment> listMembers(@PathVariable Long teamId) {
        return assignmentRepo.findByTeamId(teamId);
    }

    @PostMapping("/{teamId}/members")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamAssignment addMember(@PathVariable Long teamId, @Valid @RequestBody TeamMemberRequest req) {
        ScrumTeam team = teamService.getById(teamId);

        Account account = accountRepo.findById(req.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + req.accountId()));

        if (assignmentRepo.findByTeamIdAndAccountId(teamId, req.accountId()).isPresent()) {
            throw new ConflictException("Account already assigned to this team.");
        }

        TeamAssignment a = new TeamAssignment();
        a.setTeam(team);
        a.setAccount(account);
        a.setScrumRole(req.scrumRole() == null ? ScrumRoleType.DEVELOPER : req.scrumRole());

        return assignmentRepo.save(a);
    }

    @DeleteMapping("/{teamId}/members/{accountId}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Long teamId, @PathVariable Long accountId) {
        TeamAssignment a = assignmentRepo.findByTeamIdAndAccountId(teamId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TeamAssignment not found for team=" + teamId + " account=" + accountId
                ));
        assignmentRepo.delete(a);
    }
}
