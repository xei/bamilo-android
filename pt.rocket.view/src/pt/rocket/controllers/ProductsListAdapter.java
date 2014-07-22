package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.Collection;

import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import pt.rocket.view.fragments.Catalog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.android.volley.toolbox.NetworkImageView;

import de.akquinet.android.androlog.Log;

/**
 * This Class is used to create an adapter for the list of products. It is called by ProductsList
 * Activity.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * 
 * @version 1.00
 * 
 *          2012/06/22
 * 
 */
public class ProductsListAdapter extends BaseAdapter {
    private final static String TAG = LogTagHelper.create(ProductsListAdapter.class);

    public interface OnSelectedItemsChange {
        public void SelectedItemsChange(int numSelectedItems);
    }

    ArrayList<String> products;

    int counter = 1;

    private final String reviewLabel;

    private LayoutInflater inflater;

    private Context context;

    private Drawable isNewDrawable;

    private Drawable isFavouriteDrawable;
    private Drawable isNotFavouriteDrawable;

    private boolean showList;

    private int numColumns = 1;

    private Catalog parentCatalog;
    
    /**
     * A representation of each item on the list
     */
    static class Item {
        public ImageView image;
        public View progress;
        public TextView name;
        public RatingBar rating;
        public TextView discount;
        public TextView price;
        public TextView discountPercentage;
        public TextView reviews;
        public TextView brand;
        public ImageView isNew;
        public ImageView isFavourite;
    }

    /**
     * the constructor for this adapter
     * 
     * @param activity
     * @param showList show list (or grid)
     * @param numColumns 
     */
    public ProductsListAdapter(Context context, Catalog parent, boolean showList, int numColumns) {

        this.context = context.getApplicationContext();
        this.products = new ArrayList<String>();
        this.showList = showList;
        this.numColumns = numColumns;

        this.inflater = LayoutInflater.from(context);
        reviewLabel = context.getString(R.string.reviews);
        
        // Get is new image for respective country
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.isNewDrawable = context.getResources().getDrawable(R.drawable.img_newarrival_en);
        if(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, "en").contains("fr")){
            this.isNewDrawable = context.getResources().getDrawable(R.drawable.img_newarrival_fr);
        }
        this.isFavouriteDrawable = context.getResources().getDrawable(R.drawable.btn_fav_selected);
        this.isNotFavouriteDrawable = context.getResources().getDrawable(R.drawable.btn_fav);
        
        this.parentCatalog = parent;
    }


    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return products == null && products.isEmpty() ? 0 : products.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */

    @Override
    public long getItemId(int position) {
        return position;
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
        } else {
            int layoutId = showList ? R.layout.product_item_test : R.layout.product_item_grid_test;

            itemView = inflater.inflate(layoutId, parent, false);
        }

//        // when showing grid add margins to container on the first and last columns
//        if (!showList) {
//            LinearLayout container = (LinearLayout) itemView.findViewById(R.id.container);
//            // guarantee this layout has this view
//            if (container != null) {
//                RelativeLayout.LayoutParams containerLayoutParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
//                int column = position % numColumns;
//                if (column == 0) {
//                    int margin = context.getResources().getDimensionPixelOffset(R.dimen.margin_mid);
//                    containerLayoutParams.leftMargin = margin;
//                } else if (column == (numColumns - 1)) {
//                    int margin = context.getResources().getDimensionPixelOffset(R.dimen.margin_mid);
//                    containerLayoutParams.rightMargin = margin;
//                }
//            }
//        }

        if ((Item) itemView.getTag() == null) {
            prodItem = new Item();
            prodItem.image = (ImageView) itemView.findViewById(R.id.image_view);
            prodItem.name = (TextView) itemView.findViewById(R.id.item_name);
            if (showList) prodItem.rating = (RatingBar) itemView.findViewById(R.id.item_rating);
            prodItem.price = (TextView) itemView.findViewById(R.id.item_regprice);
            prodItem.discount = (TextView) itemView.findViewById(R.id.item_discount);
            prodItem.discountPercentage = (TextView) itemView.findViewById(R.id.discount_percentage);
            if (showList) prodItem.reviews = (TextView) itemView.findViewById(R.id.item_reviews);
            prodItem.brand = (TextView) itemView.findViewById(R.id.item_brand);
            prodItem.isNew= (ImageView) itemView.findViewById(R.id.image_is_new);
            prodItem.isNew.setImageDrawable(isNewDrawable);
            prodItem.isFavourite = (ImageView) itemView.findViewById(R.id.image_is_favourite);
            itemView.setTag(prodItem);
        } else {
            prodItem = (Item) itemView.getTag();
        }
        
//        String imageURL = "";
        Product product = parentCatalog.getProduct(products.get(position));
//        if (product.getImages().size() > 0) {
//            imageURL = product.getImages(). get(0).getUrl();
//        }
        //RocketImageLoader.instance.loadImage(imageURL, prodItem.image,  null, R.drawable.no_image_small);
        ((NetworkImageView) prodItem.image).setImageUrl(product.getFirstImageURL(), RocketImageLoader.instance.getImageLoader());

        // Set is new image
        if(product.getAttributes().isNew()) {            
            prodItem.isNew.setVisibility(View.VISIBLE);
        } else {
            prodItem.isNew.setVisibility(View.GONE);
        }

        
//        if (FavouriteTableHelper.verifyIfFavourite(product.getSKU())) {
//            product.getAttributes().setFavourite(true);
//        } else {
//            product.getAttributes().setFavourite(false);
//        }
        
        // Set is favourite image
//        if (product.getAttributes().isFavourite()) {
//            prodItem.isFavourite.setImageDrawable(isFavouriteDrawable);
//        } else {
//            prodItem.isFavourite.setImageDrawable(isNotFavouriteDrawable);
//        }
        prodItem.isFavourite.setSelected(product.getAttributes().isFavourite());
        
        prodItem.isFavourite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Product favProduct = parentCatalog.getProduct(products.get(position));
                boolean isFavourite = favProduct.getAttributes().isFavourite();
                if (!isFavourite) {
                    FavouriteTableHelper.insertPartialFavouriteProduct(favProduct);
                    favProduct.getAttributes().setFavourite(true);
                    //prodItem.isFavourite.setImageDrawable(isFavouriteDrawable);
                    Toast.makeText(context, context.getString(R.string.products_added_favourite), Toast.LENGTH_SHORT).show();
                } else {
                    FavouriteTableHelper.removeFavouriteProduct(favProduct.getSKU());
                    favProduct.getAttributes().setFavourite(false);
                    //prodItem.isFavourite.setImageDrawable(isNotFavouriteDrawable);
                    Toast.makeText(context, context.getString(R.string.products_removed_favourite), Toast.LENGTH_SHORT).show();
                }
                parentCatalog.invalidatePages();
            }
        });
        
        // Set brand
        prodItem.brand.setText(product.getBrand().toUpperCase());
        
        prodItem.name.setText(product.getName());
        prodItem.price.setText(product.getSuggestedPrice());
        if (showList) {
            if (product.getRating() != null && product.getRating() > 0) {
                prodItem.rating.setRating(product.getRating().floatValue());
                prodItem.rating.setVisibility(View.VISIBLE);
                if (product.getReviews() != null) {
                    prodItem.reviews.setText(product.getReviews() + " " + reviewLabel);
                    prodItem.reviews.setVisibility(View.VISIBLE);
                } else {
                    prodItem.reviews.setVisibility(View.INVISIBLE);
                }
            } else {
                prodItem.rating.setVisibility(View.INVISIBLE);
                prodItem.reviews.setVisibility(View.INVISIBLE);
            }
        }

        if (null != product.getSpecialPrice() && !product.getSpecialPrice().equals(product.getPrice())) {
            prodItem.discount.setText(product.getSpecialPrice());
            prodItem.discountPercentage.setText("-" + product.getMaxSavingPercentage().intValue() + "%");
            prodItem.discount.setVisibility(View.VISIBLE);
            prodItem.discountPercentage.setVisibility(View.VISIBLE);
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.price.setSelected(true);
            prodItem.price.setTextColor(context.getResources().getColor(R.color.grey_light));
            prodItem.price.setTextAppearance(context.getApplicationContext(), R.style.text_normal);
        } else {
            prodItem.discount.setVisibility(View.GONE);
            prodItem.discountPercentage.setVisibility(View.GONE);
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            prodItem.price.setTextAppearance(context.getApplicationContext(), R.style.text_bold);
            prodItem.price.setTextColor(context.getResources().getColor(R.color.red_basic));
        }
        return itemView;
    }

    /**
     * Updates the Products array list
     * 
     * @param products
     *            The array list containing the products
     */
    public void updateProducts(ArrayList<String> products) {
        Log.d(TAG, "updateProducts: size = " + products.size());
        this.products = products;
        this.notifyDataSetChanged();
    }

    public void clearProducts() {
        products.clear();
        notifyDataSetChanged();
    }

    public void appendProducts(Collection<? extends String> newProducts) {
        products.addAll(newProducts);
        notifyDataSetChanged();
    }
    
    /**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946 
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if(observer !=null){
            super.unregisterDataSetObserver(observer);    
        }
    }

    
    
}
