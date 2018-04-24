package com.example.yuanping.bike.thread;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by yuanping on 3/31/18.
 * 发送抬升车位的信息,这是取车完成和放车完成之后都要做的事情
 */

public class OverThread extends GradFatherThread {
    public OverThread(Context context, String phone) {
        super(context, phone);
    }

    @Override
    public void run() {
        super.run();
        if (socket == null || socket.isClosed()) {
            createNewSocket();
        }
        sendOverMsg();
    }

    //发送抬升车位信息
    private void sendOverMsg() {
        if (socket == null || socket.isClosed()) {
            createNewSocket();
        }
        try {
            JSONObject up = new JSONObject();
            up.put("M", "say");
            up.put("C", "UP");
            writer.write(up.toString() + "\n");
            writer.flush();
        } catch (Exception e) {
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
