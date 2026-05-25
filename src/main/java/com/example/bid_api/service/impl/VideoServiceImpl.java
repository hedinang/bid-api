package com.example.bid_api.service.impl;

import com.example.bid_api.model.dto.FfmpegDto;
import com.example.bid_api.model.dto.VideoDto;
import com.example.bid_api.model.request.VideoRequest;
import com.example.bid_api.service.FfmpegService;
import com.example.bid_api.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final FfmpegService ffmpegService;

    public VideoDto extract(VideoRequest request) {
        try {
            Path outputDir = Paths.get(request.getOutput_image_path()).getParent();
            if (outputDir != null) {
                Files.createDirectories(outputDir);
            }

            FfmpegDto firstFrameFfmpeg = ffmpegService.runCommand(Arrays.asList(
                    "ffmpeg", "-y", "-i", request.getVideo_path(),
                    "-ss", "0", "-vframes", "1", request.getOutput_image_path()
            ));

            if (firstFrameFfmpeg.getExitCode() != 0) {
                return new VideoDto(0, null, 0, 0);
            }

            FfmpegDto durationFfmpeg = ffmpegService.runCommand(Arrays.asList(
                    "ffprobe", "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    request.getVideo_path()
            ));

            if (durationFfmpeg.getExitCode() != 0) {
                return new VideoDto(0, null, 0, 0);
            }

            double duration = Double.parseDouble(durationFfmpeg.getOutput().trim());

            FfmpegDto dimensionFfmpeg = ffmpegService.runCommand(Arrays.asList(
                    "ffprobe",
                    "-v", "error",
                    "-select_streams", "v:0",
                    "-show_entries", "stream=width,height",
                    "-of", "csv=s=x:p=0",
                    request.getVideo_path()
            ));

            if (dimensionFfmpeg.getExitCode() != 0) {
                return new VideoDto(0, null, 0, 0);
            }

            String[] dimensions = dimensionFfmpeg.getOutput().trim().split("x");
            int width = 0;
            int height = 0;
            if (dimensions.length == 2) {
                try {
                    width = Integer.parseInt(dimensions[0]);
                    height = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    System.err.println("cannot get size image: " + dimensionFfmpeg.getOutput());
                    return new VideoDto(0, null, 0, 0);
                }
            }

            return new VideoDto(duration, request.getOutput_image_path(), width, height);
        } catch (Exception e) {
            return new VideoDto(0, null, 0, 0);
        }
    }
}
