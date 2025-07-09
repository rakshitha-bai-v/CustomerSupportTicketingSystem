package com.example.CustomerSupportTicketingSystem.Repository;

import com.example.CustomerSupportTicketingSystem.Entities.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TicketRepository extends MongoRepository<Ticket,String> {
    List<Ticket> findByCreatedBy(String user);
}
