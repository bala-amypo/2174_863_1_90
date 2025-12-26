package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "duplicate_rules")
public class DuplicateRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ruleName;

    private String matchType;
    private Double threshold;
    private LocalDateTime createdAt;

    @OneToMany
    @JoinColumn(name = "rule_id")
    private List<DuplicateDetectionLog> logs;

    public DuplicateRule() {
    }

    public DuplicateRule(String ruleName, String matchType, Double threshold) {
        this.ruleName = ruleName;
        this.matchType = matchType;
        this.threshold = threshold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<DuplicateDetectionLog> getLogs() {
        return logs;
    }

    public void setLogs(List<DuplicateDetectionLog> logs) {
        this.logs = logs;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}