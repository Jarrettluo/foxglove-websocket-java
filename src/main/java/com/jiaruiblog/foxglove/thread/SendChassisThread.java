package com.jiaruiblog.foxglove.thread;

import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.foxglove.schema.ChassisInfo;
import com.jiaruiblog.foxglove.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

import static com.jiaruiblog.foxglove.util.DataUtil.getFormattedBytes;

@Slf4j
public class SendChassisThread extends SendDataThread {

    public SendChassisThread(int index, int frequency, Session session) {
        super(index, frequency, session);
    }

    @Override
    public void run() {
        ChassisInfo chassis = new ChassisInfo();
        chassis.setTimestamp(DateUtil.createTimestamp());
        chassis.setChassisCode(code);
        chassis.setRtspUrl(getRtspURL());
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(chassis);
        byte[] bytes = getFormattedBytes(jsonObject.toJSONString().getBytes(), index);
        this.session.sendBinary(bytes);
        log.info("---------------chassis info:\t" + chassis);
    }

    private String getRtspURL() {
        String url = "http://127.0.0.1:8888/demo";
        if (code.equals("10000")) {
            url += "1";
        } else {
            url += "2";
        }
        return url;
    }
}
