package com.example.bid_api.service.impl;

import com.example.bid_api.mapper.OrderMapper;
import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.OrderRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.OrderSearch;
import com.example.bid_api.model.search.UserSearch;
import com.example.bid_api.repository.mongo.ItemRepository;
import com.example.bid_api.repository.mongo.OrderRepository;
import com.example.bid_api.service.OrderService;
import com.example.bid_api.util.StringUtil;
import com.example.bid_api.util.constant.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public Order storeOrder(OrderRequest request, User user) {
        Order order = orderMapper.orderRequestToMail(request);
        order.setUserId(user.getUserId());

        if (request.getItemId() != null && Objects.equals(user.getRole(), RoleType.CUSTOMER.toString())) {
            Order currentOrder = orderRepository.findByUserIdAndItemId(user.getUserId(), request.getItemId());

            if (Objects.isNull(currentOrder)) {
                order.setOrderId(StringUtil.generateId());
            } else {
                order.setId(currentOrder.getId());
            }
        } else {
            order.setOrderId(StringUtil.generateId());
        }

        return orderRepository.save(order);
    }

    public void deleteOrder(OrderRequest request) {
        orderRepository.deleteByOrderId(request.getOrderId());
    }

    public Page<Order> getOrderList(PageRequest<OrderSearch> request) {
        Page<Order> result = new Page<>();
        result.setItems(orderRepository.getOrderList(request));
        result.setTotalItems(orderRepository.countOrderList(request.getSearch()));
        return result;
    }
}
