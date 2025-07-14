package com.vonage.CustomerSupportTicketingSystem.controller;

import com.vonage.CustomerSupportTicketingSystem.dto.CommentRequest;
import com.vonage.CustomerSupportTicketingSystem.model.Comment;
import com.vonage.CustomerSupportTicketingSystem.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest request, Authentication authentication){
        try{
            Comment comment=commentService.addComment(request,authentication);
            return ResponseEntity.ok(comment);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: "+ e.getMessage());
        }
    }
    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<?> getComment(@PathVariable String ticketId, Authentication authentication) {
        try {
            List<Comment> comments = commentService.getCommentsByTicketId(ticketId, authentication);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        }
    }

}


