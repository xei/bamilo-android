package com.mobile.controllers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.product.pojo.ProductMultiple;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * This Class is used to create an adapter for the list of items addableToCart.
 * It is called by LastViewedFragment and FavouritesFragment
 *
 * @author Andre Lopes
 * @modified sergiopereira
 *
 */
public class RecentlyViewedAdapter extends RecyclerView.Adapter<RecentlyViewedAdapter.RecentlyViewedHolder> {

    public final static String TAG = RecentlyViewedAdapter.class.getSimpleName();

    private final OnClickListener mOnClickParentListener;

    private Class<? extends ProductMultiple> itemsClass;

    private ArrayList<ProductMultiple> items;

    /**
     * Constructor
     * @param context
     * @param items
     * @param parentListener
     * @author sergiopereira
     */
    public RecentlyViewedAdapter(Context context, ArrayList<ProductMultiple> items, OnClickListener parentListener) {
        this.items = items;
        LayoutInflater inflater = LayoutInflater.from(context);
        mOnClickParentListener = parentListener;

        if (!items.isEmpty()) {
            itemsClass = items.get(0).getClass();
        }
    }

    /**
     * Set this as clickable
     * @author sergiopereira
     */
    private void setClickableViews(int position, View... views){
        // For each view add position and listener
        for (View view : views) {
            view.setTag(position);
            if(mOnClickParentListener != null) view.setOnClickListener(mOnClickParentListener);
        }
    }

    /**
     * Set the image view
     * @param prodItem
     * @param addableToCart
     * @author sergiopereira
     */
    private void setImage(final RecentlyViewedHolder prodItem, ProductMultiple addableToCart){
        ImageManager.getInstance().loadImage(addableToCart.getImageUrl(), prodItem.image, prodItem.prgLoadingImage, R.drawable.no_image_large, false, new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                    Target<Drawable> target,
                    boolean isFirstResource) {
                prodItem.prgLoadingImage.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                    DataSource dataSource, boolean isFirstResource) {
                prodItem.prgLoadingImage.setVisibility(View.GONE);
                return false;
            }
        });
    }

    /**
     * Set the text content
     * @param prodItem
     * @param addableToCart
     * @author sergiopereira
     */
    private void setTextContent(RecentlyViewedHolder prodItem, ProductMultiple addableToCart) {
        if (addableToCart != null) {
            // Set brand
            String brand = addableToCart.getBrandName();
            if (brand != null) {
                brand = brand.toUpperCase();
            }
            prodItem.brand.setText(brand);
            // Set name
            prodItem.name.setText(addableToCart.getName());

            UIProductUtils.setPriceRules(addableToCart, prodItem.price, prodItem.discount);
            // Validate special price
            UIProductUtils.setDiscountRules(addableToCart, prodItem.percentage);

            if (itemsClass == ProductMultiple.class) {
                // Set visibility
                prodItem.deleteButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @NonNull
    @Override
    public RecentlyViewedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentlyViewedHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.addable_to_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyViewedHolder holder, int position) {
        // Get addableToCart
        ProductMultiple addableToCart = this.items.get(position);
        // Set Image
        setImage(holder, addableToCart);
        // Set brand, name and price
        setTextContent(holder, addableToCart);
        // Set variation
        UIProductUtils.setVariationContent(holder.varianceButton, addableToCart);
        // Set clickable views
        setClickableViews(position, holder.container, holder.addToCartButton, holder.varianceButton);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RecentlyViewedHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ProgressBar prgLoadingImage;
        private TextView name;
        private TextView discount;
        private TextView price;
        private TextView percentage;
        private TextView brand;
        //private TextView newArrivalBadge;
        private TextView varianceButton;
        private View addToCartButton;
        private View deleteButton;
        private View container;

        public RecentlyViewedHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            prgLoadingImage = itemView.findViewById(R.id.prgLoadingImage);
            name = itemView.findViewById(R.id.item_name);
            discount = itemView.findViewById(R.id.item_discount);
            price = itemView.findViewById(R.id.item_regprice);
            percentage = itemView.findViewById(R.id.item_percentage);
            brand = itemView.findViewById(R.id.item_brand);
            varianceButton = itemView.findViewById(R.id.button_variant);
            addToCartButton = itemView.findViewById(R.id.button_shop);
            deleteButton = itemView.findViewById(R.id.button_delete);
            container = itemView.findViewById(R.id.addabletocart_item_container);
        }
    }
}
