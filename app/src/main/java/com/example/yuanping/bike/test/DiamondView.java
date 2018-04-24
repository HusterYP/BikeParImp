package com.example.yuanping.bike.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuanping on 4/24/18.
 */

public class DiamondView extends ImageView {

    private Drawable drawable;
    private Bitmap bitmap;
    private Paint paint = new Paint();

    public DiamondView(Context context) {
        super(context);
    }

    public DiamondView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawable = getDrawable();
        if (drawable != null) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
    }

    public DiamondView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DiamondView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        canvas.drawRect(0,0,width,height,paint);
        canvas.translate(width / 2, height / 2);
        canvas.rotate(45);
    }
}
