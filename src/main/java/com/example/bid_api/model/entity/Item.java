package com.example.bid_api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Document(collection = "item")
@AllArgsConstructor
@NoArgsConstructor
public class Item extends MongoBaseEntity {
    @MongoId
    private ObjectId id;
    @Indexed(unique = true)
    @Field(name = "item_id")
    private String itemId;

    @Field(name = "bid_id")
    private String bidId;

    @Field(name = "title")
    private String title;

    @Field(name = "item_url")
    private String itemUrl;

    @Field(name = "rank")
    private String rank;

    @Field(name = "start_price")
    private String startPrice;

    @Field(name = "auction_order")
    private String auctionOrder;

    @Field(name = "detail_urls")
    private List<String> detailUrls;

    @Field(name = "description")
    private String description;

    @Field(name = "category")
    private String category;

    @Field(name = "branch")
    private String branch;
}
