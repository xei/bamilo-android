package com.mobile.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mobile.framework.database.FavouriteTableHelper;
import com.mobile.framework.objects.Product;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.view.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * Class used to fill the grid catalog.
 * @author sergiopereira
 *
 */
public class CatalogGridAdapter extends RecyclerView.Adapter<CatalogGridAdapter.ProductViewHolder> implements OnClickListener {
    
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    
    private static final int ITEM_VIEW_TYPE_LIST = 1;
    
    private static final int ITEM_VIEW_TYPE_GRID = 2;
    
    private static final int ITEM_VIEW_TYPE_FOOTER = 3;
    
    private ArrayList<Product> mDataset;
    
    private Context mContext;
    
    private int lastPosition = -1;

    private OnViewHolderClickListener mOnViewHolderClicked;

    private boolean isShowingGridLayout;

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
        
        /**
         * Constructor 
         * @param view
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
        }
    }

    /**
     * Provide a suitable constructor (depends on the kind of data)
     * @param context
     * @param productTeaserGroup
     * @param parentClickListener
     * @author sergiopereira
     */
    public CatalogGridAdapter(Context context, ArrayList<Product> data) {
        mContext = context;
        mDataset = data; 
        isShowingGridLayout = CustomerPreferences.getCatalogLayout(mContext);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup, int)
     */
    @Override
    public CatalogGridAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductViewHolder holder = null; 
        if(viewType == ITEM_VIEW_TYPE_HEADER)
            ; // TODO
        else if (viewType == ITEM_VIEW_TYPE_LIST)
            holder = new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item_rounded, parent, false));
        else if (viewType == ITEM_VIEW_TYPE_GRID)
            holder = new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item_grid_rounded, parent, false));
        else if (viewType == ITEM_VIEW_TYPE_FOOTER)
            ; // TODO
        else holder = new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item_rounded, parent, false)); 
        // Create a new view
        return holder;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        return isShowingGridLayout ? ITEM_VIEW_TYPE_GRID : ITEM_VIEW_TYPE_LIST;
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
    
    /*
     * (non-Javadoc)
     * @see android.support.v7.widget.RecyclerView.Adapter#onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        // Get item
        Product item = mDataset.get(position);
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
        // 
        //setAnimation(holder, position);
    }
    
    /**
     * Set the favourite view.
     * @param holder - the view holder
     * @param item - the product
     * @param position - the current position
     * @author sergiopereira
     */
    private void setFavourite(ProductViewHolder holder, Product item, int position) {
        holder.favourite.setTag(R.id.position, position);
        holder.favourite.setSelected(item.getAttributes().isFavourite());
        holder.favourite.setOnClickListener(this);
    }
    
    /**
     * Set the product price.
     * @param holder - the view holder
     * @param item - the product
     * @author sergiopereira
     */
    private void setProductPrice(ProductViewHolder holder, Product item) {
        // Case discount
        if(item.hasDiscountPercentage()) {
            holder.discount.setText(item.getSpecialPrice());
            holder.price.setText(item.getPrice());
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
     * Set some that for 
     * @param holder
     * @param item
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
     * TODO: Add the animation to holder class
     * @param holder
     * @param position
     */
    private void setAnimation(ProductViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.entry_up_from_bottom : R.anim.entry_down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
    }


    /**
     * 
     * @param isShowingGridLayout
     */
    public void updateLayout(boolean isShowingGridLayout){
        this.isShowingGridLayout = isShowingGridLayout;
        notifyDataSetChanged();
    }
    
    /**
     * 
     * @param newDataset
     */
    public void updateData(ArrayList<Product> newDataset){
        CollectionUtils.addAll(mDataset, newDataset);
        notifyDataSetChanged();
    }
    
    /**
     * 
     * @param newDataset
     */
    public void replaceData(ArrayList<Product> newDataset){
        mDataset = newDataset;
        notifyDataSetChanged();
    }

    /**
     * 
     * @param position
     * @return
     */
    public Product getItem(int position) {
        return CollectionUtils.isEmpty(mDataset) ?  null : mDataset.get(position);
    }
    
    /**
     * 
     * @param listener
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
        // Get view id
        int id = view.getId();
        if(id == R.id.image_is_favourite) onClickFavouriteButton(view);
        else if(mOnViewHolderClicked != null) 
            mOnViewHolderClicked.onViewHolderClick(this, view, (Integer) view.getTag(R.id.position), null);
    }
    
    /**
     * Process the click on the favourite button
     * @param view
     * @author sergiopereira
     */
    private void onClickFavouriteButton(View view) {
        // Get id
        int position = (Integer) view.getTag(R.id.position);
        // Get item
        Product product = mDataset.get(position);
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
}
