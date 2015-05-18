package com.mobile.utils.catalog;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.database.FavouriteTableHelper;
import com.mobile.framework.objects.CatalogPage;
import com.mobile.framework.objects.Product;
import com.mobile.framework.utils.DeviceInfoHelper;
import com.mobile.interfaces.OnHeaderClickListener;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.<br>
 * Can be used to add a header and footer view.
 * @author sergiopereira
 */
public class CatalogGridAdapter extends RecyclerView.Adapter<CatalogGridAdapter.ProductViewHolder> implements OnClickListener {
    
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    
    private static final int ITEM_VIEW_TYPE_LIST = 1;
    
    private static final int ITEM_VIEW_TYPE_GRID = 2;
    
    private static final int ITEM_VIEW_TYPE_FOOTER = 3;

    private static final int HEADER_POSITION = 0;

    private boolean isToShowHeader;

    private boolean isToShowFooter;

    private boolean isShowingGridLayout;

    private ArrayList<Product> mDataSet;
    
    private Context mContext;
    
    private int mLastPosition = -1;

    private OnViewHolderClickListener mOnViewHolderClicked;

    private String mBannerImage = "";

    private String mUrl = "";

    private String mTargetType = "";

    private String mTitle = "";

    private OnHeaderClickListener mOnHeaderClicked;
    /**
     * Provide a reference to the views for each data item.<br>
     * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br> 
     * @author sergiopereira
     *
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Data
        public TextView name;
        public TextView brand;
        public ImageView image;
        public View progress;
        public RatingBar rating;
        public TextView discount;
        public TextView price;
        public TextView percentage;
        public TextView reviews;
        public ImageView recent;
        public ImageView favourite;
        public ImageView headerImage;
        
        /**
         * Constructor 
         * @param view -  the view holder
         */
        public ProductViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            brand = (TextView) view.findViewById(R.id.item_brand);
            image = (ImageView) view.findViewById(R.id.image_view);
            progress = view.findViewById(R.id.image_loading_progress);
            rating = (RatingBar) view.findViewById(R.id.item_rating);
            price = (TextView) view.findViewById(R.id.item_regprice);
            discount = (TextView) view.findViewById(R.id.item_discount);
            percentage = (TextView) view.findViewById(R.id.discount_percentage);
            reviews = (TextView) view.findViewById(R.id.item_reviews);
            brand = (TextView) view.findViewById(R.id.item_brand);
            recent = (ImageView) view.findViewById(R.id.image_is_new);
            favourite = (ImageView) view.findViewById(R.id.image_is_favourite);
            headerImage = (ImageView) view.findViewById(R.id.catalog_header_image);
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param context - the application context
     * @param data - the array lisl
     */
    public CatalogGridAdapter(Context context, ArrayList<Product> data) {
        mContext = context;
        mDataSet = data;
        isShowingGridLayout = CustomerPreferences.getCatalogLayout(mContext);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.catalog_item_list_rounded;
        if(viewType == ITEM_VIEW_TYPE_HEADER) layout = R.layout._def_catalog_fragment_header;
        else if (viewType == ITEM_VIEW_TYPE_LIST) layout = R.layout.catalog_item_list_rounded;
        else if (viewType == ITEM_VIEW_TYPE_GRID) layout = R.layout.catalog_item_grid_rounded;
        else if (viewType == ITEM_VIEW_TYPE_FOOTER) layout = R.layout._def_catalog_fragment_footer;
        // Create a new view
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        // Case header
        if(isHeader(position)) return ITEM_VIEW_TYPE_HEADER;
        // Case footer
        if(isFooter(position)) return ITEM_VIEW_TYPE_FOOTER;
        // Case item
        return isShowingGridLayout ? ITEM_VIEW_TYPE_GRID : ITEM_VIEW_TYPE_LIST;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemCount()
     */
    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return mDataSet == null ? 0 : mDataSet.size() + (hasHeaderView() ? 1 : 0) + (hasFooterView() ? 1 : 0);
    }

    /**
     * Get the real position validating the header view.
     * @param position - the virtual position
     * @return int
     */
    private int getRealPosition(int position) {
        return position - (hasHeaderView() ? 1 : 0);
    }

    /*
      * (non-Javadoc)
      * @see android.support.v7.widget.RecyclerView.Adapter#onViewDetachedFromWindow(android.support.v7.widget.RecyclerView.ViewHolder)
      */
    @Override
    public void onViewDetachedFromWindow(ProductViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Cancel the animation for detached views
        holder.itemView.clearAnimation();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        // Set animation
        setAnimation(holder, position);
        // Case header
        if(isHeader(position)){
            setHeaderImage(holder);
            return;
        }
        // Case footer
        if(isFooter(position)){
            return;
        }
        // Get real position
        position = getRealPosition(position);
        // Get item
        Product item = mDataSet.get(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrand());
        // Set is new image
        holder.recent.setSelected(item.getAttributes().isNew());
        // Set image
        RocketImageLoader.instance.loadImage(item.getFirstImageURL(), holder.image, holder.progress, R.drawable.no_image_small);
        // Set is favorite image
        setFavourite(holder, item, position);
        // Set rating and reviews
        setSpecificViewForListLayout(holder, item);
        // Set prices
        setProductPrice(holder, item);
        // Set the parent layout
        holder.itemView.setTag(R.id.position, position);
        holder.itemView.setOnClickListener(this);
    }

    /**
     *Validate if the current position is the header view.
     * @param position - the current position
     * @return true or false
     */
    private boolean isHeader(int position) {
        return isToShowHeader && position == HEADER_POSITION;
    }

    /**
     * Validate if the current position is the footer view.
     * @param position - the current position
     * @return true or false
     */
    private boolean isFooter(int position) {
        return isToShowFooter && position == mDataSet.size() + (hasHeaderView() ? 1 : 0);
    }


    /**
     * Set the favourite view.
     * @param holder - the view holder
     * @param item - the product
     * @param position - the current position
     */
    private void setFavourite(ProductViewHolder holder, Product item, int position) {
        // TODO: REMOVE THIS PLEASE :)
        boolean isFavourite = false;
        try {
            isFavourite = FavouriteTableHelper.verifyIfFavourite(item.getSKU());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Set favourite data
        holder.favourite.setTag(R.id.position, position);
        holder.favourite.setSelected(isFavourite);
        holder.favourite.setOnClickListener(this);
        item.getAttributes().setFavourite(isFavourite);
    }
    
    /**
     * Set the product price.
     * @param holder - the view holder
     * @param item - the product
     */
    private void setProductPrice(ProductViewHolder holder, Product item) {
        // Case discount
        if(item.hasDiscountPercentage()) {
            holder.discount.setText(item.getSpecialPrice());
            holder.price.setText(item.getPrice());
            holder.price.setPaintFlags( holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.percentage.setText("-" + item.getMaxSavingPercentage().intValue() + "%");
            holder.percentage.setVisibility(View.VISIBLE);
        }
        // Case normal
        else {
            holder.discount.setText(item.getSpecialPrice());
            holder.price.setText("");
            holder.percentage.setVisibility(View.GONE);
        }
    }
    
    /**
     * Validate and set views from list layout.
     * @param holder - the view holder
     * @param item - the product
     */
    private void setSpecificViewForListLayout(ProductViewHolder holder, Product item) {
        // Validate list views
        if(holder.rating != null && holder.reviews != null) {
            // Show rating
            if (item.getRating() != null && item.getRating() > 0) {
                holder.rating.setRating(item.getRating().floatValue());
                holder.rating.setVisibility(View.VISIBLE);
                int count = item.getReviews();
                String string = mContext.getResources().getQuantityString(R.plurals.numberOfRatings, count, count);
                holder.reviews.setText(string);
            }
            // Hide rating
            else {
                holder.rating.setVisibility(View.INVISIBLE);
                holder.reviews.setText("");
            }
        }
    }

    /**
     * Set an animation for new items.
     * @param holder - the view holder
     * @param position - the current position
     */
    private void setAnimation(ProductViewHolder holder, int position) {
        if(position > mLastPosition) {
            //Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.entry_up_from_bottom);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
            //Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }


    /**
     * Set the flag used to switch between list or grid layout
     * @param isShowingGridLayout - the flag
     */
    public void updateLayout(boolean isShowingGridLayout){
        this.isShowingGridLayout = isShowingGridLayout;
        notifyDataSetChanged();
    }
    
    /**
     * Append the new data to the current data.
     * @param newDataSet - the new data
     */
    public void updateData(ArrayList<Product> newDataSet){
        CollectionUtils.addAll(mDataSet, newDataSet);
        notifyDataSetChanged();
    }
    
    /**
     * Replace the current data and update the adapter.
     * @param newDataSet - the new data
     */
    public void replaceData(ArrayList<Product> newDataSet){
        mDataSet = newDataSet;

        notifyDataSetChanged();
    }

    /**
     * Get the product from the current data.
     * @param position - the respective product position
     * @return Product or null
     */
    public Product getItem(int position) {
        return CollectionUtils.isEmpty(mDataSet) ?  null : mDataSet.get(position);
    }
    
    /**
     * Set the listener the click on view holder.
     * @param listener - the listener
     */
    public void setOnViewHolderClickListener(OnViewHolderClickListener listener) {
        this.mOnViewHolderClicked = listener;
    }

    /**
     * Set the listener the header.
     * @param listener - the listener
     */
    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        this.mOnHeaderClicked = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Case favourite
        if(id == R.id.image_is_favourite){
            onClickFavouriteButton(view);
        }
        // Header Click
        else if(id == R.id.catalog_header_image_frame){
            if(mOnHeaderClicked != null){
                mOnHeaderClicked.onHeaderClick(mTargetType, mUrl, mTitle);
            }
        }
        // Case other sent to listener
        else if(mOnViewHolderClicked != null){
            try{
                mOnViewHolderClicked.onViewHolderClick(this, (Integer) view.getTag(R.id.position));
            } catch (ClassCastException e){
                e.printStackTrace();
            }

        }

    }
    
    /**
     * Process the click on the favourite button
     * @param view - the view holder
     */
    private void onClickFavouriteButton(View view) {
        // Get id
        int position = (Integer) view.getTag(R.id.position);
        // Get item
        Product product = mDataSet.get(position);
        // Remove from favorite
        if(product.getAttributes().isFavourite()) {
            // Remove from table and notify user
            FavouriteTableHelper.removeFavouriteProduct(product.getSKU());
            product.getAttributes().setFavourite(false);
            view.setSelected(false);
            TrackerDelegator.trackRemoveFromFavorites(product.getSKU(), product.getPriceForTracking(),product.getRating());
            ToastFactory.REMOVED_FAVOURITE.show(mContext);
        }
        // Remove to favorite 
        else {
          FavouriteTableHelper.insertPartialFavouriteProduct(product);
          product.getAttributes().setFavourite(true);
          view.setSelected(true);
          TrackerDelegator.trackAddToFavorites(product.getSKU(),product.getBrand(),product.getPriceForTracking(), product.getRating(),product.getMaxSavingPercentage(), true, null);
          ToastFactory.ADDED_FAVOURITE.show(mContext);            
        }
    }

    /**
     * store info related to catalog header, and show it
     * @param catalogPage
     */
    public void setHeader(CatalogPage catalogPage){
        if(mContext != null){
            if(!DeviceInfoHelper.isTabletInLandscape(mContext)){
                mBannerImage = catalogPage.getmCatalogBanner().getPhoneImage();
            } else {
                mBannerImage = catalogPage.getmCatalogBanner().getTabletImage();
            }
            mUrl = catalogPage.getmCatalogBanner().getUrl();
            mTitle = catalogPage.getmCatalogBanner().getTitle();
            mTargetType = catalogPage.getmCatalogBanner().getTargetType();
            showHeaderView();
        } else {
            hideHeaderView();
        }

    }

    /**
     * set header image and and listener
     * @param holder
     */
    private void setHeaderImage(ProductViewHolder holder){
        if(!TextUtils.isEmpty(mBannerImage)){
            // set listener
            holder.itemView.setOnClickListener(this);
            // Set image
            RocketImageLoader.instance.loadImage(mBannerImage, holder.headerImage, null, R.drawable.no_image_large);
        }

    }

    /*
     * TODO: Implement a better approach for header view and footer view
     */

    public void showHeaderView() {
        isToShowHeader = true;
    }

    public void showFooterView() {
        isToShowFooter = true;
    }

    public void hideHeaderView() {
        isToShowHeader = false;
    }

    public void hideFooterView() {
        isToShowFooter = false;
    }

    public boolean hasHeaderView() {
        return isToShowHeader;
    }

    public boolean hasFooterView() {
        return isToShowFooter;
    }

}
