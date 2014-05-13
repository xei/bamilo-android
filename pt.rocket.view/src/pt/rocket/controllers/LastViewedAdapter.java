package pt.rocket.controllers;

import java.util.ArrayList;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.LastViewed;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import de.akquinet.android.androlog.Log;

public class LastViewedAdapter extends PagerAdapter {
    private static final String TAG = LastViewedAdapter.class.getName();
    Context mContext;
    ArrayList<LastViewed> mLastViewedList;
    LayoutInflater mLayoutInflater;
    private int partialSize = 3;

    public LastViewedAdapter(Context ctx, ArrayList<LastViewed> lastViewed,
            LayoutInflater layoutInflater, int size) {
        this.mContext = ctx;
        this.mLastViewedList = lastViewed;
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
        int lastViewedListSize = this.mLastViewedList.size();
        Log.d(TAG, "featureListSize: " + lastViewedListSize);
        int pageIndex = lastViewedListSize / this.partialSize;

        if (lastViewedListSize % this.partialSize == 0) {
            count = pageIndex;
        } else {
            count = pageIndex + 1;
        }
        Log.d(TAG, "count: " + count);
        return count;
    }

    private View generateElement(int position) {
        View view = mLayoutInflater.inflate(R.layout.element_last_viewed, null, false);

        return view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // Log.i(TAG, "code1last positions is : "+position);
        View view = mLayoutInflater.inflate(R.layout.element_last_viewed, container, false);
        if (position * this.partialSize < this.mLastViewedList.size()) {
            RelativeLayout mElement1 = (RelativeLayout) view.findViewById(R.id.element_1);
            mElement1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL,
                            mLastViewedList.get(position * partialSize).getProductUrl());
                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE,
                            R.string.gteaserrecentlyviewed_prefix);
                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                    ((BaseActivity) mContext).onSwitchFragment(
                            FragmentType.PRODUCT_DETAILS, bundle,
                            FragmentController.ADD_TO_BACK_STACK);
                }
            });
            ImageView img_1 = (ImageView) mElement1.findViewById(R.id.img_1);
            TextView name_1 = (TextView) mElement1.findViewById(R.id.name_1);
            TextView price_1 = (TextView) mElement1.findViewById(R.id.price_1);
            // Log.i(TAG, "code1last generating : "+mLastViewedList.get(position).getProductName());
            name_1.setText(mLastViewedList.get(position * this.partialSize).getProductName());
            price_1.setText(mLastViewedList.get(position * this.partialSize).getProductPrice());
            AQuery aq = new AQuery(mContext);

            aq.id(img_1).image(mLastViewedList.get(position * this.partialSize).getImageUrl(), true, true, 0, 0,
                    new BitmapAjaxCallback() {

                        @Override
                        public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            iv.setImageBitmap(bm);
                        }
                    });
        }
        if ((position * this.partialSize + 1) < this.mLastViewedList.size()) {
            RelativeLayout mElement2 = (RelativeLayout) view.findViewById(R.id.element_2);
            mElement2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL,
                            mLastViewedList.get(position * partialSize + 1).getProductUrl());
                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE,
                            R.string.gteaserprod_prefix);
                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                    ((BaseActivity) mContext).onSwitchFragment(
                            FragmentType.PRODUCT_DETAILS, bundle,
                            FragmentController.ADD_TO_BACK_STACK);

                }
            });
            ImageView img_2 = (ImageView) mElement2.findViewById(R.id.img_2);
            TextView name_2 = (TextView) mElement2.findViewById(R.id.name_2);
            TextView price_2 = (TextView) mElement2.findViewById(R.id.price_2);
            // Log.i(TAG, "code1last generating : "+mLastViewedList.get(position).getProductName());
            name_2.setText(mLastViewedList.get(position * this.partialSize + 1).getProductName());
            price_2.setText(mLastViewedList.get(position * this.partialSize + 1).getProductPrice());
            AQuery aq = new AQuery(mContext);

            aq.id(img_2).image(mLastViewedList.get(position * this.partialSize + 1).getImageUrl(), true, true, 0,
                    0, new BitmapAjaxCallback() {

                        @Override
                        public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            iv.setImageBitmap(bm);
                        }
                    });
        }
        if ((position * this.partialSize + 2) < this.mLastViewedList.size()) {
            RelativeLayout mElement3 = (RelativeLayout) view.findViewById(R.id.element_3);
            mElement3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL,
                            mLastViewedList.get(position * partialSize + 2).getProductUrl());
                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE,
                            R.string.gteaserprod_prefix);
                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                    ((BaseActivity) mContext).onSwitchFragment(
                            FragmentType.PRODUCT_DETAILS, bundle,
                            FragmentController.ADD_TO_BACK_STACK);

                }
            });
            ImageView img_3 = (ImageView) mElement3.findViewById(R.id.img_3);
            TextView name_3 = (TextView) mElement3.findViewById(R.id.name_3);
            TextView price_3 = (TextView) mElement3.findViewById(R.id.price_3);
            // Log.i(TAG, "code1last generating : "+mLastViewedList.get(position).getProductName());
            name_3.setText(mLastViewedList.get(position * this.partialSize + 2).getProductName());
            price_3.setText(mLastViewedList.get(position * this.partialSize + 2).getProductPrice());
            AQuery aq = new AQuery(mContext);

            aq.id(img_3).image(mLastViewedList.get(position * this.partialSize + 2).getImageUrl(), true, true, 0,
                    0, new BitmapAjaxCallback() {

                        @Override
                        public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                            iv.setImageBitmap(bm);
                        }
                    });
        }
        if (partialSize > 3) {
            if ((position * this.partialSize + 3) < this.mLastViewedList.size()) {
                RelativeLayout mElement4 = (RelativeLayout) view.findViewById(R.id.element_4);
                mElement4.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantsIntentExtra.CONTENT_URL,
                                mLastViewedList.get(position * partialSize + 3).getProductUrl());
                        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE,
                                R.string.gteaserprod_prefix);
                        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                        ((BaseActivity) mContext).onSwitchFragment(
                                FragmentType.PRODUCT_DETAILS, bundle,
                                FragmentController.ADD_TO_BACK_STACK);

                    }
                });
                ImageView img_4 = (ImageView) mElement4.findViewById(R.id.img_4);
                TextView name_4 = (TextView) mElement4.findViewById(R.id.name_4);
                TextView price_4 = (TextView) mElement4.findViewById(R.id.price_4);
                // Log.i(TAG,
                // "code1last generating : "+mLastViewedList.get(position).getProductName());
                name_4.setText(mLastViewedList.get(position * this.partialSize + 3).getProductName());
                price_4.setText(mLastViewedList.get(position * this.partialSize + 3).getProductPrice());
                AQuery aq = new AQuery(mContext);

                aq.id(img_4).image(mLastViewedList.get(position * this.partialSize + 3).getImageUrl(), true, true,
                        0, 0, new BitmapAjaxCallback() {

                            @Override
                            public void callback(String url, ImageView iv, Bitmap bm,
                                    AjaxStatus status) {
                                iv.setImageBitmap(bm);
                            }
                        });
            }
            if ((position * this.partialSize + 4) < this.mLastViewedList.size()) {
                RelativeLayout mElement5 = (RelativeLayout) view.findViewById(R.id.element_5);
                mElement5.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantsIntentExtra.CONTENT_URL,
                                mLastViewedList.get(position * partialSize + 4).getProductUrl());
                        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE,
                                R.string.gteaserprod_prefix);
                        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                        ((BaseActivity) mContext).onSwitchFragment(
                                FragmentType.PRODUCT_DETAILS, bundle,
                                FragmentController.ADD_TO_BACK_STACK);

                    }
                });
                ImageView img_5 = (ImageView) mElement5.findViewById(R.id.img_5);
                TextView name_5 = (TextView) mElement5.findViewById(R.id.name_5);
                TextView price_5 = (TextView) mElement5.findViewById(R.id.price_5);
                // Log.i(TAG,
                // "code1last generating : "+mLastViewedList.get(position).getProductName());
                name_5.setText(mLastViewedList.get(position * this.partialSize + 4).getProductName());
                price_5.setText(mLastViewedList.get(position * this.partialSize + 4).getProductPrice());
                AQuery aq = new AQuery(mContext);

                aq.id(img_5).image(mLastViewedList.get(position * this.partialSize + 4).getImageUrl(), true, true,
                        0, 0, new BitmapAjaxCallback() {

                            @Override
                            public void callback(String url, ImageView iv, Bitmap bm,
                                    AjaxStatus status) {
                                iv.setImageBitmap(bm);
                            }
                        });
            }

        }

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == (View) obj;
    }

}
