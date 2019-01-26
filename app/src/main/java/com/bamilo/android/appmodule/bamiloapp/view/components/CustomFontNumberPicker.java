package com.bamilo.android.appmodule.bamiloapp.view.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.bamilo.android.appmodule.modernbamilo.util.typography.TypeFaceHelper;

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
        updateView(child);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        updateView(child);
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
        updateView(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    private void updateView(View view) {
        if (!isInEditMode())
            if (view instanceof EditText)
                ((EditText) view).setTypeface(TypeFaceHelper.getInstance(getContext()).getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_REGULAR));
    }
}
