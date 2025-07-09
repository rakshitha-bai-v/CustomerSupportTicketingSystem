package com.example.CustomerSupportTicketingSystem.Service;

import com.example.CustomerSupportTicketingSystem.DTO.CommentRequest;
import com.example.CustomerSupportTicketingSystem.Entities.Comment;
import com.example.CustomerSupportTicketingSystem.Entities.Ticket;
import com.example.CustomerSupportTicketingSystem.Entities.User;
import com.example.CustomerSupportTicketingSystem.Repository.CommentRepository;
import com.example.CustomerSupportTicketingSystem.Repository.TicketRepository;
import com.example.CustomerSupportTicketingSystem.Repository.UserRepository;
import com.example.CustomerSupportTicketingSystem.Security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_success() {
        // Arrange
        String ticketId = "ticket123";
        CommentRequest request = new CommentRequest();
        request.setTicketId(ticketId);
        request.setMessage("Test comment");

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        User user = new User();
        user.setId("abc");
        user.setName("Test User");

        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getId()).thenReturn("abc");

        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById("abc")).thenReturn(Optional.of(user));

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        when(commentRepository.save(commentCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Comment savedComment = commentService.addComment(request, authentication);

        // Assert
        assertNotNull(savedComment);
        assertEquals("Test comment", savedComment.getMessage());
        assertEquals(ticket, savedComment.getTicketId());
        assertEquals(user, savedComment.getCommentedBy());
        assertNotNull(savedComment.getCreatedAt());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_ticketNotFound_throws() {
        CommentRequest request = new CommentRequest();
        request.setTicketId("not_exist");
        when(ticketRepository.findById("not_exist")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.addComment(request, authentication);
        });

        assertTrue(exception.getMessage().contains("Ticket Not Found"));
    }

    @Test
    void addComment_userNotFound_throws() {
        String ticketId = "ticket123";
        CommentRequest request = new CommentRequest();
        request.setTicketId(ticketId);

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getId()).thenReturn("abc");
        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(userRepository.findById("abc")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.addComment(request, authentication);
        });

        assertTrue(exception.getMessage().contains("User not Found"));
    }

    @Test
    void getCommentsByTicketId_success() {
        String ticketId = "ticket123";

        Comment comment1 = new Comment();
        comment1.setMessage("First comment");
        Comment comment2 = new Comment();
        comment2.setMessage("Second comment");

        when(commentRepository.findByTicketId(ticketId)).thenReturn(List.of(comment1, comment2));

        List<Comment> comments = commentService.getCommentsByTicketId(ticketId);

        assertEquals(2, comments.size());
        assertEquals("First comment", comments.get(0).getMessage());
        assertEquals("Second comment", comments.get(1).getMessage());
    }
}

