package com.example.bid_api.mapper;

import com.example.bid_api.model.dto.ItemDto;
import com.example.bid_api.model.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto itemToItemDto(Item item);
}