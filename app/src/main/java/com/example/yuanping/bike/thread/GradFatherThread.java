package com.example.yuanping.bike.thread;

import android.content.Context;

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
import java.sql.Statement;

/**
 * Created by yuanping on 3/31/18.
 * 线程公共部分的再次抽取
 */

public class GradFatherThread extends Thread {
    public Socket socket = null;
    public BufferedReader reader;
    public BufferedWriter writer;
    public MyHandler handler;
    public Context context;
    public Connection connection = null;
    private final String url = "jdbc:mysql://123.207.8.133:3306/AutoBike";
    public Statement statement = null;
    public String phoneNumber;

    public GradFatherThread(Context context, String phoneNumber) {
        this.context = context;
        this.phoneNumber = phoneNumber;
        handler = new MyHandler(context);
    }

    @Override
    public void run() {
        super.run();
        createNewSocket();
    }

    //重新建立和远程数据库的连接
    public void createNewConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(url, "AutoBike", "yuanping666");
                statement = connection.createStatement
                        (ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            } catch (Exception e) {
            }
        }
    }

    //重新建立和物联网平台连接
    public void createNewSocket() {
        try {
            socket = new Socket("www.bigiot.net", 8282);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            writer = new BufferedWriter(new OutputStreamWriter(out));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
