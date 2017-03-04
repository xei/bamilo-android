package com.mobile.view.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.Toast;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.view.R;

import java.util.List;

import static com.mobile.view.R.id.imageView;

/**
 * Created by shahrooz on 2/16/17.
 */
public class CardCheckOutAdapter  extends RecyclerView.Adapter<CardCheckOutAdapter.CardCheckOutViewHolder> {

    private List<CardChoutItem> cardList;

    public CardCheckOutAdapter(List<CardChoutItem> cardList) {
        this.cardList = cardList;
    }


    @Override
    public CardCheckOutAdapter.CardCheckOutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout._def_checkout_confirmation_product, parent, false);

        return new CardCheckOutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardCheckOutAdapter.CardCheckOutViewHolder holder, int position) {
        CardChoutItem carditem = cardList.get(position);
        holder.brand.setText(carditem.getBrand());
        holder.price.setText(CurrencyFormatter.formatCurrency(carditem.getPrice()));
        holder.count.setText("تعداد : "+carditem.getCount());
        holder.product.setText(carditem.getProduct());

        RocketImageLoader.instance.loadImage(carditem.getImageUrl().replace("-cart.jpg","-catalog_grid_3.jpg"), holder.img, null, R.drawable.no_image_small);

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class CardCheckOutViewHolder extends RecyclerView.ViewHolder {
        public TextView  brand, product, price, count;
        ImageView img;
        public CardCheckOutViewHolder(View itemView) {
            super(itemView);
            brand = (TextView) itemView.findViewById(R.id.checkout_brand);
            count = (TextView) itemView.findViewById(R.id.checkout_count);
            price= (TextView) itemView.findViewById(R.id.checkout_price);
            product = (TextView) itemView.findViewById(R.id.checkout_product);
            img = (ImageView) itemView.findViewById(R.id.checkout_product_image);

        }
    }
}
