package com.example.bid_api.mapper;

import com.example.bid_api.model.entity.Resource;
import com.example.bid_api.model.request.ResourceRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    Resource resourceRequestToResource(ResourceRequest resourceRequest);

//    Resource uploadChunkReqToResource(UploadChunkReq resourceRequest);

    Resource resourceReqToResource(ResourceRequest resourceRequest);
}
