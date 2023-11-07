package com.jiaruiblog.foxglove.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName RawImage
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/8/31 22:56
 * @Version 1.0
 **/
@Data
public class CompressedImage {

    private Timestamp timestamp;

    @JsonProperty("frame_id")
    private String frameId;

    private String format;

    private String data;

}
