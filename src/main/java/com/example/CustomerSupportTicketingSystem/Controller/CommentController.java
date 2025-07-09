package com.example.CustomerSupportTicketingSystem.Controller;

import com.example.CustomerSupportTicketingSystem.DTO.CommentRequest;
import com.example.CustomerSupportTicketingSystem.Entities.Comment;
import com.example.CustomerSupportTicketingSystem.Service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getComment(@PathVariable String ticketId){
        try{
            List<Comment> comments =commentService.getCommentsByTicketId(ticketId);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: "+ e.getMessage());
        }
    }
}


