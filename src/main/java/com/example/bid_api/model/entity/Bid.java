package com.example.bid_api.model.entity;

import com.example.bid_api.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "bid")
@AllArgsConstructor
@NoArgsConstructor
public class Bid extends MongoBaseEntity {
    @MongoId
    private ObjectId id;
    @Indexed(unique = true)
    @Field(name = "bid_id")
    private String bidId = StringUtil.generateId();

    @Field(name = "bid_status")
    private String bidStatus;

    @Field(name = "header_icon")
    private String headerIcon;

    @Field(name = "start_preview_time")
    private String startPreviewTime;

    @Field(name = "end_preview_time")
    private String endPreviewTime;

    @Field(name = "open_time")
    private String openTime;
}
