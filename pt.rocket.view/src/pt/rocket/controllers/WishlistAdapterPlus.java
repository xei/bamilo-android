package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import pt.rocket.framework.objects.Product;
import pt.rocket.view.R;
import pt.rocket.view.WishListActivity;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import org.holoeverywhere.widget.TextView;

/**
 * This Class is used to create an adapter for the wishlist of products. It is called by WishList Activity. <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * 
 * @version 1.00
 * 
 *          2012/07/03
 * 
 */
public class WishlistAdapterPlus extends BaseAdapter {

    List<Product> products;
    Activity activity;
    private LayoutInflater inflater;

    private ArrayList<Integer> selectedItems;

    final float scale;

    /**
     * A representation of each item on the list
     */
    public static class Item {
        public View itemContainer;

        public View multiselect;

        public ImageView image;
        public ImageView promotion;
        public ProgressBar progress;
        public TextView name;
        public TextView extra;
        public RatingBar rating;
        public TextView discount;
        public TextView price;
        public LinearLayout vertSeperator;
        public LinearLayout horizSeperator;

    }

    public WishlistAdapterPlus(Activity activty) {

        this.products = new ArrayList<Product>();
        this.activity = activty;
        this.selectedItems = new ArrayList<Integer>();

        this.scale = this.activity.getResources().getDisplayMetrics().density;


        // Get the Inflate Service
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // //Portait mode
        // jumpConstant = 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return this.products.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // int activePosition = position;
        Product prod = null;

        if (position < this.products.size()) {
            prod = this.products.get(position);
        }
        return prod;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View itemView;
        final Item prodItem;

        // Validate view
        if (convertView != null) {
            // if the view already exists there is no need to inflate it again
            itemView = convertView;
            prodItem = (Item) itemView.getTag();
        } else {
            // Inflate a New Item View
            itemView = inflater.inflate(R.layout.wishlistitemplus, null);

            prodItem = new Item();

            // Get controls ;
            prodItem.itemContainer = itemView.findViewById(R.id.general_container);

            prodItem.image = (ImageView) itemView.findViewById(R.id.image_view);
            prodItem.promotion = (ImageView) itemView.findViewById(R.id.item_promotion);
            prodItem.progress = (ProgressBar) itemView.findViewById(R.id.image_loading_progress);
            prodItem.name = (TextView) itemView.findViewById(R.id.item_name);
            prodItem.rating = (RatingBar) itemView.findViewById(R.id.item_rating);
            prodItem.price = (TextView) itemView.findViewById(R.id.item_regprice);
            prodItem.discount = (TextView) itemView.findViewById(R.id.item_discount);
            prodItem.vertSeperator = (LinearLayout) itemView.findViewById(R.id.wishlist_vert_seperator);
            prodItem.horizSeperator = (LinearLayout) itemView.findViewById(R.id.wishlist_bottom_separator);
            prodItem.multiselect = itemView.findViewById(R.id.wishlist_multiselect);

            // stores the item representation on the tag of the view for later
            // retrieval
            itemView.setTag(prodItem);

        }

        ViewGroup.LayoutParams itemViewParams = (ViewGroup.LayoutParams) itemView.getLayoutParams();
        if (null == itemViewParams) {
            itemViewParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        prodItem.itemContainer.setVisibility(View.VISIBLE);

        String imageURL = "";
        if (products.get(position).getImages().size() > 0) {
            imageURL = products.get(position).getImages().get(0).getUrl();
        }

        // colocar try por causa da imagem 0
        ImageLoader.getInstance().displayImage(imageURL, prodItem.image,
                new SimpleImageLoadingListener() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#
                     * onLoadingComplete(java.lang.String, android.view.View,
                     * android.graphics.Bitmap)
                     */
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Set Product Logo Visible
                        prodItem.image.setVisibility(View.VISIBLE);
                        // Remove ProgressBar
                        prodItem.progress.setVisibility(View.GONE);
                    }

                });

        prodItem.multiselect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // unselect item
                v.setVisibility(View.GONE);
                removeSelectedItem(position);

                ((WishListActivity) activity).unselectItem(selectedItems.size());
            }

        });

        prodItem.name.setText(this.products.get(position).getName());
        prodItem.extra.setText(this.products.get(position).getDescription());
        prodItem.extra.setVisibility(null == this.products.get(position).getDescription() ? View.GONE : View.VISIBLE);
        prodItem.price.setText(this.products.get(position).getSuggestedPrice());
        
        //As soon as the product from list has a rating remove the comment slashes the following lines.
        
//        if (null != this.products.get(position).getRating()) {
//            prodItem.rating.setRating(this.products.get(position).getRating().floatValue());
//            prodItem.rating.setVisibility(View.VISIBLE);
//            prodItem.extra.setVisibility(View.GONE);
//        } else {
            prodItem.rating.setVisibility(View.GONE);
//        }

        prodItem.price.setVisibility(View.VISIBLE);

        if (this.products.get(position).getSuggestedPrice() != this.products.get(position).getPrice()) {
            prodItem.discount.setText(this.products.get(position).getPrice());
            prodItem.price.setTextColor(activity.getResources().getColor(R.color.grey_dark));
            prodItem.discount.setVisibility(View.VISIBLE);
            prodItem.promotion.setVisibility(View.VISIBLE);
        } else if (null != this.products.get(position).getMaxSavingPercentage()) {
            prodItem.discount.setText(this.products.get(position).getSuggestedPrice());
            prodItem.price.setText("- " + this.products.get(position).getMaxSavingPercentage() + "%");
            prodItem.price.setTextColor(activity.getResources().getColor(R.color.red_basic));
            prodItem.discount.setVisibility(View.VISIBLE);
            prodItem.promotion.setVisibility(View.VISIBLE);
        } else {
            prodItem.price.setTextColor(activity.getResources().getColor(R.color.grey_dark));
            prodItem.discount.setVisibility(View.GONE);
            prodItem.promotion.setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams paramsH = (RelativeLayout.LayoutParams) prodItem.horizSeperator.getLayoutParams();
        RelativeLayout.LayoutParams paramsV = (RelativeLayout.LayoutParams) prodItem.vertSeperator.getLayoutParams();

        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if (position % 2 == 0) {
                paramsH.setMargins((int) (20 * scale), 0, 0, 0);
                prodItem.vertSeperator.setVisibility(View.VISIBLE);
                prodItem.itemContainer.setPadding((int) (20 * scale), prodItem.itemContainer.getPaddingTop(), prodItem.itemContainer.getPaddingRight(),
                        prodItem.itemContainer.getPaddingBottom());
            } else {
                paramsH.setMargins(0, 0, (int) (20 * scale), 0);
                prodItem.vertSeperator.setVisibility(View.GONE);
                prodItem.itemContainer.setPadding((int) (10 * scale), prodItem.itemContainer.getPaddingTop(), prodItem.itemContainer.getPaddingRight(),
                        prodItem.itemContainer.getPaddingBottom());
            }

        } else {
            paramsH.setMargins((int) (20 * scale), 0, (int) (20 * scale), 0);
            prodItem.vertSeperator.setVisibility(View.GONE);
            prodItem.itemContainer.setPadding((int) (20 * scale), prodItem.itemContainer.getPaddingTop(), prodItem.itemContainer.getPaddingRight(),
                    prodItem.itemContainer.getPaddingBottom());
        }
        prodItem.horizSeperator.setLayoutParams(paramsH);
        prodItem.horizSeperator.setVisibility(View.VISIBLE);
        prodItem.vertSeperator.setLayoutParams(paramsV);

        if (selectedItems.contains(position)) {
            prodItem.multiselect.setVisibility(View.VISIBLE);
        } else {
            prodItem.multiselect.setVisibility(View.GONE);
        }

        itemView.setLayoutParams(itemViewParams);

        // Return the Item View
        return itemView;
    }

    /**
     * Update Products
     * 
     * @param products
     */
    public void updateProducts(List<Product> products) {
        this.products = products;
        this.notifyDataSetChanged();
    }

    /**
     * adds the selected item if in multi selection mode
     * 
     * @param value
     *            the position of the item to be marked
     */
    public void addSelectedItem(int value) {
        if (!selectedItems.contains(value)) {
            selectedItems.add(value);
            this.notifyDataSetChanged();
        }
    }

    /**
     * removes the selected item if in multi selection mode
     * 
     * @param value
     *            the position of the item to be marked
     */
    public void removeSelectedItem(int value) {
        if (selectedItems.contains(value)) {
            selectedItems.remove((Integer) value);
            this.notifyDataSetChanged();
        }
    }

    /**
     * Clears all the selected items
     */
    public void clearSelectedItems() {
        selectedItems.clear();
        ((WishListActivity) activity).unselectItem(selectedItems.size());

        this.notifyDataSetChanged();
    }

    /**
     * gets the arraylist with all the selected items
     * 
     * @return the arraylist containing all the selected items
     */
    public ArrayList<Integer> getSelectedItems() {
        return selectedItems;
    }

}
