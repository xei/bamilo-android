/**
 * 
 */
package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.Collection;

import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup.TeaserCategory;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.objects.ImageTeaserGroup;
import pt.rocket.framework.objects.ImageTeaserGroup.TeaserImage;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup.TeaserProduct;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.objects.TeaserSpecification.TeaserGroupType;
import pt.rocket.view.R;
import pt.rocket.view.fragments.CategoryTeaserFragment;
import pt.rocket.view.fragments.MainOneSlideFragment;
import pt.rocket.view.fragments.ProducTeaserListFragment;
import pt.rocket.view.fragments.StaticBannerFragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import org.holoeverywhere.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * @author manuelsilva
 * 
 */
public class TeaserGroupFragmentAdapter extends ArrayAdapter<TeaserSpecification<?>> {

    private final LayoutInflater mInflater;
    private final Context context;
    private final OnClickListener onTeaserClickListener;
    private FragmentManager fragmentManager;
    private Fragment fragmentMainOneSlide;
    private Fragment fragmentStaticBanner;
    private Fragment fragmentCategoryTeaser;
    private Fragment fragmentProductListTeaser;
    /**
     * @param context
     * @param fragmentManager
     * @param textViewResourceId
     */
    public TeaserGroupFragmentAdapter(Context context,
            OnClickListener onTeaserClickListener, FragmentManager fragmentManager) {
        super(context, 0);
        this.context = context;
        this.onTeaserClickListener = onTeaserClickListener;
        this.fragmentManager = fragmentManager;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*
     * (non-Javadoc)
     * 
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
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            switch (getItem(position).getType()) {
            case MAIN_ONE_SLIDE:
                convertView = mInflater.inflate(
                        R.layout.main_one_fragment_frame, parent, false);
                break;
            case STATIC_BANNER:
                convertView = mInflater.inflate(R.layout.static_teaser_frame,
                        parent, false);
                break;
            case CATEGORIES:
                convertView = mInflater.inflate(
                        R.layout.generic_frame, parent, false);
                break;
            case PRODUCT_LIST:
                convertView = mInflater.inflate(R.layout.product_list_frame,
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
//            if (fragmentMainOneSlide == null) {
//                fragmentMainOneSlide = MainOneSlideFragment.newInstance(
//                        ((ImageTeaserGroup) getItem(position)).getTeasers(), onTeaserClickListener);
//            }
//            switchFragment(fragmentMainOneSlide, R.id.main_one_frame);
            break;
        case STATIC_BANNER:
//            if (fragmentStaticBanner == null) {
//                fragmentStaticBanner = StaticBannerFragment.newInstance(
//                        ((ImageTeaserGroup) getItem(position)).getTeasers(), onTeaserClickListener);
//            }
//            switchFragment(fragmentStaticBanner, R.id.static_teaser_frame);
            break;
        case CATEGORIES:
//            if(fragmentCategoryTeaser == null){
//                fragmentCategoryTeaser = CategoryTeaserFragment.newInstance((CategoryTeaserGroup) getItem(position), onTeaserClickListener);
//            }
//            switchFragment(fragmentCategoryTeaser, R.id.content_frame);
            break;
        case PRODUCT_LIST:
//            if(fragmentProductListTeaser == null){
//                fragmentProductListTeaser = ProducTeaserListFragment.newInstance((ProductTeaserGroup) getItem(position), onTeaserClickListener);
//            }
//            switchFragment(fragmentProductListTeaser, R.id.products_list_frame);
            break;
        }

       

        return convertView;
    }

    private void switchFragment(Fragment fragment, int id){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
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

    private void setImageToLoad(String imageUrl, View imageTeaserView) {
        final ImageView imageView = (ImageView) imageTeaserView
                .findViewById(R.id.image_view);
        final View progressBar = imageTeaserView
                .findViewById(R.id.image_loading_progress);
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
                            imageView.setVisibility(View.VISIBLE);
                        }
                    });
        }

    }

    private View createCategoryTeaserView(TeaserCategory cat, ViewGroup vg) {
        View categoryTeaserView;
        categoryTeaserView = mInflater.inflate(R.layout.category_inner_childcat, vg, false);
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
        textView.setText(context.getString(R.string.categories_toplevel_title));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
            return;
        }
        setNotifyOnChange(false);
        for (TeaserSpecification<?> item : collection) {
            add(item);
        }
        setNotifyOnChange(true);
        notifyDataSetChanged();
    }

}
