package com.example.bid_api.repository.mongo;

import com.example.bid_api.model.entity.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BidRepository extends MongoRepository<Bid, String> {
    List<Bid> findByClosed(boolean closed);

    List<Bid> findByDetailUrlIn(List<String> detailUrls);

    List<Bid> findByClosedAndBidIdNotIn(boolean closed, List<Integer> bidIds);

    Bid findByBidIdAndBidStatus(Integer bidId, String bidStatus);

    void deleteByUniqueId(String uniqueId);

    long deleteByBidIdLessThan(Integer bidId);
}
