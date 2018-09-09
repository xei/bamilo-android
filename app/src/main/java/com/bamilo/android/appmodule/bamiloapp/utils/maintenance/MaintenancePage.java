package com.bamilo.android.appmodule.bamiloapp.utils.maintenance;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.bamilo.android.framework.components.customfontviews.Button;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.R;

/**
 * Class used to set the content for maintenance page.
 * 
 * @author sergiopereira
 * 
 */
public class MaintenancePage {

    /**
     * Set map image for maintenance page.
     */
    @SuppressWarnings("unused")
    @Deprecated
    private static void rearrageLayoutForImage(Activity activity, ImageView mapImageView){
     // ImageView for map is between title text and change country button
        int height = DeviceInfoHelper.getHeight(activity.getApplicationContext());
        LayoutParams params = (LayoutParams) mapImageView.getLayoutParams();
        if (height > 1000) {
            // Set map image above maintenance message for big devices
            params.addRule(RelativeLayout.ABOVE, R.id.fallback_options_container);
        } else if (height < 500) {
            // Set map image below MAINTANCE title
            params.addRule(RelativeLayout.BELOW, R.id.fallback_title_container);
        }
    }
    
    public static void setMaintenancePageWithChooseCountry(Activity activity, EventType eventType, OnClickListener listener) {
        setMaintenancePageWithChooseCountry(activity, eventType, listener, listener);
    }
    
    /**
     * Set maintenance page content in SplashScreen.
     * 
     * @author manuel
     * @modified sergiopereira
     */
    public static void setMaintenancePageWithChooseCountry(Activity activity, EventType eventType, OnClickListener onClickRetryButton, OnClickListener onClickChooseCountry) {

       /* try {

            // Get prefs
            SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            // Set retry
            Button retry = (Button) activity.findViewById(R.id.fragment_root_retry_maintenance);
            retry.setText(R.string.try_again);
            retry.setTag(eventType.toString());
            retry.setOnClickListener(onClickRetryButton);
            // Set choose country case multi shops
            if (!activity.getResources().getBoolean(R.bool.is_single_shop_country)) {
                Button changeCountry = (Button) activity.findViewById(R.id.fragment_root_cc_maintenance);
                changeCountry.setVisibility(View.VISIBLE);
                changeCountry.setText(R.string.nav_country);
                changeCountry.setOnClickListener(onClickChooseCountry);
            }

            // Set image
            //ImageView mapBg = (ImageView) activity.findViewById(R.id.fallback_country_map);
            //RocketImageLoader.instance.loadImage(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_MAP_FLAG, ""), mapBg, null, R.drawable.img_maintenance_map);
            
            // Get flag for single shop
            boolean isSingleShop = activity.getResources().getBoolean(R.bool.is_single_shop_country);

            String country = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, "");
            TextView fallbackBest = (TextView) activity.findViewById(R.id.fallback_best);
            fallbackBest.setText(R.string.fallback_best);
            if (country.split(" ").length == 1) {
                TextView tView = (TextView) activity.findViewById(R.id.fallback_country);
                tView.setVisibility(View.VISIBLE);
                tView.setText(isSingleShop ? "" : country.toUpperCase());
                TextView txView = (TextView) activity.findViewById(R.id.fallback_options_bottom);
                txView.setVisibility(View.VISIBLE);
                txView.setText(country.toUpperCase());
                activity.findViewById(R.id.fallback_country_double).setVisibility(View.GONE);
                
            } else {
                TextView tView = (TextView) activity.findViewById(R.id.fallback_country_top);
                tView.setText(country.split(" ")[0].toUpperCase());
                TextView tViewBottom = (TextView) activity.findViewById(R.id.fallback_country_bottom);
                tViewBottom.setText(country.split(" ")[1].toUpperCase());
                fallbackBest.setTextSize(11.88f);
                TextView txView = (TextView) activity.findViewById(R.id.fallback_options_bottom);
                txView.setVisibility(View.VISIBLE);
                txView.setText(country.toUpperCase());
                activity.findViewById(R.id.fallback_country_double).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.fallback_country).setVisibility(View.GONE);

            }

            TextView mTextViewBT = (TextView) activity.findViewById(R.id.fallback_country_bottom_text);
            mTextViewBT.setText(R.string.fallback_maintenance_text);

            TextView mTextViewBT2 = (TextView) activity.findViewById(R.id.fallback_country_bottom_text2);
            mTextViewBT2.setText(R.string.fallback_maintenance_text_bottom);

            TextView mFallbackChoice = (TextView) activity.findViewById(R.id.fallback_choice);
            mFallbackChoice.setText(R.string.fallback_choice);

            TextView mFallbackDoorstep = (TextView) activity.findViewById(R.id.fallback_doorstep);
            mFallbackDoorstep.setText(R.string.fallback_doorstep);

            fallbackBest.setSelected(true);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Set maintenance page content in BaseActivity.
     * @author manuel
     * @modified sergiopereira
     */
    public static void setMaintenancePageBaseActivity(Activity activity, OnClickListener listener) {
       /* try {
            // Get retry button
            Button retry = (Button) activity.findViewById(R.id.fragment_root_retry_maintenance);
            retry.setText(R.string.try_again);
            retry.setOnClickListener(listener);

            SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            
            // Get flag for single shop
            boolean isSingleShop = activity.getResources().getBoolean(R.bool.is_single_shop_country);
            
            String country = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, "");

            TextView fallbackBest = (TextView) activity.findViewById(R.id.fallback_best);
            fallbackBest.setText(R.string.fallback_best);
            if (country.split(" ").length == 1) {
                // Set the country name
                TextView tView = (TextView) activity.findViewById(R.id.fallback_country);
                tView.setVisibility(View.VISIBLE);
                tView.setText(isSingleShop ? "" : country.toUpperCase());
                TextView txView = (TextView) activity.findViewById(R.id.fallback_options_bottom);
                txView.setVisibility(View.VISIBLE);
                txView.setText(country.toUpperCase());
                activity.findViewById(R.id.fallback_country_double).setVisibility(View.GONE);
            } else {
                TextView tView = (TextView) activity.findViewById(R.id.fallback_country_top);
                tView.setText(country.split(" ")[0].toUpperCase());
                TextView tViewBottom = (TextView) activity.findViewById(R.id.fallback_country_bottom);
                tViewBottom.setText(country.split(" ")[1].toUpperCase());
                fallbackBest.setTextSize(11.88f);
                TextView txView = (TextView) activity.findViewById(R.id.fallback_options_bottom);
                txView.setVisibility(View.VISIBLE);
                txView.setText(country.toUpperCase());
                activity.findViewById(R.id.fallback_country_double).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.fallback_country).setVisibility(View.GONE);

            }

            TextView mTextViewBT = (TextView) activity.findViewById(R.id.fallback_country_bottom_text);
            mTextViewBT.setText(R.string.fallback_maintenance_text);

            TextView mTextViewBT2 = (TextView) activity.findViewById(R.id.fallback_country_bottom_text2);
            mTextViewBT2.setText(R.string.fallback_maintenance_text_bottom);

            TextView mFallbackChoice = (TextView) activity.findViewById(R.id.fallback_choice);
            mFallbackChoice.setText(R.string.fallback_choice);

            TextView mFallbackDoorstep = (TextView) activity.findViewById(R.id.fallback_doorstep);
            mFallbackDoorstep.setText(R.string.fallback_doorstep);

            fallbackBest.setSelected(true);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Set the maintenance page for Bamilo.
     * @author sergiopereira
     */
    public static void setMaintenancePageBamilo(View view, OnClickListener listener) {
        try {
            // Get retry button
            Button retry = (Button) view.findViewById(R.id.fragment_root_retry_maintenance);
            retry.setOnClickListener(listener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
}