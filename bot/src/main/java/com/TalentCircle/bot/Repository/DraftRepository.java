package com.TalentCircle.bot.Repository;

import com.TalentCircle.bot.Entity.Draft;
import com.TalentCircle.bot.Entity.DraftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {

    List<Draft> findByStatus(DraftStatus status);
}
