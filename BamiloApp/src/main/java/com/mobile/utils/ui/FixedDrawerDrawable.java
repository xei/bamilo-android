package com.mobile.utils.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;

public class FixedDrawerDrawable extends DrawerArrowDrawable {
    private Drawable hamburgerIconDrawable;

    /**
     * @param context used to get the configuration for the drawable from
     */
    public FixedDrawerDrawable(Context context, @DrawableRes int iconResId) {
        super(context);
        this.hamburgerIconDrawable = context.getResources().getDrawable(iconResId);
    }

    @Override
    public void draw(Canvas canvas) {
        Drawable currentState = hamburgerIconDrawable.getCurrent();
        if(currentState instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable) currentState).getBitmap();
            canvas.drawBitmap(b, 0, 0, getPaint());
        }
    }
}
