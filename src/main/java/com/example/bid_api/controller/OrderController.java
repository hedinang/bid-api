package com.example.bid_api.controller;

import com.example.bid_api.model.dto.OrderDto;
import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ChangeStatusRequest;
import com.example.bid_api.model.request.OrderRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.OrderSearch;
import com.example.bid_api.service.OrderService;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/store")
    public BaseResponse<Order> store(@RequestBody OrderRequest req, @AuthenticationPrincipal User user) {
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", orderService.storeOrder(req, user));
    }

    @PostMapping("/change-status")
    public BaseResponse<Order> changeStatus(@RequestBody OrderRequest req, @AuthenticationPrincipal User user) {
        orderService.changeStatus(req, user);
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", null);
    }

    @GetMapping("/delete")
    public BaseResponse<Order> delete(@RequestBody OrderRequest req) {
        orderService.deleteOrder(req);
        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully", null);
    }

    @PostMapping("/order-list")
    public BaseResponse<Page<OrderDto>> getUserList(@RequestBody PageRequest<OrderSearch> request, @AuthenticationPrincipal User user) {
        return Response.toData(orderService.getOrderList(request, user));
    }

    @PostMapping("/order-date/change-status")
    public BaseResponse changeStatusByOrderDate(@RequestBody ChangeStatusRequest request, @AuthenticationPrincipal User user) {
        orderService.changeStatusByOrderDate(request, user);
        return null;
    }

    @PostMapping("/item-date/change-status")
    public BaseResponse changeStatusByItemDate(@RequestBody ChangeStatusRequest request, @AuthenticationPrincipal User user) {
        orderService.changeStatusByItemDate(request, user);
        return null;
    }
}
