package com.bamilo.android.appmodule.bamiloapp.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

/**
 * Created by mohsen on 3/5/18.
 */

public class CustomFontNumberPicker extends NumberPicker {
    public CustomFontNumberPicker(Context context) {
        super(context);
    }

    public CustomFontNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFontNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        initFont(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        initFont(child);
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        initFont(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        initFont(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        initFont(child);
    }

    private void initFont(View view) {
//        HoloFontLoader.applyDefaultFont(view);
    }
}
