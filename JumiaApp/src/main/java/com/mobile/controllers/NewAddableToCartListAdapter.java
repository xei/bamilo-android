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
import com.mobile.newFramework.objects.product.NewProductAddableToCart;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * This Class is used to create an adapter for the list of items addableToCart.
 * It is called by LastViewedFragment and FavouritesFragment
 *
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class NewAddableToCartListAdapter extends ArrayAdapter<NewProductAddableToCart> {

    public final static String TAG = NewAddableToCartListAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private OnClickListener mOnClickParentListener;

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
     *
     * @author sergiopereira
     */
    public NewAddableToCartListAdapter(Context context, ArrayList<NewProductAddableToCart> items, OnClickListener parentListener) {
        super(context, R.layout.addabletocart_item, items);
        mInflater = LayoutInflater.from(context);
        mOnClickParentListener = parentListener;
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
        NewProductAddableToCart addableToCart = getItem(position);
        // Set Image
        setImage(prodItem, addableToCart);
        // Set brand, name and price 
        setTextContent(prodItem, addableToCart);
        // Set variation
        setVariationContent(prodItem, addableToCart);
        // Set warnings
//        setWarnings(prodItem, addableToCart);
        // Set clickable views
//        if (itemsClass == LastViewedAddableToCart.class) {
//            setClickableViews(position, prodItem.container, prodItem.addToCartButton, prodItem.varianceButton);
//        } else if (itemsClass == Favourite.class) {
//            setClickableViews(position, prodItem.container, prodItem.deleteButton, prodItem.addToCartButton, prodItem.varianceButton);
//        } else {
        setClickableViews(position, prodItem.container, prodItem.deleteButton, prodItem.addToCartButton, prodItem.varianceButton);
//        }
        // Set incomplete item 
//        setIncompleItem(prodItem.addToCartButton, addableToCart.isComplete());
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
     *
     * @return ItemView
     * @author sergiopereira
     */
    private Item getItemView(View itemView) {
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
            item.discountPercentage = (TextView) itemView.findViewById(R.id.item_percentage);
            item.varianceButton = (Button) itemView.findViewById(R.id.button_variant);
            item.variantChooseError = itemView.findViewById(R.id.error_variant);
            item.stockError = itemView.findViewById(R.id.error_stock);
            item.addToCartButton = itemView.findViewById(R.id.button_shop);
            item.deleteButton = itemView.findViewById(R.id.button_delete);
            itemView.setTag(R.id.recycled_view, item);
        } else {
            item = (Item) itemView.getTag(R.id.recycled_view);
        }
        return item;
    }

//    /**
//     * Set warnings, stock and variation
//     * @author sergiopereira
//     */
//    private void setWarnings(Item prodItem, NewProductAddableToCart addableToCart) {
//        // Set variation error visibility
//        prodItem.variantChooseError.setVisibility(addableToCart.showChooseVariationWarning() ? View.VISIBLE : View.GONE);
//        // Set stock error visibility
//        prodItem.stockError.setVisibility(addableToCart.showStockVariationWarning() ? View.VISIBLE : View.GONE);
//    }

    /**
     * Set this as clickable
     *
     * @author sergiopereira
     */
    private void setClickableViews(int position, View... views) {
        // For each view add position and listener
        if (mOnClickParentListener != null) {
            for (View view : views) {
                view.setTag(R.id.target_position, position);
                view.setTag(R.id.target_url, getItem(position).getUrl());
                view.setTag(R.id.target_sku, getItem(position).getSku());
                view.setOnClickListener(mOnClickParentListener);
            }
        }
    }

    /**
     * Set the variation container
     *
     * @author sergiopereira
     */
    private void setVariationContent(Item prodItem, NewProductAddableToCart addableToCart) {
        // Set simple button
        if (CollectionUtils.isNotEmpty(addableToCart.getSimples())) {
            prodItem.varianceButton.setText("...");
            prodItem.varianceButton.setVisibility(View.VISIBLE);
        } else {
            prodItem.varianceButton.setVisibility(View.GONE);
        }
    }

    /**
     * Set the image view
     *
     * @author sergiopereira
     */
    private void setImage(Item prodItem, NewProductAddableToCart addableToCart) {
        // Set is new image
        prodItem.isNew.setSelected(addableToCart.isNew());
        // Set image
        RocketImageLoader.instance.loadImage(addableToCart.getImageUrl(), prodItem.image, null, R.drawable.no_image_small);
    }

    /**
     * Set the text content
     *
     * @author sergiopereira
     */
    private void setTextContent(Item prodItem, NewProductAddableToCart addableToCart) {
        // Set brand
        prodItem.brand.setText(addableToCart.getBrand());
        // Set name
        prodItem.name.setText(addableToCart.getName());
        // Validate special price
        if (Double.isNaN(addableToCart.getSpecialPrice())) {
            // Set discount
            prodItem.discount.setText(CurrencyFormatter.formatCurrency(String.valueOf(addableToCart.getSpecialPrice())));
            // TODO: palceholder
            prodItem.discountPercentage.setText("-" + addableToCart.getMaxSavingPercentage() + "%");
            // Set price
            prodItem.price.setText(CurrencyFormatter.formatCurrency(String.valueOf(addableToCart.getPrice())));
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.price.setSelected(true);
            prodItem.price.setTextColor(getContext().getResources().getColor(R.color.grey_light));
            prodItem.price.setTextAppearance(getContext(), R.style.text_normal_programatically);
            prodItem.discount.setVisibility(View.VISIBLE);
            prodItem.discountPercentage.setVisibility(View.VISIBLE);
        } else {
            // Set price
            prodItem.discount.setVisibility(View.GONE);
            prodItem.discountPercentage.setVisibility(View.INVISIBLE);
            prodItem.price.setText(CurrencyFormatter.formatCurrency(String.valueOf(addableToCart.getPrice())));
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            prodItem.price.setTextAppearance(getContext(), R.style.text_bold_programatically);
            prodItem.price.setTextColor(getContext().getResources().getColor(R.color.red_basic));
        }
    }
}
