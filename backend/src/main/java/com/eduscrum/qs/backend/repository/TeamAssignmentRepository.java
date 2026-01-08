package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.TeamAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eduscrum.qs.backend.web.dto.response.TeamLeaderboardResponse;

import java.util.List;
import java.util.Optional;

public interface TeamAssignmentRepository extends JpaRepository<TeamAssignment, Long> {
    List<TeamAssignment> findByTeamId(Long teamId);
    List<TeamAssignment> findByAccountId(Long accountId);
    Optional<TeamAssignment> findByTeamIdAndAccountId(Long teamId, Long accountId);

    @Query("""
            select new com.eduscrum.qs.backend.web.dto.response.TeamLeaderboardResponse(
                    ta.team.name,
                    ta.team.projectWorkspace.name,
                    coalesce(sum(aa.achievement.points), 0)
            )
            from TeamAssignment ta
            left join AccountAchievement aa on aa.account = ta.account
            group by ta.team.id, ta.team.name, ta.team.projectWorkspace.name
            order by coalesce(sum(aa.achievement.points), 0) desc
            """)
    List<TeamLeaderboardResponse> getTeamLeaderboard();
}
