package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BidRepository extends MongoRepository<Bid, String> {
}
