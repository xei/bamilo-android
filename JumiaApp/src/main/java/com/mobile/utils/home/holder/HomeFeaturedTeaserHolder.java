package com.mobile.utils.home.holder;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.mobile.components.ExpandableGridViewComponent;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;

/**
 *
 */
public class HomeFeaturedTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    // Data
    public ExpandableGridViewComponent container;

    public HomeFeaturedTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        container = (ExpandableGridViewComponent) view.findViewById(R.id.home_teaser_featured_stores_container_grid);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if(container.getAdapter() == null) {
            Log.i(TAG, "FEATURED_TEASER: ADAPTER IS NULL");
            // Expand grid view
            container.setExpanded(true);
            // Set adapter
            container.setAdapter(new HomeFeaturedTeaserAdapter(mContext, R.layout.home_teaser_featured_stores_item, group.getData(), mParentClickListener));
        } else {
            Log.i(TAG, "FEATURED_TEASER: ADAPTER IS NOT NULL");
        }
    }

    @Override
    public void onUpdate() {

    }

}