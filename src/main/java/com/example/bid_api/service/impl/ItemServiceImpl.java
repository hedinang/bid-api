package com.example.bid_api.service.impl;

import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.request.ItemRequest;
import com.example.bid_api.repository.mongo.ItemRepository;
import com.example.bid_api.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<Item> getList(ItemRequest itemRequest) {
        return itemRepository.getList(itemRequest).getMappedResults();
    }
}
