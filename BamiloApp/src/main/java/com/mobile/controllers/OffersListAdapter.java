package com.mobile.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.product.pojo.ProductOffer;
import com.mobile.service.objects.product.pojo.ProductSimple;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that deals with offers list presentation
 * 
 * @author Paulo Carvalho
 * 
 */
public class OffersListAdapter extends RecyclerView.Adapter<OffersListAdapter.ProductOfferHolder> {

    public final static String TAG = OffersListAdapter.class.getSimpleName();

    public interface IOffersAdapterService {
        void onAddOfferToCart(ProductOffer offer);
        void onClickVariation(ProductOffer offer);
    }

    private final Context context;

    private final IOffersAdapterService offerSelected;

    ArrayList<ProductOffer> offers = new ArrayList<>();

    /**
     * the constructor for this adapter
     *
     */
    public OffersListAdapter(Context context, ArrayList<ProductOffer> offers, @NonNull IOffersAdapterService listener) {
        this.context = context.getApplicationContext();
        this.offers = offers;
        this.offerSelected = listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */

    @Override
    public ProductOfferHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductOfferHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.other_sellers_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductOfferHolder item, int position) {
        final ProductOffer offer = offers.get(position);
        UIProductUtils.setPriceRules(offer, item.offerPrice, item.offerSpecialPrice);
        item.offerProductOwner.setText(offer.getSeller().getName());

        if( !(offer.getMinDeliveryTime() == 0 && offer.getMaxDeliveryTime() == 0) ) {
            item.offerDeliveryTime.setVisibility(View.VISIBLE);
            item.offerDeliveryTime.setText(context.getResources().getString(R.string.delivery_time_placeholder,offer.getMinDeliveryTime(), offer.getMaxDeliveryTime()));
        } else {
            item.offerDeliveryTime.setVisibility(View.GONE);
        }

        item.offerAddToCart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offerSelected.onAddOfferToCart(offer);
            }
        });

        List<ProductSimple> simples = offer.getSimples();
        if(CollectionUtils.isNotEmpty(simples) && simples.size() > 1) {

            if(offer.hasSelectedSimpleVariation()) {
                item.variations.setText(offer.getSimples().get(offer.getSelectedSimplePosition()).getVariationValue());
            }
            item.variations.setVisibility(View.VISIBLE);
            item.variations.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    offerSelected.onClickVariation(offer);
                }
            });
        } else {
            item.variations.setVisibility(View.GONE);
        }
        // Set shop first
        //UIProductUtils.setShopFirst(offer, item.shopFirst);
        // Set free shipping
        UIProductUtils.setFreeShippingInfo(offer, item.mFreeShipping);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public class ProductOfferHolder extends RecyclerView.ViewHolder {

        private final Button offerAddToCart;
        private final TextView offerPrice;
        private final TextView offerSpecialPrice;
        private final TextView offerProductOwner;
        private final TextView offerDeliveryTime;
        private final TextView variations;
        //private final ImageView shopFirst;
        private final View mFreeShipping;

        public ProductOfferHolder(View itemView) {
            super(itemView);
            offerAddToCart = (Button) itemView.findViewById(R.id.offer_addcart);
            offerPrice = (TextView) itemView.findViewById(R.id.offer_price);
            offerSpecialPrice = (TextView) itemView.findViewById(R.id.offer_price_normal);
            offerProductOwner = (TextView) itemView.findViewById(R.id.offer_owner_name);
            offerDeliveryTime = (TextView) itemView.findViewById(R.id.offer_item_delivery);
            variations = (TextView) itemView.findViewById(R.id.button_variant);
            //shopFirst = (ImageView) itemView.findViewById(R.id.item_shop_first);
            mFreeShipping = itemView.findViewById(R.id.offer_item_shipping);
        }

    }

}
