package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BidRepository extends MongoRepository<Bid, String> {
    List<Bid> findByClosed(boolean closed);

    List<Bid> findByDetailUrlIn(List<String> detailUrls);

    List<Bid> findByClosedAndBidIdNotIn(boolean closed, List<String> bidIds);

    Bid findByBidId(String bid);

    void deleteByDetailUrlNotIn(List<String> detailUrls);
}
