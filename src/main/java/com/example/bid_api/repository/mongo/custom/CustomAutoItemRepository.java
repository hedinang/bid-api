package com.example.bid_api.repository.mongo.custom;

import com.example.bid_api.model.entity.AutoItem;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.AutoItemSearch;
import com.example.bid_api.model.search.UserSearch;

import java.util.List;

public interface CustomAutoItemRepository {
    void deleteByItemIds(List<String> itemIds);

    List<AutoItem> getList(PageRequest<AutoItemSearch> request);

    long countList(AutoItemSearch request);
}
