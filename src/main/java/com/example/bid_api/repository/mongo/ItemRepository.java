package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.repository.mongo.custom.CustomItemRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String>, CustomItemRepository {
}

