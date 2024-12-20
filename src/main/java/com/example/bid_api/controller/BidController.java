package com.example.bid_api.controller;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.service.BidService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;

    @PostMapping("list")
    public BaseResponse<List<Bid>> list() {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", bidService.getList());
    }

    @PostMapping("sync")
    public BaseResponse sync() {
        bidService.sync();
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully");
    }
}
