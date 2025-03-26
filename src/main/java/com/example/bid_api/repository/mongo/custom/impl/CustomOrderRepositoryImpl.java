package com.example.bid_api.repository.mongo.custom.impl;

import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.OrderSearch;
import com.example.bid_api.model.search.UserSearch;
import com.example.bid_api.repository.mongo.custom.CustomOrderRepository;
import com.example.bid_api.util.StringUtil;
import com.example.bid_api.util.constant.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOrderRepositoryImpl implements CustomOrderRepository {
    private final MongoTemplate mongoTemplate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;


    public List<Order> getOrderList(PageRequest<OrderSearch> request, User user) {
        String userId = null;
        String userNameSearch = null;
        String itemIdSearch = null;
        String categorySearch = null;
        String branchSearch = null;
        String rankSearch = null;
        String orderTypeSearch = null;
        String orderDateSearch = null;
        String itemDateSearch = null;

        if (request.getSearch() != null && request.getSearch().getUsername() != null && !request.getSearch().getUsername().isEmpty()) {
            userNameSearch = String.format("{ $match: { \"username\": { $regex: '%s', $options: 'i' } } }", request.getSearch().getUsername());
        }

        if (request.getSearch() != null && request.getSearch().getItemId() != null && !request.getSearch().getItemId().isEmpty()) {
            itemIdSearch = String.format("{ $match: { \"item_id\": { $regex: '%s', $options: 'i' } } }", request.getSearch().getItemId());
        }

        if (request.getSearch() != null && request.getSearch().getCategory() != null && !request.getSearch().getCategory().isEmpty()) {
            categorySearch = String.format("{ $match: { \"category\": '%s' } }", request.getSearch().getCategory());
        }

        if (request.getSearch() != null && request.getSearch().getBranch() != null && !request.getSearch().getBranch().isEmpty()) {
            branchSearch = String.format("{ $match: { \"branch\": '%s' } }", request.getSearch().getBranch());
        }

        if (request.getSearch() != null && request.getSearch().getRank() != null && !request.getSearch().getRank().isEmpty()) {
            rankSearch = String.format("{ $match: { \"rank\": '%s' } }", request.getSearch().getRank());
        }

        if (request.getSearch() != null && request.getSearch().getOrderType() != null && !request.getSearch().getOrderType().isEmpty()) {
            orderTypeSearch = String.format("{ $match: { \"type\": '%s' } }", request.getSearch().getOrderType());
        }

        if (request.getSearch() != null && request.getSearch().getOrderDate() != null && !request.getSearch().getOrderDate().isEmpty()) {
            Instant orderInstant = Instant.parse(request.getSearch().getOrderDate());
            LocalDate orderDate = orderInstant.atZone(ZoneOffset.UTC).toLocalDate();
            Instant startOfOrderDay = orderDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant endOfOrderDay = orderDate.atTime(23, 59, 59, 999_999_999).atZone(ZoneOffset.UTC).toInstant();

            String startOfOrderDayStr = formatter.format(startOfOrderDay);
            String endOfOrderDayStr = formatter.format(endOfOrderDay);

            orderDateSearch = String.format("{ $match: { \"updated_at\": { $gte: '%s', $lt: '%s' } } }",
                    startOfOrderDayStr, endOfOrderDayStr);
        }

        if (request.getSearch() != null && request.getSearch().getItemDate() != null && !request.getSearch().getItemDate().isEmpty()) {
            Instant itemInstant = Instant.parse(request.getSearch().getItemDate());
            LocalDate itemDate = itemInstant.atZone(ZoneOffset.UTC).toLocalDate();
            Instant startOfItemDay = itemDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant endOfItemDay = itemDate.atTime(23, 59, 59, 999_999_999).atZone(ZoneOffset.UTC).toInstant();

            String startOfItemDayStr = formatter.format(startOfItemDay);
            String endOfItemDayStr = formatter.format(endOfItemDay);

            itemDateSearch = String.format("{ $match: { \"item_date\": { $gte: '%s', $lt: '%s' } } }",
                    startOfItemDayStr, endOfItemDayStr);
        }


        String skip = String.format("{ $skip: %s }", (request.getPage() - 1) * request.getLimit());
        String limit = String.format("{ $limit: %s }", request.getLimit());

        if (user.getRole().equals(RoleType.CUSTOMER.toString())) {
            userId = String.format("{ $match: { \"user_id\": '%s' } }", user.getUserId());
        }

        String sort = "{ $sort: { \"updated_at\": -1 } }";

        Aggregation aggregation = StringUtil.buildAggregation(Arrays.asList(userId, userNameSearch, itemIdSearch,
                categorySearch, branchSearch, rankSearch, orderTypeSearch, orderDateSearch, itemDateSearch,
                sort, skip, limit));
        return mongoTemplate.aggregate(aggregation, "order", Order.class).getMappedResults();
    }

    public long countOrderList(OrderSearch request, User user) {
        String userId = null;
        String userNameSearch = null;
        String itemIdSearch = null;
        String categorySearch = null;
        String branchSearch = null;
        String rankSearch = null;
        String orderTypeSearch = null;
        String orderDateSearch = null;
        String itemDateSearch = null;
        if (request != null && request.getUsername() != null && !request.getUsername().isEmpty()) {
            userNameSearch = String.format("{ $match: { \"username\": { $regex: '%s', $options: 'i' } } }", request.getUsername());
        }

        if (request != null && request.getItemId() != null && !request.getItemId().isEmpty()) {
            itemIdSearch = String.format("{ $match: { \"item_id\": { $regex: '%s', $options: 'i' } } }", request.getItemId());
        }

        if (request != null && request.getCategory() != null && !request.getCategory().isEmpty()) {
            categorySearch = String.format("{ $match: { \"category\": '%s' } }", request.getCategory());
        }

        if (request != null && request.getBranch() != null && !request.getBranch().isEmpty()) {
            branchSearch = String.format("{ $match: { \"branch\": '%s' } }", request.getBranch());
        }

        if (request != null && request.getRank() != null && !request.getRank().isEmpty()) {
            rankSearch = String.format("{ $match: { \"rank\": '%s' } }", request.getRank());
        }

        if (request != null && request.getOrderType() != null && !request.getOrderType().isEmpty()) {
            orderTypeSearch = String.format("{ $match: { \"type\": '%s' } }", request.getOrderType());
        }

        if (request != null && request.getOrderDate() != null && !request.getOrderDate().isEmpty()) {
            Instant orderInstant = Instant.parse(request.getOrderDate());
            LocalDate orderDate = orderInstant.atZone(ZoneOffset.UTC).toLocalDate();
            Instant startOfOrderDay = orderDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant endOfOrderDay = orderDate.atTime(23, 59, 59, 999_999_999).atZone(ZoneOffset.UTC).toInstant();

            String startOfOrderDayStr = formatter.format(startOfOrderDay);
            String endOfOrderDayStr = formatter.format(endOfOrderDay);

            orderDateSearch = String.format("{ $match: { \"updated_at\": { $gte: '%s', $lt: '%s' } } }",
                    startOfOrderDayStr, endOfOrderDayStr);
        }

        if (request != null && request.getItemDate() != null && !request.getItemDate().isEmpty()) {
            Instant itemInstant = Instant.parse(request.getItemDate());
            LocalDate itemDate = itemInstant.atZone(ZoneOffset.UTC).toLocalDate();
            Instant startOfItemDay = itemDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant endOfItemDay = itemDate.atTime(23, 59, 59, 999_999_999).atZone(ZoneOffset.UTC).toInstant();

            String startOfItemDayStr = formatter.format(startOfItemDay);
            String endOfItemDayStr = formatter.format(endOfItemDay);

            itemDateSearch = String.format("{ $match: { \"item_date\": { $gte: '%s', $lt: '%s' } } }",
                    startOfItemDayStr, endOfItemDayStr);
        }


        if (user.getRole().equals(RoleType.CUSTOMER.toString())) {
            userId = String.format("{ $match: { \"user_id\": '%s' } }", user.getUserId());
        }

        String count = "{ $count: \"total\" }";

        Aggregation aggregation = StringUtil.buildAggregation(Arrays.asList(userId, userNameSearch, itemIdSearch,
                categorySearch, branchSearch, rankSearch, orderTypeSearch, orderDateSearch, itemDateSearch, count));
        Map<String, Integer> totalItem = mongoTemplate.aggregate(aggregation, "order", Map.class).getUniqueMappedResult();

        if (totalItem == null) {
            return 0L;
        } else {
            return Long.valueOf(totalItem.get("total"));
        }
    }
}
