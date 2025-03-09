package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Order;
import com.example.bid_api.repository.mongo.custom.CustomOrderRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String>, CustomOrderRepository {
    Order findByOrderId(String orderId);

    void deleteByOrderId(String orderId);
}
