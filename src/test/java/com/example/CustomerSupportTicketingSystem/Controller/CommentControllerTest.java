package com.example.CustomerSupportTicketingSystem.Controller;

import com.example.CustomerSupportTicketingSystem.DTO.CommentRequest;
import com.example.CustomerSupportTicketingSystem.Entities.Comment;
import com.example.CustomerSupportTicketingSystem.Service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_success() {
        CommentRequest request = new CommentRequest();
        request.setTicketId("ticket123");
        request.setMessage("Sample comment");

        Comment comment = new Comment();
        comment.setMessage("Sample comment");

        when(commentService.addComment(request, authentication)).thenReturn(comment);

        ResponseEntity<?> response = commentController.addComment(request, authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Comment);
        assertEquals("Sample comment", ((Comment) response.getBody()).getMessage());
    }

    @Test
    void addComment_error() {
        CommentRequest request = new CommentRequest();
        request.setTicketId("ticket123");
        request.setMessage("Sample comment");

        when(commentService.addComment(request, authentication)).thenThrow(new RuntimeException("Error adding comment"));

        ResponseEntity<?> response = commentController.addComment(request, authentication);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error adding comment"));
    }

    @Test
    void getComment_success() {
        String ticketId = "ticket123";

        Comment comment1 = new Comment();
        comment1.setMessage("First comment");
        Comment comment2 = new Comment();
        comment2.setMessage("Second comment");

        when(commentService.getCommentsByTicketId(ticketId)).thenReturn(List.of(comment1, comment2));

        ResponseEntity<?> response = commentController.getComment(ticketId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List<?>);
        List<?> comments = (List<?>) response.getBody();
        assertEquals(2, comments.size());
    }

    @Test
    void getComment_error() {
        String ticketId = "ticket123";

        when(commentService.getCommentsByTicketId(ticketId)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = commentController.getComment(ticketId);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("DB error"));
    }
}

