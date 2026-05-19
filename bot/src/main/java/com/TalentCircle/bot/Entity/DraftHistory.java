package com.TalentCircle.bot.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "draft_history")
public class DraftHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_id", nullable = false)
    private Draft draft;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String previousContent;

    @Column(nullable = false)
    private LocalDateTime savedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Draft getDraft() { return draft; }
    public void setDraft(Draft draft) { this.draft = draft; }

    public String getPreviousContent() { return previousContent; }
    public void setPreviousContent(String previousContent) { this.previousContent = previousContent; }

    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
}
