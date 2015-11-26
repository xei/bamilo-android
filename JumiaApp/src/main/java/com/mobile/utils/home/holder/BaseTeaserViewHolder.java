package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;

import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.home.TeaserViewFactory;

/**
 * Class used to represent the base of teasers.
 */
public abstract class BaseTeaserViewHolder { //extends RecyclerView.ViewHolder {

    protected static final int NO_OFFSET = 0;

    public final View itemView;

    protected Context mContext;
    //TODO validate necessity
    protected final View.OnClickListener mParentClickListener;

    protected int mOffset = NO_OFFSET;

    protected boolean isRtl;

    /**
     * Constructor
     * @param context The application context
     * @param itemView The inflated layout
     * @param onClickListener The click listener
     */
    public BaseTeaserViewHolder(Context context, View itemView, View.OnClickListener onClickListener) {
        //super(itemView);
        this.itemView = itemView;
        mContext = context;
        mParentClickListener = onClickListener;
        isRtl = ShopSelector.isRtl();
        // Get view offset
        mOffset = TeaserViewFactory.getViewHolderOffset(context);
        // Set offset margin
        applyMargin();
    }

    /**
     * Apply margins to view
     */
    public void applyMargin(){
        itemView.setPadding(itemView.getPaddingLeft() + mOffset, itemView.getPaddingTop(), itemView.getPaddingRight() + mOffset, itemView.getPaddingBottom());
    }

    /**
     * Method to update the view
     */
    public void onUpdate() {
        // ...
    }

    /**
     * Method to set the view
     * @param group The teaser group
     */
    public abstract void onBind(BaseTeaserGroupType group);

}