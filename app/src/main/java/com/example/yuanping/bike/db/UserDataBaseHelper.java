package com.example.yuanping.bike.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuanping on 4/23/18.
 * 用户信息存储
 * 存储的用户信息有:
 * name: 登录用户名
 * pwd: 密码
 * current: 是否为当前需要记住的用户,当该值为1时,表示需要记住的当前用户,为0,则...
 */

public class UserDataBaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_USER = "create table User(" +
            "id integer primary key autoincrement," +
            "name text," +
            "pwd text," +
            "current integer)";

    public UserDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
