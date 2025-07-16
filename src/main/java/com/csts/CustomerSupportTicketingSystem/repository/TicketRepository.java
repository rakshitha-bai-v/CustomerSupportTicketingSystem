package com.csts.CustomerSupportTicketingSystem.repository;

import com.csts.CustomerSupportTicketingSystem.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TicketRepository extends MongoRepository<Ticket,String> {
    List<Ticket> findByCreatedBy(String user);
}
