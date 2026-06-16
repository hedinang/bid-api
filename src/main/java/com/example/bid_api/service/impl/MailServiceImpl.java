package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Mail;
import com.example.bid_api.model.request.MessageRequest;
import com.example.bid_api.repository.mongo.AutoItemRepository;
import com.example.bid_api.repository.mongo.MailRepository;
import com.example.bid_api.service.AutoItemService;
import com.example.bid_api.service.MailService;
import com.example.bid_api.util.StringUtil;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
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
    private final AutoItemRepository autoItemRepository;

    private final AutoItemService autoItemService;
    @Value("${email-host}")
    private String host;

    @Value("${email-port}")
    private String port;

    @Value("${email-address}")
    private String username;

    @Value("${email-password}")
    private String password;

    @Value("${target-email}")
    private String targetEmail;

    private final MailRepository mailRepository;
    private final MailSender mailSender;

    private long lastSeenUid = 0L;

    @PostConstruct
    public void init() {
        Store store = null;
        Folder inbox = null;

        try {
            store = connectStore();

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            UIDFolder uidFolder = (UIDFolder) inbox;

            lastSeenUid = uidFolder.getUIDNext() - 1;

            log.info("Email polling initialized. lastSeenUid={}", lastSeenUid);

        } catch (Exception e) {
            log.error("Cannot initialize email polling", e);
        } finally {
            close(inbox, store);
        }
    }

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

    //30 minutes
//    @Scheduled(fixedDelay = 1800000)
    @Scheduled(fixedDelay = 180000)
    public void pollEmail() {
        Store store = null;
        Folder inbox = null;

        try {
            store = connectStore();

            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            UIDFolder uidFolder = (UIDFolder) inbox;

            long oldLastSeenUid = lastSeenUid;

            long currentLastUid = uidFolder.getUIDNext() - 1;

            if (currentLastUid <= oldLastSeenUid) {
                return;
            }

            Message[] newMessages = uidFolder.getMessagesByUID(
                    oldLastSeenUid + 1,
                    currentLastUid
            );

            boolean detected = false;

            for (int i = newMessages.length - 1; i >= 0; i--) {
                Message message = newMessages[i];
                long uid = uidFolder.getUID(message);

                // phòng thủ nếu server trả dư message biên
                if (uid <= oldLastSeenUid) {
                    continue;
                }

                if (isFromTarget(message)) {
                    detected = true;
                    break;
                }
            }

            lastSeenUid = currentLastUid;

            if (detected) {
                autoItemService.scanAutoItems();
            }
        } catch (Exception e) {
            log.error("Error while polling email", e);
        } finally {
            close(inbox, store);
        }
    }

    private Store connectStore() throws Exception {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", host);
        props.put("mail.imaps.port", port);
        props.put("mail.imaps.ssl.enable", "true");

        Session session = Session.getInstance(props);

        Store store = session.getStore("imaps");
        store.connect(host, username, password);

        return store;
    }

    private boolean isFromTarget(Message message) throws Exception {
        Address[] froms = message.getFrom();

        if (froms == null || froms.length == 0) {
            return false;
        }

        for (Address from : froms) {
            if (from != null &&
                    from.toString().toLowerCase().contains(targetEmail.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private void close(Folder inbox, Store store) {
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
