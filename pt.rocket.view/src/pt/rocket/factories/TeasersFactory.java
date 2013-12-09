package pt.rocket.factories;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.widget.TextView;

import pt.rocket.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import pt.rocket.framework.objects.BrandsTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.BrandsTeaserGroup.TeaserBrand;
import pt.rocket.framework.objects.CategoryTeaserGroup.TeaserCategory;
import pt.rocket.framework.objects.ImageTeaserGroup.TeaserImage;
import pt.rocket.framework.objects.ProductTeaserGroup.TeaserProduct;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.utils.WindowHelper;
import pt.rocket.view.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.viewpagerindicator.IconPagerAdapter;

import de.akquinet.android.androlog.Log;

/**
 * Object that generates the Teasers based on the Teaser specification
 * 
 * @author manuelsilva
 * 
 */
public class TeasersFactory {

    private Context mContext;
    private TeaserSpecification<?> mTeaserSpecification;
    private OnClickListener onTeaserClickListener;
    private static final int MAX_IMAGES_ON_SCREEN = 2;
    private static final String TAG = TeasersFactory.class.getName();
    private ViewGroup mainView;
    /**
     * Empty Constructor
     */

    public TeasersFactory() {
    }

    /**
     * Constructor with parameters
     * 
     * @param context
     * @param teaserSpecification
     */
    public TeasersFactory(Context context, TeaserSpecification<?> teaserSpecification) {
        this.mContext = context;
        this.mTeaserSpecification = teaserSpecification;
    }

    public View getSpecificTeaser(Context context, ViewGroup main, TeaserSpecification<?> teaserSpecification, LayoutInflater mLayoutInflater, OnClickListener mOnClickListener) {
        mContext = context;
        mainView = main;
        onTeaserClickListener = mOnClickListener;
        View mView = null;
        Log.i(TAG, "generating teaser : "+teaserSpecification.getType());
        switch (teaserSpecification.getType()) {
        case MAIN_ONE_SLIDE:
            mView = getMainOneSlide(mLayoutInflater, (ArrayList<TeaserImage>) teaserSpecification.getTeasers());
            break;
        case STATIC_BANNER:
            mView = getStaticBanner(mLayoutInflater, (ArrayList<TeaserImage>) teaserSpecification.getTeasers());
            break;
        case CATEGORIES:
            mView = getCategoriesTeaser(mLayoutInflater, (CategoryTeaserGroup) teaserSpecification);
            break;
        case PRODUCT_LIST:
            mView = getProductsListTeaser(mLayoutInflater, (ProductTeaserGroup) teaserSpecification);
            break;
        case BRANDS_LIST:
            mView = getBrandsListTeaser(mLayoutInflater, (BrandsTeaserGroup) teaserSpecification);
            break;
        default:
            break;
        }

        return mView;
    }

    private View getMainOneSlide(LayoutInflater mInflater, ArrayList<TeaserImage> teaserImageArrayList) {
        View rootView = mInflater.inflate(R.layout.teaser_big_banner, mainView, false);
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewpager );
        pager.setSaveEnabled(false);
        final View imageContainer = rootView.findViewById(R.id.banner_view);
        if (pager.getAdapter() == null && teaserImageArrayList!=null && teaserImageArrayList.size()>1) {
            pager.setVisibility(View.VISIBLE);
            imageContainer.setVisibility(View.GONE);
            ImagePagerAdapter adapter = new ImagePagerAdapter(teaserImageArrayList, mInflater);
            pager.setAdapter(adapter);
        } else if(teaserImageArrayList!= null && teaserImageArrayList.size() == 1) {
            pager.setVisibility(View.GONE);
            imageContainer.setVisibility(View.VISIBLE);
            setImageToLoad(teaserImageArrayList.get(0).getImageUrl(),imageContainer, 0);
            attachTeaserListener(teaserImageArrayList.get(0), imageContainer);
        }
        return rootView;
    }

    private View getStaticBanner(LayoutInflater mInflater, ArrayList<TeaserImage> teaserImageArrayList) {
        View rootView = mInflater.inflate(R.layout.teaser_banners_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView
                .findViewById(R.id.teaser_group_container);

        if (teaserImageArrayList != null) {
            int maxItems = MAX_IMAGES_ON_SCREEN;
            if (teaserImageArrayList.size() < MAX_IMAGES_ON_SCREEN) {
                maxItems = teaserImageArrayList.size();
            }

            int i;
            for (i = 0; i < maxItems; i++) {
                TeaserImage image = teaserImageArrayList.get(i);
                if (i > 0)
                    mInflater.inflate(R.layout.vertical_divider, container);
                container.addView(createImageTeaserView(image, container, mInflater, maxItems));
            }

        }
        return rootView;
    }

    private View getCategoriesTeaser(LayoutInflater mInflater, CategoryTeaserGroup teaserCategoryGroup) {
        View rootView = mInflater.inflate(R.layout.teaser_categories_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView
                .findViewById(R.id.teaser_group_container);
        if (teaserCategoryGroup != null) {
            ((TextView) rootView.findViewById(R.id.teaser_group_title))
                    .setText(teaserCategoryGroup.getTitle());
            container.addView(createCategoryAllTeaserView(container, mInflater));
            for (TeaserCategory category : teaserCategoryGroup.getTeasers()) {
                container
                        .addView(createCategoryTeaserView(category, container, mInflater));
            }
        }
        return rootView;
    }

    private View getProductsListTeaser(LayoutInflater mInflater, ProductTeaserGroup productTeaserGroup) {
        View rootView = mInflater.inflate(R.layout.teaser_products_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView
                .findViewById(R.id.teaser_group_container);

        if (productTeaserGroup != null) {
            ((TextView) rootView.findViewById(R.id.teaser_group_title))
                    .setText(productTeaserGroup.getTitle());
            for (TeaserProduct product : productTeaserGroup.getTeasers()) {
                container.addView(createProductTeaserView(product, container, mInflater, productTeaserGroup.getTeasers().size()));
            }
        }
        return rootView;
    }

    private View getBrandsListTeaser(LayoutInflater mInflater, BrandsTeaserGroup brandsTeaserGroup) {
        View rootView = mInflater.inflate(R.layout.teaser_brands_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView
                .findViewById(R.id.teaser_group_container);
        if(brandsTeaserGroup!=null){
            for (TeaserBrand brand : brandsTeaserGroup.getTeasers()) {
                container.addView(createBrandTeaserView(brand, container, mInflater, brandsTeaserGroup.getTeasers().size()));
            }    
        }
        return rootView;
    }
   
    /**
     * Create the Image Teaser View
     * @param teaserImage
     * @param vg
     * @param mInflater
     * @return
     */
    private View createImageTeaserView(TeaserImage teaserImage, ViewGroup vg, LayoutInflater mInflater, int size) {
        View imageTeaserView = mInflater.inflate(R.layout.image_loadable, vg,
                false);
        setImageToLoad(teaserImage.getImageUrl(), imageTeaserView, size);
        attachTeaserListener(teaserImage, imageTeaserView);
        return imageTeaserView;
    }
    
    /**
     * Attach a listener to a teaser in order to infer later what action should be triggered.
     * @param targeting
     * @param view
     */
    private void attachTeaserListener(ITargeting targeting, View view) {
        view.setTag(R.id.target_url, targeting.getTargetUrl());
        view.setTag(R.id.target_type, targeting.getTargetType());
        view.setTag(R.id.target_title, targeting.getTargetTitle());
        view.setOnClickListener(onTeaserClickListener);
    }
    
    /**
     * Create the category teaser view
     * @param cat
     * @param vg
     * @param mInflater
     * @return
     */
    private View createCategoryTeaserView(TeaserCategory cat, ViewGroup vg, LayoutInflater mInflater) {
        View categoryTeaserView;
        categoryTeaserView = mInflater.inflate(R.layout.category_inner_childcat, vg, false);
        TextView textView = (TextView) categoryTeaserView.findViewById(R.id.text);
        textView.setText(cat
                .getName());
        attachTeaserListener(cat, categoryTeaserView);
        return categoryTeaserView;
    }
    
    /**
     * Generate the All Categories view
     * @param container
     * @param mInflater
     * @return
     */
    private View createCategoryAllTeaserView(ViewGroup container, LayoutInflater mInflater) {
        View view = mInflater.inflate(
                R.layout.category_inner_currentcat, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(mContext.getString(R.string.categories_toplevel_title));
        view.setOnClickListener(onTeaserClickListener);
        view.setTag(R.id.target_url, null);
        view.setTag(R.id.target_type, ITargeting.TargetType.CATEGORY);
        return view;
    }
    
    /**
     * Generate the Product Teaser View
     * @param product
     * @param vg
     * @param mInflater
     * @return
     */
    private View createProductTeaserView(TeaserProduct product, ViewGroup vg,
            LayoutInflater mInflater, int size) {
        View productTeaserView = mInflater.inflate(R.layout.product_item_small,
                vg, false);
        if (product.getImages().size() > 0) {
            setImageToLoad(product.getImages().get(0).getUrl(),
                    productTeaserView, size);
        }
        ((TextView) productTeaserView.findViewById(R.id.item_title))
                .setText(product.getName());
        String price;
        if (!TextUtils.isEmpty(product.getSpecialPrice())) {
            price = product.getSpecialPrice();
        } else {
            price = product.getPrice();
        }
        ((TextView) productTeaserView.findViewById(R.id.item_price))
                .setText(price);
        attachTeaserListener(product, productTeaserView);
        return productTeaserView;
    }
    
    private View createBrandTeaserView(TeaserBrand brand, ViewGroup vg, LayoutInflater mInflater, int size) {
        View brandTeaserView = mInflater.inflate(R.layout.brand_item_small,
                vg, false);
        if (brand.getImageUrl() != null) {
            setImageToLoad(brand.getImageUrl(),
                    brandTeaserView, size);
        }
        attachTeaserListener(brand, brandTeaserView);
        return brandTeaserView;
    }
    
    /**
     * Loads the image and hide progress bar
     * @param imageUrl
     * @param imageTeaserView
     */
    private void setImageToLoad(String imageUrl, View imageTeaserView, int size) {
        final ImageView imageView = (ImageView) imageTeaserView
                .findViewById(R.id.image_view);
        final View progressBar = imageTeaserView
                .findViewById(R.id.image_loading_progress);
        Log.i(TAG, "Going to resize...");
        // Adapts the Image size if needed
        if(size > 0 && imageTeaserView.getLayoutParams() != null){
            int mainContentWidth = (int) (WindowHelper.getWidth(mContext) * mContext.getResources().getFraction(R.dimen.navigation_menu_offset,1,1));
            imageTeaserView.getLayoutParams().width = mainContentWidth / size;
            Log.i(TAG, "Resize finished!");
        }
        
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageLoader.getInstance().displayImage(imageUrl, imageView,
                    new SimpleImageLoadingListener() {

                        /*
                         * (non-Javadoc)
                         * 
                         * @see
                         * com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener
                         * #onLoadingComplete(java.lang.String, android.view.View,
                         * android.graphics.Bitmap)
                         */
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            progressBar.setVisibility(View.GONE);
                            imageView.setScaleType(ScaleType.FIT_XY);
                            imageView.setVisibility(View.VISIBLE);
                        }
                        
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            // TODO Auto-generated method stub
                            super.onLoadingStarted(imageUri, view);
                            imageView.setScaleType(ScaleType.FIT_CENTER);
                        }
                    });
        }

    }
    
    /**
     * Adapter for the View Pager
     * @author manuelsilva
     *
     */
    private class ImagePagerAdapter extends PagerAdapter implements IPagerAdapter, IconPagerAdapter {

        private List<TeaserImage> teaserImages;
        private LayoutInflater mInflater;
        /**
         * @param inflater 
         * 
         */
        public ImagePagerAdapter(List<TeaserImage> teaserImages, LayoutInflater inflater) {
            this.teaserImages = teaserImages;
            this.mInflater = inflater;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return MAX_REAL_ITEMS;
        }
        
        public int getRealCount() {
            return teaserImages.size();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.support.v4.view.PagerAdapter#isViewFromObject(android.view
         * .View, java.lang.Object)
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.support.v4.view.PagerAdapter#instantiateItem(android.view
         * .View, int)
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {  
            int virtualPosition = position % getRealCount();
            return instantiateVirtualItem(container, virtualPosition);
        }
        
        public Object instantiateVirtualItem(ViewGroup container, int position) {
            TeaserImage teaserImage = teaserImages.get(position);
            View view = createImageTeaserView(teaserImage, container, mInflater, teaserImages.size());
            attachTeaserListener(teaserImage, view);
            container.addView(view);
            return view;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup
         * , int, java.lang.Object)
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        
        @Override
        public int getStartVirtualPosition() {
            return 0;
        }

        @Override
        public int getIconResId(int index) {
            return R.drawable.shape_pageindicator;            
        }

    }
}