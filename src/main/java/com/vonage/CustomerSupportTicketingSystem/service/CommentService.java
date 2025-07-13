package com.vonage.CustomerSupportTicketingSystem.service;

import com.vonage.CustomerSupportTicketingSystem.dto.CommentRequest;
import com.vonage.CustomerSupportTicketingSystem.model.Comment;
import com.vonage.CustomerSupportTicketingSystem.model.Ticket;
import com.vonage.CustomerSupportTicketingSystem.model.User;
import com.vonage.CustomerSupportTicketingSystem.repository.CommentRepository;
import com.vonage.CustomerSupportTicketingSystem.repository.TicketRepository;
import com.vonage.CustomerSupportTicketingSystem.repository.UserRepository;
import com.vonage.CustomerSupportTicketingSystem.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;

    public Comment addComment(CommentRequest request, Authentication authentication){
        try {
            Optional<Ticket> ticketOpt = ticketRepository.findById(request.getTicketId());
            if(ticketOpt.isEmpty()){
                throw new RuntimeException("Ticket Not Found");
            }
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Optional<User> user = userRepository.findById(userPrincipal.getId());
            if(user.isEmpty()){
                throw new RuntimeException("User not Found");
            }
            Comment comment=new Comment();
            comment.setTicketId(ticketOpt.get());
            comment.setCommentedBy(user.get());
            comment.setMessage(request.getMessage());
            comment.setCreatedAt(LocalDateTime.now());
            return commentRepository.save(comment);
        }catch (Exception e){
            throw  new RuntimeException("Error adding comment: " +e.getMessage());
        }
    }

    public List<Comment> getCommentsByTicketId(String ticketId, Authentication authentication) {
        try {
            Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
            if (ticketOpt.isEmpty()) {
                throw new RuntimeException("Ticket not found");
            }

            Ticket ticket = ticketOpt.get();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            if (!userPrincipal.getAuthorities().stream().anyMatch(
                    a -> a.getAuthority().equals("ROLE_ADMIN")) &&
                    !ticket.getCreatedBy().getId().equals(userPrincipal.getId())) {
                throw new RuntimeException("Access denied. You are not allowed to view these comments.");
            }

            return commentRepository.findByTicketId(ticketId);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching comments: " + e.getMessage());
        }
    }
}
