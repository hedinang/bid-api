package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BidRepository extends MongoRepository<Bid, String> {
    List<Bid> findByClosed(boolean closed);

    List<Bid> findByDetailUrlIn(List<String> detailUrls);

    List<Bid> findByClosedAndBidIdNotIn(boolean closed, List<String> bidIds);

    Bid findByBidIdAndBidStatus(String bidId, String bidStatus);

    void deleteByDetailUrlNotIn(List<String> detailUrls);

    void deleteByUniqueId(String uniqueId);
}
