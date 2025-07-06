package com.example.bid_api.service;

import com.example.bid_api.model.entity.Mail;
import com.example.bid_api.model.request.MessageRequest;

import java.util.List;

public interface MailService {
    Mail store(Mail request);

    void delete(Mail request);

    List<Mail> getList();

    void sendEmail(MessageRequest request);
}
