package com.example.demo.service.impl;

import com.example.demo.model.DuplicateDetectionLog;
import com.example.demo.model.DuplicateRule;
import com.example.demo.model.Ticket;
import com.example.demo.repository.DuplicateDetectionLogRepository;
import com.example.demo.repository.DuplicateRuleRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.service.DuplicateDetectionService;
import com.example.demo.exception.NotFoundException;
import com.example.demo.util.TextSimilarityUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class DuplicateDetectionServiceImpl implements DuplicateDetectionService {

    private final TicketRepository ticketRepository;
    private final DuplicateRuleRepository duplicateRuleRepository;
    private final DuplicateDetectionLogRepository duplicateDetectionLogRepository;

    public DuplicateDetectionServiceImpl(TicketRepository ticketRepository, DuplicateRuleRepository duplicateRuleRepository, DuplicateDetectionLogRepository duplicateDetectionLogRepository) {
        this.ticketRepository = ticketRepository;
        this.duplicateRuleRepository = duplicateRuleRepository;
        this.duplicateDetectionLogRepository = duplicateDetectionLogRepository;
    }

    @Override
    public List<DuplicateDetectionLog> detectDuplicates(Long ticketId) {
        Ticket baseTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("Ticket not found"));
        List<DuplicateRule> rules = duplicateRuleRepository.findAll();
        List<Ticket> openTickets = ticketRepository.findByStatus("OPEN");
        List<DuplicateDetectionLog> logs = new ArrayList<>();

        for (Ticket candidate : openTickets) {
            if (candidate.getId().equals(baseTicket.getId())) {
                continue;
            }

            for (DuplicateRule rule : rules) {
                double score = 0.0;
                String matchType = rule.getMatchType();

                if ("EXACT_MATCH".equalsIgnoreCase(matchType)) {
                    if (baseTicket.getSubject() != null && baseTicket.getSubject().equalsIgnoreCase(candidate.getSubject())) {
                        score = 1.0;
                    }
                } else if ("KEYWORD".equalsIgnoreCase(matchType)) {
                     score = calculateKeywordOverlap(baseTicket.getDescription(), candidate.getDescription());
                } else if ("SIMILARITY".equalsIgnoreCase(matchType)) {
                     score = TextSimilarityUtil.similarity(baseTicket.getDescription(), candidate.getDescription());
                }

                if (score >= rule.getThreshold()) {
                    DuplicateDetectionLog log = new DuplicateDetectionLog();
                    log.setTicket(baseTicket);
                    log.setMatchedTicket(candidate);
                    log.setMatchScore(score);
                    log.setDetectedAt(LocalDateTime.now());
                    
                    logs.add(duplicateDetectionLogRepository.save(log));
                }
            }
        }
        return logs;
    }

    @Override
    public List<DuplicateDetectionLog> getLogsForTicket(Long ticketId) {
        return duplicateDetectionLogRepository.findByTicket_Id(ticketId);
    }

    @Override
    public DuplicateDetectionLog getLog(Long id) {
        return duplicateDetectionLogRepository.findById(id).orElseThrow(() -> new NotFoundException("Log not found"));
    }

    private double calculateKeywordOverlap(String text1, String text2) {
        if (text1 == null || text2 == null) return 0.0;
        // Simple Jaccard Index on words
        String[] w1 = text1.toLowerCase().split("\\s+");
        String[] w2 = text2.toLowerCase().split("\\s+");
        
        Set<String> set1 = new HashSet<>();
        for (String s : w1) if (!s.isEmpty()) set1.add(s);
        
        Set<String> set2 = new HashSet<>();
        for (String s : w2) if (!s.isEmpty()) set2.add(s);
        
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }
}
