package com.example.bid_api.controller.free;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.request.BidRequest;
import com.example.bid_api.service.BidService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/free/bid")
@RequiredArgsConstructor
public class FreeBidController {
    private final BidService bidService;

    @GetMapping("/list")
    public BaseResponse<List<Bid>> list() {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", bidService.getList());
    }

    @PostMapping("/detail")
    public BaseResponse<Bid> get(@RequestBody BidRequest bidRequest) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", bidService.getBid(bidRequest));
    }
}
