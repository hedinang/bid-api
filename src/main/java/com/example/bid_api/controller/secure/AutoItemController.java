package com.example.bid_api.controller.secure;

import com.example.bid_api.model.dto.ScanDto;
import com.example.bid_api.model.request.AutoItemFileRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.request.ScanRequest;
import com.example.bid_api.model.search.AutoItemSearch;
import com.example.bid_api.service.AutoItemService;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secure/auto-item")
@RequiredArgsConstructor
public class AutoItemController {
    private final AutoItemService autoItemService;

    @PostMapping("/import")
    public BaseResponse<Void> list(@RequestBody AutoItemFileRequest request) {
        autoItemService.extractCsvFile(request.getResourceId());

        return new BaseResponse<>(HttpStatus.OK.value(), "Update window successfully");
    }

    @PostMapping("/list")
    public BaseResponse<Object> getList(@RequestBody PageRequest<AutoItemSearch> request) {
        return Response.toData(autoItemService.getList(request));
    }

    @PostMapping("/scan")
    public BaseResponse<ScanDto> scan(@RequestBody ScanRequest request) {
        return Response.toData(autoItemService.executeTrigger(request));
    }

    @PostMapping("/stop-scan")
    public BaseResponse<Object> stopScan() {
        autoItemService.stopTrigger();
        return new BaseResponse<>(HttpStatus.OK.value(), "Stop scan successfully");
    }

    @GetMapping("/check-scan")
    public BaseResponse<ScanDto> checkScan() {
        return Response.toData(autoItemService.checkScan());
    }
}
