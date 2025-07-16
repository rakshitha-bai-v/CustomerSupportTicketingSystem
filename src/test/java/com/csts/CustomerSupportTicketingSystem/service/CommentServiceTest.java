package com.csts.CustomerSupportTicketingSystem.service;

import com.csts.CustomerSupportTicketingSystem.dto.CommentRequest;
import com.csts.CustomerSupportTicketingSystem.model.Comment;
import com.csts.CustomerSupportTicketingSystem.model.Ticket;
import com.csts.CustomerSupportTicketingSystem.model.User;
import com.csts.CustomerSupportTicketingSystem.repository.CommentRepository;
import com.csts.CustomerSupportTicketingSystem.repository.TicketRepository;
import com.csts.CustomerSupportTicketingSystem.repository.UserRepository;
import com.csts.CustomerSupportTicketingSystem.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddComment_Success() {
        CommentRequest request = new CommentRequest();
        request.setTicketId("ticket123");
        request.setMessage("Hello comment");

        Ticket ticket = new Ticket();
        ticket.setId("ticket123");

        User user = new User();
        user.setId("user123");

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);

        when(auth.getPrincipal()).thenReturn(principal);
        when(principal.getId()).thenReturn("user123");

        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        Comment savedComment = new Comment();
        savedComment.setMessage("Hello comment");
        savedComment.setTicketId(ticket);
        savedComment.setCommentedBy(user);
        savedComment.setCreatedAt(LocalDateTime.now());

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Comment result = commentService.addComment(request, auth);

        assertNotNull(result);
        assertEquals("Hello comment", result.getMessage());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testGetCommentsByTicketId_AdminAccess() {
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");
        User creator = new User();
        creator.setId("user456");
        ticket.setCreatedBy(creator);

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        doReturn(authorities).when(principal).getAuthorities();
        when(auth.getPrincipal()).thenReturn(principal);
        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));
        when(commentRepository.findByTicketId("ticket123")).thenReturn(List.of(new Comment()));

        List<Comment> comments = commentService.getCommentsByTicketId("ticket123", auth);

        assertNotNull(comments);
        assertFalse(comments.isEmpty());
        verify(commentRepository, times(1)).findByTicketId("ticket123");
    }

    @Test
    public void testGetCommentsByTicketId_OwnerAccess() {
        User creator = new User();
        creator.setId("user123");
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");
        ticket.setCreatedBy(creator);

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        doReturn(authorities).when(principal).getAuthorities();
        when(principal.getId()).thenReturn("user123");
        when(auth.getPrincipal()).thenReturn(principal);
        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));
        when(commentRepository.findByTicketId("ticket123")).thenReturn(List.of(new Comment()));

        List<Comment> comments = commentService.getCommentsByTicketId("ticket123", auth);

        assertNotNull(comments);
        assertFalse(comments.isEmpty());
        verify(commentRepository, times(1)).findByTicketId("ticket123");
    }

    @Test
    public void testGetCommentsByTicketId_AccessDenied() {
        User creator = new User();
        creator.setId("user123");
        Ticket ticket = new Ticket();
        ticket.setId("ticket123");
        ticket.setCreatedBy(creator);

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = mock(UserPrincipal.class);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        doReturn(authorities).when(principal).getAuthorities();
        when(principal.getId()).thenReturn("user999");  // Different user id
        when(auth.getPrincipal()).thenReturn(principal);
        when(ticketRepository.findById("ticket123")).thenReturn(Optional.of(ticket));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.getCommentsByTicketId("ticket123", auth);
        });

        // Updated to check 'contains' to handle wrapped exception message
        assertTrue(exception.getMessage().contains("Access denied. You are not allowed to view these comments."));
        verify(commentRepository, never()).findByTicketId(any());
    }
}
