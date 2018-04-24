package com.example.yuanping.bike.test;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.yuanping.bike.thread.GradFatherThread;

import java.sql.ResultSet;

/**
 * Created by yuanping on 4/2/18.
 * 数据库内容测试
 */

public class TestSQLThread extends GradFatherThread {
    public TestSQLThread(Context context, String phoneNumber) {
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
//            statement.execute("insert into user (phone,position) values ('18787777790','1')");
//            statement.execute("delete from user where phone=18787777790");
            String result = "";
            ResultSet resultSet = statement.executeQuery("select * from user");
            while (resultSet.next()) {
                result += "电话=" + resultSet.getString("phone") + " : " + "车位=" + resultSet
                        .getString("position") + System.lineSeparator();
            }
            if (TextUtils.isEmpty(result)) {
                result = "数据库中没有数据啦!";
            }
            Log.d("@HusterYP", String.valueOf(result));
            Message msg = new Message();
            msg.obj = result;
            msg.what = handler.TEST_SQL;
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
