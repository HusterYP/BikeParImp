package com.example.yuanping.bike.thread;

import android.content.Context;

import java.sql.SQLException;

/**
 * Created by yuanping on 4/1/18.
 * 取车完成,因为相对于放车完成过程的区别是数据库操作不同,所以这里还需要单独封装
 */

public class GetBikeOver extends OverThread {
    public GetBikeOver(Context context, String phone) {
        super(context, phone);
    }

    @Override
    public void run() {
        handler.sendEmptyMessage(handler.SHOW_DIALOG);
        super.run();
        if (connection == null) {
            createNewConnection();
        }
        //将对应数据库中的数据删除(或者更新)
        try {
            statement.execute("delete from user where phone=" + phoneNumber);
        } catch (Exception e) {
        } finally {
            handler.sendEmptyMessage(handler.OVER);
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    connection = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
