package com.example.bid_api.service;

import com.example.bid_api.model.entity.Bid;
import com.example.bid_api.model.request.BidRequest;

import java.util.List;
import java.util.Set;

public interface BidService {
    List<Bid> getList();

    Bid getBid(BidRequest bidRequest);

    void stopThread(String threadName);

    void storeBid();

    void syncBid(BidRequest bidRequest);

    Set<String> listThread();
}
