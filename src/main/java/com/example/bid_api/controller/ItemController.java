package com.example.bid_api.controller;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.service.ItemService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("list")
    public BaseResponse<List<Item>> list() {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", itemService.getList());
    }
}
