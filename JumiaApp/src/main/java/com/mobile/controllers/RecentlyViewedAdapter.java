package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.utils.output.Print;
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
public class RecentlyViewedAdapter extends ArrayAdapter<ProductMultiple> {

    public final static String TAG = RecentlyViewedAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private OnClickListener mOnClickParentListener;

    private Class<? extends ProductMultiple> itemsClass;

    /**
     * A representation of each item on the list
     */
    public static class Item {
        private ImageView image;
        private TextView name;
        private TextView discount;
        private TextView price;
        private TextView percentage;
        private TextView brand;
        private View isNew;
        private Button varianceButton;
        private View addToCartButton;
        private View deleteButton;
        private View container;
    }

    /**
     * Constructor
     * @param context
     * @param items
     * @param parentListener
     * @author sergiopereira
     */
    public RecentlyViewedAdapter(Context context, ArrayList<ProductMultiple> items, OnClickListener parentListener) {
        super(context, R.layout.addabletocart_item, items);
        mInflater = LayoutInflater.from(context);
        mOnClickParentListener = parentListener;

        if (!items.isEmpty()) {
            itemsClass = items.get(0).getClass();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.i(TAG, "ON GET VIEW: " + position);
        // Validate view
        View itemView;
        // If the view already exists there is no need to inflate it again
        if (convertView != null) itemView = convertView;
        // Inflate the view
        else itemView = mInflater.inflate(R.layout.addabletocart_item, parent, false);
        // Get the class associated to the view
        Item prodItem = getItemView(itemView);
        // Get addableToCart
        ProductMultiple addableToCart = getItem(position);
        // Set Image
        setImage(prodItem, addableToCart);
        // Set brand, name and price
        setTextContent(prodItem, addableToCart);
        // Set variation
        setVariationContent(prodItem, addableToCart);
        // Set clickable views
        setClickableViews(position, prodItem.container, prodItem.addToCartButton, prodItem.varianceButton);
        // Return view
        return itemView;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#notifyDataSetChanged()
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Print.i(TAG, "ON DATA SET CHANGED");
    }

    /**
     * Get the recycled view
     * @return ItemView
     * @author sergiopereira
     */
    private Item getItemView(View itemView){
        Item item;
        if (itemView.getTag(R.id.recycled_view) == null) {
            // Create tag
            item = new Item();
            item.container = itemView.findViewById(R.id.addabletocart_item_container);
            item.isNew = itemView.findViewById(R.id.item_image_is_new);
            item.image = (ImageView) itemView.findViewById(R.id.item_image);
            item.name = (TextView) itemView.findViewById(R.id.item_name);
            item.brand = (TextView) itemView.findViewById(R.id.item_brand);
            item.price = (TextView) itemView.findViewById(R.id.item_regprice);
            item.discount = (TextView) itemView.findViewById(R.id.item_discount);
            item.percentage = (TextView) itemView.findViewById(R.id.item_percentage);
            item.varianceButton = (Button) itemView.findViewById(R.id.button_variant);
            item.addToCartButton = itemView.findViewById(R.id.button_shop);
            item.deleteButton = itemView.findViewById(R.id.button_delete);
            itemView.setTag(R.id.recycled_view, item);
        } else {
            item = (Item) itemView.getTag(R.id.recycled_view);
        }
        return item;
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
     * Set the variation container
     * @author sergiopereira
     */
    private void setVariationContent(Item prodItem, ProductMultiple product){
        // Set simple button
        if(product.hasMultiSimpleVariations()) {
            // Set simple value
            String simpleVariationValue = "...";
            if(product.hasSelectedSimpleVariation()) {
                simpleVariationValue = product.getSimples().get(product.getSelectedSimplePosition()).getVariationValue();
            }
            prodItem.varianceButton.setText(simpleVariationValue);
            prodItem.varianceButton.setVisibility(View.VISIBLE);
        } else {
            prodItem.varianceButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Set the image view
     * @param prodItem
     * @param addableToCart
     * @author sergiopereira
     */
    private void setImage(Item prodItem, ProductMultiple addableToCart){
        // Set is new image
        prodItem.isNew.setSelected(addableToCart.isNew());
        // Set image
        RocketImageLoader.instance.loadImage(addableToCart.getImageUrl(), prodItem.image,  null, R.drawable.no_image_small);
    }

    /**
     * Set the text content
     * @param prodItem
     * @param addableToCart
     * @author sergiopereira
     */
    private void setTextContent(Item prodItem, ProductMultiple addableToCart) {
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
}
