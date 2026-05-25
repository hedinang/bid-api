package com.example.bid_api.model.request;

import lombok.Data;

@Data
public class ResourceRequest {
    private String folder;
    private String fileName;
    private int chunkIndex;
    private int totalChunk;
    private String userId;
    private String type;
    private String requestUuid;
    private String contentType;
    private Double duration;
    private Integer frameCount;
    private Integer width;
    private Integer height;
    private String actualContentType;
    private Boolean isThumbnail;
    private String previewImgResourceId;
}
