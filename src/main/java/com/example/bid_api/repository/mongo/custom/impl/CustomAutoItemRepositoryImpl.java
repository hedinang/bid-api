package com.example.bid_api.repository.mongo.custom.impl;

import com.example.bid_api.model.entity.AutoItem;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.AutoItemSearch;
import com.example.bid_api.model.search.UserSearch;
import com.example.bid_api.repository.mongo.custom.CustomAutoItemRepository;
import com.example.bid_api.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAutoItemRepositoryImpl implements CustomAutoItemRepository {
    private final MongoTemplate mongoTemplate;

    public void deleteByItemIds(List<String> itemIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("itemId").in(itemIds));
        mongoTemplate.remove(query, AutoItem.class);
    }

    public List<AutoItem> getList(PageRequest<AutoItemSearch> request) {
        String skip = String.format("{ $skip: %s }", (request.getPage() - 1) * request.getLimit());
        String limit = String.format("{ $limit: %s }", request.getLimit());

        Aggregation aggregation = StringUtil.buildAggregation(Arrays.asList(skip, limit));
        return mongoTemplate.aggregate(aggregation, "auto_item", AutoItem.class).getMappedResults();
    }

    public long countList(AutoItemSearch request) {
        String count = "{ $count: \"total\" }";

        Aggregation aggregation = StringUtil.buildAggregation(Arrays.asList(count));
        Map<String, Integer> totalItem = mongoTemplate.aggregate(aggregation, "auto_item", Map.class).getUniqueMappedResult();

        if (totalItem == null) {
            return 0L;
        } else {
            return Long.valueOf(totalItem.get("total"));
        }
    }
}
