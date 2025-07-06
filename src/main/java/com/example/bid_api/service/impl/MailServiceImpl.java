package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Mail;
import com.example.bid_api.model.request.MessageRequest;
import com.example.bid_api.repository.mongo.MailRepository;
import com.example.bid_api.service.MailService;
import com.example.bid_api.util.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final MailRepository mailRepository;
    private final MailSender mailSender;

    public Mail store(Mail request) {
        if (request.getMailId() == null) {
            Mail mail = new Mail();
            mail.setAddress(request.getAddress());
            mail.setMailId(StringUtil.generateId());
            return mailRepository.save(mail);
        } else {
            Mail mail = mailRepository.findByMailId(request.getMailId());
            mail.setAddress(request.getAddress());
            return mailRepository.save(mail);
        }
    }

    public void delete(Mail request) {
        mailRepository.deleteByMailId(request.getMailId());
    }

    public List<Mail> getList() {
        return mailRepository.findAll();
    }

    public void sendEmail(MessageRequest request) {
        List<Mail> mailList = mailRepository.findAll();

        if (mailList.isEmpty()) return;

        mailList.forEach((mail -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mail.getAddress());
            message.setSubject(request.getTitle());
            message.setText(request.getContent());
            mailSender.send(message);
        }));
    }
}
