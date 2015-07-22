/**
 *
 */
package com.mobile.controllers;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.mobile.components.viewpagerindicator.IconPagerAdapter;
import com.mobile.view.R;

/**
 * @author Andre Lopes
 */
public class TipsPagerAdapter extends PagerAdapter implements IconPagerAdapter {

    private View mView;

    private int[] mTipsPages;

    private LayoutInflater mLayoutInflater;

    public TipsPagerAdapter(Context context, LayoutInflater layoutInflater, View view, int[] tipsPages/*, boolean addVariationsPadding*/) {
        mLayoutInflater = layoutInflater;
        mView = view;
        mTipsPages = tipsPages;

        if (context != null) {
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
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    if (i != mTipsPagesLength) {
                        layoutParams.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.margin_large);
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
        return view == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mTipsPages.length == 0) {
            return null;
        }
        if (position != 0 && container.getChildCount() <= 0) {
            View view = mLayoutInflater.inflate(mTipsPages[0], container, false);
            container.addView(view, 0);
        }
        View view = mLayoutInflater.inflate(mTipsPages[position], container, false);
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mView.findViewById(mTipsPages[position]));
    }

    @Override
    public int getIconResId(int index) {
        return R.drawable.bullit_showing;
    }
}
