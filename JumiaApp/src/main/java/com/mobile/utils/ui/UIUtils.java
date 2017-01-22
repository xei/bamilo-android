package com.mobile.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

/**
 * A general Class with UI utils such as set the font <p/><br>
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 *
 * @author Manuel Silva
 * @modified Andre Lopes
 *
 */
@SuppressWarnings("unused")
public class UIUtils {

    public static final String TAG = UIUtils.class.getSimpleName();

    public static int dpToPx(int dp, float density) {
        return Math.round((float)dp * density);
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static int spToPx(float dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().scaledDensity );
    }

    /**
     * Show or hide a set of views.
     * @param visibility The visibility parameter for all.
     * @param views The views that some can be null.
     * @author sergiopereira
     */
    public static void showOrHideViews(int visibility, View... views) {
        for (View view : views) if (view != null) view.setVisibility(visibility);
    }

    /**
     * Set the visibility
     * @param view The view that can be null
     * @param show The visibility parameter
     * @author sergiopereira
     */
    public static void setVisibility(View view, boolean show) {
        if (view != null) view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Animate the view with a fade in, after an offset a fade out.
     * TODO - Create this animation using XML, http://developer.android.com/guide/topics/graphics/view-animation.html
     * @param context The application context
     * @param animatedView The view
     * @param visibleOffset The visible offset
     */
    public static void animateFadeInAndOut(Context context, View animatedView, int visibleOffset){
//        if (!isAnimating(animatedView)) {
        // Set view as invisible for old Android versions
        animatedView.setVisibility(View.INVISIBLE);
        // Create the fade in and fade out animation
        Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        fadeOut.setStartOffset(fadeIn.getDuration() + visibleOffset);
        // Create a set with animations
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        // Remove the old and start the new animation
        animatedView.clearAnimation();
        animatedView.startAnimation(animation);
//        }
    }

    /**
     * Validate if the current view is being animated
     * @param view The animated view
     * @return true or false
     * @author sergio pereira
     */
    public static boolean isAnimating(View view) {
        return view != null && view.getAnimation() != null && (view.getAnimation().hasStarted() || !view.getAnimation().hasEnded());
    }

    public static void showViewFadeIn(@NonNull View view) {
        if (view.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.abc_fade_in);
            view.startAnimation(animation);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViewFadeOut(@NonNull final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(animation);
        }
    }

    public static void setDrawableByString(@NonNull ImageView imageView, String name) {
        Context context = imageView.getContext();
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        imageView.setImageResource(id);
    }

    /**
     * Animate a view sliding from down to top
     * @param animatedView -  the animated view
     * */
    public static void animateSlideUp(@NonNull View animatedView) {
        animatedView.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.slide_up);
        animatedView.startAnimation(animation);
        animatedView.setVisibility(View.GONE);
    }


    /**
     * Animate a view sliding from top to down
     * @param animatedView -  the animated view
     * */
    public static void animateSlideDown(@NonNull View animatedView) {
        animatedView.clearAnimation();
        animatedView.setVisibility(View.VISIBLE);
        Animation downAnimation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.slide_down);
        animatedView.startAnimation(downAnimation);
    }

    /**
     * method responsible for scrolling a scrollview for 60dp
     * This is used for editexts that show in a layout inside a toolbar
     */
    public static void scrollToViewByClick(final View scrollView, final View viewToDetectTouch){
        viewToDetectTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollBy(0, dpToPx(60, scrollView.getContext().getResources().getDisplayMetrics().scaledDensity));
                        }
                    }, 500);
                }
                return false;
            }
        });
    }

    public static SpannableString setSpan(String first, String second, int firstColor, int secondColor){
        SpannableString spannableString = new SpannableString(first + second);
        spannableString.setSpan(new ForegroundColorSpan(firstColor), 0, first.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(secondColor), first.length(), first.length() + second.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * Mirror rating stars case RTL and pre API 17.
     */
    public static void setProgressForRTLPreJellyMr2(View progressBar) {
        if (DeviceInfoHelper.isPreJellyBeanMR2()) {
            mirrorViewForRTL(progressBar);
        }
    }

    /**
     * Mirror view case RTL.
     */
    public static void mirrorViewForRTL(@NonNull View view) {
        if (ShopSelector.isRtl()) {
            view.setScaleX(-1.0f);
            view.setScaleY(1.0f);
        }
    }

    /**
     * Method to detect if a drawable existing at left or right in a TextView was clicked
     * */
    public static boolean drawableClicked(TextView view, MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if((!ShopSelector.isRtl() &&  event.getX() >= view.getRight() - view.getTotalPaddingRight())
                    || (ShopSelector.isRtl() && event.getX() <= view.getTotalPaddingLeft()) ){
                return true;
            }
        }
        return false;
    }

    /**
     * Method used to set a right compound drawable in the respective view. (RTL support)
     */
    public static void setDrawableRight(@NonNull TextView view, @DrawableRes int drawable) {
        if (ShopSelector.isRtl()) {
            view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
        }
    }

    /**
     * Method used to set a right compound drawable in the respective view. (RTL support)
     */
    public static void setDrawableLeft(@NonNull TextView view, @DrawableRes int drawable) {
        if (ShopSelector.isRtl()) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
        }
    }

    public static void setDrawableLeftByString(@NonNull TextView view, String name) {
        Context context = view.getContext();
        int drawable = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        setDrawableLeft(view, drawable);
    }

    /**
     * Fixes the checkbox state for Marshmallow
     * @param checkBox
     */
    public static void checkBoxDrawableStateCompat(final CheckBox checkBox){
        checkBox.setButtonDrawable(R.drawable._gen_selector_check_box);
        if(ShopSelector.isRtl()){
            checkBox.setPadding(IntConstants.DEFAULT_POSITION, IntConstants.DEFAULT_POSITION, IntConstants.PADDING_10, IntConstants.DEFAULT_POSITION);
        } else {
            checkBox.setPadding(IntConstants.PADDING_10, IntConstants.DEFAULT_POSITION, IntConstants.DEFAULT_POSITION, IntConstants.DEFAULT_POSITION);
        }

        checkBox.setCompoundDrawables(null, null, null, null);
    }

    /**
     * Process the click on call to buy
     */
    public static void onClickCallToOrder(@NonNull Activity activity) {
        // Tracking
        TrackerDelegator.trackCall(activity);
        // Get phone number
        SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, "");
        // Make a call
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhone2Call));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    public static void onClickEmailToCS(@NonNull Activity activity) {
        // Tracking
        TrackerDelegator.trackCall(activity);
        // Get phone number
        SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String mAddress2Email = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_CS_EMAIL, "");
        // Make a call

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);


        String appVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
        String sdkVersion = String.valueOf(android.os.Build.VERSION.SDK_INT);
        String deviceName = android.os.Build.MODEL;
        String deviceBrand = Build.BRAND;
        String description = "لطفا برای پیگیری بهتر اطلاعات مندرج در انتهای ایمیل را پاک نکنید";


        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"application@bamilo.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "گزارش مشکل در برنامه");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,  "\n\n\n\n\n\n\n "
                + description+"\n"
                + "android version : " + appVersion+"\n"
                + "application version : " + sdkVersion+"\n"
                + "device name : " + deviceBrand +"-"+deviceName

        );



        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
        else {
            Toast.makeText(activity,"لطفا ایمیل را نصب کنید",Toast.LENGTH_LONG).show();

        }

    }
}