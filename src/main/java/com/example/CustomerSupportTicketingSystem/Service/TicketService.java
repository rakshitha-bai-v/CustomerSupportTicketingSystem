package com.example.CustomerSupportTicketingSystem.Service;

import com.example.CustomerSupportTicketingSystem.DTO.TicketRequest;
import com.example.CustomerSupportTicketingSystem.DTO.TicketUpdateRequest;
import com.example.CustomerSupportTicketingSystem.Entities.Ticket;
import com.example.CustomerSupportTicketingSystem.Entities.User;
import com.example.CustomerSupportTicketingSystem.Repository.TicketRepository;
import com.example.CustomerSupportTicketingSystem.Repository.UserRepository;
import com.example.CustomerSupportTicketingSystem.Security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    public Ticket createTicket(TicketRequest request, Authentication authentication){
        try{
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Optional<User> user = userRepository.findById(userPrincipal.getId());
            if(user.isEmpty()){
                throw  new RuntimeException("User Not Found");
            }
            User user1 = user.get();
            Ticket ticket = new Ticket();
            ticket.setTitle(request.getTitle());
            ticket.setDescription(request.getDescription());
            ticket.setCategory(request.getCategory());
            ticket.setPriority(request.getPriority());
            ticket.setStatus(Ticket.Status.OPEN);
            ticket.setCreatedAt(LocalDateTime.now());
            ticket.setCreatedBy(user1);
            return ticketRepository.save(ticket);
        }catch (Exception e){
            throw new RuntimeException( "Error creating Ticket: " + e.getMessage());
        }
    }

    public List<Ticket> getMyTickets(Authentication authentication){
        try{
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return ticketRepository.findByCreatedBy(userPrincipal.getId());
        }catch (Exception e){
            throw new RuntimeException("Error Fetching your tickets: " + e.getMessage());

        }
    }
    public List<Ticket> getAllTickets(){
        try {
            return ticketRepository.findAll();
        }catch (Exception e){
            throw  new RuntimeException("Error Fetching all Tickets: " + e.getMessage());
        }
    }
    public Ticket updateTicket(String ticketId, TicketUpdateRequest request){
        try {
            Optional<Ticket> ticket = ticketRepository.findById(ticketId);
                if(ticket.isEmpty()){
                    throw new RuntimeException("Ticket not found");
                }
                Optional<User> assignedToUser = userRepository.findById(request.getAssignedToUserId());
                if(assignedToUser.isEmpty()){
                    throw new RuntimeException("Assigned User not found");
                }
                Ticket.Status statusEnum = Ticket.Status.valueOf(request.getStatus());
                Ticket ticket1 = ticket.get();
                ticket1.setStatus(statusEnum);
                ticket1.setAssignedTo(assignedToUser.get());
                ticket1.setUpdatedAt(LocalDateTime.now());
                return ticketRepository.save(ticket1);
        }catch (Exception e){
            throw  new RuntimeException("Error Updating Tickets: " + e.getMessage());
        }
    }
    public void deleteTicketById(String ticketId) {
        try {
            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket Not found"));
            ticketRepository.delete(ticket);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete ticket: " + e.getMessage());
        }
    }
    public Ticket getTicketsById(String id){
        try {
            return ticketRepository.findById(id).orElseThrow(()-> new RuntimeException("Ticket Not found with Id: " + id));
        }catch (Exception e){
            throw  new RuntimeException("Error Fetching Tickets: " + e.getMessage());
        }
    }
}
