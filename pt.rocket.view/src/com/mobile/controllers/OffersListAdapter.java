package com.mobile.controllers;

import java.util.ArrayList;

import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.objects.Offer;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.view.R;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;

/**
 * Class that deals with offers list presentation
 * 
 * @author Paulo Carvalho
 * 
 */
public class OffersListAdapter extends BaseAdapter {
    
    public final static String TAG = LogTagHelper.create(OffersListAdapter.class);

    public interface OnAddOfferToCart {
        public void SelectedOffer(Offer offer);
    }

    private LayoutInflater inflater;

    int counter = 1;

    private Context context;

    private OnAddOfferToCart offerSelected;

    ArrayList<Offer> offers = new ArrayList<Offer>();
    
    /**
     * A representation of each item on the list
     */
    static class Item {
        public Button offerAddToCart;
        public TextView offerPrice;
        public TextView offerProductOwner;
        public RatingBar offerRating;
        public TextView offerReview;
        public TextView offerDeliveryTime;
    }

    /**
     * the constructor for this adapter
     * 
     * @param activity
     * @param showList
     *            show list (or grid)
     * @param numColumns
     */
    public OffersListAdapter(Context context, ArrayList<Offer> offers, OnAddOfferToCart listener) {
        this.context = context.getApplicationContext();
        this.offers = offers;
        this.inflater = LayoutInflater.from(context);
        this.offerSelected = listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return offers == null ? 0 : offers.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return offers.get(position);
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

        View itemView = convertView;
        final Item item;
        if (itemView == null) {

            // Inflate a New Item View
            itemView = inflater.inflate(R.layout.offers_item, parent, false);

            item = new Item();
            
            // item.textView = (TextView) itemView.findViewById( R.id.text);
            item.offerAddToCart = (Button) itemView.findViewById(R.id.offer_addcart);
            item.offerPrice = (TextView) itemView.findViewById(R.id.offer_price);
            item.offerProductOwner = (TextView) itemView.findViewById(R.id.offer_owner_name);
            item.offerRating = (RatingBar) itemView.findViewById(R.id.item_rating);
            item.offerReview = (TextView) itemView.findViewById(R.id.item_reviews);
            item.offerDeliveryTime = (TextView) itemView.findViewById(R.id.offer_item_delivery);

            itemView.setTag(item);

        } else {

            item = (Item) itemView.getTag();
        }

        item.offerPrice.setText(offers.get(position).getPriceOffer());
        item.offerProductOwner.setText(offers.get(position).getSeller().getName());
        String reviews = context.getResources().getString(R.string.reviews);
        if(offers.get(position).getSeller().getRatingCount() == 1){
            reviews = context.getResources().getString(R.string.review);
        } else {
            reviews = context.getResources().getString(R.string.reviews);
        }
        item.offerReview.setText(offers.get(position).getSeller().getRatingCount()+ " "+reviews);
        item.offerRating.setRating(offers.get(position).getSeller().getRatingValue());
        
        item.offerDeliveryTime.setText(context.getResources().getString(R.string.product_delivery_time)+" "+
        offers.get(position).getSeller().getMinDeliveryTime()+" - "+offers.get(position).getSeller().getMaxDeliveryTime()+" "+
                context.getResources().getString(R.string.product_delivery_days));
            
        item.offerAddToCart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                offerSelected.SelectedOffer(offers.get(position));
            }
        });

        return itemView;
    }

    /**
     * Updates the Orders array list
     * 
     * @param orders
     *            The array list containing the orders
     */
    public void updateOffers(ArrayList<Offer> offers) {
        this.offers = offers;
        this.notifyDataSetChanged();
    }

    public void clearProducts() {
        offers.clear();
        notifyDataSetChanged();
    }


    public ArrayList<Offer> getOffersList() {
        return offers;
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