package com.damon43.recordviewdemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by lenovo on 2017/7/4.
 */

public class ImageTools {
    public static Bitmap drawableToBitmap(Drawable drawable) {


        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.setFilterBitmap(true);
        drawable.draw(canvas);

        return bitmap;

    }
}
