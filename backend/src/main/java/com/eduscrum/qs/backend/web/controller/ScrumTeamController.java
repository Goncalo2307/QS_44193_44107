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
import com.eduscrum.qs.backend.service.ScrumTeamService;
import com.eduscrum.qs.backend.web.dto.request.TeamMemberRequest;
import com.eduscrum.qs.backend.web.dto.request.TeamRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class ScrumTeamController {

    private final ScrumTeamService teamService;
    private final ProjectWorkspaceRepository workspaceRepo;
    private final AccountRepository accountRepo;
    private final TeamAssignmentRepository assignmentRepo;

    public ScrumTeamController(
            ScrumTeamService teamService,
            ProjectWorkspaceRepository workspaceRepo,
            AccountRepository accountRepo,
            TeamAssignmentRepository assignmentRepo
    ) {
        this.teamService = teamService;
        this.workspaceRepo = workspaceRepo;
        this.accountRepo = accountRepo;
        this.assignmentRepo = assignmentRepo;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ScrumTeam create(@Valid @RequestBody TeamRequest req) {
        ProjectWorkspace ws = workspaceRepo.findById(req.workspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectWorkspace not found: " + req.workspaceId()));

        ScrumTeam team = new ScrumTeam();
        team.setName(req.name());
        team.setProjectWorkspace(ws);

        return teamService.create(team);
    }

    @PutMapping("/{id}")
    public ScrumTeam update(@PathVariable Long id, @Valid @RequestBody TeamRequest req) {
        ScrumTeam team = teamService.getById(id);

        ProjectWorkspace ws = workspaceRepo.findById(req.workspaceId())
                .orElseThrow(() -> new ResourceNotFoundException("ProjectWorkspace not found: " + req.workspaceId()));

        team.setName(req.name());
        team.setProjectWorkspace(ws);

        return teamService.update(id, team);
    }

    @DeleteMapping("/{id}")
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Long teamId, @PathVariable Long accountId) {
        TeamAssignment a = assignmentRepo.findByTeamIdAndAccountId(teamId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TeamAssignment not found for team=" + teamId + " account=" + accountId
                ));
        assignmentRepo.delete(a);
    }
}
