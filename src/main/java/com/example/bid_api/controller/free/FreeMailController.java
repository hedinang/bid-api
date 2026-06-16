package com.example.bid_api.controller.free;

import com.example.bid_api.model.entity.Mail;
import com.example.bid_api.model.request.MessageRequest;
import com.example.bid_api.service.MailService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/free/mail")
@RequiredArgsConstructor
public class FreeMailController {
    private final MailService mailService;

    @PostMapping("/send")
    public BaseResponse<List<Mail>> sendMail(@RequestBody MessageRequest request) {
        mailService.sendEmail(request);
        return new BaseResponse<>(HttpStatus.OK.value(), "Send mail successfully", null);
    }
}
