package com.example.yuanping.bike.thread;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yuanping on 4/2/18.
 * 放车,包括数据库查询,车位下降
 */

public class PutThread extends FatherThread {
    public PutThread(Context context, String phoneNumber) {
        super(context, phoneNumber);
    }

    @Override
    public void run() {
        super.run();
        handler.sendEmptyMessage(handler.SHOW_DIALOG);
        try {
            //需要查询数据库中是否已经存放了自行车,根据电话号码查询
            if (connection == null || connection.isClosed()) {
                createNewConnection();
            }
            ResultSet resultSet = statement.executeQuery("select * from user where phone=" + phoneNumber);
            //如果已经存放了,默认只能存放一辆自行车
            if (resultSet.next()) {
                handler.sendEmptyMessage(handler.REPEAT_PUT);
            } else {
                //如果没有存放,那么分配车位
                ResultSet temp = statement.executeQuery("select * from user");
                int[] positions = new int[]{-1, -1, -1, -1, -1, -1};
                while (temp.next()) {
                    String position = temp.getString("position");
                    positions[Integer.parseInt(position)] = Integer.parseInt(position);
                }
                int i;
                for (i = 0; i < positions.length; i++) {
                    if (positions[i] <= 0) {
                        break;
                    }
                }
                //遍历出一个空车位
                //TODO 算法改进,避免重心失衡,车位从0号开始吗??
                if (i < 6) {
                    connectionDown(i + "",true);
                    Log.d("@HusterYP", String.valueOf("分配的车位为： " + i));
                } else {
                    handler.sendEmptyMessage(handler.NO_EMPTY);
                }
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
