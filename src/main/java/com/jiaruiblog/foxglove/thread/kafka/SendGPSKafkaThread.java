package com.jiaruiblog.foxglove.thread.kafka;

import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.foxglove.kafka.deserializer.LocationFixDeserializer;
import com.jiaruiblog.foxglove.schema.LocationFix;
import com.jiaruiblog.foxglove.thread.SendDataThread;
import com.jiaruiblog.foxglove.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.yeauty.pojo.Session;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static com.jiaruiblog.foxglove.util.DataUtil.getFormattedBytes;

@Slf4j
public class SendGPSKafkaThread extends SendDataThread {

    private String topic;
    private String group;

    public SendGPSKafkaThread(int index, int frequency, Session session, String topic, String group) {
        super(index, frequency, session);
        this.topic = topic;
        this.group = group;
    }

    @Override
    public void run() {
        Properties props = KafkaUtil.getConsumerProperties(group, LocationFixDeserializer.class.getName());
        try (KafkaConsumer<String, LocationFix> consumer = new KafkaConsumer<>(props)) {
            TopicPartition partition = new TopicPartition(topic, 0);
            consumer.assign(Arrays.asList(partition));
            if (StringUtils.isBlank(code)) {
                code = "10000";
            }
            while (running) {
                ConsumerRecords<String, LocationFix> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, LocationFix> record : records) {
                    LocationFix gps = record.value();
                    if (!code.equals(gps.getChassisCode())) {
                        continue;
                    }
                    JSONObject jsonObject = (JSONObject) JSONObject.toJSON(gps);
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
