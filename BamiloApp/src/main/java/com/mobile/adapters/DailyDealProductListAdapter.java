package com.mobile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;
import com.mobile.view.components.DailyDealViewComponent;

import java.util.List;
import java.util.Locale;

/**
 * Created on 10/17/2017.
 */

public class DailyDealProductListAdapter extends RecyclerView.Adapter<DailyDealProductListAdapter.ProductListViewHolder> {
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
                ImageManager.getInstance().loadImage(product.thumb, holder.imgProductThumb, null, R.drawable.no_image_large, false);
            } catch (Exception e) {
                Print.d(e.getMessage());
            }
        }

        holder.tvProductName.setText(product.name);
        holder.tvProductBrand.setText(product.brand);
        holder.tvProductPrice.setText(CurrencyFormatter.formatCurrency(product.price));
        if (product.oldPrice > 0 && product.maxSavingPercentage > 0) {
            holder.tvProductOldPrice.setVisibility(View.VISIBLE);
            holder.tvProductDiscountPercentage.setVisibility(View.VISIBLE);
            holder.tvProductOldPrice.setText(CurrencyFormatter.formatCurrency(product.oldPrice));
            holder.tvProductDiscountPercentage.setText(String.format(locale,
                    context.getString(R.string.daily_deals_item_percentage_placeholder), product.maxSavingPercentage));
        } else {
            holder.tvProductOldPrice.setVisibility(View.INVISIBLE);
            holder.tvProductDiscountPercentage.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDealProductItemClickListener != null) {
                    onDealProductItemClickListener.onDealProductClicked(v, products.get(holder.getAdapterPosition()));
                }
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

    public void setOnDealProductItemClickListener(OnDealProductItemClickListener onDealProductItemClickListener) {
        this.onDealProductItemClickListener = onDealProductItemClickListener;
    }

    public static class ProductListViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductBrand, tvProductPrice,
                tvProductOldPrice, tvProductDiscountPercentage;
        ImageView imgProductThumb;

        public ProductListViewHolder(View itemView) {
            super(itemView);
            tvProductName = (TextView) itemView.findViewById(R.id.tvDealProductName);
            tvProductBrand = (TextView) itemView.findViewById(R.id.tvDealProductBrand);
            tvProductPrice = (TextView) itemView.findViewById(R.id.tvDealProductPrice);
            tvProductOldPrice = (TextView) itemView.findViewById(R.id.tvDealProductOldPrice);
            tvProductDiscountPercentage = (TextView) itemView.findViewById(R.id.tvDealProductDiscountPercentage);
            imgProductThumb = (ImageView) itemView.findViewById(R.id.imgDealProductThumb);
        }
    }

    public interface OnDealProductItemClickListener {
        void onDealProductClicked(View v, DailyDealViewComponent.Product product);
    }
}
