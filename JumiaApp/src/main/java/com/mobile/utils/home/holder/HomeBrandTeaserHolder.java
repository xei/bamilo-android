package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.components.recycler.HorizontalListView;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * Class used that represents the brand teasers.
 */
public class HomeBrandTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    public HorizontalListView container;

    /**
     * Constructor
     */
    public HomeBrandTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        // Get horizontal container
        container = (HorizontalListView) view.findViewById(R.id.home_teaser_brand_horizontal_list);
        // Validate orientation
        container.enableRtlSupport(isRtl);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (container.getAdapter() == null) {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NULL");
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            container.setHasFixedSize(true);
            // Set adapter
            container.setAdapter(new HomeBrandTeaserAdapter(group.getData(), mParentClickListener));
        } else {
            Log.i(TAG, "BRAND_TEASERS: ADAPTER IS NOT NULL");
        }
    }

}
