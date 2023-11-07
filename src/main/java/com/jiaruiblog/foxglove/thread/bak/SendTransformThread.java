package com.jiaruiblog.foxglove.thread.bak;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.foxglove.schema.FrameTransform;
import com.jiaruiblog.foxglove.schema.Quaternion;
import com.jiaruiblog.foxglove.schema.Timestamp;
import com.jiaruiblog.foxglove.schema.Vector3;
import com.jiaruiblog.foxglove.util.DateUtil;
import org.yeauty.pojo.Session;

import static com.jiaruiblog.foxglove.util.DataUtil.getFormattedBytes;

@Deprecated
public class SendTransformThread implements Runnable {

    private int frequency;
    private int index;
    private Session session;

    public SendTransformThread(int index, int frequency, Session session) {
        this.index = index;
        this.session = session;
        this.frequency = frequency;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            i = i > 10 ? 0 : i;
            i++;
            Timestamp timestamp = DateUtil.createTimestamp();

            FrameTransform transform = new FrameTransform();
            transform.setTimestamp(timestamp);
            transform.setChildFrameId("obstacle");
            transform.setParentFrameId("car");

            Quaternion rotation = new Quaternion(1f, 0f, 1f, 1f + i / 10);
            transform.setRotation(rotation);

            Vector3 translation = new Vector3(1f, 0f + i, 0f + i);
            transform.setTranslation(translation);

            JSONObject jsonObject = (JSONObject) JSON.toJSON(transform);

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
