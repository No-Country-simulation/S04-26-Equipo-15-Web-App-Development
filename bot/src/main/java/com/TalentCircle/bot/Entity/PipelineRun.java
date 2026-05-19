package com.TalentCircle.bot.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pipeline_run")
public class PipelineRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PipelineRunStatus status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    @Column(nullable = false)
    private int draftsGenerated;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PipelineRunStatus getStatus() { return status; }
    public void setStatus(PipelineRunStatus status) { this.status = status; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    public int getDraftsGenerated() { return draftsGenerated; }
    public void setDraftsGenerated(int draftsGenerated) { this.draftsGenerated = draftsGenerated; }
}
