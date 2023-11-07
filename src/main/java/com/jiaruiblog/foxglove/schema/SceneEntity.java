package com.jiaruiblog.foxglove.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SceneEntity
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/9/27 20:30
 * @Version 1.0
 **/
@Data
public class SceneEntity {

    private Timestamp timestamp;

    private Timestamp lifetime;

    @JsonProperty("frame_id")
    private String frameId;

    private String id;

    private boolean frame_locked;

    private List<KeyValuePair> metadata;

    private List<CubePrimitive> cubes;

    private List<Object> arrows;

    private List<Object> spheres;

    private List<Object> cylinders;

    private List<Line> lines;

    private List<Object> triangles;

    private List<Object> texts;

    private List<ModelPrimitive> models;

}
