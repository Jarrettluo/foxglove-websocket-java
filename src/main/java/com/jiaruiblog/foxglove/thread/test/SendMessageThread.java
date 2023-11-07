package com.jiaruiblog.foxglove.thread.test;

import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.foxglove.thread.SendDataThread;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

import java.time.LocalTime;
import java.util.Random;

import static com.jiaruiblog.foxglove.util.DataUtil.getFormattedBytes;

@Slf4j
public class SendMessageThread extends SendDataThread {

    public SendMessageThread(int index, int frequency, Session session) {
        super(index, frequency, session);
    }

    @Override
    public void run() {

        while (running) {
            JSONObject jsonObject = new JSONObject();
            String message = "Hello at " + LocalTime.now();
            if (code != null) {
                message += " from " + code;
            }
            jsonObject.put("msg", message);

            jsonObject.put("count", new Random().nextInt(1000));
            jsonObject.put("number", new Random().nextInt(1000));

            byte[] bytes = getFormattedBytes(jsonObject.toJSONString().getBytes(), index);
            this.session.sendBinary(bytes);
            printLog(100);
            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("-----------------" + Thread.currentThread().getName() + "结束运行-------------------");

    }
}
