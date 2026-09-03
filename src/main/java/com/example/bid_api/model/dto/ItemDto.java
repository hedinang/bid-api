package com.example.bid_api.model.dto;

import com.example.bid_api.model.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto extends Item {
    private long bidPrice;
    private String orderType;
    private String orderId;

    private String startPreviewTime;
    private String endPreviewTime;
    private String openTime;
}
