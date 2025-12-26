package com.example.demo.service.impl;

import com.example.demo.model.DuplicateDetectionLog;
import com.example.demo.model.DuplicateRule;
import com.example.demo.model.Ticket;
import com.example.demo.repository.DuplicateDetectionLogRepository;
import com.example.demo.repository.DuplicateRuleRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.service.DuplicateDetectionService;
import com.example.demo.util.TextSimilarityUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DuplicateDetectionServiceImpl implements DuplicateDetectionService {

    private final TicketRepository ticketRepository;
    private final DuplicateRuleRepository ruleRepository;
    private final DuplicateDetectionLogRepository logRepository;

    public DuplicateDetectionServiceImpl(TicketRepository ticketRepository, DuplicateRuleRepository ruleRepository, DuplicateDetectionLogRepository logRepository) {
        this.ticketRepository = ticketRepository;
        this.ruleRepository = ruleRepository;
        this.logRepository = logRepository;
    }

    @Override
    public List<Ticket> detectDuplicates(Long ticketId) {
        Ticket target = ticketRepository.findById(ticketId).orElse(null);
        if (target == null) return List.of();

        List<Ticket> openTickets = ticketRepository.findByStatus("OPEN");
        List<DuplicateRule> rules = ruleRepository.findAll();
        List<Ticket> duplicates = new ArrayList<>();

        if (rules.isEmpty()) return duplicates;

        for (Ticket candidate : openTickets) {
            if (target.getId() != null && candidate.getId() != null && target.getId().equals(candidate.getId())) continue;

            boolean isMatch = false;
            double maxScore = 0.0;

            for (DuplicateRule rule : rules) {
                double score = 0.0;
                String type = rule.getMatchType();

                if ("EXACT_MATCH".equalsIgnoreCase(type)) {
                    if (target.getSubject() != null && target.getSubject().equalsIgnoreCase(candidate.getSubject())) {
                        score = 1.0;
                    }
                } else if ("KEYWORD".equalsIgnoreCase(type)) {
                    score = TextSimilarityUtil.similarity(target.getSubject(), candidate.getSubject());
                } else if ("SIMILARITY".equalsIgnoreCase(type)) {
                    score = TextSimilarityUtil.similarity(target.getDescription(), candidate.getDescription());
                }

                if (score >= rule.getThreshold()) {
                    isMatch = true;
                    maxScore = Math.max(maxScore, score);
                }
            }

            if (isMatch) {
                duplicates.add(candidate);
                logRepository.save(new DuplicateDetectionLog(target, candidate, maxScore));
            }
        }

        return duplicates;
    }

    @Override
    public List<DuplicateDetectionLog> getLogsForTicket(Long ticketId) {
        return logRepository.findByTicket_Id(ticketId);
    }
}
