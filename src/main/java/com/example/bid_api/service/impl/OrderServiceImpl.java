package com.example.bid_api.service.impl;

import com.example.bid_api.configuration.ThreadPoolConfig;
import com.example.bid_api.mapper.OrderMapper;
import com.example.bid_api.model.dto.ItemDto;
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
import com.example.bid_api.util.constant.OrderStepType;
import com.example.bid_api.util.constant.RoleType;
import com.example.bid_api.util.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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
    private final MailSender mailSender;
    private final ThreadPoolConfig threadPoolConfig;

    @Value("${main-email}")
    String mainEmailAddress;

    public Order storeOrder(OrderRequest request, User user) {
        //verify item
        List<Item> itemList = itemRepository.findByItemId(request.getItemId());
        if (itemList.isEmpty()) return null;

        Item item = itemList.get(itemList.size() - 1);
        Order order = orderMapper.orderRequestToMail(request);
        order.setUserId(user.getUserId());

        if (request.getItemId() != null && Objects.equals(user.getRole(), RoleType.CUSTOMER.toString())) {
            Order currentOrder = orderRepository.findByUserIdAndItemId(user.getUserId(), request.getItemId());

            if (Objects.isNull(currentOrder)) {
                order.setOrderId(StringUtil.generateId());
            } else {
                order.setOrderId(currentOrder.getOrderId());
                order.setId(currentOrder.getId());
            }
        } else {
            order.setOrderId(StringUtil.generateId());
        }

        order.setUpdatedAt(DateUtil.formatDateTime(new Date()));
        order.setType(OrderStepType.ORDER.toString());

        threadPoolConfig.getMailThreadPool().execute(() -> sendEmail(mainEmailAddress, "Stjtrading Order",
                String.format("User: %s - %s đã đặt 1 order với : %s - %s", user.getUsername(),
                        user.getName(), item.getItemId(), item.getTitle())));
        return orderRepository.save(order);
    }

    public void changeStatus(OrderRequest request, User user) {
        Order currentOrder = orderRepository.findByOrderId(request.getOrderId());

        if (currentOrder != null) {
            List<Item> itemList = itemRepository.findByItemId(currentOrder.getItemId());
            if (itemList.isEmpty()) return;

            Item item = itemList.get(itemList.size() - 1);
            currentOrder.setType(request.getType());
            orderRepository.save(currentOrder);
            threadPoolConfig.getMailThreadPool().execute(() -> {
                String content = "";
                String destinationMail = "";

                //customer
                if (Objects.equals(request.getType(), OrderStepType.ORDER.toString())) {
                    destinationMail = mainEmailAddress;
//                    content = String.format("User: %s - %s has stored 1 order with item: %s - %s", user.getUsername(),
//                            user.getName(), item.getItemId(), item.getTitle());
                    content = String.format("User: %s - % đã đặt 1 order với item: %s - %s", user.getUsername(),
                            user.getName(), item.getItemId(), item.getTitle());
                }

                if (Objects.equals(request.getType(), OrderStepType.CANCEL.toString())) {
                    destinationMail = mainEmailAddress;
                    content = String.format("User: %s - %s đã hủy 1 order với item: %s - %s", user.getUsername(),
                            user.getName(), item.getItemId(), item.getTitle());
                }

                //admin
                if (Objects.equals(request.getType(), OrderStepType.BIDDING.toString())) {
                    User client = userRepository.findByUserId(currentOrder.getUserId()).orElse(null);

                    if (client == null || client.getEmail() == null) {
                        return;
                    }

                    destinationMail = client.getEmail();
                    content = String.format("Order của bạn: %s - %s đã được đặt. Vui lòng đợi kết quả. Xin cảm ơn", item.getItemId(), item.getTitle());
                }

                if (Objects.equals(request.getType(), OrderStepType.SUCCESS.toString())) {
                    User client = userRepository.findByUserId(currentOrder.getUserId()).orElse(null);

                    if (client == null || client.getEmail() == null) {
                        return;
                    }

                    destinationMail = client.getEmail();
                    content = String.format("Chúc mừng, order của bạn: %s - %s đã đấu giá thành công", item.getItemId(), item.getTitle());
                }

                if (Objects.equals(request.getType(), OrderStepType.FAILED.toString())) {
                    User client = userRepository.findByUserId(currentOrder.getUserId()).orElse(null);

                    if (client == null || client.getEmail() == null) {
                        return;
                    }

                    destinationMail = client.getEmail();
                    content = String.format("Thật đáng tiếc order : %s - %s đã đấu giá thất bại", item.getItemId(), item.getTitle());
                }

                sendEmail(destinationMail, "Stjtrading Order", content);
            });
        }
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
            orderDto.setItemUrl(item.getItemUrl());

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

    public void sendEmail(String destination, String subject, String body) {
        if (destination == null || subject == null || body == null) return;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destination);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error sending mail: " + e.getMessage());
        }
    }
}
