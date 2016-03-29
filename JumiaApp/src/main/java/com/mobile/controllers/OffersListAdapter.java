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
public class OffersListAdapter extends RecyclerView.Adapter<OffersListAdapter.ProductOfferHolder> {

    public final static String TAG = OffersListAdapter.class.getSimpleName();

    public interface IOffersAdapterService {
        void onAddOfferToCart(ProductOffer offer);
        void onClickVariation(ProductOffer offer);
    }

    private Context context;

    private IOffersAdapterService offerSelected;

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
        ProductUtils.setShopFirst(productOffer,item.shopFirst);

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

        private Button offerAddToCart;
        private TextView offerPrice;
        private TextView offerSpecialPrice;
        private TextView offerProductOwner;
        private TextView offerDeliveryTime;
        private TextView variations;
        private ImageView shopFirst;

        public ProductOfferHolder(View itemView) {
            super(itemView);
            offerAddToCart = (Button) itemView.findViewById(R.id.offer_addcart);
            offerPrice = (TextView) itemView.findViewById(R.id.offer_price);
            offerSpecialPrice = (TextView) itemView.findViewById(R.id.offer_price_normal);
            offerProductOwner = (TextView) itemView.findViewById(R.id.offer_owner_name);
            offerDeliveryTime = (TextView) itemView.findViewById(R.id.offer_item_delivery);
            variations = (TextView) itemView.findViewById(R.id.button_variant);
            shopFirst = (ImageView) itemView.findViewById(R.id.item_shop_first);
        }

    }

}
