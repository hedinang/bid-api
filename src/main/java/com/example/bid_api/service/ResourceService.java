package com.example.bid_api.service;

import com.example.bid_api.model.entity.Resource;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ResourceRequest;
import com.example.bid_api.model.request.UploadFileRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.concurrent.CompletableFuture;

public interface ResourceService {
    boolean uploadProfileImage(UploadFileRequest req, User user);

    Resource uploadFileChunk(MultipartFile file, ResourceRequest resourceRequest, User user);

    ResponseEntity<StreamingResponseBody> streamFileDirectly(String resourceId, User meDto, String rangeHeader, boolean isDownload);
}
