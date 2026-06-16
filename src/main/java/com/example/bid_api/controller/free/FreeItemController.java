package com.example.bid_api.controller.free;

import com.example.bid_api.model.dto.ItemDto;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ItemRequest;
import com.example.bid_api.service.ItemService;
import com.example.bid_api.util.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/free/item")
@RequiredArgsConstructor
public class FreeItemController {
    private final ItemService itemService;

    @PostMapping("/list")
    public BaseResponse<List<ItemDto>> list(@RequestBody ItemRequest req, @AuthenticationPrincipal User user) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", itemService.getList(req, user));
    }

    @GetMapping("/detail/{itemId}")
    public BaseResponse<ItemDto> getItem(@PathVariable("itemId") String itemId, @AuthenticationPrincipal User user) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", itemService.getDetail(itemId, user));
    }
}
