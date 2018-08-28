package com.bamilo.android.appmodule.bamiloapp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.bamilo.android.framework.components.NestedVerticalListView;
import com.bamilo.android.R;

public class PickUpStationListView extends NestedVerticalListView {
    
    public PickUpStationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getAdapter() != null && getAdapter().getCount() == 1) {
            ((LinearLayout)this.getParent()).setMinimumHeight(0);
        } else {
            ((LinearLayout)this.getParent()).setMinimumHeight(this.getContext().getResources().getDimensionPixelSize(R.dimen.dimen_260dp));
        }
    }


}
