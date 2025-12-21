package com.example.demo.service.impl;

import com.example.demo.model.Ticket;
import com.example.demo.model.User;
import com.example.demo.model.TicketCategory;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.TicketCategoryRepository;
import com.example.demo.service.TicketService;
import com.example.demo.exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository ticketCategoryRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository, TicketCategoryRepository ticketCategoryRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    @Override
    public Ticket createTicket(Long userId, Long categoryId, Ticket ticket) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        TicketCategory category = ticketCategoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));

        if (ticket.getSubject() == null || ticket.getSubject().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject must not be blank");
        }
        if (ticket.getDescription() == null || ticket.getDescription().length() < 10) {
            throw new IllegalArgumentException("description must be at least 10 chars");
        }
        
        ticket.setUser(user);
        ticket.setCategory(category);
        if (ticket.getStatus() == null) {
            ticket.setStatus("OPEN");
        }
        ticket.setCreatedAt(LocalDateTime.now());
        
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicket(Long ticketId) {
        return ticketRepository.findById(ticketId).orElseThrow(() -> new NotFoundException("ticket not found"));
    }

    @Override
    public List<Ticket> getTicketsByUser(Long userId) {
        return ticketRepository.findByUser_Id(userId);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
