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
@Document(collection = "mail")
@AllArgsConstructor
@NoArgsConstructor
public class Mail extends MongoBaseEntity {
    @MongoId
    private ObjectId id;
    @Indexed(unique = true)
    @Field(name = "mail_id")
    private String mailId;
    private String address;
}
