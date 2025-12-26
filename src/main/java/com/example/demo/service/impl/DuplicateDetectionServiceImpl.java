package com.example.demo.service.impl;

import com.example.demo.model.DuplicateDetectionLog;
import com.example.demo.model.DuplicateRule;
import com.example.demo.model.Ticket;
import com.example.demo.repository.DuplicateDetectionLogRepository;
import com.example.demo.repository.DuplicateRuleRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.service.DuplicateDetectionService;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.util.TextSimilarityUtil;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

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
        Ticket baseTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("ticket not found"));
        List<DuplicateRule> rules = duplicateRuleRepository.findAll();
        if (rules.isEmpty()) {
            return new ArrayList<>();
        }

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
                } else if ("KEYWORD".equalsIgnoreCase(matchType) || "SIMILARITY".equalsIgnoreCase(matchType)) {
                     score = TextSimilarityUtil.similarity(baseTicket.getDescription(), candidate.getDescription());
                }

                if (score >= rule.getThreshold()) {
                    DuplicateDetectionLog log = new DuplicateDetectionLog();
                    log.setTicket(baseTicket);
                    log.setMatchedTicket(candidate);
                    log.setMatchScore(score);
                    // detectedAt is auto-initialized by Entity
                    
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
    
    
    public DuplicateDetectionLog getLog(Long id) {
        return duplicateDetectionLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Log not found"));
    }
}
