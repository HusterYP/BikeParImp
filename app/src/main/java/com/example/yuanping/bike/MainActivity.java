package com.example.yuanping.bike;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yuanping.bike.db.UserDataBaseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private CheckBox checkBox;
    private EditText phone;
    private EditText pwdView;
    private UserDataBaseHelper dataBaseHelper;
    private static final String USER_DB_NAME = "Count.db";
    private static final String TABLE_NAME = "User";
    private static final String ITEM_CURRENT = "current";
    private static final String ITEM_NAME = "name";
    private static final String ITEM_PWD = "pwd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        checkBox = findViewById(R.id.remember);
        phone = findViewById(R.id.phone);
        pwdView = findViewById(R.id.pwd);

        dataBaseHelper = new UserDataBaseHelper(this, USER_DB_NAME, null, 1);
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(TABLE_NAME, null, ITEM_CURRENT + "=?",
                    new String[]{"1"}, null, null, null, null);
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(ITEM_NAME));
                String pwd = cursor.getString(cursor.getColumnIndex(ITEM_PWD));
                phone.setText(name);
                pwdView.setText(pwd);
                checkBox.setChecked(true);
            }
        } catch (Exception e) {
            Log.d("@HusterYP",String.valueOf(e.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*
    默认采用一人一手机一电话对应一手机号
     */
    public void startUse(View view) {
        String phoneNumer = phone.getText().toString();
        String passWord = pwdView.getText().toString();
        if (TextUtils.isEmpty(phoneNumer) || !isPhoneRight(phoneNumer)) {
            Toast.makeText(getApplication(), String.valueOf("请输入正确的电话号码!"), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            Toast.makeText(getApplication(), String.valueOf("请输入密码!"), Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(TABLE_NAME, null, ITEM_NAME + "=?",
                    new String[]{phoneNumer}, null, null, null);
            if (cursor.moveToFirst()) {
                String pwd = cursor.getString(cursor.getColumnIndex(ITEM_PWD));
                int current = cursor.getInt(cursor.getColumnIndex(ITEM_CURRENT));
                if (pwd.equals(passWord)) {
                    if (checkBox.isChecked() && current == 0 || !checkBox.isChecked() & current
                            == 1) {
                        ContentValues values = new ContentValues();
                        values.put(ITEM_CURRENT, checkBox.isChecked() ? 1 : 0);
                        database.update(TABLE_NAME, values, ITEM_NAME + "=?", new
                                String[]{phoneNumer});
                    }
                    startActivity(phoneNumer);
                } else {
                    Toast.makeText(getApplication(), String.valueOf("用户名或密码错误!"), Toast
                            .LENGTH_SHORT)
                            .show();
                }
            } else {
                ContentValues values = new ContentValues();
                values.put(ITEM_NAME, phoneNumer);
                values.put(ITEM_PWD, passWord);
                values.put(ITEM_CURRENT, checkBox.isChecked() ? 1 : 0);
                database.insert(TABLE_NAME, null, values);
                startActivity(phoneNumer);
            }
        } catch (Exception e) {
            Log.d("@HusterYP",String.valueOf(e.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void startActivity(String phoneNumber) {
        Intent intent = new Intent(this, DealUseBike.class);
        intent.putExtra(Constant.PHONE, phoneNumber);
        startActivity(intent);
        finish();
    }

    private boolean isPhoneRight(String phone) {
        Pattern pattern = Pattern.compile(Constant.reg);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
/*
    public void help(View view) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_main_help);
        dialog.show();
    }*/
}
