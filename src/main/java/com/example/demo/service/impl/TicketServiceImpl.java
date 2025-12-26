package com.example.demo.service.impl;

import com.example.demo.model.Ticket;
import com.example.demo.model.User;
import com.example.demo.model.TicketCategory;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.TicketCategoryRepository;
import com.example.demo.service.TicketService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCategoryRepository categoryRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository, TicketCategoryRepository categoryRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Ticket createTicket(Long userId, Long categoryId, Ticket ticket) {
        User user = userRepository.findById(userId).orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("User not found"));
        TicketCategory category = categoryRepository.findById(categoryId).orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Category not found"));
        
        if (ticket.getDescription() == null || ticket.getDescription().length() < 10) { // Assuming 10 from 'short' failing test case
             // The test 'testCreateTicketShortDescription' fails when description is "short" (5 chars).
             // 'testTicketDescriptionSetter' asserts length >= 10 for "Long enough description".
             // So I'll require >= 10.
             throw new RuntimeException("Description is too short");
        }

        ticket.setUser(user);
        ticket.setCategory(category);
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Ticket not found"));
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
