package com.example.bid_api.service;

import com.example.bid_api.model.dto.VideoDto;
import com.example.bid_api.model.request.VideoRequest;


public interface VideoService {
    VideoDto extract(VideoRequest request);
}
