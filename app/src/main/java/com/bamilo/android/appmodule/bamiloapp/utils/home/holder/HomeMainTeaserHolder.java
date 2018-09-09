package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.bamilo.android.framework.components.infiniteviewpager.InfiniteCirclePageIndicator;
import com.bamilo.android.framework.components.infiniteviewpager.InfinitePagerAdapter;
import com.bamilo.android.framework.components.viewpager.PreviewViewPager;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.objects.home.object.BaseTeaserObject;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Main teaser
 */
public class HomeMainTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    public static final int DEFAULT_POSITION = 0;

    private static final double PHONE_IMAGE_RATIO = 2.44d;

    private static final double TABLET_IMAGE_RATIO = 2.71d;

    private final boolean isTablet;

    public PreviewViewPager pager;

    public InfiniteCirclePageIndicator indicator;

    public View container;

    public static int sViewPagerPosition;

    public HomeMainTeaserHolder(Context context, View itemView, View.OnClickListener onClickListener) {
        super(context, itemView, onClickListener);
        // Tablet flag
        isTablet = DeviceInfoHelper.isTabletDevice(mContext);
        // Pager indicator
        indicator = (InfiniteCirclePageIndicator) itemView.findViewById(R.id.home_teaser_main_indicator);
        // Pager indicator
        container = itemView.findViewById(R.id.home_teaser_main_container);
        // Set height
        setViewPagerHeightBasedOnImageRatio();
        // Pager
        pager = (PreviewViewPager) itemView.findViewById(R.id.home_teaser_main_pager);
        // Set the preview offset
        pager.setPreviewOffset(mOffset);
    }

    /**
     * Method used to set the height using the image ratio<br>
     * Phone:   22w / 9h = 2.44<br>
     * Tablet:  19w / 7h = 2.71<br>
     */
    private void setViewPagerHeightBasedOnImageRatio() {
        // Case phone
        double ratio = PHONE_IMAGE_RATIO;
        int width = DeviceInfoHelper.getWidth(mContext);
        // Case tablet
        if(isTablet) {
            ratio = TABLET_IMAGE_RATIO;
            width = width - 2 * mOffset;
        }
        // Set height
        container.getLayoutParams().height = (int) (width / ratio);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (pager.getAdapter() == null) {
            ArrayList<BaseTeaserObject> baseTeasers = group.getData();
            if(isRtl) {
                // reverse teaser objects array for RTL
                Collections.reverse(baseTeasers);
            }
            // Create adapter
            HomeMainTeaserAdapter adapter = new HomeMainTeaserAdapter(mContext, baseTeasers, mParentClickListener, isTablet);
            InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(adapter);
            infinitePagerAdapter.setOneItemMode();
            infinitePagerAdapter.enableInfinitePages(adapter.getCount() > 1);
            // Add adapter to pager
            pager.setAdapter(infinitePagerAdapter);
            // Add pager to indicator
            indicator.setViewPager(pager);
            if(isRtl) {
                // reverse pager position for RTL
                sViewPagerPosition = adapter.getCount() - 1;
            }
            // Set default position
            pager.setCurrentItem(sViewPagerPosition);
        } else {
        }
    }

    @Override
    public void applyMargin() {
        // ...
    }

    public int getViewPagerPosition() {
        sViewPagerPosition = (pager.getAdapter() instanceof InfinitePagerAdapter) ?
                ((InfinitePagerAdapter) pager.getAdapter()).getVirtualPosition(pager.getCurrentItem())
                : pager.getCurrentItem();
        return sViewPagerPosition;
    }
    
}