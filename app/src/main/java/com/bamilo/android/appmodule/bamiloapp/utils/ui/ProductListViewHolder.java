package com.bamilo.android.appmodule.bamiloapp.utils.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import android.widget.TextView;
import com.bamilo.android.R;

/**
 * Provide a reference to the views for each data item.<br>
 * Complex data items may need more than one view per item, and you provide access to all the views for a data item in a view holder<br>
 * @author sergiopereira
 *
 */
public class ProductListViewHolder extends RecyclerView.ViewHolder {

    // Data
    public TextView name;
    public TextView brand;
    public TextView quantity;
    public ImageView image;
    public View progress;
    public ViewGroup ratingContainer;
    public RatingBar rating;
    public TextView discount;
    public TextView price;
    public TextView percentage;
    public TextView reviews;
    public TextView newArrivalBadge;
    public ImageView favourite;
    public ImageView headerImage;
    public ImageView shopFirst;
    public RelativeLayout price_rl;
    public TextView specialBadge;


    /**
     * Constructor
     * @param view -  the view holder
     */
    public ProductListViewHolder(View view) {
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
        newArrivalBadge = (TextView) view.findViewById(R.id.new_arrival_badge);
        favourite = (ImageView) view.findViewById(R.id.image_is_favourite);
        headerImage = (ImageView) view.findViewById(R.id.catalog_header_image);
        ratingContainer = (ViewGroup)view.findViewById(R.id.rating_container);
        shopFirst = (ImageView)view.findViewById(R.id.item_shop_first);
        price_rl = (RelativeLayout)view.findViewById(R.id.item_regprice_rl);
        specialBadge = view.findViewById(R.id.defCatalogItemGrid_xeiTextView_specialBadge);
    }
}

