package com.example.bid_api.controller;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.service.BidService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;

    @GetMapping("list")
    public BaseResponse<List<Bid>> list() {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", bidService.getList());
    }

    @GetMapping("/{bidId}")
    public BaseResponse<Bid> get(@PathVariable String bidId) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", bidService.getBid(bidId));
    }

    @PostMapping("store/bid")
    public BaseResponse storeBid() {
        bidService.storeBid();
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully");
    }

    @PostMapping("sync/{bidId}")
    public BaseResponse sync(@PathVariable String bidId) {
        bidService.syncBid(bidId);
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully");
    }

    @PostMapping("stop/{threadName}")
    public BaseResponse stopThread(@PathVariable String threadName) {
        bidService.stopThread(threadName);
        return new BaseResponse<>(HttpStatus.OK.value(), "stop thread successfully");
    }
}
