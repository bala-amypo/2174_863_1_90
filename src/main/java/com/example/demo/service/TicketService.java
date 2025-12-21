package com.example.demo.service;

import com.example.demo.model.Ticket;
import java.util.List;

public interface TicketService {
    Ticket createTicket(Long userId, Long categoryId, Ticket ticket);
    Ticket getTicket(Long ticketId);
    List<Ticket> getTicketsByUser(Long userId);
    List<Ticket> getAllTickets();
}