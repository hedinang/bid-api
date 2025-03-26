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
    //order status
    @Field(name = "type")
    private String type;


    //item
    @Field(name = "bid_status")
    private String bidStatus;

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

    @Field(name = "item_date")
    private String itemDate;

    //user
    @Field(name = "username")
    private String username;
    @Field(name = "name")
    private String name;
    @Field(name = "email")
    private String email;
    @Field(name = "phone")
    private String phone;
}
