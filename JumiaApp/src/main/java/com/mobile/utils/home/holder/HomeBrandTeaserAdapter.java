package com.mobile.utils.home.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * 
 * @author sergiopereira
 *
 */
public class HomeBrandTeaserAdapter extends RecyclerView.Adapter<HomeBrandTeaserAdapter.ViewHolder> {

    private final View.OnClickListener mOnClickListener;

    private ArrayList<BaseTeaserObject> mDataSet;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     * @author sergiopereira
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Data
        private ImageView mImage;
        private View mProgress;

        /**
         * Constructor
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param teasers
     * @author sergiopereira
     */
    public HomeBrandTeaserAdapter(ArrayList<BaseTeaserObject> teasers, View.OnClickListener listener) {
        mDataSet = teasers;
        mOnClickListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public HomeBrandTeaserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_teaser_brand_item, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get item
        BaseTeaserObject item = mDataSet.get(position);
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
        // Return the size of your data set (invoked by the layout manager)
        return CollectionUtils.isNotEmpty(mDataSet) ? mDataSet.size() : 0;
    }
    
}
