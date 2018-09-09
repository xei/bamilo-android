package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.bamilo.android.framework.components.recycler.DividerItemDecoration;
import com.bamilo.android.framework.components.recycler.HorizontalListView;
import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.R;

/**
 * Small teaser
 */
public class HomeSmallTeaserHolder extends BaseTeaserViewHolder {

    public static final String TAG = HomeSmallTeaserHolder.class.getSimpleName();

    public static final int NUMBER_OF_PIECES = 3;

    private int minItemWidth;

    public HorizontalListView horizontal;

    private boolean secondTime = false;

    private final Context context;

    /**
     * Constructor
     */
    public HomeSmallTeaserHolder(Context context, View view, View.OnClickListener onClickListener) {
        super(context, view, onClickListener);
        // Get horizontal container
        horizontal = (HorizontalListView) view.findViewById(R.id.home_teaser_small_horizontal_list);
        // Disable fading for tablets
        horizontal.setHorizontalFadingEdgeEnabled(mOffset == NO_OFFSET);
        horizontal.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));
        // Validate orientation
        horizontal.enableRtlSupport(isRtl);

        this.context = context;
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
            horizontal.setAdapter(new HomeSmallTeaserAdapter(group.getData(), mParentClickListener, minItemWidth));
        }
    }

    /**
     * set specific margins for small teasers
     * @param group
     */
    public void setTeaserGroupTypeMargins(BaseTeaserGroupType group) {
        // Calculate the width for each item: SCREEN_WIDTH - LEFT_OFFSET - RIGHT_OFFSET / NUMBER_OF_PIECES
        minItemWidth = (DeviceInfoHelper.getWidth(context) - (2 * mOffset)) / NUMBER_OF_PIECES;

        int numPieces = group.getData().size();

        if (numPieces > NUMBER_OF_PIECES){
            int sizePiece = (DeviceInfoHelper.getWidth(context)) / numPieces;
            //If the calculated new size (without offset) is bigger than min width defined
            if (sizePiece > minItemWidth) {
                minItemWidth = sizePiece;
            }
            //Offset is discarted
            mOffset = 0;
        }
        secondTime = true;
        applyMargin();
    }

    @Override
    public void applyMargin() {
        if(secondTime){
            super.applyMargin();
        }

    }
}