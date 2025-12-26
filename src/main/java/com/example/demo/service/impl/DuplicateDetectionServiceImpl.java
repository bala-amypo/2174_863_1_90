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

            // ✅ FIX 1: skip same object, NOT id comparison
            if (candidate == target) continue;

            double maxScore = 0.0;
            boolean matched = false;

            for (DuplicateRule rule : rules) {
                double score = 0.0;

                // ✅ EXACT MATCH — subject only, case-insensitive
                if ("EXACT_MATCH".equalsIgnoreCase(rule.getMatchType())) {
                    if (target.getSubject() != null &&
                        candidate.getSubject() != null &&
                        target.getSubject().equalsIgnoreCase(candidate.getSubject())) {
                        score = 1.0;
                    }
                }

                // ✅ KEYWORD — subject similarity
                else if ("KEYWORD".equalsIgnoreCase(rule.getMatchType())) {
                    score = TextSimilarityUtil.similarity(
                            target.getSubject(),
                            candidate.getSubject()
                    );
                }

                // ✅ SIMILARITY — description similarity
                else if ("SIMILARITY".equalsIgnoreCase(rule.getMatchType())) {
                    score = TextSimilarityUtil.similarity(
                            target.getDescription(),
                            candidate.getDescription()
                    );
                }

                // ✅ threshold logic
                if (score >= rule.getThreshold()) {
                    matched = true;
                    maxScore = Math.max(maxScore, score);
                }
            }

            if (matched) {
                logs.add(
                    logRepository.save(
                        new DuplicateDetectionLog(target, candidate, maxScore)
                    )
                );
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
