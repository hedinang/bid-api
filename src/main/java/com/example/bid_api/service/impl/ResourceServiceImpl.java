package com.example.bid_api.service.impl;

import com.example.bid_api.mapper.ResourceMapper;
import com.example.bid_api.model.dto.VideoDto;
import com.example.bid_api.model.entity.Resource;
import com.example.bid_api.model.entity.User;
import com.example.bid_api.model.request.ResourceRequest;
import com.example.bid_api.model.request.UploadFileRequest;
import com.example.bid_api.model.request.VideoRequest;
import com.example.bid_api.repository.mongo.ResourceRepository;
import com.example.bid_api.repository.mongo.UserRepository;
import com.example.bid_api.service.ResourceService;
import com.example.bid_api.service.VideoService;
import com.example.bid_api.util.StringUtil;
import com.example.bid_api.util.constant.MessageContentType;
import com.example.bid_api.util.date.DateUtil;
import com.example.bid_api.util.exception.ServiceException;
import com.example.bid_api.util.response.CustomHttpStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    @Value("${root.file.store}")
    String rootStore;
    @Value("${root.folder}")
    String rootFolder;
    @Value("${root.file.name}")
    String rootName;
    @Value("${img-size-limit}")
    int imgSizeLimit;
    @Value("${stream-chunk-file-rate-limit}")
    int streamChunkFileRateLimit;

    public static final long CHUNK_SIZE = 2 * 1024 * 1024;

    private final List<String> videoTypes = List.of(".mp4", ".avi", ".mkv", ".webm", ".quicktime", ".x-flv", ".x-ms-wmv", ".mov", "3g2", "3gp", "aaf", "asf", "avchd", "avi", "drc", "flv", "m2v", "m3u8", "m4p", "m4v", "mkv", "mng", "mov", "mp2", "mp4", "mpe", "mpeg", "mpg", "mpv", "mxf", "nsv", "ogg", "ogv", "qt", "rm", "rmvb", "roq", "svi", "vob", "webm", "wmv", "yuv");


    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    private final ResourceMapper resourceMapper;

    private final VideoService videoService;

    @Override
    @Transactional
    public boolean uploadProfileImage(UploadFileRequest request, User user) {
        return userRepository.updateAvatarByUserId(user.getUserId(), request.getAvatar());
    }

    @Override
    public Resource uploadFileChunk(MultipartFile file, ResourceRequest resourceRequest, User user) {
        try {
            if (org.apache.commons.lang3.StringUtils.isBlank(resourceRequest.getContentType()))
                resourceRequest.setContentType("CHUNK");

            String chunkName = resourceRequest.getRequestUuid() + ".part" + resourceRequest.getChunkIndex();
            String date = DateUtil.getNowDateFolder();

            String fileStore = String.format("%s/%s/%s/%s/%s-%s",
                    rootStore, resourceRequest.getFolder(), resourceRequest.getContentType(), date, user.getUserId(), chunkName);

            File targetFile = new File(fileStore);
            FileUtils.forceMkdirParent(targetFile);
            Files.copy(file.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String fileName = String.format("%s/%s/%s/%s/%s-%s",
                    rootName, resourceRequest.getFolder(), resourceRequest.getContentType(), date, user.getUserId(), chunkName);

            Resource resource = resourceMapper.resourceReqToResource(resourceRequest);
            resource.setPath(fileName);
            resource.setResourceId(StringUtil.generateId());
            resource.setUserId(user.getUserId());
            resource.setVolume((int) file.getSize());
            resource.setDate(date);

            String actualContentType = resourceRequest.getActualContentType();
            if (actualContentType == null || actualContentType.isBlank()) {
                actualContentType = "unknown";
            }
            resource.setActualContentType(actualContentType);

            resourceRepository.save(resource);

            if (resourceRequest.getChunkIndex() == resourceRequest.getTotalChunk()) {
                long count = resourceRepository.countByRequestUuid(resourceRequest.getRequestUuid());

                if (count >= resourceRequest.getTotalChunk()) {
                    return mergeFileChunk(resourceRequest, user);
                } else {
                    Resource error = new Resource();
                    error.setResourceId("-1");
                    return error;
                }
            }
            return resource;
        } catch (IOException e) {
            log.error("V2 Chunk Upload failed for UUID: {}", resourceRequest.getRequestUuid(), e);
            return null;
        }
    }

    public Resource mergeFileChunk(ResourceRequest resourceRequest, User user) {
        List<Resource> chunkList = resourceRepository.findAllByRequestUuidOrderByChunkIndexAsc(resourceRequest.getRequestUuid());
        if (chunkList.size() < resourceRequest.getTotalChunk()) {
            log.warn("Not enough chunks for UUID: {}", resourceRequest.getRequestUuid());
            return null;
        }

        chunkList = new ArrayList<>(chunkList.stream().collect(Collectors.toMap(
                        Resource::getChunkIndex,
                        Function.identity(),
                        (a, b) -> a
                ))
                .values()
        );

        String date = DateUtil.getNowDateFolder();
        Resource firstChunk = chunkList.get(0);

        chunkList.sort(Comparator.comparingInt(Resource::getChunkIndex));

        try {
            String fileExtension = StringUtil.getFileExtension(resourceRequest.getFileName());
            String finalContentType = determineMessageType(resourceRequest.getActualContentType());

            String relativePath = String.format("%s/%s/%s/%s/%s-%s",
                    resourceRequest.getFolder(),
                    finalContentType,
                    date,
                    user.getUserId(),
                    firstChunk.getResourceId(),
                    fileExtension);
            String absolutePath = rootStore + "/" + relativePath;

            long totalVolume = fastConcatenate(chunkList, absolutePath);

            if (totalVolume > 0) {
                // --- THÊM ĐOẠN NÀY ĐỂ DETECT LẠI CONTENT-TYPE CHO CHUẨN ---
                String actualContentType = resourceRequest.getActualContentType();
                if (actualContentType == null || actualContentType.isBlank() || "unknown".equals(actualContentType)) {
                    try {
                        actualContentType = Files.probeContentType(Paths.get(absolutePath));
                    } catch (IOException e) {
                        log.warn("Could not probe content type for {}", absolutePath);
                    }

                    // Fallback cứng dựa theo đuôi file nếu Server (đặc biệt Linux Docker) không có sẵn bảng map MIME type
                    if (actualContentType == null) {
                        String ext = fileExtension.toLowerCase();
                        if (ext.equals(".pdf")) actualContentType = "application/pdf";
                        else if (ext.equals(".docx"))
                            actualContentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                        else if (ext.equals(".doc")) actualContentType = "application/msword";
                        else if (ext.equals(".xlsx"))
                            actualContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                        else if (ext.equals(".xls")) actualContentType = "application/vnd.ms-excel";
                        else if (ext.equals(".pptx"))
                            actualContentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                        else if (ext.equals(".ppt")) actualContentType = "application/vnd.ms-powerpoint";
                    }
                    firstChunk.setActualContentType(actualContentType != null ? actualContentType : "unknown");
                }
                // -------------------------------------------------------------

                firstChunk.setContentType(finalContentType);
                firstChunk.setPath(rootName + "/" + relativePath);
                firstChunk.setVolume((int) totalVolume);
                firstChunk.setFileName(resourceRequest.getFileName());

                switch (resourceRequest.getActualContentType()) {
                    // ===== VIDEO =====
                    case "video/mp4":
                    case "video/webm":
                    case "video/ogg":
                    case "video/quicktime":
                    case "video/x-msvideo":
                    case "video/x-ms-wmv":
                    case "video/mpeg":
                    case "video/3gpp":
                    case "video/x-matroska":
                        extractPreviewVideo(firstChunk, absolutePath, fileExtension, resourceRequest, user);
                        break;
                    default:
                        // dont do something
                        break;
                }

                resourceRepository.deleteByRequestUuidAndContentType(resourceRequest.getRequestUuid(), MessageContentType.CHUNK.toString());
                return resourceRepository.save(firstChunk);
            }
        } catch (Exception e) {
            log.error("Merge failed for UUID: {}", resourceRequest.getRequestUuid(), e);
        }
        return null;
    }

    @Override
    public ResponseEntity<StreamingResponseBody> streamFileDirectly(String resourceId, User meDto, String rangeHeader, boolean isDownload) {
        Resource resource = resourceRepository.findFirstByResourceId(resourceId).orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(),
                "Resource not found"));

        try {
            ResponseEntity<StreamingResponseBody> r = createStreamingResponse(resource, rangeHeader);
            return r;
        } catch (IOException e) {
            throw new CompletionException("Failed to stream video", e);
        }
    }

    private ResponseEntity<StreamingResponseBody> createStreamingResponse(Resource resource, String rangeHeader) throws IOException {
        String subPath = String.format("%s/%s", rootFolder, resource.getPath());
        Path filePath = Paths.get(subPath);
        File file = filePath.toFile();

        if (!file.exists()) {
            log.error("File not found in dir: {}", subPath);
            throw new ServiceException(CustomHttpStatus.FILE_NOT_FOUND, "File does not exist");
        }

        long fileLength = file.length();
        HttpHeaders headers = new HttpHeaders();

        String contentType = Files.probeContentType(filePath);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        String filename = resource.getFileName();

        if (filename != null && !filename.isEmpty()) {
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
            String fallbackFilename = filename.replaceAll("[^a-zA-Z0-9.\\-]", "_");
            String disposition = (contentType.startsWith("image/") || contentType.startsWith("video/") || contentType.equals("application/pdf")) ? "inline" : "attachment";
            headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("%s; filename=\"%s\"; filename*=UTF-8''%s", disposition, fallbackFilename, encodedFilename));
        }

        headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        long rangeStart;
        long rangeEnd;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");

            try {
                rangeStart = Long.parseLong(ranges[0]);

                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    rangeEnd = Long.parseLong(ranges[1]);
                } else {
                    rangeEnd = rangeStart + CHUNK_SIZE - 1;
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                log.warn("Invalid Range header format: {}", rangeHeader);
                return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            }
        } else {
            rangeStart = 0;
            rangeEnd = fileLength - 1;
        }

        rangeStart = Math.max(0, rangeStart);
        rangeEnd = Math.min(rangeEnd, fileLength - 1);

        if (rangeStart > fileLength) {
            log.warn("Requested range not satisfiable: start > fileLength. start={}, fileLength={}", rangeStart, fileLength);
            return new ResponseEntity<>(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        }

        long contentLength = (rangeEnd - rangeStart) + 1;
        final long finalRangeStart = rangeStart;

        StreamingResponseBody responseBody = outputStream -> {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
                randomAccessFile.seek(finalRangeStart);
                byte[] buffer = new byte[streamChunkFileRateLimit];
                long bytesToWrite = contentLength;
                int bytesRead;

                while (bytesToWrite > 0 && (bytesRead = randomAccessFile.read(buffer, 0, (int) Math.min(buffer.length, bytesToWrite))) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    bytesToWrite -= bytesRead;
                }
            } catch (IOException e) {
                log.warn("I/O error stream (maybe client closed connection): {}", e.getMessage());
            }
        };

        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);

        HttpStatus status = (rangeHeader == null) ? HttpStatus.OK : HttpStatus.PARTIAL_CONTENT;

        return new ResponseEntity<>(responseBody, headers, status);
    }

    private void extractPreviewVideo(Resource resource, String absolutePath, String ext, ResourceRequest req, User user) {
        if (videoTypes.contains(ext.toLowerCase())) {
            try {
                req.setActualContentType(null);
                String previewName = String.format("%s/%s/%s/%s/%s.jpg",
                        req.getFolder(), "IMAGE", resource.getDate(), user.getUserId(), resource.getResourceId());
                String previewPath = String.format("%s/%s", rootStore, previewName);

                VideoDto videoDto = videoService.extract(new VideoRequest(absolutePath, previewPath));

                if (videoDto != null) {
                    req.setHeight(videoDto.getHeight());
                    req.setWidth(videoDto.getWidth());
                    String imgPreviewVideoPath = String.format("%s/%s", rootStore, previewName);
                    Resource imgPreview = storePreviewImage(req, resource.getDate(), imgPreviewVideoPath, user);

                    resource.setPreviewVideoImage(imgPreview.getPath());
                    resource.setVideoImgResourceId(imgPreview.getResourceId());
                    resource.setDuration(videoDto.getDuration());
                    resource.setWidth(videoDto.getWidth());
                    resource.setHeight(videoDto.getHeight());
                }
            } catch (Exception e) {
                log.error("Async video processing failed", e);
            }
        }
    }

    private Resource storePreviewImage(ResourceRequest resourceRequest, String date, String imgPreviewVideoPath, User user) {
        Resource imgPreviewResource = resourceMapper.resourceRequestToResource(resourceRequest);
        imgPreviewResource.setFolder(resourceRequest.getFolder());
        imgPreviewResource.setDate(date);
        imgPreviewResource.setPath(imgPreviewVideoPath);
        imgPreviewResource.setResourceId(StringUtil.generateId());
        imgPreviewResource.setUserId(user.getUserId());
        imgPreviewResource.setContentType(MessageContentType.IMAGE.toString());
        imgPreviewResource.setWidth(resourceRequest.getWidth());
        imgPreviewResource.setHeight(resourceRequest.getHeight());
        imgPreviewResource.setIsThumbnail(true);
        log.info("img done");
        resourceRepository.save(imgPreviewResource);
        return imgPreviewResource;
    }

    private long fastConcatenate(List<Resource> chunkList, String targetPath) throws IOException {
        File targetFile = new File(targetPath);
        FileUtils.forceMkdirParent(targetFile);

        try (FileOutputStream fos = new FileOutputStream(targetFile);
             FileChannel destChannel = fos.getChannel()) {

            long totalBytes = 0;
            for (Resource chunk : chunkList) {
                String relativePath = chunk.getPath().replace(rootName + "/", "");
                File chunkFile = new File(rootStore + "/" + relativePath);

                if (chunkFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(chunkFile);
                         FileChannel srcChannel = fis.getChannel()) {

                        long size = srcChannel.size();
                        long transferred = 0;

                        while (transferred < size) {
                            long s = srcChannel.transferTo(transferred, size - transferred, destChannel);
                            if (s <= 0) break;
                            transferred += s;
                        }
                        totalBytes += transferred;
                    }
                    Files.deleteIfExists(chunkFile.toPath());
                } else {
                    log.error("Chunk file không tồn tại tại đường dẫn: {}", chunkFile.getAbsolutePath());
                    throw new FileNotFoundException("Missing chunk part: " + chunk.getChunkIndex());
                }
            }
            destChannel.force(true);
            return totalBytes;
        }
    }

    private String determineMessageType(String actualContentType) {
        if (actualContentType.startsWith("image/")) {
            return MessageContentType.IMAGE.toString();
        }
//        else if (actualContentType.startsWith("video/")) {
//            return MessageContentType.VIDEO.toString();
//        }
        return MessageContentType.FILE.toString();
    }
}
