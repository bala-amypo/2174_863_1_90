package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository categoryRepository;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            TicketCategoryRepository categoryRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Ticket createTicket(Long userId, Long categoryId, Ticket ticket) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        TicketCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (ticket.getDescription() == null || ticket.getDescription().length() < 10) {
            throw new RuntimeException("Description is too short");
        }

        ticket.setUser(user);
        ticket.setCategory(category);
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getTicketsByUser(Long userId) {
        return ticketRepository.findByUser_Id(userId);
    }
}
