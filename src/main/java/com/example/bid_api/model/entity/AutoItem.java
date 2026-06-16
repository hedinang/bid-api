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
@Document(collection = "auto_item")
@AllArgsConstructor
@NoArgsConstructor
public class AutoItem extends MongoBaseEntity {
    @MongoId
    private ObjectId id;
    @Indexed(unique = true)
    @Field(name = "item_id")
    private String itemId;
    @Field(name = "item_number")
    private String itemNumber;

    @Field(name = "item_name")
    private String itemName;

    @Field(name = "auction_order")
    private String auctionOrder;

    @Field(name = "rank")
    private String rank;

    @Field(name = "starting_price")
    private String startingPrice;

    @Field(name = "pre_bidding_price")
    private String preBiddingPrice;

    private long notes;

    @Field(name = "start_scheduled")
    private String startScheduled;

    @Field(name = "max_price")
    private long maxPrice;
}
