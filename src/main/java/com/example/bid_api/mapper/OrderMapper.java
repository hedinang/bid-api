package com.example.bid_api.mapper;

import com.example.bid_api.model.dto.OrderDto;
import com.example.bid_api.model.entity.Item;
import com.example.bid_api.model.entity.Order;
import com.example.bid_api.model.request.OrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order orderRequestToMail(OrderRequest orderRequest);

    OrderDto orderToOrderDto(Order order);

    @Mapping(target = "id", ignore = true)
    Order itemToOrder(Item item);
}
