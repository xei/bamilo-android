package pt.rocket.factories;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.widget.TextView;

import pt.rocket.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import pt.rocket.framework.objects.BrandsTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup.TeaserCategory;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup.TeaserProduct;
import pt.rocket.framework.objects.TeaserBrand;
import pt.rocket.framework.objects.TeaserCampaign;
import pt.rocket.framework.objects.TeaserGroupCampaigns;
import pt.rocket.framework.objects.TeaserGroupTopBrands;
import pt.rocket.framework.objects.TeaserGroupTopBrands.TeaserTopBrand;
import pt.rocket.framework.objects.TeaserImage;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.utils.WindowHelper;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.utils.imageloader.RocketImageLoader.RocketImageLoaderListener;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.viewpagerindicator.IconPagerAdapter;

import de.akquinet.android.androlog.Log;

/**
 * Object that generates the Teasers based on the Teaser specification
 * 
 * @author manuelsilva
 * 
 */
public class TeasersFactory {

    private static final String TAG = TeasersFactory.class.getSimpleName();
    
    private Context mContext;
    
    private OnClickListener onTeaserClickListener;
    
    private static final int MAX_IMAGES_ON_SCREEN = 2;
    
    private boolean isToResize = false;
    
    private LayoutInflater mInflater;
    
    private int mContentWidth;

    /**
     * Constructor with parameters
     * 
     * @param context
     * @param teaserSpecification
     */
    public TeasersFactory(Context context, LayoutInflater layoutInflater, OnClickListener onClickListener) {
        this.mContext = context;
        this.mInflater = layoutInflater;
        this.onTeaserClickListener = onClickListener;
        this.mContentWidth = WindowHelper.getWidth(mContext);
    }
    
    /**
     * Set the container width to fill the product list
     * @param width
     * @author sergiopereira
     */
    public void setContainerWidthToLoadImage(int width){
        this.mContentWidth = width;
    }

    /**
     * 
     * @param main
     * @param teaserSpecification
     * @return
     */
    public View getSpecificTeaser(ViewGroup main, TeaserSpecification<?> teaserSpecification) {
        View mView = null;
        Log.i(TAG, "generating teaser : "+teaserSpecification.getType());
        isToResize = false;
        switch (teaserSpecification.getType()) {
        case MAIN_ONE_SLIDE:
            isToResize = true;
            mView = getMainOneSlide(main, (ArrayList<TeaserImage>) teaserSpecification.getTeasers());
            break;
        case STATIC_BANNER:
            isToResize = true;
            mView = getStaticBanner(main, (ArrayList<TeaserImage>) teaserSpecification.getTeasers());
            break;
        case CATEGORIES:
            mView = getCategoriesTeaser(main, (CategoryTeaserGroup) teaserSpecification);
            break;
        case PRODUCT_LIST:
            mView = getProductsListTeaser(main, (ProductTeaserGroup) teaserSpecification);
            break;
        case BRANDS_LIST:
            isToResize = true;
            mView = getBrandsListTeaser(main, (BrandsTeaserGroup) teaserSpecification);
            break;
        case TOP_BRANDS_LIST:
            mView = getTeaserTopBrands(main, (TeaserGroupTopBrands) teaserSpecification);
            break;
        case CAMPAIGNS_LIST:
        	mView = getTeaserCampaigns(main, (TeaserGroupCampaigns) teaserSpecification);
        default:
            break;
        }

        return mView;
    }

    private View getMainOneSlide(ViewGroup mainView, ArrayList<TeaserImage> teaserImageArrayList) {
        View rootView = mInflater.inflate(R.layout.teaser_big_banner, mainView, false);
        final ViewPager pager = (ViewPager) rootView.findViewById(R.id.viewpager);
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
            
            // Validate device and orientation
            if(mContext.getResources().getBoolean(R.bool.isTablet) && teaserImageArrayList.get(0).getImageTableUrl() != null) {
                //Log.d(TAG, "SLIDE IMG: LOADED TABLET " + teaserImageArrayList.get(0).getImageTableUrl());
                setImageToLoad(teaserImageArrayList.get(0).getImageTableUrl(),imageContainer, 0, R.drawable.no_image_large);
            } else {
                //Log.d(TAG, "SLIDE IMG: LOADED PHONE " + teaserImageArrayList.get(0).getImageUrl());
                setImageToLoad(teaserImageArrayList.get(0).getImageUrl(),imageContainer, 0, R.drawable.no_image_large);
            }
            
            attachTeaserListener(teaserImageArrayList.get(0), imageContainer);
        }
        return rootView;
    }

    private View getStaticBanner(ViewGroup mainView, ArrayList<TeaserImage> teaserImageArrayList) {
        View rootView = mInflater.inflate(R.layout.teaser_banners_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView.findViewById(R.id.teaser_group_container);

        if (null != mContext) {
            int width = WindowHelper.getWidth(mContext);
            container.requestLayout();
            LayoutParams params = container.getLayoutParams();
            
            if(!mContext.getResources().getBoolean(R.bool.isTablet)){
                params.height = width / 2;
            } else {
                params.height = mContext.getResources().getDimensionPixelSize(R.dimen.teaser_banners_group_height);
            }

            container.setLayoutParams(params);
            
        } 
        if (teaserImageArrayList != null) {
            int maxItems = MAX_IMAGES_ON_SCREEN;
            if (teaserImageArrayList.size() < MAX_IMAGES_ON_SCREEN) {
                maxItems = teaserImageArrayList.size();
            }

            int i;
            for (i = 0; i < maxItems; i++) {
                TeaserImage image = teaserImageArrayList.get(i);
                if (i > 0) mInflater.inflate(R.layout.vertical_divider, container);
                container.addView(createImageTeaserView(image, container, mInflater, maxItems));
            }

        }
        return rootView;
    }

    private View getCategoriesTeaser(ViewGroup mainView, CategoryTeaserGroup teaserCategoryGroup) {
        View rootView = mInflater.inflate(R.layout.teaser_categories_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView.findViewById(R.id.teaser_group_container);
        if (teaserCategoryGroup != null) {
            ((TextView) rootView.findViewById(R.id.teaser_group_title)).setText(teaserCategoryGroup.getTitle());
            // Add each item
            for (TeaserCategory category : teaserCategoryGroup.getTeasers())
                container.addView(createCategoryTeaserView(category, container, mInflater));
            // Add item for all categories
            container.addView(createCategoryAllTeaserView(container, mInflater));
        }
        return rootView;
    }

    /**
     * 
     * @param mainView
     * @param productTeaserGroup
     * @return
     */
    private View getProductsListTeaser(ViewGroup mainView, ProductTeaserGroup productTeaserGroup) {
        View rootView = mInflater.inflate(R.layout.teaser_products_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView.findViewById(R.id.teaser_group_container);

        if (productTeaserGroup != null) {
            ((TextView) rootView.findViewById(R.id.teaser_group_title)).setText(productTeaserGroup.getTitle());
            for (TeaserProduct product : productTeaserGroup.getTeasers())
                container.addView(createProductTeaserView(product, container, mInflater, productTeaserGroup.getTeasers().size()));
        }
        return rootView;
    }

    /**
     * 
     * @param mainView
     * @param brandsTeaserGroup
     * @return
     */
    private View getBrandsListTeaser(ViewGroup mainView, BrandsTeaserGroup brandsTeaserGroup) {
        View rootView = mInflater.inflate(R.layout.teaser_brands_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView.findViewById(R.id.teaser_group_container);
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
        View imageTeaserView = mInflater.inflate(R.layout.image_loadable, vg, false);
        
        // Validate device and orientation
        if(mContext.getResources().getBoolean(R.bool.isTablet) && teaserImage.getImageTableUrl() != null) {
            // Log.d(TAG, "T IMG: LOADED TABLET " + teaserImage.getImageTableUrl());
            setImageToLoad(teaserImage.getImageTableUrl(), imageTeaserView, size, R.drawable.no_image_large);
        } else {
            // Log.d(TAG, "T IMG: LOADED PHONE " + teaserImage.getImageUrl());
            setImageToLoad(teaserImage.getImageUrl(), imageTeaserView, size, R.drawable.no_image_large);
        }
        
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
        categoryTeaserView = mInflater.inflate(R.layout.teaser_inner_childcat, vg, false);
        categoryTeaserView.findViewById(R.id.divider).setVisibility(View.GONE);
        TextView textView = (TextView) categoryTeaserView.findViewById(R.id.text);
        textView.setText(cat.getName());
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
        View view = mInflater.inflate(R.layout.teaser_inner_currentcat, container, false);
        view.findViewById(R.id.divider).setVisibility(View.GONE);
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
    private View createProductTeaserView(TeaserProduct product, ViewGroup vg, LayoutInflater mInflater, int size) {
        View productTeaserView = mInflater.inflate(R.layout.product_item_small, vg, false);
        // Tablet
        if(mContext.getResources().getBoolean(R.bool.isTablet) && product.getImagesTablet() != null && product.getImagesTablet().size() > 0) {
            // Log.d(TAG, "PROD IMG: LOADED TABLET " + product.getImagesTablet().get(0).getUrl());
            setImageToLoad(product.getImagesTablet().get(0).getUrl(), productTeaserView, 0, R.drawable.no_image_large);
        } // Portrait
        else if (product.getImages() != null && product.getImages().size() > 0) {
            // Log.d(TAG, "PROD IMG: LOADED PHONE " + product.getImages().get(0).getUrl());
            setImageToLoad(product.getImages().get(0).getUrl(), productTeaserView, 0, R.drawable.no_image_large);
        }
        // Set data
        ((TextView) productTeaserView.findViewById(R.id.item_brand)).setText(product.getBrand());
        ((TextView) productTeaserView.findViewById(R.id.item_title)).setText(product.getName());
        // Set price
        String price = (!TextUtils.isEmpty(product.getSpecialPrice())) ? product.getSpecialPrice() : product.getPrice(); 
        ((TextView) productTeaserView.findViewById(R.id.item_price)).setText(price);
        attachTeaserListener(product, productTeaserView);
        return productTeaserView;
    }
    

    private View createBrandTeaserView(TeaserBrand brand, ViewGroup vg, LayoutInflater mInflater, int size) {
        View brandTeaserView = mInflater.inflate(R.layout.brand_item_small, vg, false);
        
        // Tablet
        if(mContext.getResources().getBoolean(R.bool.isTablet) && brand.getImageTableUrl() != null && brand.getImageTableUrl().length() > 0) {
            // Log.d(TAG, "BRAND IMG: LOADED TABLET " + brand.getImageTableUrl());
            setImageToLoad(brand.getImageTableUrl(), brandTeaserView, size, R.drawable.no_image_large);
        } // Portrait
        else if (brand.getImageUrl() != null) {
            // Log.d(TAG, "BRAND IMG: LOADED PHONE " + brand.getImageUrl());
            setImageToLoad(brand.getImageUrl(), brandTeaserView, size, R.drawable.no_image_large);
        }
        
        attachTeaserListener(brand, brandTeaserView);
        return brandTeaserView;
    }
   
    /*
     * ################## TOP BRANDS ##################
     */
    
    /**
     * Create the view to list the top brands
     * @param mInflater
     * @param teaserGroupTopBrands
     * @return View
     * @author sergiopereira
     */
    private View getTeaserTopBrands(ViewGroup mainView, TeaserGroupTopBrands teaserGroupTopBrands) {
        View rootView = mInflater.inflate(R.layout.teaser_top_brands_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView.findViewById(R.id.teaser_group_container);
        if (teaserGroupTopBrands != null) {
            ((TextView) rootView.findViewById(R.id.teaser_group_title)).setText(teaserGroupTopBrands.getTitle().replace("_", " "));
            // Add each brand
            for (TeaserTopBrand teaser : teaserGroupTopBrands.getTeasers()) {
                container.addView(createTopBrandTeaserView(teaser, container, mInflater));
            }
        }
        return rootView;
    }
    
    /**
     * Create the top brand teaser view
     * @param teaser
     * @param vg
     * @param mInflater
     * @return the brand view
     * @author sergiopereira
     */
    private View createTopBrandTeaserView(TeaserTopBrand teaser, ViewGroup vg, LayoutInflater mInflater) {
        View mTopBrandTeaserView;
        mTopBrandTeaserView = mInflater.inflate(R.layout.teaser_inner_childcat, vg, false);
        mTopBrandTeaserView.findViewById(R.id.divider).setVisibility(View.GONE);
        TextView textView = (TextView) mTopBrandTeaserView.findViewById(R.id.text);
        textView.setText(teaser.getName());
        attachTeaserListener(teaser, mTopBrandTeaserView);
        return mTopBrandTeaserView;
    }
    
    /**
     * Generate the All Categories view
     * @param container
     * @param mInflater
     * @return the view that represent all brands
     * @author sergiopereira
     */
    @SuppressWarnings("unused")
    private View createItemAllBrandsView(ViewGroup container, LayoutInflater mInflater) {
        View view = mInflater.inflate(R.layout.teaser_inner_currentcat, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(mContext.getString(R.string.top_brands_toplevel_title));
        view.setOnClickListener(onTeaserClickListener);
        view.setTag(R.id.target_url, null);
        view.setTag(R.id.target_type, ITargeting.TargetType.BRAND);
        return view;
    }  
    
    /*
     * ################## CAMPAIGNS ##################
     */
    
    /**
     * Create the view to list the top brands
     * @param mInflater
     * @param teaserGroupTopBrands
     * @return View
     * @author sergiopereira
     */
    private View getTeaserCampaigns(ViewGroup mainView, TeaserGroupCampaigns teaserGroupCampaigns) {
        View rootView = mInflater.inflate(R.layout.teaser_campaigns_group, mainView, false);
        ViewGroup container = (ViewGroup) rootView.findViewById(R.id.teaser_group_container);
        if (teaserGroupCampaigns != null) {
            // Get teaser campaigns
            ArrayList<TeaserCampaign> campaigns = teaserGroupCampaigns.getTeasers();
            // Get size
            int size = campaigns.size();
            // Validatin
            switch (size) {
            // Case empty
            case 0:
                rootView.findViewById(R.id.teaser_group_title_container).setVisibility(View.GONE);
                break;
            // Case one
            case 1:
                // Set title
                ((TextView) rootView.findViewById(R.id.teaser_group_title)).setText(campaigns.get(0).getTargetTitle());
                attachTeaserListener(campaigns.get(0), rootView.findViewById(R.id.teaser_group_title_container));
                break;
            // Case multi
            default:
                // Set title
                ((TextView) rootView.findViewById(R.id.teaser_group_title)).setText(teaserGroupCampaigns.getTitle());
                attachTeaserListener(campaigns.get(0), rootView.findViewById(R.id.teaser_group_title_container));
                // Create views
                for (int i = 0; i < campaigns.size(); i++) {
                    View view = createCampaignTeaserView(campaigns.get(i), container, mInflater);
                    view.setTag(R.id.position, i);
                    container.addView(view);
                }
                break;
            }
        }
        return rootView;
    }
    
    /**
     * Create the top brand teaser view
     * @param teaser
     * @param vg
     * @param mInflater
     * @return the brand view
     * @author sergiopereira
     */
    private View createCampaignTeaserView(TeaserCampaign teaser, ViewGroup vg, LayoutInflater mInflater) {
    	View campaignTeaserView = mInflater.inflate(R.layout.teaser_inner_childcat, vg, false);
    	campaignTeaserView.findViewById(R.id.divider).setVisibility(View.GONE);
        TextView textView = (TextView) campaignTeaserView.findViewById(R.id.text);
        textView.setText(teaser.getTargetTitle());
        attachTeaserListener(teaser, campaignTeaserView);
        return campaignTeaserView;
    }
    
    /*
     * ################## IMAGE ##################
     */
    
    
    /**
     * Loads the image and hide progress bar
     * @param imageUrl
     * @param imageTeaserView
     */
    private void setImageToLoad(String imageUrl, View imageTeaserView, int size, int placeHolder) {
        final ImageView imageView = (ImageView) imageTeaserView.findViewById(R.id.image_view);
        final View progressBar = imageTeaserView.findViewById(R.id.image_loading_progress);

        // Adapts the Image size if needed
        if(size > 0 && imageTeaserView.getLayoutParams() != null) {
            if(mContentWidth == 0) mContentWidth = WindowHelper.getWidth(mContext);
            imageTeaserView.getLayoutParams().width = mContentWidth / size;
        }
        
        // Validate place holder
        placeHolder = placeHolder < 0 ? R.drawable.no_image_large : placeHolder;
        
        if (!TextUtils.isEmpty(imageUrl)) {
            // Flag for FIT_XY
            final boolean resize = isToResize;
            // Load image
            RocketImageLoader.instance.loadImage(imageUrl, imageView, progressBar, placeHolder, new RocketImageLoaderListener() {
                
                @Override
                public void onLoadedSuccess(Bitmap bitmap) { if(resize) imageView.setScaleType(ScaleType.FIT_XY); }
                
                @Override
                public void onLoadedError() { }
                
                @Override
                public void onLoadedCancel(String imageUrl) { }
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
