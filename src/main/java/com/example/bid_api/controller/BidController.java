package com.example.bid_api.controller;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.request.BidRequest;
import com.example.bid_api.model.request.DeleteBidRequest;
import com.example.bid_api.model.request.ThreadStopRequest;
import com.example.bid_api.service.BidService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/public/bid")
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService;

    @GetMapping("list")
    public BaseResponse<List<Bid>> list() {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", bidService.getList());
    }

    @PostMapping("/{bidId}")
    public BaseResponse<Bid> get(@RequestBody BidRequest bidRequest) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", bidService.getBid(bidRequest));
    }

    @PostMapping("store/bid")
    public BaseResponse storeBid() {
        bidService.storeBid();
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully");
    }

    @PostMapping("sync")
    public BaseResponse sync(@RequestBody BidRequest bidRequest) {
        bidService.syncBid(bidRequest);
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully");
    }

    @PostMapping("stop")
    public BaseResponse stopThread(@RequestBody ThreadStopRequest request) {
        bidService.stopThread(request.getThreadName());
        return new BaseResponse<>(HttpStatus.OK.value(), "stop thread successfully");
    }

    @PostMapping("thread/list")
    public BaseResponse<Set<String>> listThread() {
        bidService.getList();
        return new BaseResponse<>(HttpStatus.OK.value(), "get thread list successfully", bidService.listThread());
    }

    @PostMapping("delete")
    public BaseResponse<String> deleteBid(@RequestBody DeleteBidRequest deleteBidRequest) {
        bidService.deleteBid(deleteBidRequest);
        return new BaseResponse<>(HttpStatus.OK.value(), "delete successfully", null);
    }
}
