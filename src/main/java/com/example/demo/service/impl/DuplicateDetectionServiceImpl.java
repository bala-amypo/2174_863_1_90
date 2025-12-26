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

        for (DuplicateRule rule : rules) {
            double score = 0.0;

            // EXACT MATCH
            if ("EXACT_MATCH".equalsIgnoreCase(rule.getMatchType())) {
                String t1 = normalize(target.getSubject() + " " + target.getDescription());
                String t2 = normalize(candidate.getSubject() + " " + candidate.getDescription());

                if (t1.equals(t2)) {
                    score = 100.0;
                }
            }

            // KEYWORD MATCH
            else if ("KEYWORD".equalsIgnoreCase(rule.getMatchType())) {
                score = keywordMatchPercentage(
                        target.getSubject() + " " + target.getDescription(),
                        candidate.getSubject() + " " + candidate.getDescription()
                );
            }

            if (score >= rule.getThreshold()) {
                DuplicateDetectionLog log =
                        logRepository.save(new DuplicateDetectionLog(target, candidate, score));
                logs.add(log);
                break; // stop checking rules for this ticket
            }
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
