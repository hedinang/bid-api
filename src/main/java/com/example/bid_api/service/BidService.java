package com.example.bid_api.service;

import com.example.bid_api.model.entity.Bid;

import java.util.List;

public interface BidService {
    List<Bid> getList();

    Bid getBid(String bidId);

    void sync();
}
