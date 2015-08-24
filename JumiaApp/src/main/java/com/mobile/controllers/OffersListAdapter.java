package com.mobile.controllers;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;

import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductOffer;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class that deals with offers list presentation
 * 
 * @author Paulo Carvalho
 * 
 */
public class OffersListAdapter extends BaseAdapter {
    
    public final static String TAG = OffersListAdapter.class.getSimpleName();

    public interface IOffersAdapterService {
        void onAddOfferToCart(ProductOffer offer);
    }

    private LayoutInflater inflater;

    private Context context;

    private IOffersAdapterService offerSelected;

    ArrayList<ProductOffer> offers = new ArrayList<>();
    
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
     * @param context
     * @param offers
     * @param listener
     */
    public OffersListAdapter(Context context, ArrayList<ProductOffer> offers, IOffersAdapterService listener) {
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

        item.offerPrice.setText(CurrencyFormatter.formatCurrency(offers.get(position).getFinalPriceString()));
        item.offerProductOwner.setText(offers.get(position).getSeller().getName());

        int ratingCount = offers.get(position).getSeller().getRatingCount();
        String reviewLabel = context.getResources().getQuantityString(R.plurals.reviews_array, ratingCount, ratingCount);

        item.offerReview.setText(reviewLabel);
        item.offerRating.setRating(offers.get(position).getSeller().getRatingValue());

        if( !(offers.get(position).getMinDeliveryTime() == 0 && offers.get(position).getMaxDeliveryTime() == 0) ) {
            item.offerDeliveryTime.setVisibility(View.VISIBLE);
            item.offerDeliveryTime.setText(context.getResources().getString(R.string.product_delivery_time) + " " +
                    offers.get(position).getMinDeliveryTime() + " - " + offers.get(position).getMaxDeliveryTime() + " " +
                    context.getResources().getString(R.string.product_delivery_days));
        } else {
            item.offerDeliveryTime.setVisibility(View.GONE);
        }

        item.offerAddToCart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                offerSelected.onAddOfferToCart(offers.get(position));
            }
        });

        return itemView;
    }

    /**
     * Updates the Orders array list
     * 
     * @param offers
     *            The array list containing the orders
     */
    public void updateOffers(ArrayList<ProductOffer> offers) {
        this.offers = offers;
        this.notifyDataSetChanged();
    }

    public void clearProducts() {
        offers.clear();
        notifyDataSetChanged();
    }


    public ArrayList<ProductOffer> getOffersList() {
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
