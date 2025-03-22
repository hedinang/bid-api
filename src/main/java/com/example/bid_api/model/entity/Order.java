package com.example.bid_api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "order")
@AllArgsConstructor
@NoArgsConstructor
public class Order extends MongoBaseEntity {
    @MongoId
    private ObjectId id;
    @Indexed(unique = true)
    @Field(name = "order_id")
    private String orderId;
    @Field(name = "user_id")
    private String userId;
    @Field(name = "bid_id")
    private String bidId;
    @Field(name = "item_id")
    private String itemId;
    @Field(name = "bid_price")
    private long bidPrice;
    @Field(name = "type")
    private String type;
}
