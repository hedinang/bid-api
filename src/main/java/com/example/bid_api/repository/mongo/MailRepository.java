package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Mail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MailRepository extends MongoRepository<Mail, String> {
    Mail findByMailId(String mailId);
}
