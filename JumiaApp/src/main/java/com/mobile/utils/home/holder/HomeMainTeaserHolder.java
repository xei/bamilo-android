package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.components.infiniteviewpager.InfiniteCirclePageIndicator;
import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.components.viewpager.PreviewViewPager;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * Main teaser
 */
public class HomeMainTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

//    private static final int DEFAULT_POSITION_PHONE = 0;
//
//    private static final int DEFAULT_REVERSE_POSITION_PHONE = 1;
//
//    private static final int DEFAULT_POSITION_TABLET = 1;
//
//    private static final int DEFAULT_REVERSE_POSITION_TABLET = 2;

    private static final double PHONE_IMAGE_RATIO = 2.44d;

    private static final double TABLET_IMAGE_RATIO = 2.71d;

    private final boolean isTablet;

    public PreviewViewPager pager;

    public InfiniteCirclePageIndicator indicator;

    public View container;

    public static int viewPagerPosition;

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
            Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NULL");
            // Create adapter
            HomeMainTeaserAdapter adapter = new HomeMainTeaserAdapter(mContext, group.getData(), mParentClickListener, isTablet);

            InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(adapter);
            infinitePagerAdapter.setOneItemMode();
            infinitePagerAdapter.enableInfinitePages(adapter.getCount() > 1);

            // Add adapter to pager
            pager.setAdapter(infinitePagerAdapter);
            // Add pager to indicator
            indicator.setViewPager(pager);

            pager.setCurrentItem(viewPagerPosition);
        } else {
            Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NOT NULL");
        }
    }

//    /**
//     * Get the default position
//     * @param size The number of items
//     * @return int
//     */
//    @Deprecated
//    private int getDefaultPosition(int size) {
//        int position;
//        if(!isTablet) {
//            position = !isRtl ? DEFAULT_POSITION_PHONE : size - DEFAULT_REVERSE_POSITION_PHONE;
//        } else {
//            position = !isRtl ? DEFAULT_POSITION_TABLET : size - DEFAULT_REVERSE_POSITION_TABLET;
//        }
//        return position;
//    }

    @Override
    public void onUpdate() {

    }

    public int getViewPagerPosition() {
        viewPagerPosition = (pager.getAdapter() instanceof InfinitePagerAdapter) ?
                ((InfinitePagerAdapter) pager.getAdapter()).getVirtualPosition(pager.getCurrentItem())
                : pager.getCurrentItem();
        return viewPagerPosition;
    }

    @Override
    public void apllyMargin() {

    }
}