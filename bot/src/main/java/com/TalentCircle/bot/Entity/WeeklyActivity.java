package com.TalentCircle.bot.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_activity")
public class WeeklyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 1000)
    private String url;

    @Column(length = 100)
    private String author;

    @Column(nullable = false)
    private int upvotes;

    @Column(nullable = false)
    private int comments;

    // score = upvotes + (comments * 2)
    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActivityType type;

    @Column(nullable = false)
    private LocalDate weekOf;

    @Column(nullable = false)
    private LocalDateTime collectedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getUpvotes() { return upvotes; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public LocalDate getWeekOf() { return weekOf; }
    public void setWeekOf(LocalDate weekOf) { this.weekOf = weekOf; }

    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }
}
