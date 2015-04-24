package com.mobile.utils.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.objects.home.type.EnumTeaserTargetType;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 *
 * @author sergiopereira
 */
public class HomeFeaturedTeaserAdapter extends RecyclerView.Adapter<HomeFeaturedTeaserAdapter.ProductViewHolder> {

    private ArrayList<BaseTeaserObject> mDataSet;

    private OnClickListener mOnClickListener;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
     *
     * @author sergiopereira
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Data
        public TextView title;
        public TextView sub;
        public ImageView image;

        /**
         * Constructor
         *
         * @param view -  the view holder
         */
        public ProductViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.home_teaser_stores_image);
            title = (TextView) view.findViewById(R.id.home_teaser_stores_text_title);
            sub = (TextView) view.findViewById(R.id.home_teaser_stores_text_sub);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     *
     * @param data - the array lisl
     */
    public HomeFeaturedTeaserAdapter(ArrayList<BaseTeaserObject> data, OnClickListener listener) {
        mDataSet = data;
        mOnClickListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout._def_home_teaser_featured_stores_item, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        return CollectionUtils.isEmpty(mDataSet) ? 0 : mDataSet.size();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        // Get item
        BaseTeaserObject item = mDataSet.get(position);
        // Set title
        holder.title.setText(item.getTitle());
        // Set sub title
        holder.sub.setText(item.getSubTitle());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImagePhone(), holder.image, null, R.drawable.no_image_large);
        // Set listener
        setClickableView(holder.itemView, item);
    }

    private void setClickableView(View view, BaseTeaserObject teaser) {
        if(mOnClickListener != null) {
            view.setTag(R.id.target_title, teaser.getTitle());
            view.setTag(R.id.target_type, EnumTeaserTargetType.CATALOG.getType());
            view.setTag(R.id.target_url, teaser.getUrl());
            view.setOnClickListener(mOnClickListener);
        }
    }

}
