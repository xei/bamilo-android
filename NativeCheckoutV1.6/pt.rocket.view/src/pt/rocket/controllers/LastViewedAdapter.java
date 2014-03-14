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
    public LastViewedAdapter(Context ctx, ArrayList<LastViewed> lastViewed , LayoutInflater layoutInflater) {
        this.mContext = ctx;
        this.mLastViewedList = lastViewed;
        this.mLayoutInflater = layoutInflater;
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
        if (mLastViewedList.size() == 0){
            count = 0;
        } else if(mLastViewedList.size()/3 == 0 || (mLastViewedList.size()/3 == 1 && mLastViewedList.size()%3 == 0 )){
            count = 1;
        } else if(mLastViewedList.size()/3 == 1 || (mLastViewedList.size()/3 == 2 && mLastViewedList.size()%3 == 0 )){
            count = 2;
        } else if(mLastViewedList.size()/3 >= 2){
            count = 3;
        }
        return count;
    }

    private View generateElement(int position){
        View view = mLayoutInflater.inflate(R.layout.element_last_viewed, null, false);
        
        return view;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
//        Log.i(TAG, "code1last positions is : "+position);
        View view = mLayoutInflater.inflate(R.layout.element_last_viewed, container, false);
        if(position*3<this.mLastViewedList.size()){
            RelativeLayout mElement1 = (RelativeLayout) view.findViewById(R.id.element_1);
            mElement1.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, mLastViewedList.get(position*3).getProductUrl());
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
//            Log.i(TAG, "code1last generating : "+mLastViewedList.get(position).getProductName());
            name_1.setText(mLastViewedList.get(position*3).getProductName());
            price_1.setText(mLastViewedList.get(position*3).getProductPrice());
            AQuery aq = new AQuery(mContext);
            
            aq.id(img_1).image(mLastViewedList.get(position*3).getImageUrl(), true, true, 0, 0, new BitmapAjaxCallback() {

                @Override
                public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    iv.setImageBitmap(bm);
                }
            });
        }
        if((position*3+1)<this.mLastViewedList.size()){
            RelativeLayout mElement2 = (RelativeLayout) view.findViewById(R.id.element_2);
            mElement2.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, mLastViewedList.get(position*3+1).getProductUrl());
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
//            Log.i(TAG, "code1last generating : "+mLastViewedList.get(position).getProductName());
            name_2.setText(mLastViewedList.get(position*3+1).getProductName());
            price_2.setText(mLastViewedList.get(position*3+1).getProductPrice());
            AQuery aq = new AQuery(mContext);
            
            aq.id(img_2).image(mLastViewedList.get(position*3+1).getImageUrl(), true, true, 0, 0, new BitmapAjaxCallback() {

                @Override
                public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    iv.setImageBitmap(bm);
                }
            });
        }
        if((position*3+2)<this.mLastViewedList.size()){
            RelativeLayout mElement3 = (RelativeLayout) view.findViewById(R.id.element_3);
            mElement3.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, mLastViewedList.get(position*3+2).getProductUrl());
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
//            Log.i(TAG, "code1last generating : "+mLastViewedList.get(position).getProductName());
            name_3.setText(mLastViewedList.get(position*3+2).getProductName());
            price_3.setText(mLastViewedList.get(position*3+2).getProductPrice());
            AQuery aq = new AQuery(mContext);
            
            aq.id(img_3).image(mLastViewedList.get(position*3+2).getImageUrl(), true, true, 0, 0, new BitmapAjaxCallback() {

                @Override
                public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    iv.setImageBitmap(bm);
                }
            });
        }
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == (View) obj;
    }

}
