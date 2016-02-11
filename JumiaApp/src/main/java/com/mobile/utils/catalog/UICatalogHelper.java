package com.mobile.utils.catalog;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * Created by spereira on 3/2/15.
 */
public class UICatalogHelper {

    private static final String TAG = UICatalogHelper.class.getSimpleName();


    /**
     * Set the catalog title
     */
    public static void setCatalogTitle(BaseActivity baseActivity, String name) {
        baseActivity.setActionBarTitle(name);
    }


    /**
     * Set the filter button state, to show as selected or not.
     */
    public static void setFilterButtonState(View button, boolean hasFilterValues) {
        try {
            button.setSelected(hasFilterValues);
            button.setEnabled(hasFilterValues);
            Log.d(TAG, "SET FILTER BUTTON STATE: " + button.isSelected());
        } catch (NullPointerException e) {
            Log.w(TAG, "BUTTON OR VALUE IS NULL", e);
        }
    }

    /**
     * Set button state when catalog show no internet connection error.
     */
    public static void setFilterButtonActionState(View button, boolean selectable, View.OnClickListener listener){
        if (button != null) {
            if (!selectable) {
                button.setOnClickListener(null);
                button.setEnabled(false);
            } else {
                button.setOnClickListener(listener);
                button.setEnabled(true);
            }
        }
    }

    /**
     * Show the goto top button
     * @param context - the application context
     * @param button - the button
     */
    public static void showGotoTopButton(Context context, View button) {
        if(button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom);
            button.startAnimation(animation);
        }
    }

    /**
     * Hide the goto top button.
     * @param context - the application context
     * @param button - the button
     */
    public static void hideGotoTopButton(Context context, View button) {
        if(button.getVisibility() != View.INVISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_bottom);
            button.startAnimation(animation);
            button.setVisibility(View.INVISIBLE);
        }
    }


//    /**
//     * Show tips if is the first time the user uses the app.
//     */
//    public static void isToShowWizard(BaseFragment fragment, ViewStub stub, View.OnClickListener listener) {
//        try {
//            if (WizardPreferences.isFirstTime(fragment.getActivity(), WizardPreferences.WizardType.CATALOG)) {
//                Log.i(TAG, "SHOW WIZARD");
//                // Inflate view in stub
//                stub.setVisibility(View.VISIBLE);
//                // Get view
//                View view = fragment.getView();
//                // Get view and set wizard
//                ViewPager viewPagerTips = (ViewPager) view.findViewById(R.id.catalog_wizard_viewpager);
//                int[] tipsPages = { R.layout.catalog_fragment_wizard_favourite};
//                TipsPagerAdapter mTipsPagerAdapter = new TipsPagerAdapter(fragment.getActivity(), fragment.getActivity().getLayoutInflater(), view, tipsPages);
//                viewPagerTips.setAdapter(mTipsPagerAdapter);
//                viewPagerTips.setOnPageChangeListener(new TipsOnPageChangeListener(view, tipsPages));
//                viewPagerTips.setCurrentItem(0);
//                view.findViewById(R.id.catalog_wizard_button_ok).setOnClickListener(listener);
//            }
//        } catch (NullPointerException e) {
//            Log.w(TAG, "WARNING: NPE ON SHOW WIZARD" , e);
//            stub.setVisibility(View.GONE);
//        }
//    }
}
