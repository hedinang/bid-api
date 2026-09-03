package com.example.bid_api.service.impl;

import com.example.bid_api.mapper.ItemMapper;
import com.example.bid_api.model.dto.ItemDto;
import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ItemRequest;
import com.example.bid_api.repository.mongo.ItemRepository;
import com.example.bid_api.repository.mongo.OrderRepository;
import com.example.bid_api.service.BidService;
import com.example.bid_api.service.ItemService;
import com.example.bid_api.util.constant.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final ItemMapper itemMapper;

    private final BidService bidService;

    public Page<ItemDto> getPage(ItemRequest itemRequest, User user) {
        List<Bid> bids = new ArrayList<>();

        if (itemRequest.getBidId() == null) {
            bids = bidService.getList();
            itemRequest.setBidIds(bids.stream().map(Bid::getBidId).toList());
        }

        return new Page<>(pageItem(itemRequest, bids, user), itemRepository.countItem(itemRequest));
    }

    private List<ItemDto> pageItem(ItemRequest itemRequest, List<Bid> bids, User user) {
        List<Item> itemList = itemRepository.pageItem(itemRequest);
        List<String> itemIds = itemList.stream().map(Item::getItemId).toList();
        List<ItemDto> itemDtoList = itemList.stream().map(itemMapper::itemToItemDto).toList();

        Map<Integer, Bid> bidMap = new HashMap<>();
        if (bids != null && !bids.isEmpty()) {
            bidMap = bids.stream().collect(Collectors.toMap(Bid::getBidId, bid -> bid, (a, b) -> a));
        }

        for (ItemDto itemDto : itemDtoList) {
            if (bidMap.containsKey(itemDto.getBidId())) {
                itemDto.setStartPreviewTime(bidMap.get(itemDto.getBidId()).getStartPreviewTime());
                itemDto.setEndPreviewTime(bidMap.get(itemDto.getBidId()).getEndPreviewTime());
                itemDto.setOpenTime(bidMap.get(itemDto.getBidId()).getOpenTime());
            }
        }

        if (user != null && Objects.equals(RoleType.CUSTOMER.toString(), user.getRole())) {
            List<Order> orders = orderRepository.findByUserIdAndItemIdIn(user.getUserId(), itemIds);
            Map<String, Order> orderMap = orders.stream().collect(Collectors.toMap(Order::getItemId, order -> order, (a, b) -> a));

            for (ItemDto itemDto : itemDtoList) {
                if (orderMap.containsKey(itemDto.getItemId())) {
                    itemDto.setBidPrice(orderMap.get(itemDto.getItemId()).getBidPrice());
                    itemDto.setOrderType(orderMap.get(itemDto.getItemId()).getType());
                    itemDto.setOrderId(orderMap.get(itemDto.getItemId()).getOrderId());
                }
            }
        }

        return itemDtoList;
    }

    @Override
    public ItemDto getDetail(String itemId, User user) {
        List<Item> itemList = itemRepository.findByItemId(itemId);
        if (itemList.isEmpty()) return null;

        ItemDto itemDto = itemMapper.itemToItemDto(itemList.get(itemList.size() - 1));

        if (user != null && Objects.equals(RoleType.CUSTOMER.toString(), user.getRole())) {
            Order order = orderRepository.findByUserIdAndItemId(user.getUserId(), itemDto.getItemId());

            if (order != null) {
                itemDto.setBidPrice(order.getBidPrice());
                itemDto.setOrderType(order.getType());
                itemDto.setOrderId(order.getOrderId());
            }
        }

        return itemDto;
    }
}
