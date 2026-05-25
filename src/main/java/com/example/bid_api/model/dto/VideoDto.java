package com.example.bid_api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDto {
    private double duration;
    private String presentImg;
    private int width;
    private int height;

    public VideoDto(double duration, String imagePath) {
        this(duration, imagePath, 0, 0);
    }
}
