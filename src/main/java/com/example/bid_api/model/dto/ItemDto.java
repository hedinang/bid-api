package com.example.bid_api.model.dto;

import com.example.bid_api.model.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto extends Item {
    private long bidPrice;
    private String orderType;
    private String orderId;
}
