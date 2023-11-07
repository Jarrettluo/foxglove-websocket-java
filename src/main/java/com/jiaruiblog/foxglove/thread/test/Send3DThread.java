package com.jiaruiblog.foxglove.thread.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.foxglove.schema.*;
import com.jiaruiblog.foxglove.thread.SendDataThread;
import com.jiaruiblog.foxglove.util.SceneUtil;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jiaruiblog.foxglove.util.DataUtil.getFormattedBytes;

@Slf4j
public class Send3DThread extends SendDataThread {

    private List<SceneUpdate> updateList;
    private List<ModelPrimitive> models;
    private List<String> oldIdList = new ArrayList<>();

    public Send3DThread(int index, int frequency, Session session) {
        super(index, frequency, session);
        updateList = this.readEntityData();
        models = SceneUtil.addModels();
    }

    private List<SceneUpdate> readEntityData() {
        List<SceneUpdate> updateList = new ArrayList<>();
        String file = "E:\\foxglove\\obstacle_data.data";
        String str;
        String oldTs = null;
        List<SceneEntity> entityList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((str = br.readLine()) != null) {
                String[] data = str.split(",");

                String currentTs = data[0];
                if (!currentTs.equals(oldTs)) {
                    if (entityList.size() > 0) {
                        SceneUpdate sceneUpdate = new SceneUpdate();
                        sceneUpdate.setEntities(entityList);
                        updateList.add(sceneUpdate);
                    }
                    entityList = new ArrayList<>();
                    oldTs = currentTs;
                }

                SceneEntity entity = SceneUtil.createEntity(data[1], "obstacle", "vehicle.truck", null);
                entity.setCubes(SceneUtil.addCubes(data));

                entityList.add(entity);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("==============解析完毕，共有" + updateList.size() + "条点云记录======================");
        return updateList;
    }

    @Override
    public void run() {
        int i = 0, size = updateList.size();
        while (running) {
            if (i >= size) {
                i = 0;
                log.info("==============播放完毕，新的轮回======================");
            }
            SceneUpdate sceneUpdate = updateList.get(i);
            List<SceneEntity> entities = sceneUpdate.getEntities();
            Timestamp timestamp = entities.get(0).getTimestamp();
            List<String> newIdList = entities.stream().map(SceneEntity::getId).collect(Collectors.toList());
            oldIdList.removeAll(newIdList);
            if (oldIdList.size() > 0) {
                List<SceneEntityDeletion> deleteList = oldIdList.stream().map(s -> {
                    SceneEntityDeletion del = new SceneEntityDeletion();
                    del.setId(s);
                    del.setType(0);
                    del.setTimestamp(timestamp);
                    return del;
                }).collect(Collectors.toList());
                sceneUpdate.setDeletions(deleteList);
            }

            // 减少车辆模型的显示频率
            if (i % 5 == 0) {
                SceneEntity carEntity = SceneUtil.createEntity("drive_car", "obstacle", "vehicle.car", timestamp);
                carEntity.setModels(models);
                entities.add(carEntity);
            }

            oldIdList = newIdList;
            i++;
            printLog(100);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(sceneUpdate);
            byte[] bytes = getFormattedBytes(jsonObject.toJSONString().getBytes(), index);
            this.session.sendBinary(bytes);
            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
