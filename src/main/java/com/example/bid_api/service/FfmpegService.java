package com.example.bid_api.service;

import com.example.bid_api.model.dto.FfmpegDto;

import java.io.IOException;
import java.util.List;

public interface FfmpegService {
    FfmpegDto runCommand(List<String> command) throws IOException, InterruptedException;
}
