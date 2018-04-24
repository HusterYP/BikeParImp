package com.example.yuanping.bike.thread;

import android.content.Context;

/**
 * Created by yuanping on 4/1/18.
 * 放车完成,因为相对于取车完成过程的区别是数据库操作不同,所以这里还需要单独封装
 */

public class PutBikeOver extends OverThread {
    public PutBikeOver(Context context, String phone) {
        super(context, phone);
    }

    @Override
    public void run() {
        handler.sendEmptyMessage(handler.SHOW_DIALOG);
        super.run();
        handler.sendEmptyMessage(handler.OVER);
    }
}
