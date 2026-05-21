package com.TalentCircle.bot.Repository;

import com.TalentCircle.bot.Entity.WeeklyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyActivityRepository extends JpaRepository<WeeklyActivity, Long> {
}