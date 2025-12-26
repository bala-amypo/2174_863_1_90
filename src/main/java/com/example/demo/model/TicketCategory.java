package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TicketCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;
    private String description;
    private LocalDateTime createdAt;

    public TicketCategory() {
        this.createdAt = LocalDateTime.now();
    }

    public TicketCategory(String categoryName, String description) {
        this();
        this.categoryName = categoryName;
        this.description = description;
    }

    
    public Long getId() { 
                return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getCategoryName() { 
        return categoryName; 
    }
    public void setCategoryName(String categoryName) { 
        this.categoryName = categoryName; 
    }
    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}