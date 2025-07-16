package com.csts.CustomerSupportTicketingSystem.service;

import com.csts.CustomerSupportTicketingSystem.dto.CommentRequest;
import com.csts.CustomerSupportTicketingSystem.exception.AccessDeniedException;
import com.csts.CustomerSupportTicketingSystem.model.Comment;
import com.csts.CustomerSupportTicketingSystem.model.Ticket;
import com.csts.CustomerSupportTicketingSystem.model.User;
import com.csts.CustomerSupportTicketingSystem.repository.CommentRepository;
import com.csts.CustomerSupportTicketingSystem.repository.TicketRepository;
import com.csts.CustomerSupportTicketingSystem.repository.UserRepository;
import com.csts.CustomerSupportTicketingSystem.security.UserPrincipal;
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

    public Comment addComment(CommentRequest request, Authentication authentication) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(request.getTicketId());
        if (ticketOpt.isEmpty()) {
            throw new RuntimeException("Ticket Not Found");
        }

        Ticket ticket = ticketOpt.get();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        boolean isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = ticket.getCreatedBy().getId().equals(userPrincipal.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to add comment on this ticket.");
        }

        Optional<User> user = userRepository.findById(userPrincipal.getId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not Found");
        }

        Comment comment = new Comment();
        comment.setTicketId(ticket);
        comment.setCommentedBy(user.get());
        comment.setMessage(request.getMessage());
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTicketId(String ticketId, Authentication authentication) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new RuntimeException("Ticket not found");
        }

        Ticket ticket = ticketOpt.get();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        boolean isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = ticket.getCreatedBy().getId().equals(userPrincipal.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied. You are not allowed to view these comments.");
        }
        return commentRepository.findByTicketId(ticketId);
    }
}
