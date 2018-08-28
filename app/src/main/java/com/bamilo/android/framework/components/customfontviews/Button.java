
package com.bamilo.android.framework.components.customfontviews;

import android.content.Context;
import android.util.AttributeSet;

import com.bamilo.android.appmodule.modernbamilo.customview.XeiButton;

import org.jetbrains.annotations.NotNull;


public class Button extends XeiButton {
    public Button(@NotNull Context context) {
        super(context);
    }

    public Button(@NotNull Context context, @NotNull AttributeSet attrs) {
        super(context, attrs);
    }

    public Button(@NotNull Context context, @NotNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
