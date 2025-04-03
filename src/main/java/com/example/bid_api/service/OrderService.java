package com.example.bid_api.service;

import com.example.bid_api.model.dto.OrderDto;
import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ChangeStatusRequest;
import com.example.bid_api.model.request.OrderRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.OrderSearch;

public interface OrderService {
    Order storeOrder(OrderRequest request, User user);

    void changeStatus(OrderRequest request, User user);

    void deleteOrder(OrderRequest request);

    Page<OrderDto> getOrderList(PageRequest<OrderSearch> request, User user);

    void changeStatusByOrderDate(ChangeStatusRequest request, User user);

    void changeStatusByItemDate(ChangeStatusRequest request, User user);
}
