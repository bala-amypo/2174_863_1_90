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
            if (candidate.getId().equals(target.getId())) {
                continue;
            }

            boolean matched = false;
            double maxScore = 0.0;

            for (DuplicateRule rule : rules) {
                double score = 0.0;

                // ✅ EXACT MATCH — SUBJECT (CASE INSENSITIVE)
                if ("EXACT_MATCH".equalsIgnoreCase(rule.getMatchType())) {
                    if (target.getSubject() != null &&
                        candidate.getSubject() != null &&
                        target.getSubject().equalsIgnoreCase(candidate.getSubject())) {
                        score = 1.0;
                    }
                }

                // ✅ KEYWORD — SUBJECT SIMILARITY
                else if ("KEYWORD".equalsIgnoreCase(rule.getMatchType())) {
                    score = TextSimilarityUtil.similarity(
                            target.getSubject(),
                            candidate.getSubject()
                    );
                }

                // ✅ SIMILARITY — DESCRIPTION SIMILARITY
                else if ("SIMILARITY".equalsIgnoreCase(rule.getMatchType())) {
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
