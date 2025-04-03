package com.example.bid_api.repository.mongo.custom;

import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ChangeStatusRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.OrderSearch;

import java.util.List;

public interface CustomOrderRepository {
    List<Order> getOrderList(PageRequest<OrderSearch> request, User user);

    long countOrderList(OrderSearch request, User user);

    void updateOrderDate(ChangeStatusRequest request);

    void updateItemDate(ChangeStatusRequest request);
}
