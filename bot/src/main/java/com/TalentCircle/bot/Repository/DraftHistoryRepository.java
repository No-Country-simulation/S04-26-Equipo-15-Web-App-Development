package com.TalentCircle.bot.Repository;

import com.TalentCircle.bot.Entity.DraftHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DraftHistoryRepository extends JpaRepository<DraftHistory, Long> {
}
