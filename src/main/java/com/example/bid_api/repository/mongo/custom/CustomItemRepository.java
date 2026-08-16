package com.example.bid_api.repository.mongo.custom;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.ItemRequest;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.List;

public interface CustomItemRepository {
    AggregationResults<Item> getList(ItemRequest itemRequest);

    List<Item> pageItem(ItemRequest itemRequest);

    Long countItem(ItemRequest itemRequest);
}
