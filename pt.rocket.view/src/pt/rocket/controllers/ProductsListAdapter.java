package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.Collection;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.RocketImageLoader;
import pt.rocket.view.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.androidquery.AQuery;

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

    ArrayList<Product> products;

    int counter = 1;

    private final String reviewLabel;

    private LayoutInflater inflater;

    private Context context;

    private Drawable isNewDrawable;

    /**
     * A representation of each item on the list
     */
    static class Item {

        public ImageView image;
        public ImageView promotion;
        public View progress;
        public TextView name;
        public RatingBar rating;
        public TextView discount;
        public TextView price;
        public TextView discountPercentage;
        public TextView reviews;
        public TextView brand;
        public ImageView isNew;

        // /*
        // * (non-Javadoc)
        // *
        // * @see java.lang.Object#finalize()
        // */
        // @Override
        // protected void finalize() throws Throwable {
        //
        // image = null;
        // promotion = null;
        // progress = null;
        // name = null;
        // rating = null;
        // discount = null;
        // price = null;
        // discountPercentage = null;
        // reviews = null;
        //
        // super.finalize();
        // }
    }

    /**
     * the constructor for this adapter
     * 
     * @param activity
     */
    public ProductsListAdapter(Context context) {

        this.context = context.getApplicationContext();
        this.products = new ArrayList<Product>();

        this.inflater = LayoutInflater.from(context);
        reviewLabel = context.getString(R.string.reviews);
        
        // Get is new image for respective country
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.isNewDrawable = context.getResources().getDrawable(R.drawable.img_newarrival_en);
        if(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, "en").contains("fr")){
            this.isNewDrawable = context.getResources().getDrawable(R.drawable.img_newarrival_fr);
        }

    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see java.lang.Object#finalize()
    // */
    // @Override
    // protected void finalize() throws Throwable {
    // this.products = null;
    // this.selectedItems = null;
    // context = null;
    // this.onSelectedItemsChange = null;
    // System.gc();
    // super.finalize();
    // }

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
        // int activePosition = position - jumpConstant;
        Product prod = null;

        // if (activePosition > -1 && activePosition < this.products.size()) {
        prod = this.products.get(position);
        // }
        return prod;
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
            itemView = inflater.inflate(R.layout.product_item, parent, false);
        }

        if ((Item) itemView.getTag() == null) {
            prodItem = new Item();
            prodItem.image = (ImageView) itemView.findViewById(R.id.image_view);
            prodItem.promotion = (ImageView) itemView.findViewById(R.id.item_promotion);
            prodItem.progress = itemView.findViewById(R.id.image_loading_progress);
            prodItem.name = (TextView) itemView.findViewById(R.id.item_name);
            prodItem.rating = (RatingBar) itemView.findViewById(R.id.item_rating);
            prodItem.price = (TextView) itemView.findViewById(R.id.item_regprice);
            prodItem.discount = (TextView) itemView.findViewById(R.id.item_discount);
            prodItem.discountPercentage = (TextView) itemView.findViewById(R.id.discount_percentage);
            // prodItem.vertSeperator = itemView.findViewById(R.id.prod_right_seperator);
            // prodItem.multiselect = itemView.findViewById(R.id.products_multiselect);
            prodItem.reviews = (TextView) itemView.findViewById(R.id.item_reviews);

            // Get brand
            prodItem.brand = (TextView) itemView.findViewById(R.id.item_brand);
            // Get is new
            prodItem.isNew= (ImageView) itemView.findViewById(R.id.image_is_new);
            
            // stores the item representation on the tag of the view for later
            // retrieval
            itemView.setTag(prodItem);
        } else {
            prodItem = (Item) itemView.getTag();
        }

        AQuery aq = new AQuery(itemView);
        
        String imageURL = "";
        Product product = products.get(position);
        if (product.getImages().size() > 0) {
            imageURL = product.getImages().get(0).getUrl();
        }
//        prodItem.progress.setVisibility(View.VISIBLE);
        RocketImageLoader.instance.loadImage(imageURL, prodItem.image,  prodItem.progress, R.drawable.no_image_small);
//        (imageURL, prodItem.image, new RocketImageLoaderListener() {
//            
//            @Override
//            public void onLoadedSuccess(Bitmap bitmap) {
//                prodItem.image.setImageBitmap(bitmap);
//                prodItem.progress.setVisibility(View.GONE);
//            }
//            
//            @Override
//            public void onLoadedError() {
//                // TODO Auto-generated method stub
//                
//            }
//            
//            @Override
//            public void onLoadedCancel(String imageUrl) {
//                // TODO Auto-generated method stub
//                
//            }
//        });
//        aq.id(prodItem.image).image(imageURL, true, true, 0, 0, new BitmapAjaxCallback() {
//
//            @Override
//            public void callback(String url, ImageView iv, Bitmap bm,
//                    AjaxStatus status) {
//
//                iv.setImageBitmap(bm);
//                prodItem.progress.setVisibility(View.GONE);
//
//            }
//        });

        // Set is new image
        if(product.getAttributes().isNew()) {
            prodItem.isNew.setImageDrawable(isNewDrawable);
            prodItem.isNew.setVisibility(View.VISIBLE);
        } else {
            prodItem.isNew.setVisibility(View.GONE);
        }
        
        // Set brand
        prodItem.brand.setText(product.getBrand().toUpperCase());
        
        aq.id(prodItem.name).text(product.getName());
        aq.id(prodItem.price).text(product.getSuggestedPrice());
        if (product.getRating() != null && product.getRating() > 0) {
            aq.id(prodItem.rating).rating(product.getRating().floatValue());
            aq.id(prodItem.rating).visibility(View.VISIBLE);
            if (product.getReviews() != null) {
                aq.id(prodItem.reviews).text(product.getReviews() + " " + reviewLabel);
                aq.id(prodItem.reviews).visibility(View.VISIBLE);
            } else {
                aq.id(prodItem.reviews).visibility(View.GONE);
            }
        } else {
            aq.id(prodItem.rating).visibility(View.GONE);
            aq.id(prodItem.reviews).visibility(View.GONE);
        }

        if (null != product.getSpecialPrice()
                && !product.getSpecialPrice().equals(product.getPrice())) {
            aq.id(prodItem.discount).text(product.getSpecialPrice());
            aq.id(prodItem.discountPercentage).text("-"
                    + product.getMaxSavingPercentage().intValue() + "%");
            aq.id(prodItem.discount).visibility(View.VISIBLE);
            aq.id(prodItem.promotion).visibility(View.VISIBLE);
            aq.id(prodItem.discountPercentage).visibility(View.VISIBLE);
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.price.setSelected(true);
            aq.id(prodItem.price).textColor(context.getResources().getColor(
                    R.color.grey_light));
            prodItem.price.setTextAppearance(context.getApplicationContext(), R.style.text_normal);
        } else {
            aq.id(prodItem.discount).visibility(View.GONE);
            aq.id(prodItem.promotion).visibility(View.GONE);
            aq.id(prodItem.discountPercentage).visibility(View.GONE);
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags()
                    & (~Paint.STRIKE_THRU_TEXT_FLAG));
            prodItem.price.setTextAppearance(context.getApplicationContext(), R.style.text_bold);
            aq.id(prodItem.price).textColor(context.getResources().getColor(
                    R.color.red_basic));
        }
        return itemView;
    }

    /**
     * Updates the Products array list
     * 
     * @param products
     *            The array list containing the products
     */
    public void updateProducts(ArrayList<Product> products) {
        Log.d(TAG, "updateProducts: size = " + products.size());
        this.products = products;
        this.notifyDataSetChanged();
    }

    public void clearProducts() {
        products.clear();
        notifyDataSetChanged();
    }

    public void appendProducts(Collection<? extends Product> newProducts) {
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
