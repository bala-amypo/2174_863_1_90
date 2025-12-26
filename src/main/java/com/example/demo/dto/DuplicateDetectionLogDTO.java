package com.example.demo.dto;

import java.time.LocalDateTime;

public class DuplicateDetectionLogDTO {
    private Long id;
    private Long ticketId;
    private Long duplicateTicketId;
    private Double matchScore;
    private LocalDateTime detectedAt;

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public Long getTicketId() { 
        return ticketId; 
    }
    public void setTicketId(Long ticketId) { 
        this.ticketId = ticketId; 
    }
    public Long getDuplicateTicketId() { 
        return duplicateTicketId; 
    }
    public void setDuplicateTicketId(Long duplicateTicketId) { 
        this.duplicateTicketId = duplicateTicketId; 
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
