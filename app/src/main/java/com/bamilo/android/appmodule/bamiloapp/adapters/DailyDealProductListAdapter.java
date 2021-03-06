package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.view.components.DailyDealViewComponent;
import com.bamilo.android.appmodule.modernbamilo.util.extension.StringExtKt;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Created on 10/17/2017.
 */

public class DailyDealProductListAdapter extends
        RecyclerView.Adapter<DailyDealProductListAdapter.ProductListViewHolder> {

    private List<DailyDealViewComponent.Product> products;
    private OnDealProductItemClickListener onDealProductItemClickListener;

    public DailyDealProductListAdapter(List<DailyDealViewComponent.Product> products) {
        this.products = products;
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_daily_deals_product_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(final ProductListViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Locale locale = new Locale("fa", "ir");
        final DailyDealViewComponent.Product product = products.get(position);

        if (product.thumb != null) {
            try {
                ImageManager.getInstance().loadImage(product.thumb, holder.imgProductThumb, null,
                        R.drawable.no_image_large, false);
            } catch (Exception e) {
            }
        }

        holder.tvProductName.setText(product.name);
        holder.tvProductBrand.setText(product.brand);
        holder.tvProductPrice.setText(StringExtKt
                .persianizeDigitsInString(CurrencyFormatter.formatCurrency(product.price)));
        holder.viewOutOfStockCover.setVisibility(View.GONE);
        if (!product.hasStock) {
            holder.tvProductOldPrice.setVisibility(View.INVISIBLE);
            holder.tvProductDiscountPercentage.setVisibility(View.VISIBLE);
            holder.tvProductDiscountPercentage.setBackgroundResource(R.drawable.gray_1_badge_bg);
            holder.tvProductDiscountPercentage.setTextColor(Color.WHITE);
            holder.tvProductDiscountPercentage.setText(R.string.deal_item_out_of_stock_label);
            holder.viewOutOfStockCover.setVisibility(View.VISIBLE);
        } else if (product.oldPrice > 0 && product.maxSavingPercentage > 0) {
            holder.tvProductOldPrice.setVisibility(View.VISIBLE);
            holder.tvProductOldPrice.setPaintFlags(
                    holder.tvProductOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvProductOldPrice.setText(StringExtKt
                    .persianizeDigitsInString(CurrencyFormatter.formatCurrency(product.oldPrice)));
            holder.tvProductDiscountPercentage.setVisibility(View.VISIBLE);
            holder.tvProductDiscountPercentage.setBackgroundResource(R.drawable.gray_badge_bg);
            holder.tvProductDiscountPercentage
                    .setTextColor(ContextCompat.getColor(context, R.color.recommendation_grey));
            holder.tvProductDiscountPercentage
                    .setText(StringExtKt.persianizeDigitsInString(String.format(locale,
                            context.getString(R.string.daily_deals_item_percentage_placeholder),
                            product.maxSavingPercentage)));
        } else {
            holder.tvProductOldPrice.setVisibility(View.INVISIBLE);
            holder.tvProductDiscountPercentage.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (onDealProductItemClickListener != null) {
                onDealProductItemClickListener
                        .onDealProductClicked(v, products.get(holder.getAdapterPosition()),
                                holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (products == null) {
            return 0;
        }
        return products.size();
    }

    public List<DailyDealViewComponent.Product> getProducts() {
        return products;
    }

    public void setProducts(List<DailyDealViewComponent.Product> products) {
        this.products = products;
    }

    public OnDealProductItemClickListener getOnDealProductItemClickListener() {
        return onDealProductItemClickListener;
    }

    public void setOnDealProductItemClickListener(
            OnDealProductItemClickListener onDealProductItemClickListener) {
        this.onDealProductItemClickListener = onDealProductItemClickListener;
    }

    static class ProductListViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvProductBrand, tvProductPrice,
                tvProductOldPrice, tvProductDiscountPercentage;
        ImageView imgProductThumb;
        View viewOutOfStockCover;

        ProductListViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvDealProductName);
            tvProductBrand = itemView.findViewById(R.id.tvDealProductBrand);
            tvProductPrice = itemView.findViewById(R.id.tvDealProductPrice);
            tvProductOldPrice = itemView.findViewById(R.id.tvDealProductOldPrice);
            tvProductDiscountPercentage = itemView
                    .findViewById(R.id.tvDealProductDiscountPercentage);
            imgProductThumb = itemView.findViewById(R.id.imgDealProductThumb);
            viewOutOfStockCover = itemView.findViewById(R.id.viewOutOfStockCover);
        }
    }

    public interface OnDealProductItemClickListener {

        void onDealProductClicked(View v, DailyDealViewComponent.Product product, int position);
    }
}
