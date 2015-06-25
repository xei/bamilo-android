package com.mobile.utils.home.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * 
 * @author sergiopereira
 *
 */
public class HomeSmallTeaserAdapter extends RecyclerView.Adapter<HomeSmallTeaserAdapter.ViewHolder> {

    private int mViewHolderWidth;

    private View.OnClickListener mOnClickListener;

    private ArrayList<BaseTeaserObject> mDataSet;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     * @author sergiopereira
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Views
        public TextView mTitle;
        private TextView mSubTitle;
        private ImageView mImage;
        private View mProgress;

        /**
         * Constructor
         */
        public ViewHolder(View view) {
            super(view);
            mTitle = (TextView) view.findViewById(R.id.home_teaser_small_title);
            mSubTitle = (TextView) view.findViewById(R.id.home_teaser_small_sub_title);
            mImage = (ImageView) view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @author sergiopereira
     */
    public HomeSmallTeaserAdapter(ArrayList<BaseTeaserObject> teasers, View.OnClickListener listener, int width) {
        mDataSet = teasers;
        mOnClickListener = listener;
        mViewHolderWidth = width;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public HomeSmallTeaserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_teaser_small_item, parent, false));
        // Set the width
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
        int adjust = parent.getContext().getResources().getDimensionPixelSize(R.dimen.dimen_mat_1_25px);
        params.width = mViewHolderWidth - params.leftMargin - params.rightMargin - adjust;
        return viewHolder;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get item
        BaseTeaserObject item = mDataSet.get(position);
        // Set title
        holder.mTitle.setText(item.getTitle());
        // Set title
        holder.mSubTitle.setText(item.getSubTitle());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImage(), holder.mImage, holder.mProgress, R.drawable.no_image_large);
        // Set listener and tags
        TeaserViewFactory.setClickableView(holder.itemView, item, mOnClickListener);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return CollectionUtils.isNotEmpty(mDataSet) ? mDataSet.size() : 0;
    }
    
}
