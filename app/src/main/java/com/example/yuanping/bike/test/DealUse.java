package com.example.yuanping.bike.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.yuanping.bike.Constant;
import com.example.yuanping.bike.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 建表语句: create table user(phone varchar(40),position varchar(2)),flag varchar(10)
 * 处理取车和放车事件
 */
public class DealUse extends AppCompatActivity {

    private final String url = "jdbc:mysql://123.207.8.133:3306/AutoBike";
    private final String HASBIKE = "hasBike";
    private final String YES = "yes";
    private final String NO = "no";
    private final int NO_BIKE = 0; //没有存放自行车
    private final int HAS_BIKE = 1; //已经存放了自行车
    private final int DEVICE_NOT_ONLINE = 2; //设备不在线
    private final int NO_POSITTION = 3;// 没有空车位
    private final String DEVICE_ID = "D4471";
    private String phone = "";
    private ProgressDialog dialog = null;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            switch (msg.what) {
                case NO_BIKE: {
                    toast("还没有存放自行车哦!");
                    break;
                }
                case HAS_BIKE: {
                    toast("只能存放一辆自行车哦!");
                    break;
                }
                case DEVICE_NOT_ONLINE: {
                    toast("设备连接失败啦!请稍后再试哦!");
                    break;
                }
                case NO_POSITTION: {
                    toast("没有空车位啦!");
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_deal_use);
        phone = getIntent().getStringExtra(Constant.PHONE);
    }

    public void getBike(View view) {
        dialogShow();
        final String phoneNumber = phone;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Connection connection = null;
                try {
                    if (chechHasBike()) {
                        Class.forName("com.mysql.jdbc.Driver");
                        connection = DriverManager.getConnection(url, "AutoBike", "yuanping666");
                        Statement statement = connection.createStatement
                                (ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet resultSet = statement.executeQuery("select * from user where phone=" + phoneNumber);
                        //查找到
                        if (resultSet.next()) {
                            String position = resultSet.getString("position");
                            if (!TextUtils.isEmpty(position)) {
                                connectionGetBike(position);
                            }
                        }
                    } else {
                        Message msg = new Message();
                        msg.what = NO_BIKE;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    public void putBike(View view) {
        dialogShow();
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (chechHasBike()) {
                    Message msg = new Message();
                    msg.what = HAS_BIKE;
                    handler.sendMessage(msg);
                }
                connectionPutBike();
            }
        }.start();
    }

    private void connectionGetBike(String position) {
        Socket socket = null;
        OutputStream out = null;
        InputStream inputStream = null;
        try {
            socket = new Socket("www.bigiot.net", 8282);
            JSONObject json = new JSONObject();
            json.put("M", "checkin");
            json.put("ID", "4551");
            json.put("K", "03af921df");
            out = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(json.toString() + "\n");
            writer.flush();
            inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String result = reader.readLine();
                JSONObject object = new JSONObject(result);
                //登录成功,发送取车命令
                if (object.get("M").equals("checkinok")) {
                    JSONObject getBike = new JSONObject();
                    getBike.put("M", "say");
                    getBike.put("C", "DOWN");
                    getBike.put("ID", object.get("ID"));
                    getBike.put("SIGN", position + "");
                    writer.write(getBike.toString() + "\n");
                    writer.flush();
                    Log.d("@HusterYP", String.valueOf("取车命令发送完成!"));
                }
                //TODO 接收到车位下来的信息后,取消等待框,然后删除数据库中信息
                //具体格式需要再协商
                if (object.get("M").equals("say")) {

                }
                //发送心跳包
                if (object.get("M").equals("b")) {
                    JSONObject beat = new JSONObject();
                    beat.put("M", "beat");
                }
                Log.d("@HusterYP", String.valueOf(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void connectionPutBike() {
        Socket socket = null;
        OutputStream out = null;
        InputStream inputStream = null;
        Connection connection = null;
        try {
            socket = new Socket("www.bigiot.net", 8282);
            JSONObject json = new JSONObject();
            json.put("M", "checkin");
            json.put("ID", "4551");
            json.put("K", "03af921df");
            out = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(json.toString() + "\n");
            writer.flush();
            inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String result = reader.readLine();
                JSONObject object = new JSONObject(result);
                Log.d("@HusterYP", String.valueOf(result));
                if (object.get("M").equals("checkinok")) {
                    JSONObject check = new JSONObject();
                    check.put("M", "isOL");
                    check.put("ID", DEVICE_ID);//TODO D4471
                    writer.write(check.toString() + "\n");
                    writer.flush();
                }
                /*if (object.get("M").equals("isOL")) {
                    //TODO 根据取得车位发送存车信息
                    JSONObject put = new JSONObject();
                    put.put("M", "say");
                    put.put("ID", DEVICE_ID);
                    put.put("C", "DOWN");
                    put.put("SIGN", 1 + "");
                    writer.write(put.toString() + "\n");
                    writer.flush();
                }*/

                //检查设备是否在线
                if (object.get("M").equals("isOL")) {
                    String res = ((JSONObject) (object.get("R"))).getString(DEVICE_ID);
                    //设备在线,分配车位
                    if (res.equals("1")) {
                        Class.forName("com.mysql.jdbc.Driver");
                        connection = DriverManager.getConnection(url, "AutoBike", "yuanping666");
                        Statement statement = connection.createStatement
                                (ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet resultSet = statement.executeQuery("select * from user");
//                        int position = getPosition(resultSet); //TODO bug
                        int position = 0;
                        if (position < 0) {
                            Message msg = new Message();
                            msg.what = NO_POSITTION;
                            handler.sendMessage(msg);
                        } else {
                            //TODO 根据取得车位发送存车信息
                            JSONObject put = new JSONObject();
                            put.put("M", "say");
                            put.put("ID", DEVICE_ID);
                            put.put("C", "UP");
                            put.put("SIGN", position + "");
                            writer.write(put.toString() + "\n");
                            writer.flush();
                        }
                    } else {
                        Message msg = new Message();
                        msg.what = DEVICE_NOT_ONLINE;
                        handler.sendMessage(msg);
                    }
                }
                //TODO 接受放车成功的信息
                if (object.get("M").equals("say")) {

                }
                //发送心跳包
                if (object.get("M").equals("b")) {
                    JSONObject beat = new JSONObject();
                    beat.put("M", "beat");
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
            }
        }
    }

    //获取当前应该存储的车位
    //算法还需要改进,现在的存储方式是顺序存储,可能会考虑重心等问题
    private int getPosition(ResultSet resultSet) {
        int[] positions = {-1, -1, -1, -1, -1, -1};
        try {
            while (resultSet.next()) {
                int temp = Integer.parseInt(resultSet.getString("position"));
                positions[temp] = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int result = -1;
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] < 0) {
                result = i;
                break;
            }
        }
        return result;
    }

    //本地检查是否取放车
    private void writeToPreference(boolean hasBike) {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (hasBike) {
            editor.putString(HASBIKE, YES);
        } else {
            editor.putString(HASBIKE, NO);
        }
        editor.commit();
    }

    private boolean chechHasBike() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        String hasBike = preferences.getString(HASBIKE, "");
        if (TextUtils.isEmpty(hasBike) || hasBike.equals(NO)) {
            return false;
        } else if (hasBike.equals(YES)) {
            return true;
        }
        return false;
    }

    private void toast(String msg) {
        Toast.makeText(getApplication(), String.valueOf(msg), Toast.LENGTH_SHORT).show();
    }

    private void dialogShow() {
        if (dialog == null) {
            dialog = Constant.showSpinnerDialog(this, "请稍后...");
        } else {
            dialog.show();
        }
    }

    //测试
    public void Connect() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Connection connection = null;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(url, "AutoBike", "yuanping666");
                    Statement statement = connection.createStatement
                            (ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    String query = "select * from test";
                    statement.execute("insert into test (id,name) values ('1','HusterYP')");
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        Log.d("@HusterYP", String.valueOf(resultSet.getString("id") +
                                resultSet.getString("name")));
                    }
                    Log.d("@HusterYP", String.valueOf("Over!"));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();
    }
}
