package com.mobile.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ProductUtils;
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
public class RecentlyViewedAdapter extends RecyclerView.Adapter<RecentlyViewedAdapter.ProductMultipleHolder> {

    public final static String TAG = RecentlyViewedAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;

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
        mInflater = LayoutInflater.from(context);
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
    private void setImage(ProductMultipleHolder prodItem, ProductMultiple addableToCart){
        // Set is new image
        prodItem.newArrivalBadge.setVisibility(addableToCart.isNew() ? View.VISIBLE : View.GONE);
        // Set image
        RocketImageLoader.instance.loadImage(addableToCart.getImageUrl(), prodItem.image,  null, R.drawable.no_image_small);
    }

    /**
     * Set the text content
     * @param prodItem
     * @param addableToCart
     * @author sergiopereira
     */
    private void setTextContent(ProductMultipleHolder prodItem, ProductMultiple addableToCart) {
        if (addableToCart != null) {
            // Set brand
            String brand = addableToCart.getBrand();
            if (brand != null) {
                brand = brand.toUpperCase();
            }
            prodItem.brand.setText(brand);
            // Set name
            prodItem.name.setText(addableToCart.getName());

            ProductUtils.setPriceRules(addableToCart, prodItem.price, prodItem.discount);
            // Validate special price
            ProductUtils.setDiscountRules(addableToCart, prodItem.percentage);

            if (itemsClass == ProductMultiple.class) {
                // Set visibility
                prodItem.deleteButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public ProductMultipleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductMultipleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.addabletocart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductMultipleHolder holder, int position) {
        // Get addableToCart
        ProductMultiple addableToCart = this.items.get(position);
        // Set Image
        setImage(holder, addableToCart);
        // Set brand, name and price
        setTextContent(holder, addableToCart);
        // Set variation
        ProductUtils.setVariationContent(holder.varianceButton, addableToCart);
        // Set clickable views
        setClickableViews(position, holder.container, holder.addToCartButton, holder.varianceButton);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ProductMultipleHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView discount;
        private TextView price;
        private TextView percentage;
        private TextView brand;
        private TextView newArrivalBadge;
        private TextView varianceButton;
        private View addToCartButton;
        private View deleteButton;
        private View container;

        public ProductMultipleHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.item_image);
            name = (TextView) itemView.findViewById(R.id.item_name);
            discount = (TextView) itemView.findViewById(R.id.item_discount);
            price = (TextView) itemView.findViewById(R.id.item_regprice);
            percentage = (TextView) itemView.findViewById(R.id.item_percentage);
            brand = (TextView) itemView.findViewById(R.id.item_brand);
            newArrivalBadge = (TextView) itemView.findViewById(R.id.new_arrival_badge);
            varianceButton = (TextView) itemView.findViewById(R.id.button_variant);
            addToCartButton = itemView.findViewById(R.id.button_shop);
            deleteButton = itemView.findViewById(R.id.button_delete);
            container = itemView.findViewById(R.id.addabletocart_item_container);
        }
    }

}
