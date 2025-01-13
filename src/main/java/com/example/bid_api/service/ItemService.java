package com.example.bid_api.service;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.ItemRequest;

import java.util.List;

public interface ItemService {
    List<Item> getList(ItemRequest itemRequest);

    Item getDetail(String itemId);
}
