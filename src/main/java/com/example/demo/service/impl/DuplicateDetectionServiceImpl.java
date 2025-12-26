package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.*;
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

    public DuplicateDetectionServiceImpl(
            TicketRepository ticketRepository,
            DuplicateRuleRepository ruleRepository,
            DuplicateDetectionLogRepository logRepository) {
        this.ticketRepository = ticketRepository;
        this.ruleRepository = ruleRepository;
        this.logRepository = logRepository;
    }

    @Override
    public List<DuplicateDetectionLog> detectDuplicates(Long ticketId) {
        Ticket target = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        List<DuplicateRule> rules = ruleRepository.findAll();
        List<Ticket> openTickets = ticketRepository.findByStatus("OPEN");

        List<DuplicateDetectionLog> logs = new ArrayList<>();

        for (Ticket candidate : openTickets) {
            if (candidate.getId().equals(target.getId())) continue;

            double maxScore = 0.0;
            boolean matched = false;

            for (DuplicateRule rule : rules) {
                double score = 0.0;

                if ("EXACT_MATCH".equalsIgnoreCase(rule.getMatchType())) {
                    if (target.getSubject() != null &&
                        target.getSubject().equalsIgnoreCase(candidate.getSubject())) {
                        score = 1.0;
                    }
                } else {
                    score = TextSimilarityUtil.similarity(
                            target.getDescription(),
                            candidate.getDescription()
                    );
                }

                if (score >= rule.getThreshold()) {
                    matched = true;
                    maxScore = Math.max(maxScore, score);
                }
            }

            if (matched) {
                DuplicateDetectionLog log =
                        logRepository.save(new DuplicateDetectionLog(target, candidate, maxScore));
                logs.add(log);
            }
        }
        return logs;
    }

    @Override
    public List<DuplicateDetectionLog> getLogsForTicket(Long ticketId) {
        return logRepository.findByTicket_Id(ticketId);
    }

    @Override
    public DuplicateDetectionLog getLog(Long id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log not found"));
    }
}
