package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.components.recycler.HorizontalListView;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.view.R;

/**
 * Small teaser
 */
public class HomeSmallTeaserHolder extends BaseTeaserViewHolder {

    public static final String TAG = HomeSmallTeaserHolder.class.getSimpleName();

    public static final int NUMBER_OF_PIECES = 3;

    private int mItemWidth;

    public HorizontalListView horizontal;

    private boolean secondTime = false;

    /**
     * Constructor
     */
    public HomeSmallTeaserHolder(Context context, View view, View.OnClickListener onClickListener) {
        super(context, view, onClickListener);
        // Get horizontal container
        horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_small_horizontal_list);
        // Disable fading for tablets
        horizontal.setHorizontalFadingEdgeEnabled(mOffset == NO_OFFSET);
        // Validate orientation
        horizontal.enableRtlSupport(isRtl);
        // Calculate the width for each item: SCREEN_WIDTH - LEFT_OFFSET - RIGHT_OFFSET / NUMBER_OF_PIECES
        mItemWidth = (DeviceInfoHelper.getWidth(context) - (2 * mOffset)) / NUMBER_OF_PIECES;
    }

    /**
     *
     * @param group The teaser group
     */
    @Override
    public void onBind(BaseTeaserGroupType group) {
        setTeaserGroupTypeMargins(group);
        if (horizontal.getAdapter() == null) {
            // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
            horizontal.setHasFixedSize(true);
            // Set adapter
            horizontal.setAdapter(new HomeSmallTeaserAdapter(group.getData(), mParentClickListener, mItemWidth));
        }
    }

    @Override
    public void onUpdate() {
    }

    /**
     * set specific margins for small teasers
     * @param group
     */
    public void setTeaserGroupTypeMargins(BaseTeaserGroupType group) {
        if (group.getData() != null && group.getData().size() == NUMBER_OF_PIECES) {
            secondTime = true;
            apllyMargin();
        }
    }

    @Override
    public void apllyMargin() {
        if(secondTime){
            super.apllyMargin();
        }

    }
}