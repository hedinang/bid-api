package com.example.bid_api.model.search;

import lombok.Data;

@Data
public class OrderSearch {
    public String username;
    public String itemId;
    public String category;
    public String branch;
    public String rank;
    public String orderType;
    public String orderDate;
    public String itemDate;
}
