package com.example.bid_api.repository.mongo.custom.impl;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.ItemRequest;
import com.example.bid_api.repository.mongo.custom.CustomBidRepository;
import com.example.bid_api.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomItemRepositoryImpl implements CustomBidRepository {
    private final MongoTemplate mongoTemplate;

    public AggregationResults<Item> getList(ItemRequest itemRequest) {
        try {
            String matchBidId = String.format("{ $match: { 'bid_id': %s } }", itemRequest.getBidId());
            String matchLimit = String.format("{ $limit: %s}", itemRequest.getLimit());
            String matchSkip = String.format("{ $skip: %s}", itemRequest.getSkip());

            Aggregation aggregation = StringUtil.buildAggregation(Arrays.asList(matchBidId, matchLimit, matchSkip));
            return mongoTemplate.aggregate(aggregation, "item", Item.class);
        } catch (Exception e) {
            log.error("getList CustomTaskRepositoryImpl error : {}", e.getMessage());
            return null;
        }
    }
}
