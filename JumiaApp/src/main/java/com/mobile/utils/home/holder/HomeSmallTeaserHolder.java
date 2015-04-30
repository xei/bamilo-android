package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.components.recycler.HorizontalListView;
import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
import com.mobile.view.R;

/**
 *
 */
public class HomeSmallTeaserHolder extends BaseTeaserViewHolder {

    public static final String TAG = HomeSmallTeaserHolder.class.getSimpleName();

    public HorizontalListView horizontal;

    /**
     *
     * @param context
     * @param view
     * @param onClickListener
     */
    public HomeSmallTeaserHolder(Context context, View view, View.OnClickListener onClickListener) {
        super(context, view, onClickListener);
        horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_small_horizontal_list);
    }

    /**
     *
     * @param group The teaser group
     */
    @Override
    public void onBind(BaseTeaserGroupType group) {
        if (horizontal.getAdapter() == null) {
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            horizontal.setHasFixedSize(true);
            // RTL
            Boolean isRTL = mContext.getResources().getBoolean(R.bool.is_bamilo_specific);
            if (isRTL) horizontal.enableReverseLayout();
            // Set adapter
            horizontal.setAdapter(new HomeSmallTeaserAdapter(group.getData(), mParentClickListener));
        }
    }

    @Override
    public void onUpdate() {

    }
}