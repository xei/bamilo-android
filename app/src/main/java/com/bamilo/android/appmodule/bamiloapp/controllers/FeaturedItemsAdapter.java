package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.framework.service.objects.catalog.FeaturedItem;
import com.bamilo.android.framework.service.objects.catalog.FeaturedItemBrand;
import com.bamilo.android.framework.service.objects.catalog.FeaturedItemProduct;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import java.util.ArrayList;

/**
 * PagerAdapter used on a ViewPager for featured items
 *
 * @author Andre Lopes
 */
public class FeaturedItemsAdapter extends PagerAdapter {

    private static final String TAG = FeaturedItemsAdapter.class.getName();
    Context mContext;
    ArrayList<FeaturedItem> mFeaturedList;
    LayoutInflater mLayoutInflater;
    private int partialSize = 3;

    public FeaturedItemsAdapter(Context ctx, ArrayList<FeaturedItem> featured,
            LayoutInflater layoutInflater, int size) {
        this.mContext = ctx;
        this.mFeaturedList = featured;
        this.mLayoutInflater = layoutInflater;
        this.partialSize = size;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        int count;

        int featureListSize = this.mFeaturedList.size();
        int pageIndex = featureListSize / this.partialSize;

        if (featureListSize % this.partialSize == 0) {
            count = pageIndex;
        } else {
            count = pageIndex + 1;
        }
        return count;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        int viewId = R.layout.element_last_viewed;
        // only use the landscape layout when partialSize > 3
        if (this.partialSize > 3) {
            viewId = R.layout.element_last_viewed_landscape;
        }
        View view = mLayoutInflater.inflate(viewId, container, false);

        int featureListSize = this.mFeaturedList.size();

        // first item
        int index = position * this.partialSize;
        if (index < featureListSize) {
            final FeaturedItem featuredItem = mFeaturedList.get(index);

            setViewForFeaturedItem(featuredItem, view, R.id.element_1, R.id.img_1, R.id.progress_1,
                    R.id.name_1, R.id.price_1, index);
        }

        // second item
        index = position * this.partialSize + 1;
        if (index < featureListSize) {
            final FeaturedItem featuredItem = mFeaturedList.get(index);

            setViewForFeaturedItem(featuredItem, view, R.id.element_2, R.id.img_2, R.id.progress_2,
                    R.id.name_2, R.id.price_2, index);
        }

        index = position * this.partialSize + 2;
        if (index < featureListSize) {
            final FeaturedItem featuredItem = mFeaturedList.get(index);
            setViewForFeaturedItem(featuredItem, view, R.id.element_3, R.id.img_3, R.id.progress_3,
                    R.id.name_3, R.id.price_3, index);
        }

        if (partialSize > 3) {
            index = position * this.partialSize + 3;
            if (index < featureListSize) {
                final FeaturedItem featuredItem = mFeaturedList.get(index);

                setViewForFeaturedItem(featuredItem, view, R.id.element_4, R.id.img_4,
                        R.id.progress_4, R.id.name_4, R.id.price_4, index);
            }

            index = position * this.partialSize + 4;
            if (index < featureListSize) {
                final FeaturedItem featuredItem = mFeaturedList.get(index);

                setViewForFeaturedItem(featuredItem, view, R.id.element_5, R.id.img_5,
                        R.id.progress_5, R.id.name_5, R.id.price_5, index);
            }
        }

        container.addView(view);
        return view;
    }

    /**
     * OnClickListener with constructor to save an url Opens a new Fragment with a product or brand
     */
    class ElementOnClickListener implements OnClickListener {

        FeaturedItem featuredItem;

        public ElementOnClickListener(FeaturedItem featuredItem) {
            this.featuredItem = featuredItem;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            // default settings from FeaturedProduct
            int navigationSourceId = R.string.gteaserprod_prefix;
            FragmentType search = FragmentType.PRODUCT_DETAILS;
            // change behaviour depending on type of FeaturedItem
            if (featuredItem instanceof FeaturedItemProduct) {
                bundle.putString(ConstantsIntentExtra.CONTENT_ID,
                        ((FeaturedItemProduct) featuredItem).getSku());
                navigationSourceId = R.string.gsearch;
                search = FragmentType.PRODUCT_DETAILS;
            } else if (featuredItem instanceof FeaturedItemBrand) {
                bundle.putString(ConstantsIntentExtra.CONTENT_ID,
                        TargetLink.getIdFromTargetLink(featuredItem.getTarget()));
                navigationSourceId = R.string.gsearch;
                search = FragmentType.CATALOG_BRAND;
                // add title for Brands
                bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, featuredItem.getName());
            }

            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSourceId);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            ((BaseActivity) mContext)
                    .onSwitchFragment(search, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * set View on index <code>index</code> based on ids of layouts
     */
    void setViewForFeaturedItem(FeaturedItem featuredItem, View view, int idRelativeLayout,
            int idImage, int idProgress, int idName, int idPrice, int index) {
        RelativeLayout mElement = view.findViewById(idRelativeLayout);

        boolean hasPrice = false;
        if (featuredItem instanceof FeaturedItemProduct) {
            hasPrice = true;
        }

        if (mElement != null) {
            ElementOnClickListener elementOnClickListener = new ElementOnClickListener(
                    featuredItem);
            mElement.setOnClickListener(elementOnClickListener);
            ImageView img = mElement.findViewById(idImage);
            View progress = mElement.findViewById(idProgress);
            TextView name = mElement.findViewById(idName);

            name.setText(featuredItem.getName());
            if (hasPrice) {
                TextView textView = mElement.findViewById(idPrice);
                double price = ((FeaturedItemProduct) featuredItem).getPrice();
                double special = ((FeaturedItemProduct) featuredItem).getSpecialPrice();
                if (!Double.isNaN(special) && special > 0) {
                    textView.setText(CurrencyFormatter.formatCurrency(special));
                } else {
                    textView.setText(CurrencyFormatter.formatCurrency(price));
                }
            }
            ImageManager.getInstance()
                    .loadImage(featuredItem.getImageUrl(), img, progress, R.drawable.no_image_large,
                            false);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }
}
