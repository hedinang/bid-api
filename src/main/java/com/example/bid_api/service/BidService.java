package com.example.bid_api.service;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.request.BidRequest;
import com.example.bid_api.model.request.DeleteBidRequest;

import java.util.List;
import java.util.Set;

public interface BidService {
    List<Bid> getList();

    Bid getBid(BidRequest bidRequest);

    void stopThread(String threadName);

    void storeBid();

    void storeBidV2();

    void syncBid(BidRequest bidRequest);

    void syncBidV2(BidRequest bidRequest);

    Set<String> listThread();

    void deleteBid(DeleteBidRequest deleteBidRequest);
}
