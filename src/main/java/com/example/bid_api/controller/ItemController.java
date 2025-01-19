package com.example.bid_api.controller;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.ItemRequest;
import com.example.bid_api.service.ItemService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/list")
    public BaseResponse<List<Item>> list(@RequestBody ItemRequest req) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", itemService.getList(req));
    }

    @GetMapping("/detail/{itemId}")
    public BaseResponse<Item> list(@PathVariable("itemId") String itemId) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", itemService.getDetail(itemId));
    }
}
