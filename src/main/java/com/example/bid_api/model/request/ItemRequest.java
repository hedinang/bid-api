package com.example.bid_api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    String bidId;
    String bidStatus;
    int limit;
    int page;
    String searchBranch;
    String searchRank;
    String searchCategory;
}
