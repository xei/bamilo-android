package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.Collection;

import pt.rocket.components.customfontviews.TextView;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.Toast;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import pt.rocket.view.fragments.CatalogFragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.android.volley.toolbox.ImageLoader.ImageContainer;

import de.akquinet.android.androlog.Log;

/**
 * This Class is used to create an adapter for the list of products. It is
 * called by ProductsList Activity.
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

    private ArrayList<String> products;

    int counter = 1;

    private final String reviewLabel;
    
    private final String reviewsLabel;

    private LayoutInflater inflater;

    private Context context;
    private int isNewResource;

    private boolean showList;

    private CatalogFragment parentCatalog;

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
        public ImageView newFlag;
        public ImageView isFavourite;
    }

    /**
     * the constructor for this adapter
     * 
     * @param activity
     * @param showList
     *            show list (or grid)
     * @param numColumns
     */
    public ProductsListAdapter(Context context, CatalogFragment parent, boolean showList, int numColumns, boolean isFrench) {

        this.context = context.getApplicationContext();
        this.products = new ArrayList<String>();
        this.showList = showList;

        this.inflater = LayoutInflater.from(context);
        reviewLabel = context.getString(R.string.review);
        reviewsLabel = context.getString(R.string.reviews);

        // Get is new image for respective country
        this.isNewResource = !isFrench ? R.drawable.selector_is_new_en : R.drawable.selector_is_new_fr;

        this.parentCatalog = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return products == null ? 0 : products.size();
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
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
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
            int layoutId = showList ? R.layout.catalog_item_rounded : R.layout.catalog_item_grid_rounded;

            itemView = inflater.inflate(layoutId, parent, false);
        }

        if ((Item) itemView.getTag() == null) {
            prodItem = new Item();
            prodItem.image = (ImageView) itemView.findViewById(R.id.image_view);
            prodItem.progress = itemView.findViewById(R.id.image_loading_progress);

            prodItem.name = (TextView) itemView.findViewById(R.id.item_name);
            if (showList) prodItem.rating = (RatingBar) itemView.findViewById(R.id.item_rating);

            prodItem.price = (TextView) itemView.findViewById(R.id.item_regprice);
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            prodItem.discount = (TextView) itemView.findViewById(R.id.item_discount);
            prodItem.discountPercentage = (TextView) itemView.findViewById(R.id.discount_percentage);
            if (showList) prodItem.reviews = (TextView) itemView.findViewById(R.id.item_reviews);

            prodItem.brand = (TextView) itemView.findViewById(R.id.item_brand);
            prodItem.newFlag = (ImageView) itemView.findViewById(R.id.image_is_new);
            prodItem.newFlag.setImageResource(isNewResource);

            prodItem.isFavourite = (ImageView) itemView.findViewById(R.id.image_is_favourite);
            itemView.setTag(prodItem);
        } else {
            prodItem = (Item) itemView.getTag();
            ImageContainer imgContainer = (ImageContainer) prodItem.image.getTag();
            if (null != imgContainer) {
                imgContainer.cancelRequest();
            }
        }

        final Product product = parentCatalog.getProduct(products.get(position));
        prodItem.image.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image_small));
        RocketImageLoader.instance.loadImage(product.getFirstImageURL(), prodItem.image, prodItem.progress, R.drawable.no_image_small, CatalogFragment.requestTag);

        // Set is new image
        prodItem.newFlag.setSelected(product.getAttributes().isNew());

        // Set is favourite image
        prodItem.isFavourite.setSelected(product.getAttributes().isFavourite());

        prodItem.isFavourite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Product favProduct = parentCatalog.getProduct(products.get(position));
                final boolean isFavourite = favProduct.getAttributes().isFavourite();
                if (!isFavourite) {
                    FavouriteTableHelper.insertPartialFavouriteProduct(favProduct);
                    favProduct.getAttributes().setFavourite(true);
                    TrackerDelegator.trackAddToFavorites(favProduct.getSKU(),favProduct.getBrand(),favProduct.getPriceForTracking(),
                            favProduct.getRating(),favProduct.getMaxSavingPercentage(), true, null);
                    Toast.makeText(context, context.getString(R.string.products_added_favourite), Toast.LENGTH_SHORT).show();
                } else {
                    FavouriteTableHelper.removeFavouriteProduct(favProduct.getSKU());
                    favProduct.getAttributes().setFavourite(false);
                    TrackerDelegator.trackRemoveFromFavorites(favProduct.getSKU(), favProduct.getPriceForTracking(),favProduct.getRating());
                    Toast.makeText(context, context.getString(R.string.products_removed_favourite), Toast.LENGTH_SHORT).show();
                }
                parentCatalog.invalidatePages();
            }
        });

        // Set brand
        prodItem.brand.setText(product.getBrand().toUpperCase());

        prodItem.name.setText(product.getName());
        prodItem.price.setText(product.getPrice());

        if (showList) {
            if (product.getRating() != null && product.getRating() > 0) {
                prodItem.rating.setRating(product.getRating().floatValue());
                prodItem.rating.setVisibility(View.VISIBLE);
                if(product.getReviews() == 1){
                    prodItem.reviews.setText(product.getReviews() + " " + reviewLabel);    
                } else {
                    prodItem.reviews.setText(product.getReviews() + " " + reviewsLabel);    
                }
                
                
            } else {
                prodItem.rating.setVisibility(View.INVISIBLE);
                prodItem.reviews.setText("");
            }
        }

        if (null != product.getSpecialPrice() && !product.getSpecialPrice().equals(product.getPrice())) {
            prodItem.discount.setText(product.getSpecialPrice());
            prodItem.price.setText(product.getPrice());
            prodItem.discountPercentage.setText("-" + product.getMaxSavingPercentage().intValue() + "%");
            prodItem.discountPercentage.setVisibility(View.VISIBLE);
        } else {
            prodItem.discount.setText(product.getSpecialPrice());
            prodItem.price.setText("");
            prodItem.discountPercentage.setVisibility(View.GONE);
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
        for (String sku : newProducts) {
            if (!products.contains(sku)) {
                products.add(sku);
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<String> getProductsList() {
        return products;
    }

    /**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     * 
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

}
