package com.mobile.adapters;

import android.graphics.Paint;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.extlibraries.emarsys.predict.recommended.Item;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.List;

public class RecommendGridAdapter extends RecyclerView.Adapter<RecommendGridAdapter.RecommendViewHolder> {
    public static final int TYPE_CATEGORIES_DROP_DOWN = 1, TYPE_PRODUCT = 2;
    private List<Item> items;
    private String categoryDropdownTitle;
    private final OnRecommendItemClickListener onRecommendItemClickListener;
    private View.OnClickListener onCategoryDropdownClickListener;

    public RecommendGridAdapter(List<Item> items, OnRecommendItemClickListener onRecommendItemClickListener) {
        this.items = items;
        this.onRecommendItemClickListener = onRecommendItemClickListener;
    }

    @Override
    public RecommendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @LayoutRes int layoutId = R.layout.cell_recommend_item;
        if (viewType == TYPE_CATEGORIES_DROP_DOWN) {
            layoutId = R.layout.row_my_bamilo_category_selection;
        }
        return new RecommendViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecommendViewHolder holder, int position) {
        if (position == 0) {
            if (categoryDropdownTitle == null) {
                holder.mCategoryDropdownTitle.setText(R.string.all_categories);
            } else {
                holder.mCategoryDropdownTitle.setText(categoryDropdownTitle);
            }
            holder.cvCategoryDropDown.setOnClickListener(onCategoryDropdownClickListener);
        } else {
            Item item = items.get(position - 1);
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
                                items.get(holder.getAdapterPosition() - 1), holder.getAdapterPosition() - 1);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_CATEGORIES_DROP_DOWN;
        }
        return TYPE_PRODUCT;
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

    public String getCategoryDropdownTitle() {
        return categoryDropdownTitle;
    }

    public void setCategoryDropdownTitle(String categoryDropdownTitle) {
        this.categoryDropdownTitle = categoryDropdownTitle;
        notifyItemChanged(0);
    }

    public View.OnClickListener getOnCategoryDropdownClickListener() {
        return onCategoryDropdownClickListener;
    }

    public void setOnCategoryDropdownClickListener(View.OnClickListener onCategoryDropdownClickListener) {
        this.onCategoryDropdownClickListener = onCategoryDropdownClickListener;
    }

    public static class RecommendViewHolder extends RecyclerView.ViewHolder {
        // Views of the recommend item
        private final ImageView mImage;
        private final View mProgress;
        private final TextView mBrand;
        private final TextView mName;
        private final TextView mPrice;
        private final TextView mOldPrice;

        // Views of the category item
        private final TextView mCategoryDropdownTitle;
        private CardView cvCategoryDropDown;
        private LinearLayout llCategoryDropDownParent;

        public RecommendViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.home_teaser_item_image);
            mProgress = view.findViewById(R.id.home_teaser_item_progress);
            mBrand = (TextView) view.findViewById(R.id.brand);
            mName = (TextView) view.findViewById(R.id.name);
            mPrice = (TextView) view.findViewById(R.id.price);
            mOldPrice = (TextView) view.findViewById(R.id.old_price);

            mCategoryDropdownTitle = (TextView) view.findViewById(R.id.tvCategoryDropdownTitle);
            cvCategoryDropDown = (CardView) view.findViewById(R.id.cvCategoryDropDown);
            llCategoryDropDownParent = (LinearLayout) view.findViewById(R.id.llCategoryDropDownParent);
        }
    }

    public interface OnRecommendItemClickListener {
        void onRecommendItemClicked(View v, Item item, int position);
    }
}
