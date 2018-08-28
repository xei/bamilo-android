package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.bamilo.android.framework.components.ExpandedGridViewComponent;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
import com.bamilo.android.R;


/**
 *
 */
public class HomeFeaturedTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    public ExpandedGridViewComponent container;

    /**
     * Constructor
     */
    public HomeFeaturedTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        container = (ExpandedGridViewComponent) view.findViewById(R.id.home_teaser_featured_stores_container_grid);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        if(container.getAdapter() == null) {
            // Expand grid view
            container.setExpanded(true);
            // Set adapter
            container.setAdapter(new HomeFeaturedTeaserAdapter(mContext, R.layout.home_teaser_featured_stores_item, group.getData(), mParentClickListener));
        } else {
        }
    }

}