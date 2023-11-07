package com.jiaruiblog.foxglove.util;

import com.jiaruiblog.foxglove.schema.*;

import java.util.Arrays;
import java.util.List;

public class SceneUtil {

    public static SceneEntity createEntity(String id, String frameId, String metaDataValue, Timestamp ts) {
        if (ts == null) {
            ts = DateUtil.createTimestamp();
        }

        List<KeyValuePair> metadata = Arrays.asList(new KeyValuePair("category", metaDataValue));

        SceneEntity entity = new SceneEntity();
        entity.setTimestamp(ts);
        entity.setFrameId(frameId);
        entity.setFrame_locked(true);
        entity.setId(id);
        entity.setMetadata(metadata);
        return entity;
    }

    public static List<ModelPrimitive> addModels() {
        ModelPrimitive model = new ModelPrimitive();
        Pose pose = new Pose();
        Vector3 position = new Vector3(-11.200000f, -8.499990f, 1.623f);
        Quaternion orientation = new Quaternion(0.0f, 0.0f, 0f, 0.1f);
        pose.setPosition(position);
        pose.setOrientation(orientation);

        Vector3 scale = new Vector3(1f, 1f, 1f);
        //Color color = new Color(0.6f, 0.2f, 1f, 0.8f);
        Color color = new Color(1f, 0.38823529411764707f, 0.2784313725490196f, 0.5f);
        byte[] bytes = DataUtil.loadGlbData("car.glb");

        model.setColor(color);
        model.setPose(pose);
        model.setData(bytes);
        model.setScale(scale);
        model.setMedia_type("model/gltf-binary");

        return Arrays.asList(model);
    }

    public static List<CubePrimitive> addCubes(String[] data) {
        CubePrimitive cube = new CubePrimitive();
        Color color = setCubeColor(Integer.parseInt(data[7]));

        float length = Float.parseFloat(data[4]);
        float width = Float.parseFloat(data[5]);
        float height = length < width ? length : width;
        Vector3 size = new Vector3(length, width, height);

        Pose pose = new Pose();
        float x = Float.parseFloat(data[2]);
        float y = Float.parseFloat(data[3]);
        Vector3 position = new Vector3(x, y, 10f);
        float w = Float.parseFloat(data[6]);
        Quaternion orientation = new Quaternion(0f, 0f, 1f, w);
        pose.setPosition(position);
        pose.setOrientation(orientation);

        cube.setColor(color);
        cube.setSize(size);
        cube.setPose(pose);
        List<CubePrimitive> cubeList = Arrays.asList(cube);
        return cubeList;
    }

    public static Color setCubeColor(int type) {
        switch (type) {
            case 0:
                return new Color(0.1843137254901961f, 0.30980392156862746f, 0.30980392156862746f, 0.5f);
            case 1:
                return new Color(0.1137254901960784f, 0.0784313725490196f, 0.1f, 0.58f);
            case 2:
                return new Color(0f, 0f, 0.9019607843137255f, 0.5f);
            default:
                return new Color(1f, 0.38823529411764707f, 0.2784313725490196f, 0.5f);
        }
    }
}
