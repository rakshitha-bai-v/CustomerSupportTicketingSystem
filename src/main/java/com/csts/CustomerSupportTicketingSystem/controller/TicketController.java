package com.csts.CustomerSupportTicketingSystem.controller;

import com.csts.CustomerSupportTicketingSystem.dto.TicketRequest;
import com.csts.CustomerSupportTicketingSystem.dto.TicketUpdateRequest;
import com.csts.CustomerSupportTicketingSystem.model.Ticket;
import com.csts.CustomerSupportTicketingSystem.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketRequest request, Authentication authentication){
        try{
            Ticket createdTicket=ticketService.createTicket(request,authentication);
            return ResponseEntity.ok(createdTicket);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/my-tickets")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getMyTickets(Authentication authentication){
        try{
            List<Ticket> tickets =ticketService.getMyTickets(authentication);
            return ResponseEntity.ok(tickets);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTickets(){
        try{
            return new ResponseEntity<>(ticketService.getAllTickets(),HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    @PutMapping("/update/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTicket(@PathVariable String ticketId,@Valid @RequestBody TicketUpdateRequest request){
        try{
            Ticket ticket = ticketService.updateTicket(ticketId,request);
            return ResponseEntity.ok(ticket);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<?> getTicketsById(@PathVariable String ticketId){
        try{
            return new ResponseEntity<>(ticketService.getTicketsById(ticketId),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTicket(@PathVariable String ticketId) {
        try {
            ticketService.deleteTicketById(ticketId);
            return ResponseEntity.ok("Ticket Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
