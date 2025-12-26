package com.example.demo.dto;

public class TicketDTO {
    private Long id;
    private String subject;
    private String description;
    private Long categoryId;
    private Long userId;
    private String status;

    public Long getId() { 
        return id;
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getSubject() { 
        return subject; 
    }
    public void setSubject(String subject) { 
        this.subject = subject; 
    }
    public String getDescription() { 
        return description; 
    }
    public void setDescription(String description) { 
        this.description = description; 
    }
    public Long getCategoryId() { 
        return categoryId; 
    }
    public void setCategoryId(Long categoryId) { 
        this.categoryId = categoryId; 
    }
    public Long getUserId() { 
        return userId; 
    }
    public void setUserId(Long userId) { 
        this.userId = userId; 
    }
    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }

}
