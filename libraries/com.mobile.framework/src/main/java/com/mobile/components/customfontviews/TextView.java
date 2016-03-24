
package com.mobile.components.customfontviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.mobile.components.customfontviews.HoloFontLoader.FontStyleProvider;
import com.mobile.framework.R;

public class TextView extends android.widget.TextView implements FontStyleProvider {
    private String mFontFamily;
    private int mFontStyle;

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TextView.construct(this, context, attrs, defStyle);
    }

    
    public static <T extends android.widget.TextView & FontStyleProvider> void construct(T textView, Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView, defStyle, 0);
        final int textAppearance = a.getResourceId(R.styleable.TextView_android_textAppearance, 0);
        a.recycle();
        TextView.setTextAppearance(textView, context, textAppearance);

        a = context.obtainStyledAttributes(attrs, R.styleable.TextAppearance, defStyle, 0);
        TextView.setTextAppearance(textView, a);
        a.recycle();
    }

    private static Object[] parseFontStyle(TypedArray a) {
        boolean force = true;
        int fontStyle = HoloFontLoader.TEXT_STYLE_NORMAL;
        String fontFamily = null;
        TypedValue value = new TypedValue();
        a.getValue(R.styleable.TextAppearance_android_fontFamily, value);
        if (value.string == null) {
            a.getValue(R.styleable.TextAppearance_android_typeface, value);
        }
        if (value.string == null) {
            force = false;
        } else {
            Object[] z = HoloFontLoader.parseFontStyle(value.string.toString());
            fontStyle = (Integer) z[0];
            fontFamily = (String) z[1];
        }
        final int defaultStyle = fontStyle & (HoloFontLoader.TEXT_STYLE_BOLD | HoloFontLoader.TEXT_STYLE_ITALIC);
        fontStyle &= ~defaultStyle;
        fontStyle |= a.getInt(R.styleable.TextAppearance_android_textStyle, defaultStyle);
        return new Object[]{force, fontStyle, fontFamily};
    }

    public static void setAllCaps(android.widget.TextView textView, boolean allCaps) {
        if (allCaps) {
            textView.setTransformationMethod(new AllCapsTransformationMethod(textView.getContext()));
        } else {
            textView.setTransformationMethod(null);
        }
    }

    public static <T extends android.widget.TextView & FontStyleProvider> void setFontStyle(T textView, String fontFamily, int fontStyle) {
        HoloFontLoader.applyDefaultFont(textView);
    }

    public static <T extends android.widget.TextView & FontStyleProvider> void setTextAppearance(T textView, Context context, int resid) {
        if (resid == 0) {
            return;
        }
        TypedArray appearance = context.obtainStyledAttributes(resid, R.styleable.TextAppearance);
        setTextAppearance(textView, appearance);
        appearance.recycle();
    }

    public static <T extends android.widget.TextView & FontStyleProvider> void setTextAppearance(T textView, TypedArray appearance) {
//        // android:textColorHighlight
//        int color = appearance.getColor(R.styleable.TextAppearance_android_textColorHighlight, 0);
//        if (color != 0) {
//            textView.setHighlightColor(color);
//        }
//        // android:textColor
//        ColorStateList colors = appearance.getColorStateList(R.styleable.TextAppearance_android_textColor);
//        if (colors != null) {
//            textView.setTextColor(colors);
//        }
//        // android:textSize
//        int ts = appearance.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
//        if (ts != 0) {
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ts);
//        }
//        // android:textColorHint
//        colors = appearance.getColorStateList(R.styleable.TextAppearance_android_textColorHint);
//        if (colors != null) {
//            textView.setHintTextColor(colors);
//        }
//        // android:textColorLink
//        colors = appearance.getColorStateList(R.styleable.TextAppearance_android_textColorLink);
//        if (colors != null) {
//            textView.setLinkTextColor(colors);
//        }
//        // android:textAllCaps
//        if (appearance.getBoolean(R.styleable.TextAppearance_android_textAllCaps, false)) {
//            textView.setTransformationMethod(new AllCapsTransformationMethod(textView.getContext()));
//        }

        // android:fontFamily 
        Object[] font = parseFontStyle(appearance);
        textView.setFontStyle((String) font[2], (Integer) font[1] | ((Boolean) font[0] ? 0 : textView.getFontStyle()));
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
