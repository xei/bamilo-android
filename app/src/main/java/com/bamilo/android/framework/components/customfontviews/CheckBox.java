
package com.bamilo.android.framework.components.customfontviews;

import android.content.Context;
import android.util.AttributeSet;

import com.bamilo.android.appmodule.modernbamilo.customview.XeiCheckBox;

import org.jetbrains.annotations.NotNull;


public class CheckBox extends XeiCheckBox {
    public CheckBox(@NotNull Context context) {
        super(context);
    }

    public CheckBox(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBox(@NotNull Context context, @NotNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
