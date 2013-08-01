package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.Collection;

import pt.rocket.framework.objects.Product;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import de.akquinet.android.androlog.Log;

/**
 * This Class is used to create an adapter for the list of products. It is called by ProductsList Activity. <p/><br> 
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
 *          2012/06/22
 *
 */
public class ProductsListAdapter extends BaseAdapter {
	private final static String TAG = LogTagHelper.create(ProductsListAdapter.class);
	
    public interface OnSelectedItemsChange {
        public void SelectedItemsChange(int numSelectedItems);
    }

    private OnSelectedItemsChange onSelectedItemsChange;

    ArrayList<Product> products;
    private ArrayList<Integer> selectedItems;

    private int jumpConstant = 0;
    int counter = 1;
    
    private final String reviewLabel;

	private LayoutInflater inflater;
	
	private Context context;


    /**
     * A representation of each item on the list
     */
    private static class Item {

        public ImageView image;
        public ImageView promotion;
        public View progress;
        public TextView name;
        public RatingBar rating;
        public TextView discount;
        public TextView price;
        public TextView discountPercentage;
        public TextView reviews;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#finalize()
         */
        @Override
        protected void finalize() throws Throwable {

            image = null;
            promotion = null;
            progress = null;
            name = null;

            rating = null;
            discount = null;
            price = null;
            discountPercentage = null;
            reviews = null;

            super.finalize();
        }
    }

    /**
     * the constructor for this adapter
     * 
     * @param activity
     */
    public ProductsListAdapter(Context context) {

        this.context = context.getApplicationContext();
        this.products = new ArrayList<Product>();
        this.selectedItems = new ArrayList<Integer>();

        this.inflater = LayoutInflater.from(context);

        // Portrait mode
        jumpConstant = 0;
        reviewLabel = context.getString(R.string.reviews);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        this.products = null;
        this.selectedItems = null;
        context = null;
        this.onSelectedItemsChange = null;

        super.finalize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return this.products.size() + jumpConstant;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        int activePosition = position - jumpConstant;
        Product prod = null;

        if (activePosition > -1 && activePosition < this.products.size()) {
            prod = this.products.get(activePosition);
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
        
        
        if((Item) itemView.getTag() == null){
            prodItem = new Item();
            prodItem.image = (ImageView) itemView.findViewById(R.id.image_view);
            prodItem.promotion = (ImageView) itemView.findViewById(R.id.item_promotion);
            prodItem.progress = itemView.findViewById(R.id.image_loading_progress);
            prodItem.name = (TextView) itemView.findViewById(R.id.item_name);
            prodItem.rating = (RatingBar) itemView.findViewById(R.id.item_rating);
            prodItem.price = (TextView) itemView.findViewById(R.id.item_regprice);
            prodItem.discount = (TextView) itemView.findViewById(R.id.item_discount);
            prodItem.discountPercentage = (TextView) itemView.findViewById(R.id.discount_percentage);
//            prodItem.vertSeperator = itemView.findViewById(R.id.prod_right_seperator);
//            prodItem.multiselect = itemView.findViewById(R.id.products_multiselect);
            prodItem.reviews = (TextView) itemView.findViewById(R.id.item_reviews);

            // stores the item representation on the tag of the view for later
            // retrieval
            itemView.setTag(prodItem);
        } else {
            prodItem = (Item) itemView.getTag();
        }

        String imageURL = "";
        Product product = products.get(position);
        if (product.getImages().size() > 0) {
            imageURL = product.getImages().get(0).getUrl();
        }
        
        prodItem.progress.setVisibility(View.GONE);
        
        ImageLoader.getInstance().displayImage(imageURL, prodItem.image,
                new ImageLoadingListener() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener
                     * #onLoadingComplete(java.lang.String, android.view.View,
                     * android.graphics.Bitmap)
                     */
                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                        prodItem.progress.setVisibility(View.GONE);
                        prodItem.image.setVisibility(View.VISIBLE);                        
                    }

                    @Override
                    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                        prodItem.progress.setVisibility(View.GONE);
                        prodItem.image.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                        prodItem.progress.setVisibility(View.GONE);
                        prodItem.image.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                        prodItem.progress.setVisibility(View.VISIBLE);
                    }
            
        });

        prodItem.name.setText(product.getName());
        prodItem.price.setText(product.getSuggestedPrice());
        if(product.getRating() != null && product.getRating() > 0) {
        	prodItem.rating.setRating(product.getRating().floatValue());
        	prodItem.rating.setVisibility(View.VISIBLE);
        	if(product.getReviews() != null) {
        		prodItem.reviews.setText(product.getReviews() + " " + reviewLabel);
        		prodItem.reviews.setVisibility(View.VISIBLE);
        	} else {
        		prodItem.reviews.setVisibility(View.GONE);
        	}
        } else {
        	prodItem.rating.setVisibility(View.GONE);
        	prodItem.reviews.setVisibility(View.GONE);
        }

        if (null != product.getSpecialPrice()
                && !product.getSpecialPrice().equals(product.getPrice())) {
			prodItem.discount.setText(product.getSpecialPrice());
			prodItem.discountPercentage.setText("-"
					+ product.getMaxSavingPercentage().intValue() + "%");
			prodItem.discount.setVisibility(View.VISIBLE);
			prodItem.promotion.setVisibility(View.VISIBLE);
			prodItem.discountPercentage.setVisibility(View.VISIBLE);
			prodItem.price.setPaintFlags(prodItem.price.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			prodItem.price.setTextColor(context.getResources().getColor(
					R.color.grey_light));
			prodItem.price.setTextAppearance(context.getApplicationContext(), R.style.text_normal);
        } else {
            prodItem.discount.setVisibility(View.GONE);
            prodItem.promotion.setVisibility(View.GONE);
            prodItem.discountPercentage.setVisibility(View.GONE);
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            prodItem.price.setTextAppearance(context.getApplicationContext(), R.style.text_bold);
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
    	Log.d( TAG, "updateProducts: size = " + products.size());
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
     * Updated the information to portrait mode
     */
    public void updateJumpConstant() {
        jumpConstant = 0;
    }

    /**
     * Retrieves the current orientation of the adapter
     * 
     * @return The orientation constant
     */
    public int getJumpConstant() {
        return jumpConstant;
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
        // ((ProductsActivity) activity).unselectItem(selectedItems.size());
        if (null != onSelectedItemsChange) {
            onSelectedItemsChange.SelectedItemsChange(selectedItems.size());
        }

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

    /**
     * sets the listener to be called when the number of selected items changes
     * 
     * @param value
     *            the listener
     */
    public void setOnSelectedItemsChanged(OnSelectedItemsChange value) {
        onSelectedItemsChange = value;
    }
    
    static class ViewHolder {
        public ImageView imageView;
        public TextView brand;
        public TextView name;
        public TextView priceStroke;
        public TextView priceNormal;
        public TextView pricePackage;

    }

}
