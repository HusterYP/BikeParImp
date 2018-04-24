package com.example.yuanping.bike.thread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.yuanping.bike.Constant;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by yuanping on 3/31/18.
 * 线程的公共部分抽取
 */

public class FatherThread extends GradFatherThread {

    public ProgressDialog dialog;
    public final String HASBIKE = "hasBike";
    public final String YES = "yes";
    public final String NO = "no";
    private String dialog_msg = "请稍后...";
    public final String DEVICE_ID = "D4471";

    public FatherThread(Context context, String phoneNumber) {
        super(context, phoneNumber);
        dialog = Constant.showSpinnerDialog((Activity) context, dialog_msg);
    }

    //不在本地存储是否存放了自行车,直接在数据库中查询是否有对应项
    public boolean chechHasBike() {
        SharedPreferences preferences = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
        String hasBike = preferences.getString(HASBIKE, "");
        if (TextUtils.isEmpty(hasBike) || hasBike.equals(NO)) {
            return false;
        } else if (hasBike.equals(YES)) {
            return true;
        }
        return false;
    }

    //使车位下降,这里添加的isPut字段,是为了使取车和放车可以公用该方法,isPut是用户放车时插入数据库的
    public void connectionDown(String position, boolean isPut) {
        try {
            //登录设备
            //这里将设备Socket抽取出来,只是用一个,这是为了防止同一用户(一次过程中)多次登录
            if (socket == null || socket.isClosed()) {
                createNewSocket();
            }
            JSONObject json = new JSONObject();
            json.put("M", "checkin");
            json.put("ID", "4551");
            json.put("K", "03af921df");
            writer.write(json.toString() + "\n");
            writer.flush();
            while (true) {
                String result = reader.readLine();
                JSONObject object = new JSONObject(result);
                Log.d("@HusterYP", String.valueOf("取车: " + result));
                //如果登录成功
                if (object.get("M").equals("checkinok")) {
                    //先查询设备是否在线
                    JSONObject check = new JSONObject();
                    check.put("M", "isOL");
                    check.put("ID", DEVICE_ID);
                    writer.write(check.toString() + "\n");
                    writer.flush();
                }
                //检查设备在线信息
                if (object.get("M").equals("isOL")) {
                    String res = ((JSONObject) (object.get("R"))).getString(DEVICE_ID);
                    if (res.equals("1")) {
                        //设备在线,发送指令 `{"M":"say","C":"DOWN","SIGN":"车位号"}`,车位下降
                        JSONObject down = new JSONObject();
                        down.put("M", "say");
                        down.put("C", "DOWN");
                        down.put("SIGN", position);
                        writer.write(down.toString() + "\n");
                        writer.flush();
                    } else {
                        //注意当设备不在线时,就应该退出while循环,防止用户再次点击出错
                        handler.sendEmptyMessage(handler.DEVICE_OFFILINE);
                        break;
                    }
                }
                //如果上面发送了车位下降的信息,这里接收到车位下降完成的信息
                if (object.get("M").equals("say")) {
                    if (object.get("C").equals("1")) {
                        if(isPut){
                            handler.sendEmptyMessage(handler.DOWN_PUT_COMPLETE);
                        }else{
                            handler.sendEmptyMessage(handler.DOWN_GET_COMPLETE);
                        }
                        //放车阶段插入数据库
                        if (isPut) {
                            saveData(position);
                        }
                        //注意这里需要退出while循环,防止用户再次点击出错
                        break;
                    }
                }
                //发送心跳包
                if (object.get("M").equals("b")) {
                    JSONObject beat = new JSONObject();
                    beat.put("M", "beat");
                    writer.write(beat.toString() + "\n");
                    writer.flush();
                }
            }
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

    //放车阶段保存数据到数据库中
    private void saveData(String pos) {
        try {
            if (connection == null || connection.isClosed()) {
                createNewConnection();
            }
            statement.execute("insert into user (phone,position) values ('"
                    + phoneNumber + "','" + pos + "')");
            Log.d("@HusterYP", String.valueOf("插入数据: " + "insert into user (phone,position) values ('"
                    + phoneNumber + "','" + pos + "')"));
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
}
