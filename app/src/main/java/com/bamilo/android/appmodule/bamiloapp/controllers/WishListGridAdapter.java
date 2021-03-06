package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.interfaces.OnWishListViewHolderClickListener;
import com.bamilo.android.framework.service.objects.product.pojo.ProductMultiple;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.product.UIProductUtils;
import com.bamilo.android.R;

import java.util.ArrayList;

/**
 *
 * @author rsoares
 */
public class WishListGridAdapter extends RecyclerView.Adapter<WishListGridAdapter.WishListProductViewHolder> implements View.OnClickListener {

    private final ArrayList<ProductMultiple> products;
    private final OnWishListViewHolderClickListener listener;

    public WishListGridAdapter(ArrayList<ProductMultiple> products, OnWishListViewHolderClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    /**
     * A representation of each item on the list
     */
    public class WishListProductViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView discount;
        public TextView price;
        public TextView percentage;
        public TextView brand;
        public TextView newArrivalBadge;
        public TextView varianceButton;
        public View addToCartButton;
        public View deleteButton;
        public View container;

        public WishListProductViewHolder(View view){
            super(view);
            container = itemView.findViewById(R.id.addabletocart_item_container);
            newArrivalBadge = itemView.findViewById(R.id.new_arrival_badge);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            brand = itemView.findViewById(R.id.item_brand);
            price = itemView.findViewById(R.id.item_regprice);
            discount = itemView.findViewById(R.id.item_discount);
            percentage = itemView.findViewById(R.id.item_percentage);
            varianceButton = itemView.findViewById(R.id.button_variant);
            addToCartButton = itemView.findViewById(R.id.button_shop);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }

    @NonNull
    @Override
    public WishListProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WishListProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.addable_to_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WishListProductViewHolder holder, int position) {
        ProductMultiple item = products.get(position);
        // Set image
        setImage(holder, item);
        // Set brand, name and price
        setTextContent(holder, item);
        // Set variation
        UIProductUtils.setVariationContent(holder.varianceButton, item);
        // Set clickable views
        setClickableViews(position, holder.container, holder.deleteButton, holder.addToCartButton, holder.varianceButton);
    }

    /**
     * Set this as clickable
     *
     * @author sergiopereira
     */
    private void setClickableViews(int position, View... views) {
        // For each view add position and listener
        if (listener != null) {
            for (View view : views) {
                view.setTag(R.id.target_position, position);
                view.setTag(R.id.target_sku, getItem(position).getSku());
                view.setOnClickListener(this);
            }
        }
    }

    /**
     * Set the image view
     *
     * @author sergiopereira
     */
    private void setImage(WishListProductViewHolder prodItem, ProductMultiple addableToCart) {
        // Set is new image
        prodItem.newArrivalBadge.setVisibility(addableToCart.isNew() ? View.VISIBLE : View.GONE);
        // Set image
        ImageManager.getInstance().loadImage(addableToCart.getImageUrl(), prodItem.image, null, R.drawable.no_image_large, false);
    }

    /**
     * Set the text content
     *
     * @author sergiopereira
     */
    private void setTextContent(WishListProductViewHolder prodItem, ProductMultiple product) {
        // Set brand
        prodItem.brand.setText(product.getBrandName());
        // Set name
        prodItem.name.setText(product.getName());

        UIProductUtils.setPriceRules(product, prodItem.price, prodItem.discount);

        UIProductUtils.setDiscountRules(product, prodItem.percentage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public ProductMultiple getItem(int position) {
        return CollectionUtils.isEmpty(products) ?  null : products.get(position);
    }

    public void remove(ProductMultiple productMultiple){
        products.remove(productMultiple);
        notifyDataSetChanged();
    }

    public boolean isEmpty(){
        return products.isEmpty();
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            // Get view id
            int id = view.getId();
            // Case delete
            if (id == R.id.button_delete) listener.onClickDeleteItem(view);
            // Case add to cart
            else if (id == R.id.button_shop) listener.onClickAddToCart(view);
            // Case simple
            else if (id == R.id.button_variant) listener.onClickVariation(view);
            //
            else if(id == R.id.addabletocart_item_container) listener.onItemClick(view);
        }
    }
}
