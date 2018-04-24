package com.example.yuanping.bike.test;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.yuanping.bike.thread.GradFatherThread;

import java.sql.ResultSet;

/**
 * Created by yuanping on 4/23/18.
 * 测试: 查询车位信息
 */

public class TestGetPosition extends GradFatherThread {
    public TestGetPosition(Context context, String phoneNumber) {
        super(context, phoneNumber);
    }

    @Override
    public void run() {
        super.run();
        handler.sendEmptyMessage(handler.SHOW_DIALOG);
        try {
            if (connection == null || connection.isClosed()) {
                createNewConnection();
            }
            statement.execute("delete from user");
            String result = "有车: " + System.lineSeparator();
            int temp[] = new int[]{-1, -1, -1, -1, -1, -1};
            ResultSet resultSet = statement.executeQuery("select * from user");
            while (resultSet.next()) {
                result += resultSet.getString("position") + "(" + resultSet.getString("phone") +
                        ")" + System.lineSeparator();
                temp[Integer.parseInt(resultSet.getString("position"))] = Integer.parseInt
                        (resultSet.getString("position"));
            }
            result += "无车: " + System.lineSeparator();
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] <= 0) {
                    result += i + " , ";
                }
            }
            Message msg = new Message();
            msg.what = handler.TEST_POSITION;
            msg.obj = result;
            handler.sendMessage(msg);
        } catch (Exception e) {
            Log.d("@HusterYP", String.valueOf(e));
        } finally {
            handler.sendEmptyMessage(handler.OVER);
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (Exception e) {
                }
            }
        }
    }
}
