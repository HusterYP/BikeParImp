package com.example.yuanping.bike.test;

import android.content.Context;
import android.util.Log;

import com.example.yuanping.bike.thread.GradFatherThread;

/**
 * Created by yuanping on 4/23/18.
 * 测试: 清除数目库内容
 */

public class TestClearSQLThread extends GradFatherThread {
    public TestClearSQLThread(Context context, String phoneNumber) {
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
