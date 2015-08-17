package com.mobile.utils.catalog;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
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
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.newFramework.objects.catalog.Banner;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

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

    private boolean isTabletInLandscape;

    private ArrayList<ProductRegular> mDataSet;
    
    private Context mContext;
    
    private int mLastPosition = -1;

    private OnViewHolderClickListener mOnViewHolderClicked;

    private String mBannerImage;

    private String mUrl;

    private String mTargetType;

    private String mTitle;

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
    public CatalogGridAdapter(Context context, ArrayList<ProductRegular> data) {
        mContext = context;
        mDataSet = data;
        isShowingGridLayout = CustomerPreferences.getCatalogLayout(mContext);
        isTabletInLandscape = DeviceInfoHelper.isTabletInLandscape(mContext);
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
        ProductRegular item = mDataSet.get(position);
        // Set name
        holder.name.setText(item.getName());
        // Set brand
        holder.brand.setText(item.getBrand());
        // Set is new image
        holder.recent.setSelected(item.isNew());
        // Set image
        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
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
     */
    private void setFavourite(ProductViewHolder holder, ProductRegular item, int position) {
        // Set favourite data
        holder.favourite.setTag(R.id.position, position);
        holder.favourite.setSelected(item.isWishList());
        holder.favourite.setOnClickListener(this);

    }
    
    /**
     * Set the product price.
     * @param holder - the view holder
     * @param item - the product
     */
    private void setProductPrice(ProductViewHolder holder, ProductRegular item) {
        // Case discount
        if(item.hasDiscount()) {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getSpecialPrice()));
            holder.price.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setPaintFlags( holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.percentage.setText(String.format(mContext.getString(R.string.format_discount_percentage), item.getMaxSavingPercentage()));
            holder.percentage.setVisibility(View.VISIBLE);
        }
        // Case normal
        else {
            holder.discount.setText(CurrencyFormatter.formatCurrency(item.getPrice()));
            holder.price.setText("");
            holder.percentage.setVisibility(View.GONE);
        }
    }
    
    /**
     * Validate and set views from list layout.
     * @param holder - the view holder
     * @param item - the product
     */
    private void setSpecificViewForListLayout(ProductViewHolder holder, ProductRegular item) {
        // Validate list views
        if(holder.rating != null && holder.reviews != null) {
            // Show rating
            if (item.getAvgRating() > 0) {
                holder.rating.setRating((float) item.getAvgRating());
                holder.rating.setVisibility(View.VISIBLE);
                int count = item.getTotalReviews();
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
    
//    /**
//     * Append the new data to the current data.
//     * @param newDataSet - the new data
//     */
//    public void updateData(ArrayList<Product> newDataSet){
//        CollectionUtils.addAll(mDataSet, newDataSet);
//        notifyDataSetChanged();
//    }
//
//    /**
//     * Replace the current data and update the adapter.
//     * @param newDataSet - the new data
//     */
//    public void replaceData(ArrayList<Product> newDataSet){
//        mDataSet = newDataSet;
//
//        notifyDataSetChanged();
//    }

    /**
     * Get the product from the current data.
     * @param position - the respective product position
     * @return Product or null
     */
    public ProductRegular getItem(int position) {
        return CollectionUtils.isEmpty(mDataSet) ?  null : mDataSet.get(position);
    }
    
    /**
     * Set the listener the click on view holder.
     * @param listener - the listener
     */
    public void setOnViewHolderClickListener(OnViewHolderClickListener listener) {
        this.mOnViewHolderClicked = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Case other sent to listener
        if (mOnViewHolderClicked != null) {
            // Get view id
            int id = view.getId();
            // position
            int position = (Integer) view.getTag(R.id.position);
            // Case favourite
            if (id == R.id.image_is_favourite) {
                mOnViewHolderClicked.onWishListClick(view, this, position);
            }
            // Case header
            else if (id == R.id.catalog_header_image_frame) {
                mOnViewHolderClicked.onHeaderClick(mTargetType, mUrl, mTitle);
            }
            // Case item
            else {
                mOnViewHolderClicked.onViewHolderClick(this, position);
            }
        }
    }

    /**
     * store info related to catalog header, and show it
     */
    public void setHeader(@Nullable Banner banner){
        if(banner == null) {
            hideHeaderView();
        } else {
            mBannerImage = !isTabletInLandscape ? banner.getPhoneImage() : banner.getTabletImage();
            mUrl = banner.getUrl();
            mTitle = banner.getTitle();
            mTargetType = banner.getTargetType();
            showHeaderView();
        }
    }

    /**
     * set header image and and listener
     */
    private void setHeaderImage(ProductViewHolder holder) {
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
