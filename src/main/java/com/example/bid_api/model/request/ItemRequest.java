package com.example.bid_api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    List<Integer> bidIds;
    Integer bidId;
    String bidStatus;
    int limit;
    int page;
    String searchBranch;
    String searchDate;
    String searchRank;
    String searchCategory;
}
