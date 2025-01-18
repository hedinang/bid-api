package com.example.bid_api.repository.mongo.custom.impl;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.ItemRequest;
import com.example.bid_api.repository.mongo.custom.CustomItemRepository;
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
public class CustomItemRepositoryImpl implements CustomItemRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public AggregationResults<Item> getList(ItemRequest itemRequest) {
        try {
            String matchBidId = String.format("{ $match: {bid_id: '%s' } }", itemRequest.getBidId());
            String matchBidStatus = String.format("{ $match: {bid_status: '%s' } }", itemRequest.getBidStatus());
            String matchBranch = null;
            String matchRank = null;
            String matchCategory = null;
            String matchLimit = String.format("{ $limit: %s}", itemRequest.getLimit());
            String matchSkip = String.format("{ $skip: %s}", (itemRequest.getPage() - 1) * itemRequest.getLimit());

            if (itemRequest.getSearchBranch() != null && !itemRequest.getSearchBranch().trim().isEmpty()) {
                matchBranch = String.format("{ $match: {branch: '%s' } }", itemRequest.getSearchBranch().trim());
            }

            if (itemRequest.getSearchRank() != null && !itemRequest.getSearchRank().trim().isEmpty()) {
                matchRank = String.format("{ $match: {rank: '%s' } }", itemRequest.getSearchRank().trim());
            }

            if (itemRequest.getSearchCategory() != null && !itemRequest.getSearchCategory().trim().isEmpty()) {
                matchCategory = String.format("{ $match: {category: '%s' } }", itemRequest.getSearchCategory().trim());
            }

            Aggregation aggregation = StringUtil.buildAggregation(Arrays.asList(matchBidId, matchBidStatus, matchBranch, matchRank, matchCategory, matchSkip, matchLimit));
            return mongoTemplate.aggregate(aggregation, "item", Item.class);
        } catch (Exception e) {
            log.error("getList CustomTaskRepositoryImpl error : {}", e.getMessage());
            return null;
        }
    }
}
