package com.mobile.adapters;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.extlibraries.emarsys.predict.recommended.Item;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.List;

public class RecommendGridAdapter extends RecyclerView.Adapter<RecommendGridAdapter.RecommendViewHolder> {
    private List<Item> items;
    private final OnRecommendItemClickListener onRecommendItemClickListener;

    public RecommendGridAdapter(List<Item> items, OnRecommendItemClickListener onRecommendItemClickListener) {
        this.items = items;
        this.onRecommendItemClickListener = onRecommendItemClickListener;
    }

    @Override
    public RecommendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecommendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_recommend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecommendViewHolder holder, int position) {
        Item item = items.get(position);
        holder.mName.setText(item.getTitle());
        String imageUrl = item.getImage();
        if (imageUrl != null) {
            try {
                ImageManager.getInstance().loadImage(imageUrl, holder.mImage, holder.mProgress, R.drawable.no_image_large, false);
            } catch (Exception e) {
                Print.d(e.getMessage());
            }
        }
        holder.mBrand.setText(item.getBrand());
        double price = item.getPrice();
        double special = item.getSpecialPrice();
        if (price != special) {
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(price));
            holder.mOldPrice.setText(CurrencyFormatter.formatCurrency(special));
            holder.mOldPrice.setPaintFlags(holder.mOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mOldPrice.setVisibility(View.VISIBLE);
        } else {
            holder.mPrice.setText(CurrencyFormatter.formatCurrency(price));
            holder.mOldPrice.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRecommendItemClickListener != null) {
                    onRecommendItemClickListener.onRecommendItemClicked(v,
                            items.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class RecommendViewHolder extends RecyclerView.ViewHolder {
        // Views
        private final ImageView mImage;
        private final View mProgress;
        private final TextView mBrand;
        private final TextView mName;
        private final TextView mPrice;
        private final TextView mOldPrice;

        public RecommendViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
            mBrand = (TextView) view.findViewById(R.id.brand);
            mName = (TextView) view.findViewById(R.id.name);
            mPrice = (TextView) view.findViewById(R.id.price);
            mOldPrice = (TextView) view.findViewById(R.id.old_price);
        }
    }

    public interface OnRecommendItemClickListener {
        void onRecommendItemClicked(View v, Item item, int position);
    }
}
