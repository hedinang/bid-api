package com.example.bid_api.model.entity;

import com.example.bid_api.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@Document(collection = "resource")
@AllArgsConstructor
@NoArgsConstructor
public class Resource extends MongoBaseEntity {
    @MongoId
    private ObjectId id;
    @Indexed(unique = true)
    @Field(name = "resource_id")
    private String resourceId = StringUtil.generateId();

    @Field(name = "folder")
    private String folder;

    @Field(name = "date")
    private String date;

    @Field(name = "file_name")
    private String fileName;

    @Field(name = "path")
    private String path;

    @Field(name = "user_id")
    private String userId;

    private String type;
    @Field(name = "content_type")
    private String contentType;
    // calculate by byte
    private Integer volume;

    // serve for chunk
    @Field(name = "request_uuid")
    private String requestUuid;

    @Field(name = "chunk_index")
    private int chunkIndex;

    @Field(name = "total_chunk")
    private int totalChunk;

    @Field(name = "video_img_resource_id")
    private String videoImgResourceId;

    @Field(name = "preview_video_image")
    private String previewVideoImage;

    @Field(name = "preview_img_resource_id")
    private String previewImgResourceId;

    @Field(name = "is_thumbnail")
    private Boolean isThumbnail;

    @Field(name = "actual_content_type")
    private String actualContentType;

    private Integer width;
    private Integer height;
    private Double duration;

    @Field(name = "office_preview_url")
    private String officePreviewUrl;
    @Field(name = "pdf_preview_resource_id")
    private String pdfPreviewResourceId;
}
