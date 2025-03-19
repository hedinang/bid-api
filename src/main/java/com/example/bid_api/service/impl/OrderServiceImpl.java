package com.example.bid_api.service.impl;

import com.example.bid_api.mapper.OrderMapper;
import com.example.bid_api.model.dto.OrderDto;
import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.OrderRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.OrderSearch;
import com.example.bid_api.model.search.UserSearch;
import com.example.bid_api.repository.mongo.ItemRepository;
import com.example.bid_api.repository.mongo.OrderRepository;
import com.example.bid_api.repository.mongo.UserRepository;
import com.example.bid_api.service.OrderService;
import com.example.bid_api.util.StringUtil;
import com.example.bid_api.util.constant.RoleType;
import com.example.bid_api.util.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
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

        order.setUpdatedAt(DateUtil.formatDateTime(new Date()));
        return orderRepository.save(order);
    }

    public void deleteOrder(OrderRequest request) {
        orderRepository.deleteByOrderId(request.getOrderId());
    }

    public Page<OrderDto> getOrderList(PageRequest<OrderSearch> request, User user) {
        Page<OrderDto> result = new Page<>();
        List<Order> orders = orderRepository.getOrderList(request, user);
        Map<String, Item> itemMap = itemRepository.findByItemIdIn(orders.stream().map(Order::getItemId).toList()).stream()
                .collect(Collectors.toMap(Item::getItemId, item -> item, (a, b) -> a));
        Map<String, User> userMap = new HashMap<>();

        if (!user.getRole().equals(RoleType.CUSTOMER.toString())) {
            userMap = userRepository.findByUserIdIn(orders.stream().map(Order::getUserId).toList()).stream()
                    .collect(Collectors.toMap(User::getUserId, e -> e, (a, b) -> a));
        }

        Map<String, User> finalUserMap = userMap;

        List<OrderDto> orderDtoList = orders.stream().map(order -> {
            OrderDto orderDto = orderMapper.orderToOrderDto(order);
            Item item = itemMap.get(order.getItemId());
            orderDto.setBidId(item.getBidId());
            orderDto.setItemId(item.getItemId());
            orderDto.setBranch(item.getBranch());
            orderDto.setTitle(item.getTitle());
            orderDto.setDetailUrls(item.getDetailUrls());
            orderDto.setDescription(item.getDescription());
            orderDto.setCategory(item.getCategory());
            orderDto.setRank(item.getRank());

            if (!user.getRole().equals(RoleType.CUSTOMER.toString())) {
                User client = finalUserMap.get(order.getUserId());
                orderDto.setUserId(client.getUserId());
                orderDto.setUsername(client.getUsername());
                orderDto.setName(client.getName());
                orderDto.setEmail(client.getEmail());
                orderDto.setPhone(client.getPhone());
            }

            return orderDto;
        }).toList();

        result.setItems(orderDtoList);
        result.setTotalItems(orderRepository.countOrderList(request.getSearch()));
        return result;
    }
}
