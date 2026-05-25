package com.example.bid_api.model.dto;

import lombok.Data;

@Data
public class FfmpegDto {
    private final int exitCode;
    private final String output;

}
