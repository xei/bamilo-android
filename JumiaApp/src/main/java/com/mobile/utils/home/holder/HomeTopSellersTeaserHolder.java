package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.components.recycler.HorizontalListView;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 *
 */
public class HomeTopSellersTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    public HorizontalListView horizontalListView;

    /**
     * Constructor
     */
    public HomeTopSellersTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        // Get horizontal container
        horizontalListView = (HorizontalListView) view.findViewById(R.id.home_teaser_top_sellers_horizontal_list);
        // Validate orientation
        horizontalListView.enableRtlSupport(isRtl);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (horizontalListView.getAdapter() == null) {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            horizontalListView.setHasFixedSize(true);
            // Set adapter
            horizontalListView.setAdapter(new HomeTopSellersTeaserAdapter(group.getData(), mParentClickListener));
        } else {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
        }
    }

}