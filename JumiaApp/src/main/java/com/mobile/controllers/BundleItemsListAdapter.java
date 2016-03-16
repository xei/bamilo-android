package com.mobile.controllers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Ad
 * @author paulocarvalho
 *
 */
public class BundleItemsListAdapter extends RecyclerView.Adapter<BundleItemsListAdapter.ViewHolder> implements OnItemSelectedListener {
    
    private final ArrayList<ProductBundle> mDataset;
    private final OnItemSelected itemSelected;
    private final OnItemChecked itemChecked;
    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br> 
     * @author paulocarvalho
     *
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Data
        public TextView mBrand;
        public TextView mTitle;
        private final ImageView mImage;
        private final ProgressBar mProgress;
        private final TextView mPrice;
        private final CheckBox mCheck;
        private final View mContainer;
        private final IcsSpinner mSizeSpinner;
        private final RelativeLayout mSizeSpinnerContainer;
        
        /**
         * Constructor
         */
        public ViewHolder(View view) {
            super(view);
            mContainer = view.findViewById(R.id.item_container);
            mBrand = (TextView) view.findViewById(R.id.item_brand);
            mTitle = (TextView) view.findViewById(R.id.item_title);
            mImage = (ImageView) view.findViewById(R.id.image_view);
            mCheck = (CheckBox) view.findViewById(R.id.item_check);
            mSizeSpinnerContainer = (RelativeLayout) view.findViewById(R.id.bundle_size_container);
            mSizeSpinner = (IcsSpinner) view.findViewById(R.id.bundle_simple_button);
            mProgress = (ProgressBar) view.findViewById(R.id.image_loading_progress);
            mPrice = (TextView) view.findViewById(R.id.item_price);
        }
    }
    
    public interface OnItemSelected {
        void SelectedItem();
    }
    
    public interface OnItemChecked {
        void checkItem(ProductBundle selectedProduct, boolean isChecked, int pos);
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @author paulocarvalho
     */
    public BundleItemsListAdapter(ArrayList<ProductBundle> bundleItemsList, OnItemSelected selectedClickListener, OnItemChecked checkedClickListener) {
        mDataset = bundleItemsList;
        itemSelected = selectedClickListener;
        itemChecked = checkedClickListener;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public BundleItemsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_bundle, parent, false));
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Get item
        final ProductBundle item = mDataset.get(position);
        // Set brand
        holder.mBrand.setText(item.getBrandName());
        // Set title
        holder.mTitle.setText(item.getName());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.mImage, holder.mProgress, R.drawable.no_image_large);
        // Set price
        if(item.hasDiscount() ){
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
        } else {
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
        }

        holder.mContainer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                itemSelected.SelectedItem();
            }
        });
        
        if(item.isChecked()){
            holder.mCheck.setChecked(true);
            if(position == 0){
                holder.mCheck.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.mCheck.setEnabled(false);
                    }
                });
            } else {
                holder.mCheck.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.mCheck.setEnabled(true);
                    }
                });
            }
        } else {
            holder.mCheck.setChecked(false);
            
        }

        holder.mCheck.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(position != 0){
                    if(holder.mCheck.isChecked()){
                        item.setChecked(true);
                    } else {
                        item.setChecked(false);
                    }
                    itemChecked.checkItem(item, holder.mCheck.isChecked(), position); 
                }
            }
        });

        // Set size
        setSizeContainer(holder, item, position);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        // Return the size of your data set (invoked by the layout manager)
        return mDataset == null ? 0 : mDataset.size();
    }

    private ProductBundle getItem(int position) {
        return mDataset.get(position);
    }

    private void setSizeContainer(ViewHolder view, ProductBundle item, int position){
        // all products have at least one simple
        if(item.hasMultiSimpleVariations()) {
            view.mSizeSpinnerContainer.setVisibility(View.VISIBLE);
            // Get sizes
            ArrayList<ProductSimple> sizes = item.getSimples();
            // Create an ArrayAdapter using the sizes values
            ArrayAdapter<ProductSimple> adapter = new ArrayAdapter<>(JumiaApplication.INSTANCE.getApplicationContext(), R.layout.campaign_spinner_item, sizes);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.campaign_spinner_dropdown_item);
            // Apply the adapter to the spinner
            view.mSizeSpinner.setAdapter(adapter);
            // Save position in spinner
            view.mSizeSpinner.setTag(R.id.target_position, position);
            // Check pre selection
            view.mSizeSpinner.setSelection(item.getSelectedSimplePosition());
            // Force reload content to redraw the default selection value
            adapter.notifyDataSetChanged();
            view.mSizeSpinner.setOnItemSelectedListener(this);
        } else {
            view.mSizeSpinnerContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
        // Save selected simple position
        getItem((int) parent.getTag(R.id.target_position)).setSelectedSimplePosition(position);
    }

    @Override
    public void onNothingSelected(IcsAdapterView<?> parent) {
        // ...
    }

}
