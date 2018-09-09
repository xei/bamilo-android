package com.bamilo.android.appmodule.bamiloapp.utils.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.bamilo.android.appmodule.bamiloapp.utils.ComboGridView;

/**
 * Created by alexandra pires 31/08/2015.
 * Gridview for variation products in variations page
 */
public class VariationProductsGridView extends ComboGridView {

    public static final String TAG = VariationProductsGridView.class.getSimpleName();

    public VariationProductsGridView(Context context) {
        super(context);
    }

    public VariationProductsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VariationProductsGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
