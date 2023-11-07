package com.jiaruiblog.foxglove.thread.bak;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.foxglove.schema.PackedElement;
import com.jiaruiblog.foxglove.schema.PointCloud;
import com.jiaruiblog.foxglove.schema.Timestamp;
import com.jiaruiblog.foxglove.thread.SendDataThread;
import com.jiaruiblog.foxglove.util.DataUtil;
import com.jiaruiblog.foxglove.util.DateUtil;
import com.jiaruiblog.foxglove.util.PointCloudUtil;
import org.apache.commons.lang3.StringUtils;
import org.yeauty.pojo.Session;

import java.util.Base64;

import static com.jiaruiblog.foxglove.util.DataUtil.getFormattedBytes;

public class SendPointCloudThread extends SendDataThread {

    public SendPointCloudThread(int index, int frequency, Session session) {
        super(index, frequency, session);
    }

    @Override
    public void run() {
        int i = 1;
        int MAX_COUNT = 100;
        int MAX_LENGTH = 6;
        String folder = "E:\\foxglove\\lidar\\lidar_main\\";
        while (true) {
            if (i > MAX_COUNT) {
                i = 1;
            }
            String fileName = StringUtils.leftPad(String.valueOf(i * 5), MAX_LENGTH, "0") + ".pcd";
            fileName = folder + fileName;
            PointCloud pointCloud = createPointCloud(fileName);

            JSONObject jsonObject = (JSONObject) JSON.toJSON(pointCloud);
            byte[] bytes = DataUtil.getFormattedBytes(jsonObject.toJSONString().getBytes(), pointCloud.timestamp.getNsec(), index);
            this.session.sendBinary(bytes);
            i++;
            printLog(100);
            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private PointCloud createPointCloud(String file) {

        PointCloud pointCloud = new PointCloud();
        pointCloud.setPointStride(16);
        pointCloud.setFrameId("LIDAR_MAIN");

        Timestamp timestamp = DateUtil.createTimestamp();
        pointCloud.setTimestamp(timestamp);

        PackedElement[] fields = new PackedElement[4];
        fields[0] = new PackedElement("x", 0, 7);
        fields[1] = new PackedElement("y", 4, 7);
        fields[2] = new PackedElement("z", 8, 7);
        fields[3] = new PackedElement("intensity", 12, 2);
        pointCloud.setFields(fields);

        byte[] bytes = PointCloudUtil.readFile(file);
        byte[] encode = Base64.getEncoder().encode(bytes);
        String data = new String(encode);
        pointCloud.setData(data);

        return pointCloud;
    }
}
