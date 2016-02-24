package com.mobile.controllers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Ad
 * @author sergiopereira
 *
 */
public class RelatedItemsListAdapter extends RecyclerView.Adapter<RelatedItemsListAdapter.ViewHolder> {
    
    private ArrayList<ProductRegular> mDataset;
    private OnClickListener mParentClickListener;

    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br> 
     * @author sergiopereira
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Data
        public TextView mBrand;
        public TextView mTitle;
        private ImageView mImage;
        private ProgressBar mProgress;
        private TextView mPrice;
        private View mContainer;
        
        /**
         * Constructor
         */
        public ViewHolder(View view) {
            super(view);
            mContainer = view.findViewById(R.id.item_container);
            mBrand = (TextView) view.findViewById(R.id.item_brand);
            mTitle = (TextView) view.findViewById(R.id.item_title);
            mImage = (ImageView) view.findViewById(R.id.image_view);
            mProgress = (ProgressBar) view.findViewById(R.id.image_loading_progress);
            mPrice = (TextView) view.findViewById(R.id.item_price);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @author sergiopereira
     */
    public RelatedItemsListAdapter(ArrayList<ProductRegular> relatedItemsList, OnClickListener parentClickListener) {
        mDataset = relatedItemsList;
        mParentClickListener = parentClickListener;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public RelatedItemsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_small, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Get item
        ProductRegular item = mDataset.get(position);
        // Set brand
        holder.mBrand.setText(item.getBrandName());
        // Set title
        holder.mTitle.setText(item.getName());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.mImage, holder.mProgress, R.drawable.no_image_large);
        // Set price
        if(item.hasDiscount()){
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
        } else {
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
        }
        // Set listener and tags
        holder.mContainer.setTag(R.id.target_sku, item.getSku());
        holder.mContainer.setOnClickListener(mParentClickListener);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return mDataset == null ? 0 : mDataset.size();
    }
    
}
