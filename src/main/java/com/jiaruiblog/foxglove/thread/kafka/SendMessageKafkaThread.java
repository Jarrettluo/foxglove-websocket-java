package com.jiaruiblog.foxglove.thread.kafka;

import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.foxglove.kafka.deserializer.RawMessageDeserializer;
import com.jiaruiblog.foxglove.schema.RawMessage;
import com.jiaruiblog.foxglove.thread.SendDataThread;
import com.jiaruiblog.foxglove.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.yeauty.pojo.Session;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static com.jiaruiblog.foxglove.util.DataUtil.getFormattedBytes;

@Slf4j
public class SendMessageKafkaThread extends SendDataThread {

    private String topic;
    private String group;

    public SendMessageKafkaThread(int index, int frequency, Session session, String topic, String group) {
        super(index, frequency, session);
        this.topic = topic;
        this.group = group;
    }

    @Override
    public void run() {
        Properties props = KafkaUtil.getConsumerProperties(group, RawMessageDeserializer.class.getName());
        try (KafkaConsumer<String, RawMessage> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList(topic));
            boolean validCode = StringUtils.isNotBlank(code);
            while (running) {
                ConsumerRecords<String, RawMessage> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, RawMessage> record : records) {
                    RawMessage message = record.value();
                    if (validCode) {
                        message.setChassisCode(code);
                    }
                    JSONObject jsonObject = (JSONObject) JSONObject.toJSON(message);
                    byte[] bytes = getFormattedBytes(jsonObject.toJSONString().getBytes(), index);
                    this.session.sendBinary(bytes);
                    Thread.sleep(frequency);
                    super.printLog();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("--------------------Kafka线程已经停止: " + Thread.currentThread().getName());
    }
}
