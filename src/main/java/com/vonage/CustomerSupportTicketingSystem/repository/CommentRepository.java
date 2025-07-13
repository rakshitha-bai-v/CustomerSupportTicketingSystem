package com.vonage.CustomerSupportTicketingSystem.repository;

import com.vonage.CustomerSupportTicketingSystem.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends MongoRepository<Comment,String> {
    List<Comment> findByTicketId(String ticket);
}
