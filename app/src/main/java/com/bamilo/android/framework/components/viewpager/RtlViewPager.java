package com.bamilo.android.framework.components.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * <br> <br>View pager with RTL support. In order to work the adapter must implement @RtlService
 * and then method call #enableRtl or #disableRtl.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/06/18
 */
public class RtlViewPager extends ViewPager {

    private boolean isRtlEnabled;

    public interface RtlService{
        void invertItems();
    }

    public RtlViewPager(Context context) {
        super(context);
    }

    public RtlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * #invertItems is called if adapter is instance of @RtlService.
     */
    public void enableRtl(){
        if(getAdapter() instanceof  RtlService && !isRtlEnabled){
            ((RtlService) getAdapter()).invertItems();
            isRtlEnabled = true;
        }
    }

    /**
     * #invertItems is called if adapter is instance of @RtlService.
     */
    public void disableRtl(){
        if(getAdapter() instanceof  RtlService && isRtlEnabled){
            ((RtlService) getAdapter()).invertItems();
            isRtlEnabled = false;
        }
    }

}
