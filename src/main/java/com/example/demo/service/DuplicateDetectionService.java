package com.example.demo.service;

import com.example.demo.model.DuplicateDetectionLog;
import com.example.demo.model.Ticket;
import java.util.List;

public interface DuplicateDetectionService {
    List<Ticket> detectDuplicates(Long ticketId);
    List<DuplicateDetectionLog> getLogsForTicket(Long ticketId);
}
