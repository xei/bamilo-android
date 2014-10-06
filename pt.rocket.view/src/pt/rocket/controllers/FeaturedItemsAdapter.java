package pt.rocket.controllers;

import java.util.ArrayList;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.FeaturedBrand;
import pt.rocket.framework.objects.FeaturedItem;
import pt.rocket.framework.objects.FeaturedProduct;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * PagerAdapter used on a ViewPager for featured items
 * 
 * @author Andre Lopes
 * 
 */
public class FeaturedItemsAdapter extends PagerAdapter {
    private static final String TAG = FeaturedItemsAdapter.class.getName();
    Context mContext;
    ArrayList<FeaturedItem> mFeaturedList;
    LayoutInflater mLayoutInflater;
    private int partialSize = 3;

    public FeaturedItemsAdapter(Context ctx, ArrayList<FeaturedItem> featured, LayoutInflater layoutInflater, int size) {
        this.mContext = ctx;
        this.mFeaturedList = featured;
        this.mLayoutInflater = layoutInflater;
        this.partialSize = size;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        int count = 0;

        int featureListSize = this.mFeaturedList.size();
        Log.d(TAG, "featureListSize: " + featureListSize);
        int pageIndex = featureListSize / this.partialSize;

        if (featureListSize % this.partialSize == 0) {
            count = pageIndex;
        } else {
            count = pageIndex + 1;
        }
        Log.d(TAG, "count: " + count);
        return count;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
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

            setViewForFeaturedItem(featuredItem, view, R.id.element_1, R.id.img_1, R.id.progress_1, R.id.name_1, R.id.price_1, index);
        } else {
            hideViewForFeaturedItem(view, R.id.element_1);
        }

        // second item
        index = position * this.partialSize + 1;
        if (index < featureListSize) {
            final FeaturedItem featuredItem = mFeaturedList.get(index);

            setViewForFeaturedItem(featuredItem, view, R.id.element_2, R.id.img_2, R.id.progress_2, R.id.name_2, R.id.price_2, index);
        } else {
            hideViewForFeaturedItem(view, R.id.element_2);
        }

        // third item
        index = position * this.partialSize + 2;
        if (index < featureListSize) {
            final FeaturedItem featuredItem = mFeaturedList.get(index);

            setViewForFeaturedItem(featuredItem, view, R.id.element_3, R.id.img_3, R.id.progress_3, R.id.name_3, R.id.price_3, index);
        } else {
            hideViewForFeaturedItem(view, R.id.element_3);
        }

        if (partialSize > 3) {

            // forth item
            index = position * this.partialSize + 3;
            if (index < featureListSize) {
                final FeaturedItem featuredItem = mFeaturedList.get(index);

                setViewForFeaturedItem(featuredItem, view, R.id.element_4, R.id.img_4, R.id.progress_4, R.id.name_4, R.id.price_4, index);
            } else {
                hideViewForFeaturedItem(view, R.id.element_4);
            }

            // fifth item
            index = position * this.partialSize + 4;
            if (index < featureListSize) {
                final FeaturedItem featuredItem = mFeaturedList.get(index);

                setViewForFeaturedItem(featuredItem, view, R.id.element_5, R.id.img_5, R.id.progress_5, R.id.name_5, R.id.price_5, index);
            } else {
                hideViewForFeaturedItem(view, R.id.element_5);
            }
        }

        container.addView(view);
        return view;
    }

    /**
     * OnClickListener with constructor to save an url Opens a new Fragment with a product or brand
     * 
     */
    class ElementOnClickListener implements OnClickListener {
        FeaturedItem featuredItem;

        public ElementOnClickListener(FeaturedItem featuredItem) {
            this.featuredItem = featuredItem;
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, featuredItem.getUrl());

            // default settings from FeaturedProduct
            int navigationSourceId = R.string.gteaserprod_prefix;
            FragmentType search = FragmentType.PRODUCT_DETAILS;

            // change behaviour depending on type of FeaturedItem
            if (featuredItem instanceof FeaturedProduct) {
                navigationSourceId = R.string.gsearch;
                search = FragmentType.PRODUCT_DETAILS;
            } else if (featuredItem instanceof FeaturedBrand) {
                navigationSourceId = R.string.gsearch;
                search = FragmentType.PRODUCT_LIST;
                // add title for Brands
                bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, featuredItem.getName());
            }

            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSourceId);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            ((BaseActivity) mContext).onSwitchFragment(search, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    };

    /**
     * set View on index <code>index</code> based on ids of layouts
     * 
     * @param featuredItem
     * @param view
     * @param idRelativeLayout
     * @param idImage
     * @param idProgress
     * @param idName
     * @param idPrice
     * @param index
     */
    void setViewForFeaturedItem(FeaturedItem featuredItem, View view, int idRelativeLayout, int idImage, int idProgress, int idName, int idPrice, int index) {
        RelativeLayout mElement = (RelativeLayout) view.findViewById(idRelativeLayout);

        // set price TextView visible for FeaturedProduct
        boolean hasPrice = false;
        if (featuredItem instanceof FeaturedProduct) {
            hasPrice = true;
        }

        if (mElement != null) {
            ElementOnClickListener elementOnClickListener = new ElementOnClickListener(featuredItem);
            mElement.setOnClickListener(elementOnClickListener);

            ImageView img = (ImageView) mElement.findViewById(idImage);
            View progress = mElement.findViewById(idProgress);
            TextView name = (TextView) mElement.findViewById(idName);
            // Log.i(TAG, "code1last generating : "+mFeaturedList.get(position).getProductName());
            name.setText(featuredItem.getName());
            if (hasPrice) {
                TextView price = (TextView) mElement.findViewById(idPrice);
                price.setText(((FeaturedProduct) featuredItem).getPrice());
            }

            // RocketImageLoader.instance.loadImage(featuredItem.getImageUrl(), img);
            RocketImageLoader.instance.loadImage(featuredItem.getImageUrl(), img, progress, R.drawable.no_image_large);
        } else {
            Log.e(TAG, "setViewForFeaturedItem for index: " + index + " with no layout available!");
        }
    }

    /**
     * 
     * @param view
     * @param idRelativeLayout
     */
    void hideViewForFeaturedItem(View view, int idRelativeLayout) {
        view.findViewById(idRelativeLayout).setVisibility(View.GONE);
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == (View) obj;
    }

}
