package com.mobile.view.components;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;
import com.mobile.view.widget.LimitedCountLinearLayoutManager;

import java.util.List;

public class CategoriesCarouselComponent implements BaseComponent<List<CategoriesCarouselComponent.CategoryItem>> {

    private List<CategoryItem> categoryItems;
    private OnCarouselItemClickListener onCarouselItemClickListener;

    public CategoriesCarouselComponent(List<CategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    @Override
    public View getView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.component_categories_carousel, null);
        RecyclerView rvCarouselItems = (RecyclerView) rootView.findViewById(R.id.rvCarouselItems);
        LimitedCountLinearLayoutManager layoutManager = new LimitedCountLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false,
                context.getResources().getInteger(R.integer.categories_carousel_items_quantity));
        rvCarouselItems.setLayoutManager(layoutManager);
        rvCarouselItems.setAdapter(new CarouselAdapter(categoryItems, onCarouselItemClickListener));

        return rootView;
    }

    @Override
    public void setContent(List<CategoryItem> content) {
        this.categoryItems = content;
    }

    public void setOnCarouselItemClickListener(OnCarouselItemClickListener onCarouselItemClickListener) {
        this.onCarouselItemClickListener = onCarouselItemClickListener;
    }

    public static class CategoryItem {
        public CategoryItem(String title, int imageResourceId, String targetLink) {
            this.title = title;
            this.imageResourceId = imageResourceId;
            this.targetLink = targetLink;
        }

        public CategoryItem(String title, String imageUrl, String targetLink) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.targetLink = targetLink;
            imageResourceId = 0;
        }

        public String title;
        public String imageUrl;
        @DrawableRes
        public int imageResourceId;
        public String targetLink;
    }

    public interface OnCarouselItemClickListener {
        void onCarouselClicked(View v, int position, CategoryItem item);
    }

    private class CarouselAdapter extends RecyclerView.Adapter<CarouselViewHolder> {
        private List<CategoryItem> categoryItems;
        private OnCarouselItemClickListener onCarouselItemClickListener;

        private CarouselAdapter(List<CategoryItem> categoryItems,
                                OnCarouselItemClickListener onCarouselItemClickListener) {
            this.categoryItems = categoryItems;
            this.onCarouselItemClickListener = onCarouselItemClickListener;
        }

        @Override
        public CarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CarouselViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.component_categories_carousel_cell, parent, false));
        }

        @Override
        public void onBindViewHolder(final CarouselViewHolder holder, int position) {
            final CategoryItem item = categoryItems.get(position);
            holder.tvCategoryTitle.setText(item.title);
            if (item.imageResourceId == 0) {
                setImageToLoad(item.imageUrl, holder.imgCategoryIcon);
            } else {
                holder.imgCategoryIcon.setImageResource(item.imageResourceId);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCarouselItemClickListener != null) {
                        onCarouselItemClickListener.onCarouselClicked(v, holder.getAdapterPosition(), item);
                    }
                }
            });
        }

        private void setImageToLoad(String imageUrl, ImageView imageView) {
            ImageManager.getInstance().loadImage(imageUrl, imageView, null, R.drawable.no_image_small, false);
        }

        @Override
        public int getItemCount() {
            if (categoryItems != null) {
                return categoryItems.size();
            }
            return 0;
        }
    }

    private class CarouselViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgCategoryIcon;
        private TextView tvCategoryTitle;

        public CarouselViewHolder(View itemView) {
            super(itemView);
            imgCategoryIcon = (ImageView) itemView.findViewById(R.id.imgCategoryIcon);
            tvCategoryTitle = (TextView) itemView.findViewById(R.id.tvCategoryTitle);
        }
    }
}
