package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Order;
import com.example.bid_api.repository.mongo.custom.CustomOrderRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>, CustomOrderRepository {
    Order findByOrderId(String orderId);

    void deleteByOrderId(String orderId);

    Order findByUserIdAndItemId(String userId, String itemId);

    List<Order> findByUserIdAndItemIdIn(String userId, List<String> itemIds);
}
