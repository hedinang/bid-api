package com.example.bid_api.service;

import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.request.OrderRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.OrderSearch;

public interface OrderService {
    Order storeOrder(OrderRequest request);

    void deleteOrder(OrderRequest request);

    Page<Order> getOrderList(PageRequest<OrderSearch> request);
}
