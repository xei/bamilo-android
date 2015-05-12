package com.mobile.utils.home.holder;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.mobile.components.viewpager.PreviewViewPager;
import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
import com.mobile.framework.utils.DeviceInfoHelper;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Main teaser
 */
public class HomeMainTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    private static final int DEFAULT_POSITION = 1;

    private static final int DEFAULT_REVERSE_POSITION = 2;

    private static final double PHONE_IMAGE_RATIO = 2.44d;

    private static final double TABLET_IMAGE_RATIO = 2.71d;

    private final boolean isTablet;

    // Views
    public PreviewViewPager pager;
    public CirclePageIndicator indicator;

    public HomeMainTeaserHolder(Context context, View itemView, View.OnClickListener onClickListener) {
        super(context, itemView, onClickListener);
        // Tablet flag
        isTablet = DeviceInfoHelper.isTabletDevice(mContext);
        // Pager indicator
        indicator = (CirclePageIndicator) itemView.findViewById(R.id.home_teaser_main_indicator);
        // Pager
        pager = (PreviewViewPager) itemView.findViewById(R.id.home_teaser_main_pager);
        // Set the preview offset
        pager.setPreviewOffset(mOffset);
        // Set height
        setViewPagerHeightBasedOnImageRatio();
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
        pager.getLayoutParams().height = (int) (width / ratio);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (pager.getAdapter() == null) {
            Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NULL");
            // Create adapter
            HomeMainTeaserAdapter adapter = new HomeMainTeaserAdapter(mContext, group.getData(), mParentClickListener, isTablet);
            // Add adapter to pager
            pager.setAdapter(adapter);
            // Add pager to indicator
            indicator.setViewPager(pager);
            // Set default position
            int position = DEFAULT_POSITION;
            if (isRtl) {
                position = group.hasData() ? adapter.getCount() - DEFAULT_REVERSE_POSITION : 0;
            }
            pager.setCurrentItem(position);
        } else {
            Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NOT NULL");
        }
    }

    @Override
    public void onUpdate() {

    }
}