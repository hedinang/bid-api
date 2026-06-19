package com.example.bid_api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanDto {
    private Instant endTime;
    private long maxRunningMinutes;
}
