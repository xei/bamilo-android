//package com.bamilo.android.appmodule.bamiloapp.adapters;
//
//import android.graphics.Paint;
//import android.support.annotation.LayoutRes;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys.predict.recommended.Item;
//import com.bamilo.android.appmodule.modernbamilo.util.extension.StringExtKt;
//import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
//import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
//import com.bamilo.android.R;
//
//import java.util.List;
//
//public class RecommendGridAdapter extends RecyclerView.Adapter<RecommendGridAdapter.RecommendViewHolder> {
//    public static final int TYPE_CATEGORIES_DROP_DOWN = 1, TYPE_PRODUCT = 2;
//    private List<Item> items;
//    private String categoryDropdownTitle;
//    private final OnRecommendItemClickListener onRecommendItemClickListener;
//    private View.OnClickListener onCategoryDropdownClickListener;
//
//    public RecommendGridAdapter(List<Item> items, OnRecommendItemClickListener onRecommendItemClickListener) {
//        this.items = items;
//        this.onRecommendItemClickListener = onRecommendItemClickListener;
//    }
//
//    @NonNull
//    @Override
//    public RecommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        @LayoutRes int layoutId = R.layout.cell_recommend_item;
//        if (viewType == TYPE_CATEGORIES_DROP_DOWN) {
//            layoutId = R.layout.row_my_bamilo_category_selection;
//        }
//        return new RecommendViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final RecommendViewHolder holder, int position) {
//        if (position == 0) {
//            if (categoryDropdownTitle == null) {
//                holder.mCategoryDropdownTitle.setText(R.string.all_categories);
//            } else {
//                holder.mCategoryDropdownTitle.setText(categoryDropdownTitle);
//            }
//            holder.cvCategoryDropDown.setOnClickListener(onCategoryDropdownClickListener);
//        } else {
//            Item item = items.get(position - 1);
//            holder.mName.setText(item.getTitle());
//            String imageUrl = item.getImage();
//            if (imageUrl != null) {
//                try {
//                    ImageManager.getInstance().loadImage(imageUrl, holder.mImage, holder.mProgress, R.drawable.no_image_large, false);
//                } catch (Exception ignored) {
//                }
//            }
//            holder.mBrand.setText(item.getBrand());
//            double price = item.getPrice();
//            double special = item.getSpecialPrice();
//            if (price != special) {
//                holder.mPrice.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(price)));
//                holder.mOldPrice.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(special)));
//                holder.mOldPrice.setPaintFlags(holder.mOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                holder.mOldPrice.setVisibility(View.VISIBLE);
//            } else {
//                holder.mPrice.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(price)));
//                holder.mOldPrice.setVisibility(View.INVISIBLE);
//            }
//
//            holder.itemView.setOnClickListener(v -> {
//                if (onRecommendItemClickListener != null) {
//                    onRecommendItemClickListener.onRecommendItemClicked(v,
//                            items.get(holder.getAdapterPosition() - 1), holder.getAdapterPosition() - 1);
//                }
//            });
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_CATEGORIES_DROP_DOWN;
//        }
//        return TYPE_PRODUCT;
//    }
//
//    @Override
//    public int getItemCount() {
//        if (items == null) {
//            return 0;
//        }
//        return items.size();
//    }
//
//    public List<Item> getItems() {
//        return items;
//    }
//
//    public void setItems(List<Item> items) {
//        this.items = items;
//    }
//
//    public String getCategoryDropdownTitle() {
//        return categoryDropdownTitle;
//    }
//
//    public void setCategoryDropdownTitle(String categoryDropdownTitle) {
//        this.categoryDropdownTitle = categoryDropdownTitle;
//        notifyItemChanged(0);
//    }
//
//    public View.OnClickListener getOnCategoryDropdownClickListener() {
//        return onCategoryDropdownClickListener;
//    }
//
//    public void setOnCategoryDropdownClickListener(View.OnClickListener onCategoryDropdownClickListener) {
//        this.onCategoryDropdownClickListener = onCategoryDropdownClickListener;
//    }
//
//    public static class RecommendViewHolder extends RecyclerView.ViewHolder {
//        // Views of the recommend item
//        private final ImageView mImage;
//        private final View mProgress;
//        private final TextView mBrand;
//        private final TextView mName;
//        private final TextView mPrice;
//        private final TextView mOldPrice;
//
//        // Views of the category item
//        private final TextView mCategoryDropdownTitle;
//        private CardView cvCategoryDropDown;
//        private LinearLayout llCategoryDropDownParent;
//
//        public RecommendViewHolder(View view) {
//            super(view);
//            mImage = view.findViewById(R.id.home_teaser_item_image);
//            mProgress = view.findViewById(R.id.home_teaser_item_progress);
//            mBrand = view.findViewById(R.id.brand);
//            mName = view.findViewById(R.id.name);
//            mPrice = view.findViewById(R.id.price);
//            mOldPrice = view.findViewById(R.id.old_price);
//
//            mCategoryDropdownTitle = view.findViewById(R.id.tvCategoryDropdownTitle);
//            cvCategoryDropDown = view.findViewById(R.id.cvCategoryDropDown);
//            llCategoryDropDownParent = view.findViewById(R.id.llCategoryDropDownParent);
//        }
//    }
//
//    public interface OnRecommendItemClickListener {
//        void onRecommendItemClicked(View v, Item item, int position);
//    }
//}
