package com.example.bid_api.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageRequest<T> {
    private int limit;
    private int page;
    private T search;
}
