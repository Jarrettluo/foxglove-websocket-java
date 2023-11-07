package com.jiaruiblog.foxglove.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PointCloud {

    public Timestamp timestamp;

    @JsonProperty("frame_id")
    private String frameId;

    @JsonProperty("point_stride")
    private Integer pointStride;

    private String data;

    private String pose;

    private PackedElement[] fields;
}
