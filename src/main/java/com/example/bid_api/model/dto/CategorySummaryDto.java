package com.example.bid_api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySummaryDto {
    private String categoryName;
//    private int totalItem;
    private int uploadedCount;
}
