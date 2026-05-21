package com.TalentCircle.bot.draft.repository;

import com.TalentCircle.bot.Entity.PipelineRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineRunRepository extends JpaRepository<PipelineRun, Long> {
}