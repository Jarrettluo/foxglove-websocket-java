package com.jiaruiblog.foxglove.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName FrameTransform
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/9/28 18:11
 * @Version 1.0
 **/
@Data
public class FrameTransform {

    private Timestamp timestamp;

    @JsonProperty("parent_frame_id")
    private String parentFrameId;

    @JsonProperty("child_frame_id")
    private String childFrameId;

    private Vector3 translation;

    private Quaternion rotation;
}
