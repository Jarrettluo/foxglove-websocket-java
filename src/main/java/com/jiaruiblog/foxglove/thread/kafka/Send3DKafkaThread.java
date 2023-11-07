package com.jiaruiblog.foxglove.thread.kafka;

import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.foxglove.kafka.deserializer.SceneUpdateDeserializer;
import com.jiaruiblog.foxglove.schema.ModelPrimitive;
import com.jiaruiblog.foxglove.schema.SceneEntity;
import com.jiaruiblog.foxglove.schema.SceneUpdate;
import com.jiaruiblog.foxglove.schema.Timestamp;
import com.jiaruiblog.foxglove.thread.SendDataThread;
import com.jiaruiblog.foxglove.util.KafkaUtil;
import com.jiaruiblog.foxglove.util.SceneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.yeauty.pojo.Session;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.jiaruiblog.foxglove.util.DataUtil.getFormattedBytes;

@Slf4j
public class Send3DKafkaThread extends SendDataThread {

    private String topic;
    private String group;

    public Send3DKafkaThread(int index, int frequency, Session session,String topic,String group) {
        super(index, frequency, session);
        this.topic = topic;
        this.group = group;
    }

    @Override
    public void run() {
        Properties props = KafkaUtil.getConsumerProperties(group, SceneUpdateDeserializer.class.getName());
        List<ModelPrimitive> models = SceneUtil.addModels();
        try (KafkaConsumer<String, SceneUpdate> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList(topic));
            boolean validCode = StringUtils.isNotBlank(code);
            int i = 0;
            while (running) {
                ConsumerRecords<String, SceneUpdate> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, SceneUpdate> record : records) {
                    SceneUpdate update = record.value();
                    if (validCode) {
                        update.setChassisCode(code);
                    }
                    // 减少车辆模型的显示频率
                    if (i == 5) {
                        i = 0;
                        List<SceneEntity> entities = update.getEntities();
                        Timestamp timestamp = entities.get(0).getTimestamp();
                        SceneEntity carEntity = SceneUtil.createEntity("drive_car", "obstacle", "vehicle.car", timestamp);
                        carEntity.setModels(models);
                        entities.add(carEntity);
                    }
                    i++;
                    JSONObject jsonObject = (JSONObject) JSONObject.toJSON(update);
                    byte[] bytes = getFormattedBytes(jsonObject.toJSONString().getBytes(), index);
                    this.session.sendBinary(bytes);
                    Thread.sleep(frequency);
                    printLog();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("--------------------Kafka线程已经停止: " + Thread.currentThread().getName());
    }
}
