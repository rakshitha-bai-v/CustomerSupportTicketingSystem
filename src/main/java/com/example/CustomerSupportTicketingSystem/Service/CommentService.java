package com.example.CustomerSupportTicketingSystem.Service;

import com.example.CustomerSupportTicketingSystem.DTO.CommentRequest;
import com.example.CustomerSupportTicketingSystem.Entities.Comment;
import com.example.CustomerSupportTicketingSystem.Entities.Ticket;
import com.example.CustomerSupportTicketingSystem.Entities.User;
import com.example.CustomerSupportTicketingSystem.Repository.CommentRepository;
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

    public List<Comment> getCommentsByTicketId(String ticketId){
        try {
            return commentRepository.findByTicketId(ticketId);
        }catch (Exception e){
            throw new RuntimeException("Error fetching comments" + e.getMessage());
        }
    }
}
