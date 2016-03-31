
package com.mobile.components.customfontviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.mobile.components.customfontviews.HoloFontLoader.FontStyleProvider;

public class RadioButton extends AppCompatRadioButton implements FontStyleProvider {
    private String mFontFamily;

    private int mFontStyle;
    @ColorInt public static final int BLACK_800    = 0xFF808080;
    @ColorInt public static final int BLUE1        = 0xFF416998;

    public RadioButton(Context context) {
        this(context, null);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.radioButtonStyle);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TextView.construct(this, context, attrs, defStyle);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{

                        BLACK_800
                        , BLUE1
                }
        );
        this.setSupportButtonTintList(colorStateList);

    }

    @Override
    public String getFontFamily() {
        return mFontFamily;
    }

    @Override
    public int getFontStyle() {
        return mFontStyle;
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        TextView.setAllCaps(this, allCaps);
    }

    @Override
    public void setFontStyle(String fontFamily, int fontStyle) {
        mFontFamily = fontFamily;
        mFontStyle = fontStyle;
        TextView.setFontStyle(this, fontFamily, fontStyle);
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        TextView.setTextAppearance(this, context, resid);
    }
}
