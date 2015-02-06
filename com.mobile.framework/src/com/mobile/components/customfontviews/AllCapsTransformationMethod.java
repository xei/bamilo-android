
package com.mobile.components.customfontviews;

import java.util.Locale;

import android.content.Context;
import android.graphics.Rect;
import android.text.method.TransformationMethod;
import android.view.View;

public class AllCapsTransformationMethod implements TransformationMethod {
    private boolean mEnabled = true;
    private Locale mLocale;

    public AllCapsTransformationMethod(Context context) {
        mLocale = context.getResources().getConfiguration().locale;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        if (mEnabled) {
            return source != null ? source.toString().toUpperCase(mLocale) : null;
        }
        return source;
    }

    @Override
    public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction,
                               Rect previouslyFocusedRect) {
    }
    
}
