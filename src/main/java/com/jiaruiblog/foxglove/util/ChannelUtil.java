package com.jiaruiblog.foxglove.util;

import com.jiaruiblog.foxglove.entity.ChannelInfo;
import com.jiaruiblog.foxglove.thread.SendChassisThread;
import com.jiaruiblog.foxglove.thread.SendDataThread;
import com.jiaruiblog.foxglove.thread.SendImageThread;
import com.jiaruiblog.foxglove.thread.kafka.Send3DKafkaThread;
import com.jiaruiblog.foxglove.thread.kafka.SendGPSKafkaThread;
import com.jiaruiblog.foxglove.thread.kafka.SendMessageKafkaThread;
import com.jiaruiblog.foxglove.thread.test.Send3DThread;
import com.jiaruiblog.foxglove.thread.test.SendGPSThread;
import com.jiaruiblog.foxglove.thread.test.SendMessageThread;
import org.yeauty.pojo.Session;

import java.util.ArrayList;
import java.util.List;

public class ChannelUtil {

    public static List<ChannelInfo> createChannels() {
        String schema = null;

        ChannelInfo channelChassis = new ChannelInfo();
        channelChassis.setId(0);
        channelChassis.setTopic("/drive/chassis_code");
        channelChassis.setEncoding("json");
        channelChassis.setSchemaName("底盘编码(用于切换播放源)");
        channelChassis.setSchema("{\"type\": \"object\", \"properties\": {\"chassis_code\": {\"type\": \"string\"}, \"rtsp_url\": {\"type\": \"string\"}}}");
        channelChassis.setSchemaEncoding("jsonschema");

        ChannelInfo channelMessage = new ChannelInfo();
        channelMessage.setId(1);
        channelMessage.setTopic("/drive/raw_msg");
        channelMessage.setEncoding("json");
        channelMessage.setSchemaName("纯文字消息");
        channelMessage.setSchema("{\"type\": \"object\", \"properties\": {\"msg\": {\"type\": \"string\"}, \"count\": {\"type\": \"number\"}}}");
        channelMessage.setSchemaEncoding("jsonschema");

        ChannelInfo channel3D = new ChannelInfo();
        channel3D.setId(2);
        channel3D.setTopic("/drive/3D");
        channel3D.setEncoding("json");
        channel3D.setSchemaName("foxglove.SceneUpdate");
        schema = DataUtil.loadJsonSchema("SceneUpdate.json");
        channel3D.setSchema(schema);
        channel3D.setSchemaEncoding("jsonschema");

        ChannelInfo channelGPS = new ChannelInfo();
        channelGPS.setId(3);
        channelGPS.setTopic("/drive/gps");
        channelGPS.setEncoding("json");
        channelGPS.setSchemaName("foxglove.LocationFix");
        schema = DataUtil.loadJsonSchema("LocationFix.json");
        channelGPS.setSchema(schema);
        channelGPS.setSchemaEncoding("jsonschema");

        ChannelInfo channelImage = new ChannelInfo();
        channelImage.setId(4);
        channelImage.setTopic("/drive/image");
        channelImage.setEncoding("json");
        channelImage.setSchemaName("foxglove.CompressedImage");
        schema = DataUtil.loadJsonSchema("CompressedImage.json");
        channelImage.setSchema(schema);
        channelImage.setSchemaEncoding("jsonschema");


        List<ChannelInfo> channelList = new ArrayList<>();
        channelList.add(channelChassis);
        channelList.add(channelMessage);
        channelList.add(channel3D);
        channelList.add(channelGPS);
        channelList.add(channelImage);
        return channelList;
    }

    public static SendDataThread getTestSendThread(int id, int channelId, int frequency, Session session) {
        switch (channelId) {
            case 0:
                return new SendChassisThread(id, frequency, session);
            case 1:
                return new SendMessageThread(id, frequency, session);
            case 2:
                return new Send3DThread(id, frequency, session);
            case 3:
                return new SendGPSThread(id, frequency, session);
            case 4:
                String rtsp = "rtsp://127.0.0.1:8554/demo1";
                return new SendImageThread(id, frequency, session, rtsp);
            default:
                return null;
        }
    }

    public static SendDataThread getKafkaSendThread(int id, int channelId, int frequency, Session session) {
        String group = "test-group-1";
        switch (channelId) {
            case 0:
                return new SendChassisThread(id, frequency, session);
            case 1:
                return new SendMessageKafkaThread(id, frequency, session, "raw_message", group);
            case 2:
                return new Send3DKafkaThread(id, frequency, session, "drive_3d", group);
            case 3:
                return new SendGPSKafkaThread(id, frequency, session, "drive_gps_mixed", group);
            case 4:
                String rtsp = "rtsp://127.0.0.1:8554/demo1";
                return new SendImageThread(id, frequency, session, rtsp);
            default:
                return null;
        }
    }
}
