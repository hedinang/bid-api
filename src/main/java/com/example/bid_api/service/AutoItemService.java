package com.example.bid_api.service;

import com.example.bid_api.model.dto.Page;
import com.example.bid_api.model.dto.ScanDto;
import com.example.bid_api.model.entity.AutoItem;
import com.example.bid_api.model.request.AutoItemRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.request.ScanRequest;
import com.example.bid_api.model.search.AutoItemSearch;

import java.util.List;

public interface AutoItemService {
    List<AutoItem> extractCsvFile(String resourceId);

    Page<AutoItem> getList(PageRequest<AutoItemSearch> request);

    void delete(String itemId);

    void deleteAll();

    void edit(AutoItemRequest request);

    ScanDto executeTrigger(ScanRequest request);

    void stopTrigger();

    ScanDto checkScan();
}
