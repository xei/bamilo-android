
package com.bamilo.android.framework.components.customfontviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.modernbamilo.customview.XeiEditText;

import org.jetbrains.annotations.NotNull;


public class EditText extends MaterialEditText {
    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttrs(context, attrs, defStyle);
    }

    // TODO: 8/28/18 farshid
    private void setAttrs(Context context, AttributeSet attrs, int defStyle){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyle, 0);
        final int textAppearance = a.getResourceId(R.styleable.TextView_android_textAppearance, 0);
        a.recycle();
        a = context.obtainStyledAttributes(attrs, R.styleable.TextAppearance, defStyle, 0);
        a.recycle();
    }
}
