package com.example.bid_api.repository.mongo.custom.impl;

import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.UserSearch;
import com.example.bid_api.repository.mongo.custom.CustomUserRepository;
import com.example.bid_api.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final MongoTemplate mongoTemplate;

    public List<User> getUserList(PageRequest<UserSearch> request) {
        String userNameSearch = "";
        String roleSearch = "";
        String statusSearch = "";

        if (request.getSearch() != null && request.getSearch().getUsername() != null && !request.getSearch().getUsername().isEmpty()) {
            userNameSearch = String.format("{ $match: { \"username\": { $regex: '%s', $options: 'i' } } }", request.getSearch().getUsername());
        }

        if (request.getSearch() != null && request.getSearch().getRole() != null && !request.getSearch().getRole().isEmpty()) {
            roleSearch = String.format("{ $match: { \"role\": '%s' } }", request.getSearch().getRole());
        }

        if (request.getSearch() != null && request.getSearch().getStatus() != null && !request.getSearch().getStatus().isEmpty()) {
            statusSearch = String.format("{ $match: { \"status\": '%s' } }", request.getSearch().getStatus());
        }

        String skip = String.format("{ $skip: %s }", (request.getPage() - 1) * 20);
        String limit = String.format("{ $limit: %s }", 20);

        Aggregation aggregation = StringUtil.buildAggregation(Arrays.asList(userNameSearch, roleSearch, statusSearch, skip, limit));
        return mongoTemplate.aggregate(aggregation, "user", User.class).getMappedResults();
    }

    public long countUserList(UserSearch request) {
        String userNameSearch = "";
        String roleSearch = "";
        String statusSearch = "";

        if (request != null && request.getUsername() != null && !request.getUsername().isEmpty()) {
            userNameSearch = String.format("{ $match: { \"username\": { $regex: '%s', $options: 'i' } } }", request.getUsername());
        }

        if (request != null && request.getRole() != null && !request.getRole().isEmpty()) {
            roleSearch = String.format("{ $match: { \"role\": '%s' } }", request.getRole());
        }

        if (request != null && request.getStatus() != null && !request.getStatus().isEmpty()) {
            statusSearch = String.format("{ $match: { \"status\": '%s' } }", request.getStatus());
        }

        String count = "{ $count: \"total\" }";

        Aggregation aggregation = StringUtil.buildAggregation(Arrays.asList(userNameSearch, roleSearch, statusSearch, count));
        Map<String, Integer> totalItem = mongoTemplate.aggregate(aggregation, "user", Map.class).getUniqueMappedResult();

        if (totalItem == null) {
            return 0L;
        } else {
            return Long.valueOf(totalItem.get("total"));
        }
    }
}
