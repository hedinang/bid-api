package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {
    List<Item> findByBidId(String bidId);
}

