package com.example.bid_api.controller;

import com.example.bid_api.model.entity.Mail;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.MessageRequest;
import com.example.bid_api.service.MailService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/store")
    public BaseResponse<Mail> create(@RequestBody Mail request, @AuthenticationPrincipal User user) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", mailService.store(request));
    }

    @GetMapping("/delete")
    public BaseResponse<Mail> delete(@RequestBody Mail request, @AuthenticationPrincipal User user) {
        mailService.delete(request);
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", null);
    }

    @PostMapping("/list")
    public BaseResponse<List<Mail>> getItem(@AuthenticationPrincipal User user) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", mailService.getList());
    }

    @PostMapping("/send")
    public BaseResponse<List<Mail>> sendMail(@RequestBody MessageRequest request) {
        mailService.sendEmail(request);
        return new BaseResponse<>(HttpStatus.OK.value(), "Send mail successfully", null);
    }
}
