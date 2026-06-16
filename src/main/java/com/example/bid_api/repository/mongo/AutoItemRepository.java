package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.AutoItem;
import com.example.bid_api.repository.mongo.custom.CustomAutoItemRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AutoItemRepository extends MongoRepository<AutoItem, String>, CustomAutoItemRepository {
    List<AutoItem> findByItemNumberIn(List<String> itemNumbers);
}
