package com.example.bid_api.repository.mongo.custom;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.ItemRequest;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

public interface CustomItemRepository {
    AggregationResults<Item> getList(ItemRequest itemRequest);
}
