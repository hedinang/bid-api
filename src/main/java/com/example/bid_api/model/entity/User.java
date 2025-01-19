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
@Document(collection = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User extends MongoBaseEntity {
    @MongoId
    private ObjectId id;
    @Indexed(unique = true)
    @Field(name = "user_id")
    private String userId;

    @Field(name = "username")
    private String username;

    @Field(name = "password")
    private String password;

    @Field(name = "access_token")
    private String accessToken;

    @Field(name = "role")
    private String role;
}
