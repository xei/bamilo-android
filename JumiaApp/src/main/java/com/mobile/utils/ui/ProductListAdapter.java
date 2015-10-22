package com.mobile.utils.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.interfaces.OnProductListViewHolderClickListener;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.List;

/**
 * Created by rsoares on 10/22/15.
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListViewHolder>{

    protected List<ProductRegular> mDataSet;

    private OnProductListViewHolderClickListener mOnViewHolderClicked;

    public ProductListAdapter(@NonNull List<ProductRegular> mDataSet) {
        this.mDataSet = mDataSet;
    }

    public ProductListAdapter(@NonNull List<ProductRegular> mDataSet, @Nullable OnProductListViewHolderClickListener mOnViewHolderClicked) {
        this(mDataSet);
        this.mOnViewHolderClicked = mOnViewHolderClicked;
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gen_product_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductListViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Set the favourite view.
     * @param holder - the view holder
     * @param item - the product
     */
    protected void setFavourite(ProductListViewHolder holder, ProductRegular item, int position) {
        // Set favourite data
        holder.favourite.setTag(R.id.position, position);
        holder.favourite.setSelected(item.isWishList());
//        holder.favourite.setOnClickListener(this);
    }

    /**
     * Set the product price.
     * @param holder - the view holder
     * @param item - the product
     */
    protected void setProductPrice(ProductListViewHolder holder, ProductRegular item) {

        ProductUtils.setPriceRules(item, holder.price, holder.discount);
        // Case discount
        ProductUtils.setDiscountRules(item, holder.percentage);
    }

    /**
     * Validate and set views from list layout.
     * @param holder - the view holder
     * @param item - the product
     */
    protected void setSpecificViewForListLayout(ProductListViewHolder holder, ProductRegular item) {
        // Validate list views
        if(holder.rating != null && holder.reviews != null) {
            // Show rating
            if (item.getAvgRating() > 0) {
                holder.rating.setRating((float) item.getAvgRating());
                holder.rating.setVisibility(View.VISIBLE);
                int count = item.getTotalRatings();
//                String string = mContext.getResources().getQuantityString(R.plurals.numberOfRatings, count, count);
                String string = "("+count+")";
                holder.reviews.setText(string);
            }
            // Hide rating
            else {
                holder.rating.setVisibility(View.INVISIBLE);
                holder.reviews.setText("");
            }
        }
    }

}
