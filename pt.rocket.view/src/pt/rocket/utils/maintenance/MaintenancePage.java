package pt.rocket.utils.maintenance;

import org.holoeverywhere.widget.Button;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.utils.DeviceInfoHelper;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * Class used to set the content for maintenance page.
 * 
 * @author sergiopereira
 * 
 */
public class MaintenancePage {

    /**
     * Set map image for maintenance page.
     * 
     * @param activity
     * @param mapImageView
     */
    @SuppressWarnings("unused")
    @Deprecated
    private static void rearrageLayoutForImage(Activity activity, ImageView mapImageView){
     // ImageView for map is between title text and change country button
        int height = DeviceInfoHelper.getHeight(activity.getApplicationContext());
        RelativeLayout.LayoutParams params = (LayoutParams) mapImageView.getLayoutParams();
        if (height > 1000) {
            // Set map image above maintenance message for big devices
            params.addRule(RelativeLayout.ABOVE, R.id.fallback_options_container);
        } else if (height < 500) {
            // Set map image below MAINTANCE title
            params.addRule(RelativeLayout.BELOW, R.id.fallback_title_container);
        }
    }
    
    /**
     * Set maintenance page content in SplashScreen.
     * 
     * @param activity
     * @param listener
     * @author manuel
     * @modified sergiopereira
     */
    public static void setContentForSplash(Activity activity, OnClickListener listener) {

        try {

            // Get prefs
            SharedPreferences sharedPrefs = activity.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            // Set retry
            Button retry = (Button) activity.findViewById(R.id.fallback_retry);
            retry.setText(R.string.try_again);
            retry.setOnClickListener(listener);
            // Set choose country
            Button changeCountry = (Button) activity.findViewById(R.id.fallback_change_country);
            changeCountry.setVisibility(View.VISIBLE);
            changeCountry.setText(R.string.nav_country);
            changeCountry.setOnClickListener(listener);
            // Set image
            //ImageView mapBg = (ImageView) activity.findViewById(R.id.fallback_country_map);
            //RocketImageLoader.instance.loadImage(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_MAP_FLAG, ""), mapBg, null, R.drawable.img_maintenance_map);

            String country = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, "");
            TextView fallbackBest = (TextView) activity.findViewById(R.id.fallback_best);
            fallbackBest.setText(R.string.fallback_best);
            if (country.split(" ").length == 1) {
                TextView tView = (TextView) activity.findViewById(R.id.fallback_country);
                tView.setVisibility(View.VISIBLE);
                TextView txView = (TextView) activity.findViewById(R.id.fallback_options_bottom);
                txView.setVisibility(View.VISIBLE);
                txView.setText(country.toUpperCase());
                activity.findViewById(R.id.fallback_country_double).setVisibility(View.GONE);
                tView.setText(country.toUpperCase());
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
        }
    }

    /**
     * Set maintenance page content in BaseActivity.
     * 
     * @param activity
     * @param listener
     * @author manuel
     * @modified sergiopereira
     */
    public static void setContentForBase(Activity activity, OnClickListener listener) {

        try {

            // Get retry button
            Button retry = (Button) activity.findViewById(R.id.fallback_retry);
            retry.setText(R.string.try_again);
            retry.setOnClickListener(listener);

            SharedPreferences sharedPrefs = activity.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            //ImageView mapImageView = (ImageView) activity.findViewById(R.id.fallback_country_map);
            //RocketImageLoader.instance.loadImage(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_MAP_FLAG, ""), mapImageView, null, R.drawable.img_maintenance_map);
            
            String country = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, "");

            TextView fallbackBest = (TextView) activity.findViewById(R.id.fallback_best);
            fallbackBest.setText(R.string.fallback_best);
            if (country.split(" ").length == 1) {
                TextView tView = (TextView) activity.findViewById(R.id.fallback_country);
                tView.setVisibility(View.VISIBLE);
                tView.setText(country.toUpperCase());
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
        }

    }

}
