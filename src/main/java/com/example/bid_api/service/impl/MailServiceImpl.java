package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Mail;
import com.example.bid_api.model.request.MessageRequest;
import com.example.bid_api.repository.mongo.MailRepository;
import com.example.bid_api.service.MailService;
import com.example.bid_api.util.StringUtil;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    @Value("${email-host}")
    private String host;

    @Value("${email-port}")
    private String port;

    @Value("${email-address}")
    private String username;

    @Value("${email-password}")
    private String password;

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

//    @Scheduled(fixedDelay = 4000)
    public void pollEmail(){
        Store store = null;
        Folder inbox = null;

        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", host);
            props.put("mail.imaps.port", port);
            props.put("mail.imaps.ssl.enable", "true");

            Session session = Session.getInstance(props);
            store = session.getStore("imaps");
            store.connect(host, username, password);

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            // Chỉ lấy mail chưa đọc
            Message[] messages = inbox.search(
                    new jakarta.mail.search.FlagTerm(
                            new Flags(Flags.Flag.SEEN),
                            false
                    )
            );

            for (Message message : messages) {
                String from = message.getFrom()[0].toString();
                String subject = message.getSubject();
                String body = getTextFromMessage(message);

                System.out.println("New email:");
                System.out.println("From: " + from);
                System.out.println("Subject: " + subject);
                System.out.println("Body: " + body);

                // TODO: xử lý logic của bạn
                handleNewEmail(from, subject, body);

                // Mark mail là đã đọc để lần sau không xử lý lại
                message.setFlag(Flags.Flag.SEEN, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inbox != null && inbox.isOpen()) {
                    inbox.close(false);
                }
                if (store != null && store.isConnected()) {
                    store.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void handleNewEmail(String from, String subject, String body) {
        // Ví dụ:
        // 1. lưu DB
        // 2. call API khác
        // 3. parse nội dung email
        // 4. trigger workflow

        System.out.println("Processing email from: " + from);
    }

    private String getTextFromMessage(Message message) throws Exception {
        Object content = message.getContent();

        if (content instanceof String) {
            return (String) content;
        }

        if (content instanceof MimeMultipart) {
            return getTextFromMimeMultipart((MimeMultipart) content);
        }

        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(html);
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }

        return result.toString();
    }
}
