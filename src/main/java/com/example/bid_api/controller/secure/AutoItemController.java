package com.example.bid_api.controller.secure;

import com.example.bid_api.model.request.AutoItemFileRequest;
import com.example.bid_api.model.request.PageRequest;
import com.example.bid_api.model.search.AutoItemSearch;
import com.example.bid_api.service.AutoItemService;
import com.example.bid_api.util.response.BaseResponse;
import com.example.bid_api.util.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
