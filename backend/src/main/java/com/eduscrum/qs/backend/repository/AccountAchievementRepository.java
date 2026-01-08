package com.eduscrum.qs.backend.repository;

import com.eduscrum.qs.backend.domain.model.AccountAchievement;
import com.eduscrum.qs.backend.web.dto.response.LeaderboardResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountAchievementRepository extends JpaRepository<AccountAchievement, Long> {


    List<AccountAchievement> findByAccount_IdOrderByAssignedAtDesc(Long accountId);

    boolean existsByAccount_IdAndAchievement_Id(Long accountId, Long achievementId);

    @Query("""
            select new com.eduscrum.qs.backend.web.dto.response.LeaderboardResponse(
                aa.account.name,
                sum(coalesce(aa.achievement.points, 0))
            )
            from AccountAchievement aa
            group by aa.account.id, aa.account.name
            order by sum(coalesce(aa.achievement.points, 0)) desc
            """)
    List<LeaderboardResponse> getLeaderboard();
}
