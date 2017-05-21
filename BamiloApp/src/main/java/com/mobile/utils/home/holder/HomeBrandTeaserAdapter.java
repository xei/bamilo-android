package com.mobile.utils.home.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.service.objects.home.object.BaseTeaserObject;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to fill the brand teasers container.
 * @author sergiopereira
 */
public class HomeBrandTeaserAdapter extends RecyclerView.Adapter<HomeBrandTeaserAdapter.ViewHolder> {
    public static final String TAG = HomeBrandTeaserAdapter.class.getSimpleName();
    private final View.OnClickListener mOnClickListener;
    private ArrayList<BaseTeaserObject> mDataSet;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     * @author sergiopereira
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private View mProgress;

        public ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
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
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_teaser_brand_item, parent, false));
        // Calculate and set width to fill the parent
        setWidthToFillParent(parent, viewHolder);
        // Return
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
        // Set image
        //RocketImageLoader.instance.loadImage(item.getImage(), holder.mImage, holder.mProgress, R.drawable.no_image_large);
        ImageManager.getInstance().loadImage(item.getImage(), holder.mImage, holder.mProgress, R.drawable.no_image_large, false);
        // Set listener and tags
        TeaserViewFactory.setClickableView(holder.itemView, item, mOnClickListener, position);
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

    /**
     * Calculate the new view holder width foreach item to fill the parent width.<br>
     * Is warranted a min width for each item that comes from xml (layout_width).
     * @param parent The view group
     * @param viewHolder The current view holder
     */
    private void setWidthToFillParent(ViewGroup parent, ViewHolder viewHolder) {
        // Validate size
        if (getItemCount() > 0) {
            // Get view holder params
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            // Calculate the new width to fill parent width
            int width = (parent.getWidth() / getItemCount()) - params.leftMargin - params.rightMargin;
            // Validate the new width is greater than width from params (xml)
            if (width > params.width) {
                // Apply the new width
                params.width = width;
            }
        }
    }
    
}
