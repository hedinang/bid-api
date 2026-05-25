package com.example.bid_api.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor

@NoArgsConstructor
public class VideoRequest {
    private String video_path;
    private String output_image_path;
}
