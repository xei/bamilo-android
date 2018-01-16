package com.mobile.view.components;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.classes.models.SimpleEventModel;
import com.mobile.constants.tracking.EventActionKeys;
import com.mobile.constants.tracking.EventConstants;
import com.mobile.managers.TrackerManager;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;
import com.mobile.view.widget.LimitedCountLinearLayoutManager;

import java.util.List;
import java.util.Locale;

public class CategoriesCarouselViewComponent extends BaseViewComponent<List<CategoriesCarouselViewComponent.CategoryItem>> {

    private List<CategoryItem> categoryItems;
    private OnCarouselItemClickListener onCarouselItemClickListener;

    @Override
    public View getView(Context context) {
        if (categoryItems != null) {
            View rootView = LayoutInflater.from(context).inflate(R.layout.component_categories_carousel, null);
            RecyclerView rvCarouselItems = (RecyclerView) rootView.findViewById(R.id.rvCarouselItems);
            LimitedCountLinearLayoutManager layoutManager = new LimitedCountLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false,
                    context.getResources().getInteger(R.integer.categories_carousel_items_quantity));
            rvCarouselItems.setLayoutManager(layoutManager);
            rvCarouselItems.setAdapter(new CarouselAdapter(categoryItems, onCarouselItemClickListener));

            return rootView;
        }
        return null;
    }

    @Override
    public void setContent(List<CategoryItem> content) {
        this.categoryItems = content;
    }

    public void setOnCarouselItemClickListener(OnCarouselItemClickListener onCarouselItemClickListener) {
        this.onCarouselItemClickListener = onCarouselItemClickListener;
    }

    public static class CategoryItem {
        public CategoryItem(String title, @DrawableRes int imageResourceId, String targetLink) {
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
        private static final String ALL_CATEGORIES_SCREEN = "all-categories-screen";
        private List<CategoryItem> categoryItems;
        private OnCarouselItemClickListener onCarouselItemClickListener;
        private CategoryItem allCategoriesItem;

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
        public void onBindViewHolder(final CarouselViewHolder holder, final int position) {
            // the first item is all categories button
            CategoryItem item;
            if (position == 0) {
                Context context = holder.itemView.getContext();
                allCategoriesItem = new CategoryItem(context.getString(R.string.all_categories), R.drawable.carousel_categories_icon,
                        ALL_CATEGORIES_SCREEN);
                item = allCategoriesItem;
            } else {
                item = categoryItems.get(position - 1);
            }
            holder.tvCategoryTitle.setText(item.title);
            if (item.imageResourceId == 0) {
                setImageToLoad(item.imageUrl, holder.imgCategoryIcon);
            } else {
                holder.imgCategoryIcon.setImageResource(item.imageResourceId);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CategoryItem item;
                    if (position == 0) {
                        item = allCategoriesItem;
                    } else {
                        item = categoryItems.get(holder.getAdapterPosition() - 1);
                    }
                    if (mPage != null) {
                        String category = String.format(Locale.US, "%s+%s_%d", mPage, ComponentType.Carousel.toString(), mInstanceIndex);
                        String action = EventActionKeys.TEASER_TAPPED;
                        String label = item.targetLink;
                        SimpleEventModel sem = new SimpleEventModel(category, action, label, SimpleEventModel.NO_VALUE);
                        TrackerManager.trackEvent(v.getContext(), EventConstants.TeaserTapped, sem);
                    }
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
                return categoryItems.size() + 1;
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
