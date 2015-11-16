package com.mobile.controllers;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;

import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductOffer;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.utils.ui.ProductUtils;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that deals with offers list presentation
 * 
 * @author Paulo Carvalho
 * 
 */
public class OffersListAdapterNew extends BaseAdapter {

    public final static String TAG = OffersListAdapterNew.class.getSimpleName();

    public interface IOffersAdapterService {
        void onAddOfferToCart(ProductOffer offer);
        void onClickVariation(ProductOffer offer);
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
        public TextView offerSpecialPrice;
        public TextView offerProductOwner;
        public RatingBar offerRating;
        public TextView offerReview;
        public TextView offerDeliveryTime;
        public TextView variations;
    }

    /**
     * the constructor for this adapter
     *
     */
    public OffersListAdapterNew(Context context, ArrayList<ProductOffer> offers, @NonNull IOffersAdapterService listener) {
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
            itemView = inflater.inflate(R.layout.other_sellers_list_item, parent, false);
            item = new Item();

            // item.textView = (TextView) itemView.findViewById( R.id.text);
            item.offerAddToCart = (Button) itemView.findViewById(R.id.offer_addcart);
            item.offerPrice = (TextView) itemView.findViewById(R.id.offer_price);
            item.offerSpecialPrice = (TextView) itemView.findViewById(R.id.offer_price_normal);
            item.offerProductOwner = (TextView) itemView.findViewById(R.id.offer_owner_name);

            item.offerRating = (RatingBar) itemView.findViewById(R.id.item_rating);
            item.offerReview = (TextView) itemView.findViewById(R.id.item_reviews);
            item.offerDeliveryTime = (TextView) itemView.findViewById(R.id.offer_item_delivery);
            item.variations = (TextView) itemView.findViewById(R.id.button_variant);
            itemView.setTag(item);

        } else {

            item = (Item) itemView.getTag();
        }

        final ProductOffer productOffer = offers.get(position);
        ProductUtils.setPriceRules(productOffer, item.offerPrice, item.offerSpecialPrice);
        item.offerProductOwner.setText(productOffer.getSeller().getName());

        if( !(productOffer.getMinDeliveryTime() == 0 && productOffer.getMaxDeliveryTime() == 0) ) {
            item.offerDeliveryTime.setVisibility(View.VISIBLE);
            item.offerDeliveryTime.setText(context.getResources().getString(R.string.delivery_time_placeholder,productOffer.getMinDeliveryTime(), productOffer.getMaxDeliveryTime()));
        } else {
            item.offerDeliveryTime.setVisibility(View.GONE);
        }

        item.offerAddToCart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                offerSelected.onAddOfferToCart(productOffer);
            }
        });

        List<ProductSimple> simples = productOffer.getSimples();
        if(CollectionUtils.isNotEmpty(simples) && simples.size() > 1) {

            if(productOffer.hasSelectedSimpleVariation()) {
                item.variations.setText(productOffer.getSimples().get(productOffer.getSelectedSimplePosition()).getVariationValue());
            }
            item.variations.setVisibility(View.VISIBLE);
            item.variations.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    offerSelected.onClickVariation(productOffer);
                }
            });
        } else {
            item.variations.setVisibility(View.GONE);
        }

        return itemView;
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
