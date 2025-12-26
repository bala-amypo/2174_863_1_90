package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DuplicateDetectionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "duplicate_ticket_id")
    private Ticket duplicate; 
    private Double matchScore;
    private LocalDateTime detectedAt;

    public DuplicateDetectionLog() {
        this.detectedAt = LocalDateTime.now();
    }

    public DuplicateDetectionLog(Ticket ticket, Ticket duplicate, Double matchScore) {
        this();
        this.ticket = ticket;
        this.duplicate = duplicate;
        this.matchScore = matchScore;
    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public Ticket getTicket() { 
        return ticket; 
    }
    public void setTicket(Ticket ticket) { 
        this.ticket = ticket; 
    }
    public Ticket getDuplicate() { 
        return duplicate; 
    }

    public void setDuplicate(Ticket duplicate) { 
        this.duplicate = duplicate;
     }
    public Double getMatchScore() { 
        return matchScore; 
    }
    public void setMatchScore(Double matchScore) { 
        this.matchScore = matchScore; 
    }
    public LocalDateTime getDetectedAt() { 
        return detectedAt; 
    }
    public void setDetectedAt(LocalDateTime detectedAt) { 
        this.detectedAt = detectedAt; 
    }
}
