/**
 * 
 */
package com.mobile.controllers;

import com.mobile.view.R;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.viewpagerindicator.IconPagerAdapter;

/**
 * @author Andre Lopes
 * 
 */
public class TipsPagerAdapter extends PagerAdapter implements IconPagerAdapter {

    private View mView;

    private int[] mTipsPages;

    private int mTopMargin = 0;
    
    private int mPadding = 0;

    // private boolean mAddVariationsPadding = false;

    private LayoutInflater mLayoutInflater;

    public TipsPagerAdapter(Context context, LayoutInflater layoutInflater, View view, int[] tipsPages/*, boolean addVariationsPadding*/) {
        mLayoutInflater = layoutInflater;
        mView = view;
        mTipsPages = tipsPages;

        if (context != null) {
            Resources resources = context.getResources();
            mTopMargin = resources.getDimensionPixelSize(R.dimen.product_tip_variations_top_margin);
            mPadding = resources.getDimensionPixelSize(R.dimen.product_tip_padding);

            // Generate dynamically the indicators for each tip page
            LinearLayout indicatorsContainer = (LinearLayout) view.findViewById(R.id.indicators_container);
            if (indicatorsContainer != null) {
                indicatorsContainer.removeAllViews();
                int mTipsPagesLength = mTipsPages.length;
                for (int i = 1; i <= mTipsPagesLength; i++) {
                    ImageView imageView = new ImageView(context);
                    imageView.setTag("indicator" + i);
                    int idImageResource = R.drawable.bullit;
                    // change first item to bullit selected
                    if (i == 1) {
                        idImageResource = R.drawable.bullit_showing;
                    }
                    imageView.setImageResource(idImageResource);

                    // add right padding on all items except last
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                    if (i != mTipsPagesLength) {
                        layoutParams.rightMargin = resources.getDimensionPixelSize(R.dimen.margin_large);
                    }
                    imageView.setLayoutParams(layoutParams);
                    indicatorsContainer.addView(imageView);
                }
            }
        }
        /*mAddVariationsPadding = addVariationsPadding;*/
    }

    @Override
    public int getCount() {
        return mTipsPages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object arg1) {
        return view == ((View) arg1);
    }

    /**
     * Change addVariationsPadding to define if tips will have extra height regarding the variants
     * View
     * 
     * @param addVariationsPadding
     */
    /*-public void setAddVariationsPadding(boolean addVariationsPadding) {
        this.mAddVariationsPadding = addVariationsPadding;
    }*/

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mTipsPages.length == 0) {
            return null;
        }

        if (position != 0 && container.getChildCount() <= 0) {
            View view = mLayoutInflater.inflate(mTipsPages[0], container, false);
            ((ViewPager) container).addView(view, 0);
        }

        View view = mLayoutInflater.inflate(mTipsPages[position], container, false);
        ((ViewPager) container).addView(view, 0);

//        if (mAddVariationsPadding) {
            ImageView tipImg = (ImageView) view.findViewById(R.id.tip_img);
            if (tipImg != null && tipImg.isShown()) {
                RelativeLayout.LayoutParams tipImgLayoutParams = (LayoutParams) tipImg.getLayoutParams();
                tipImgLayoutParams.topMargin = mTopMargin;
            }
            // set spacer height,  if exists, to mPadding
            View tipSpacer = view.findViewById(R.id.tip_spacer);
            if (tipSpacer != null) {
                RelativeLayout.LayoutParams tipSpacerLayoutParams = (LayoutParams) tipSpacer.getLayoutParams();
                tipSpacerLayoutParams.height = mPadding;

            }
//        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(mView.findViewById(mTipsPages[position]));
    }

    @Override
    public int getIconResId(int index) {
        return R.drawable.bullit_showing;
    }
}
