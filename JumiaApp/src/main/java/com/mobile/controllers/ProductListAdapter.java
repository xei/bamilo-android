package com.mobile.controllers;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ProductListViewHolder;
import com.mobile.utils.ui.ProductUtils;
import com.mobile.view.R;

import java.util.List;

/**
 * Created by rsoares on 10/22/15.
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListViewHolder>{

    protected List<? extends ProductRegular> mDataSet;

    protected Resources resources;

    public ProductListAdapter(@NonNull List<? extends ProductRegular> mDataSet) {
        this.mDataSet = mDataSet;
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
        // Show / Hide New Arrival Badge
        holder.newArrivalBadge.setVisibility(item.isNew() ? View.VISIBLE : View.GONE);
        //Show/Hide Shop First
        if(ShopSelector.isRtlShop()){
            holder.shopFirst.setVisibility(View.GONE);
        }
        // Set image
        RocketImageLoader.instance.loadImage(item.getImageUrl(), holder.image, holder.progress, R.drawable.no_image_small);
        // Set is favorite image
        setFavourite(holder, item, position);
        // Set rating and reviews
        setSpecificViewForListLayout(holder, item);
        // Set prices
        setProductPrice(holder, item);
    }


    public void setResources(Resources resources)
    {
        this.resources = resources;
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
