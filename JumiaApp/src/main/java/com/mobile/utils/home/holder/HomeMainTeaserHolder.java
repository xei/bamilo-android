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

    // Views
    public PreviewViewPager pager;
    public CirclePageIndicator indicator;

    public HomeMainTeaserHolder(Context context, View itemView, View.OnClickListener onClickListener) {
        super(context, itemView, onClickListener);
        pager = (PreviewViewPager) itemView.findViewById(R.id.home_teaser_main_pager);
        indicator = (CirclePageIndicator) itemView.findViewById(R.id.home_teaser_main_indicator);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (pager.getAdapter() == null) {
            Log.i(TAG, "MAIN_TEASERS: ADAPTER IS NULL");
            // Create adapter
            HomeMainTeaserAdapter adapter = new HomeMainTeaserAdapter(mContext, group.getData(), mParentClickListener);
            // Add adapter to pager
            pager.setAdapter(adapter);
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