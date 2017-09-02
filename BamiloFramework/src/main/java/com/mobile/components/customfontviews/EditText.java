
package com.mobile.components.customfontviews;

import android.content.Context;
import android.util.AttributeSet;

import com.mobile.components.customfontviews.HoloFontLoader.FontStyleProvider;



public class EditText extends MaterialEditText implements FontStyleProvider {
    private String mFontFamily;

    private int mFontStyle;

    public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TextView.construct(this, context, attrs, defStyle);
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
        HoloFontLoader.applyDefaultFont(this);
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        TextView.setTextAppearance(this, context, resid);
    }
}
