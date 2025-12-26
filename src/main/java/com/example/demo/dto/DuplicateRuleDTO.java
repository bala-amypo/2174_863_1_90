package com.example.demo.dto;

public class DuplicateRuleDTO {
    private Long id;
    private String ruleName;
    private String matchType;
    private Double threshold;

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
}
