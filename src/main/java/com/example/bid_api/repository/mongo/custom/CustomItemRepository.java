package com.example.bid_api.repository.mongo.custom;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.ItemRequest;

import java.util.List;

public interface CustomItemRepository {
    List<Item> getList(ItemRequest itemRequest);
}
