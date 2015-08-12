package com.mobile.controllers;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.AddableToCart;
import com.mobile.newFramework.objects.product.LastViewedAddableToCart;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.UIUtils;
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
public class AddableToCartListAdapter extends ArrayAdapter<AddableToCart> {
    
    public final static String TAG = AddableToCartListAdapter.class.getSimpleName();
    
    private static int RECYCLED_VIEW_KEY = R.id.recycled_view;

    private LayoutInflater mInflater;

    private OnClickListener mOnClickParentListener;

    private Class<? extends AddableToCart> itemsClass;

    /**
     * A representation of each item on the list
     */
    public static class Item {
        private ImageView image;
        private TextView name;
        private TextView discount;
        private TextView price;
        private TextView discountPercentage;
        private TextView brand;
        private View isNew;
        private Button varianceButton;
        private View variantChooseError;
        private View addToCartButton;
        private View deleteButton;
        private View container;
        private View stockError;
    }
    
    /**
     * Constructor 
     * @param context
     * @param items
     * @param parentListener
     * @author sergiopereira
     */
    public AddableToCartListAdapter(Context context, ArrayList<AddableToCart> items, OnClickListener parentListener) {
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
        AddableToCart addableToCart = getItem(position);
        // Set Image
        setImage(prodItem, addableToCart);
        // Set brand, name and price 
        setTextContent(prodItem, addableToCart);
        // Set variation
        setVariationContent(prodItem, addableToCart);
        // Set warnings
        setWarnings(prodItem, addableToCart);
        // Set clickable views
        if (itemsClass == LastViewedAddableToCart.class) {
            setClickableViews(position, prodItem.container, prodItem.addToCartButton, prodItem.varianceButton);
        } else {
            setClickableViews(position, prodItem.container, prodItem.deleteButton, prodItem.addToCartButton, prodItem.varianceButton);
        }
        // Set incomplete item 
        setIncompleItem(prodItem.addToCartButton, addableToCart.isComplete());
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
        if (itemView.getTag(RECYCLED_VIEW_KEY) == null) {
            // Create tag
            item = new Item();
            item.container = itemView.findViewById(R.id.addabletocart_item_container);
            item.isNew = itemView.findViewById(R.id.item_image_is_new);
            item.image = (ImageView) itemView.findViewById(R.id.item_image);
            item.name = (TextView) itemView.findViewById(R.id.item_name);
            item.brand = (TextView) itemView.findViewById(R.id.item_brand);
            item.price = (TextView) itemView.findViewById(R.id.item_regprice);
            item.discount = (TextView) itemView.findViewById(R.id.item_discount);
            item.discountPercentage = (TextView) itemView.findViewById(R.id.item_percentage);
            item.varianceButton = (Button) itemView.findViewById(R.id.button_variant);
            item.variantChooseError = itemView.findViewById(R.id.error_variant);
            item.stockError = itemView.findViewById(R.id.error_stock);
            item.addToCartButton = itemView.findViewById(R.id.button_shop);
            item.deleteButton = itemView.findViewById(R.id.button_delete);
            itemView.setTag(RECYCLED_VIEW_KEY, item);
        } else {
            item = (Item) itemView.getTag(RECYCLED_VIEW_KEY);
        }
        return item;
    }
    
    /**
     * Disable the add to cart button for incomplete products
     * @param view
     * @param isComplete
     * @author sergiopereira
     */
    private void setIncompleItem(View view, boolean isComplete) {
        if(!isComplete) {
            view.setOnClickListener(null);
            view.setEnabled(false);
            view.setBackgroundResource(R.drawable.btn_grey);
            UIUtils.setAlpha(view, 0.5f);
        }
    }
    
    /**
     * Set warnings, stock and variation
     * @param prodItem
     * @param addableToCart
     * @author sergiopereira
     */
    private void setWarnings(Item prodItem, AddableToCart addableToCart) {
        // Set variation error visibility 
        prodItem.variantChooseError.setVisibility(addableToCart.showChooseVariationWarning() ? View.VISIBLE : View.GONE);
        // Set stock error visibility 
        prodItem.stockError.setVisibility(addableToCart.showStockVariationWarning() ? View.VISIBLE : View.GONE);
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
    private void setVariationContent(Item prodItem, AddableToCart addableToCart){
        // Set simple button
        if(addableToCart.hasSimples()) {
            // Set simple value
            String simpleValue = "..."; 
            if(addableToCart.getSelectedSimple() != AddableToCart.NO_SIMPLE_SELECTED ) simpleValue = addableToCart.getSelectedSimpleValue();
            else addableToCart.setSelectedSimple(AddableToCart.NO_SIMPLE_SELECTED);
            // Set visibilty
            prodItem.varianceButton.setText(simpleValue);
            prodItem.varianceButton.setVisibility(View.VISIBLE);
        } else {
            // addableToCart.setSelectedSimple(0);
            prodItem.varianceButton.setVisibility(View.GONE);
        }
    }
    
    /**
     * Set the image view
     * @param prodItem
     * @param addableToCart
     * @author sergiopereira
     */
    private void setImage(Item prodItem, AddableToCart addableToCart){
        // Set is new image
        prodItem.isNew.setSelected(addableToCart.isNew());
        // Set image
        String imageURL = (addableToCart.getImageList().size() > 0) ? addableToCart.getImageList().get(0) : "";
        RocketImageLoader.instance.loadImage(imageURL, prodItem.image,  null, R.drawable.no_image_small);
    }

    /**
     * Set the text content
     * @param prodItem
     * @param addableToCart
     * @author sergiopereira
     */
    private void setTextContent(Item prodItem, AddableToCart addableToCart) {
        if (addableToCart != null) {
            // Set brand
            String brand = addableToCart.getBrand();
            if (brand != null) {
                brand = brand.toUpperCase();
            }
            prodItem.brand.setText(brand);
            // Set name
            prodItem.name.setText(addableToCart.getName());
            // Validate special price
            if (addableToCart.hasDiscount()) {
                // Set discount 
                prodItem.discount.setText(CurrencyFormatter.formatCurrency(addableToCart.getSpecialPrice()));
                // TODO placeholder
                int discountPercentage = addableToCart.getMaxSavingPercentage();
                prodItem.discountPercentage.setText("-" + discountPercentage + "%");
                prodItem.discount.setVisibility(View.VISIBLE);
                prodItem.discountPercentage.setVisibility(View.VISIBLE);
                // Set price
                prodItem.price.setText(CurrencyFormatter.formatCurrency(addableToCart.getPrice()));
                prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                prodItem.price.setSelected(true);
                prodItem.price.setTextColor(getContext().getResources().getColor(R.color.grey_light));
                prodItem.price.setTextAppearance(getContext(), R.style.text_normal_programatically);
            } else {
                // Set price
                prodItem.discount.setVisibility(View.GONE);
                prodItem.discountPercentage.setVisibility(View.INVISIBLE);
                prodItem.price.setText(CurrencyFormatter.formatCurrency(addableToCart.getPrice()));
                prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                prodItem.price.setTextAppearance(getContext(), R.style.text_bold_programatically);
                prodItem.price.setTextColor(getContext().getResources().getColor(R.color.red_basic));
            }
            if (itemsClass == LastViewedAddableToCart.class) {
                // Set visibility
                prodItem.deleteButton.setVisibility(View.INVISIBLE);
            }
        }
    }
}
