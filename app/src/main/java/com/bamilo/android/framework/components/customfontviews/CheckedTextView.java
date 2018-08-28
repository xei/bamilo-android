
package com.bamilo.android.framework.components.customfontviews;

import android.content.Context;
import android.util.AttributeSet;

import com.bamilo.android.appmodule.modernbamilo.customview.XeiCheckBox;

import org.jetbrains.annotations.NotNull;


public class CheckedTextView extends XeiCheckBox {
    public CheckedTextView(@NotNull Context context) {
        super(context);
    }

    public CheckedTextView(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckedTextView(@NotNull Context context, @NotNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
