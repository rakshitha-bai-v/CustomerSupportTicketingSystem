package com.csts.CustomerSupportTicketingSystem.controller;

import com.csts.CustomerSupportTicketingSystem.dto.CommentRequest;
import com.csts.CustomerSupportTicketingSystem.model.Comment;
import com.csts.CustomerSupportTicketingSystem.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest request, Authentication authentication) {
        Comment comment = commentService.addComment(request, authentication);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<?> getComment(@PathVariable String ticketId, Authentication authentication) {
        List<Comment> comments = commentService.getCommentsByTicketId(ticketId, authentication);
        return ResponseEntity.ok(comments);
    }
}
