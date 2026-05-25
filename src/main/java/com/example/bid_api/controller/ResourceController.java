package com.example.bid_api.controller;

import com.example.bid_api.model.entity.Resource;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ResourceRequest;
import com.example.bid_api.service.ResourceService;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/resource")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping(
            value = "/upload-chunk",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BaseResponse<Resource> uploadChunk(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") ResourceRequest resourceRequest,
            @AuthenticationPrincipal User user) {
        return Response.toData(resourceService.uploadFileChunk(file, resourceRequest, user));
    }

    @GetMapping("/file/{resourceId}")
    public CompletableFuture<ResponseEntity<StreamingResponseBody>> getFile(
            @AuthenticationPrincipal User meDto,
            @PathVariable String resourceId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        if (resourceId == null || resourceId.isBlank()) {
            return CompletableFuture.completedFuture(
                    new ResponseEntity<>(HttpStatus.BAD_REQUEST)
            );
        }

        return CompletableFuture.completedFuture(
                resourceService.streamFileDirectly(resourceId, meDto, rangeHeader, false)
        );
    }
}
