package com.example.demo.repository;

import com.example.demo.model.DuplicateRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DuplicateRuleRepository extends JpaRepository<DuplicateRule, Long> {
    Optional<DuplicateRule> findByRuleName(String ruleName);
}
