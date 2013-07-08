/**
 * 
 */
package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.rocket.controllers.NormalizingViewPagerWrapper.IPagerAdapter;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup.TeaserCategory;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.objects.Image;
import pt.rocket.framework.objects.ImageTeaserGroup;
import pt.rocket.framework.objects.ImageTeaserGroup.TeaserImage;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup.TeaserProduct;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.objects.TeaserSpecification.TeaserGroupType;
import pt.rocket.view.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

/**
 * @author nutzer2
 * 
 */
public class TeaserGroupAdapter extends ArrayAdapter<TeaserSpecification<?>> {

	private static final int MAX_IMAGES_ON_SCREEN = 2;
    private final LayoutInflater mInflater;
	private final Context context;
	private final OnClickListener onTeaserClickListener;

	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public TeaserGroupAdapter(Context context,
			OnClickListener onTeaserClickListener) {
		super(context, 0);
		this.context = context;
		this.onTeaserClickListener = onTeaserClickListener;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		return TeaserGroupType.values().length - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		return getItem(position).getType().value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			switch (getItem(position).getType()) {
			case MAIN_ONE_SLIDE:
				convertView = mInflater.inflate(
						R.layout.teaser_swipe_banners_group, parent, false);
				break;
			case STATIC_BANNER:
				convertView = mInflater.inflate(R.layout.teaser_banners_group,
						parent, false);
				break;
			case CATEGORIES:
				convertView = mInflater.inflate(
						R.layout.teaser_categories_group, parent, false);
				break;
			case PRODUCT_LIST:
				convertView = mInflater.inflate(R.layout.teaser_products_group,
						parent, false);
				break;
			}
		}
		ViewGroup container = (ViewGroup) convertView
				.findViewById(R.id.teaser_group_container);
		if (container != null)
			container.removeAllViews();
		switch (getItem(position).getType()) {
		case MAIN_ONE_SLIDE:
			ViewPager pager = (ViewPager) convertView.findViewById(R.id.viewpager );
			if (pager.getAdapter() == null) {
                ImagePagerAdapter adapter = new ImagePagerAdapter(
                        ((ImageTeaserGroup) getItem(position)).getTeasers());
                pager.setAdapter(adapter);
                PageIndicator indicator = (PageIndicator) convertView
                        .findViewById(R.id.indicator);
                NormalizingViewPagerWrapper pagerWrapper = new NormalizingViewPagerWrapper(
                        getContext(), pager, adapter, indicator);
                indicator.setViewPager(pagerWrapper);
			}
			break;
		case STATIC_BANNER:
			ImageTeaserGroup imageGroup = (ImageTeaserGroup) getItem(position);
			ArrayList<TeaserImage> images  = imageGroup.getTeasers();
            int maxItems = MAX_IMAGES_ON_SCREEN;
            if ( images.size() < MAX_IMAGES_ON_SCREEN) {
                maxItems = images.size();
            }
            
            int i;
            for( i = 0; i < maxItems; i++ ) {
                TeaserImage image = images.get(i);
                if (i > 0)
                    mInflater.inflate(R.layout.vertical_divider, container);
                container.addView(createImageTeaserView(image, container));
            }
			break;
		case CATEGORIES:
			CategoryTeaserGroup categoryGroup = (CategoryTeaserGroup) getItem(position);
			((TextView) convertView.findViewById(R.id.teaser_group_title))
					.setText(categoryGroup.getTitle());
            container.addView(createCategoryAllTeaserView( container));
			for (TeaserCategory category : categoryGroup.getTeasers()) {
				container
						.addView(createCategoryTeaserView(category, container));
			}
			break;
		case PRODUCT_LIST:
			ProductTeaserGroup productGroup = (ProductTeaserGroup) getItem(position);
			((TextView) convertView.findViewById(R.id.teaser_group_title))
					.setText(productGroup.getTitle());
			for (TeaserProduct product : productGroup.getTeasers()) {
				container.addView(createProductTeaserView(product, container));
			}
			break;
		}
		return convertView;
	}

    private View createImageTeaserView(TeaserImage teaserImage, ViewGroup vg) {
		View imageTeaserView = mInflater.inflate(R.layout.image_loadable, vg,
				false);
		setImageToLoad(teaserImage.getImageUrl(), imageTeaserView);
		attachTeaserListener(teaserImage, imageTeaserView);
		return imageTeaserView;
	}

	private View createProductTeaserView(TeaserProduct product, ViewGroup vg) {
		View productTeaserView = mInflater.inflate(R.layout.product_item_small,
				vg, false);
		if (product.getImages().size() > 0) {
			setImageToLoad(product.getImages().get(0).getUrl(),
					productTeaserView);
		}
		((TextView) productTeaserView.findViewById(R.id.item_title))
				.setText(product.getName());
		String price;
		if ( !TextUtils.isEmpty(product.getSpecialPrice())) {
		    price = product.getSpecialPrice();
		} else {
		    price = product.getPrice();
		}
		((TextView) productTeaserView.findViewById(R.id.item_price))
				.setText(price);
		attachTeaserListener(product, productTeaserView);
		return productTeaserView;
	}

    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        final ImageView imageView = (ImageView) imageTeaserView
                .findViewById(R.id.image_view);
        final View progressBar = imageTeaserView
                .findViewById(R.id.image_loading_progress);
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageLoader.getInstance().displayImage(imageUrl, imageView,
                    new ImageLoadingListener() {

                        /*
                         * (non-Javadoc)
                         * 
                         * @see
                         * com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener
                         * #onLoadingComplete(java.lang.String, android.view.View,
                         * android.graphics.Bitmap)
                         */
                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                            progressBar.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                    });
        }

    }

	private View createCategoryTeaserView(TeaserCategory cat, ViewGroup vg) {
		View categoryTeaserView;
		categoryTeaserView = mInflater.inflate( R.layout.category_inner_childcat, vg, false);
		TextView textView = (TextView) categoryTeaserView.findViewById(R.id.text);
		textView.setText(cat
				.getName());
		attachTeaserListener(cat, categoryTeaserView);
		return categoryTeaserView;
	}

    private View createCategoryAllTeaserView(ViewGroup container) {
        View view = mInflater.inflate(
                    R.layout.category_inner_currentcat, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(context.getString( R.string.categories_toplevel_title));
        view.setOnClickListener(onTeaserClickListener);
        view.setTag(R.id.target_url, null);
        view.setTag(R.id.target_type, ITargeting.TargetType.CATEGORY);
        return view;
    }

	private void attachTeaserListener(ITargeting targeting, View view) {
		view.setTag(R.id.target_url, targeting.getTargetUrl());
		view.setTag(R.id.target_type, targeting.getTargetType());
		view.setTag(R.id.target_title, targeting.getTargetTitle());
		view.setOnClickListener(onTeaserClickListener);
	}
	
	@SuppressLint("NewApi")
	public void myAddAll(Collection<? extends TeaserSpecification<?>> collection) {
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.addAll(collection);
			return;
		}
		setNotifyOnChange(false);
		for( TeaserSpecification<?> item: collection) {
			add( item );
		}
		setNotifyOnChange(true);
		notifyDataSetChanged();
	}

	private class ImagePagerAdapter extends PagerAdapter implements IPagerAdapter, IconPagerAdapter {

		private final List<TeaserImage> teaserImages;

		/**
		 * 
		 */
		public ImagePagerAdapter(List<TeaserImage> teaserImages) {
			this.teaserImages = teaserImages;
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
			View view = createImageTeaserView(teaserImage, container);
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
