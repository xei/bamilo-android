package com.mobile.components.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * <p/>
 * <br> <br>View pager with RTL support. In order to work the adapter must implement @RtlService
 * and then method call #enableRtl or #disableRtl.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/06/18
 */
public class RtlViewPager extends ViewPager {

    private boolean isRtl;

    public RtlViewPager(Context context) {
        super(context);
    }

    public RtlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Method used to enable the reverse layout to support RTL direction.<br>
     *
     * @param rtl The RTL flag
     */
    public void enableRtlSupport(boolean rtl) {
        isRtl = rtl;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (getAdapter() instanceof RtlDynamicFragmentAdapter) {
            ((RtlDynamicFragmentAdapter) getAdapter()).enableRtl(isRtl);
        }
    }

    @Override
    public void setCurrentItem(int item) {
        int count = getAdapter().getCount();
        super.setCurrentItem(count == 0 ? 0 : (isRtl ? count - 1 - item : item));
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        int count = getAdapter().getCount();
        super.setCurrentItem(count == 0 ? 0 : (isRtl ? count - 1 - item : item), smoothScroll);
    }
}
