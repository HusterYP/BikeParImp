package com.example.yuanping.bike;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.yuanping.bike.test.TestClearSQLThread;
import com.example.yuanping.bike.test.TestGetPosition;
import com.example.yuanping.bike.test.TestSQLThread;
import com.example.yuanping.bike.thread.GetBikeOver;
import com.example.yuanping.bike.thread.GetThread;
import com.example.yuanping.bike.thread.MyHandler;
import com.example.yuanping.bike.thread.PutBikeOver;
import com.example.yuanping.bike.thread.PutThread;

/**
 * Created by yuanping on 3/31/18.
 * 建表语句: create table user(phone varchar(40),position varchar(2),flag varchar(10))
 */
//TODO 检查网速,有时候网速很慢或者该应用无法使用数据流量时,可能会出错
public class DealUseBike extends AppCompatActivity {

    private String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_use);
        phone = getIntent().getStringExtra(Constant.PHONE);
        init();
    }

    //TODO 网络不可用的时候,禁止点击按钮
    private void init() {
        boolean isNetworkAvailable = Utils.isNetworkAvailable(this);
        if (!isNetworkAvailable) {
            Toast.makeText(getApplication(), String.valueOf("网络不可用!请检查您的网络状态!"),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void putBike(View view) {
        if (Utils.isNetworkAvailable(this)) {
            new PutThread(this, phone).start();
        } else {
            Toast.makeText(getApplication(), String.valueOf("网络不可用!请检查您的网络状态!"),
                    Toast.LENGTH_LONG).show();
        }
    }

    //取车: 直到车位下降
    public void getBike(View view) {
        if (!TextUtils.isEmpty(phone)) {
            if (Utils.isNetworkAvailable(this)) {
                new GetThread(this, phone).start();
            } else {
                Toast.makeText(getApplication(), String.valueOf("网络不可用!请检查您的网络状态!"),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplication(), String.valueOf("电话号码不能为空哦!"), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //取车完毕,发送抬升命令
    public void getOver(View view) {
        new GetBikeOver(this, phone).start();
    }

    //放车完毕,发送抬升命令
    public void putOver(View view) {
        new PutBikeOver(this, phone).start();
    }

    //数据库数据测试
    public void SQLTest(View view) {
        new TestSQLThread(this, phone).start();
    }

    //测试: 数据库内容清除
    public void clear(View view) {
        new TestClearSQLThread(this, phone).start();
    }

    //测试: 车位信息查询
    public void getPosition(View view) {
        new TestGetPosition(this, phone).start();
    }

    //TODO 审核使用说明文档
    public void help(View view) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_help);
        dialog.show();
    }

    //版权声明
    public void copyRight(View view) {
        Toast.makeText(getApplication(), String.valueOf("版权所有!"), Toast.LENGTH_SHORT).show();
    }

    //关于
    public void aboutMe(View view) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.about_me_dialog);
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && MyHandler.isDialogShow){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
