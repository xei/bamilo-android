package com.mobile.utils.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.framework.objects.home.object.BaseTeaserObject;
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

        /**
         * Constructor
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.home_teaser_brand_image);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param teasers
     * @author sergiopereira
     */
    public HomeBrandTeaserAdapter(ArrayList<BaseTeaserObject> teasers) {
        mDataSet = teasers;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public HomeBrandTeaserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_brand_item, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Get item
        BaseTeaserObject item = mDataSet.get(position);
        // Set image
        RocketImageLoader.instance.loadImage(item.getImagePhone(), holder.mImage);
        // Set listener and tags
        //holder.mContainer.setOnClickListener(mParentClickListener);
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
