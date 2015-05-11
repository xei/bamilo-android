package com.mobile.utils.home.holder;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.mobile.components.viewpager.PreviewViewPager;
import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Main teaser
 */
public class HomeMainTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    private static final int DEFAULT_POSITION = 1;

    // Views
    public PreviewViewPager pager;
    public CirclePageIndicator indicator;

    public HomeMainTeaserHolder(Context context, View itemView, View.OnClickListener onClickListener) {
        super(context, itemView, onClickListener);
        // Pager indicator
        indicator = (CirclePageIndicator) itemView.findViewById(R.id.home_teaser_main_indicator);
        // Pager
        pager = (PreviewViewPager) itemView.findViewById(R.id.home_teaser_main_pager);
        // Set the preview offset
        pager.setPreviewOffset(mOffset);

    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (pager.getAdapter() == null) {
            Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NULL");
            // Create adapter
            HomeMainTeaserAdapter adapter = new HomeMainTeaserAdapter(mContext, group.getData(), mParentClickListener);
            // Add adapter to pager
            pager.setAdapter(adapter);
            // Set default position
            pager.setCurrentItem(DEFAULT_POSITION);
            // Add pager to indicator
            indicator.setViewPager(pager);
        } else {
            Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NOT NULL");
        }
    }

    @Override
    public void onUpdate() {

    }
}