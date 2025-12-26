package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.util.TextSimilarityUtil;
import com.example.demo.service.DuplicateDetectionService;
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

            String matchType = rule.getMatchType();

            if ("EXACT_MATCH".equalsIgnoreCase(matchType)) {
                if (target.getSubject() != null &&
                    candidate.getSubject() != null &&
                    target.getSubject().equalsIgnoreCase(candidate.getSubject()) &&
                    target.getDescription() != null &&
                    candidate.getDescription() != null &&
                    target.getDescription().equalsIgnoreCase(candidate.getDescription())) {

                    score = 100.0;
                }
            }

            else if ("KEYWORD".equalsIgnoreCase(matchType)) {
                score = TextSimilarityUtil.similarity(
                        target.getSubject(),
                        candidate.getSubject()
                ) * 100;
            }

            else if ("SIMILARITY".equalsIgnoreCase(matchType)) {
                score = TextSimilarityUtil.similarity(
                        target.getDescription(),
                        candidate.getDescription()
                ) * 100;
            }

            if (score >= rule.getThreshold()) {
                DuplicateDetectionLog log =
                        logRepository.save(new DuplicateDetectionLog(target, candidate, score));
                logs.add(log);
                break;
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
    private String normalize(String text) {
    return text == null ? "" :
            text.toLowerCase().replaceAll("[^a-z0-9 ]", "").trim();
}

private double keywordMatchPercentage(String a, String b) {
    String[] wordsA = normalize(a).split("\\s+");
    String[] wordsB = normalize(b).split("\\s+");

    int matches = 0;
    for (String w : wordsA) {
        for (String x : wordsB) {
            if (w.equals(x)) {
                matches++;
                break;
            }
        }
    }
    return wordsA.length == 0 ? 0.0 : (matches * 100.0) / wordsA.length;
}

}
