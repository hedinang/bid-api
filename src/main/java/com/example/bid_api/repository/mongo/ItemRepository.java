package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.repository.mongo.custom.CustomItemRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String>, CustomItemRepository {
    List<Item> findByBidIdIn(List<String> bidIds);

    List<Item> findByItemId(String itemId);

    List<Item> findByItemIdIn(List<String> itemIds);
}

