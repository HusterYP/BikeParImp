package com.example.yuanping.bike.thread;

import android.content.Context;
import android.text.TextUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yuanping on 3/31/18.
 * 取车: 直到车位下降
 */

public class GetThread extends FatherThread {

    public GetThread(Context context, String phoneNumber) {
        super(context, phoneNumber);
    }

    @Override
    public void run() {
        super.run();
        //开启等待框
        handler.sendEmptyMessage(handler.SHOW_DIALOG);
        try {
            //检查是否存放了自行车,是在数据库中查询是否有对应项
                //根据电话号码从数据库查询存放车位
                if (connection == null || connection.isClosed()) {
                    createNewConnection();
                }
                ResultSet resultSet = statement.executeQuery("select * from user where phone=" + phoneNumber);
                //查找到
                if (resultSet.next()) {
                    String position = resultSet.getString("position");
                    if (!TextUtils.isEmpty(position)) {
                        connectionDown(position,false);
                    }
                } else {
                    //没有存放过自行车就提醒
                    handler.sendEmptyMessage(handler.NO_BIKE);
                }
        } catch (Exception e) {
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
