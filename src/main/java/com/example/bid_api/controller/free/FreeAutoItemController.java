package com.example.bid_api.controller.free;

import com.example.bid_api.service.AutoItemService;
import com.example.bid_api.service.impl.AutoItemServiceImpl;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/free/auto-item")
@RequiredArgsConstructor
public class FreeAutoItemController {
    private final AutoItemService autoItemService;

    @PostMapping("/test")
    public BaseResponse<List<AutoItemServiceImpl.BidItem>> test() throws Exception {
        return Response.toData(autoItemService.test());
    }
}
