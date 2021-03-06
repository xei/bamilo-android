package com.bamilo.android.appmodule.bamiloapp.utils.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bamilo.android.BuildConfig;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.utils.Toast;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.framework.components.customfontviews.CheckBox;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;

/**
 * A general Class with UI utils such as set the font <p/><br> <p> Copyright (C) 2012 Rocket
 * Internet - All Rights Reserved <p/> <p> Unauthorized copying of this file, via any medium is
 * strictly prohibited <br> Proprietary and confidential.
 *
 * @author Manuel Silva
 * @modified Andre Lopes
 */
public class UIUtils {

    public static final String TAG = UIUtils.class.getSimpleName();

    public static int dpToPx(int dp, float density) {
        return Math.round((float) dp * density);
    }

    public static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(dp * density);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    private int spToPx(Context context, Float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    public static int spToPx(float dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static Drawable createRoundDrawable(String colorHex, int round) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius((float) round);
        shape.setColor(Color.parseColor(colorHex));
        return shape;
    }

    /**
     * Show or hide a set of views.
     *
     * @param visibility The visibility parameter for all.
     * @param views The views that some can be null.
     * @author sergiopereira
     */
    public static void showOrHideViews(int visibility, View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(visibility);
            }
        }
    }

    /**
     * Set the visibility
     *
     * @param view The view that can be null
     * @param show The visibility parameter
     * @author sergiopereira
     */
    public static void setVisibility(View view, boolean show) {
        if (view != null) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Animate the view with a fade in, after an offset a fade out. TODO - Create this animation
     * using XML, http://developer.android.com/guide/topics/graphics/view-animation.html
     *
     * @param context The application context
     * @param animatedView The view
     * @param visibleOffset The visible offset
     */
    public static void animateFadeInAndOut(Context context, View animatedView, int visibleOffset) {
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
     *
     * @param view The animated view
     * @return true or false
     * @author sergio pereira
     */
    public static boolean isAnimating(View view) {
        return view != null && view.getAnimation() != null && (view.getAnimation().hasStarted()
                || !view.getAnimation().hasEnded());
    }

    public static void showViewFadeIn(@NonNull View view) {
        if (view.getVisibility() != View.VISIBLE) {
            Animation animation = AnimationUtils
                    .loadAnimation(view.getContext(), R.anim.abc_fade_in);
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
     *
     * @param animatedView -  the animated view
     */
    public static void animateSlideUp(@NonNull View animatedView) {
        animatedView.clearAnimation();
        Animation animation = AnimationUtils
                .loadAnimation(animatedView.getContext(), R.anim.slide_up);
        animatedView.startAnimation(animation);
        animatedView.setVisibility(View.GONE);
    }


    /**
     * Animate a view sliding from top to down
     *
     * @param animatedView -  the animated view
     */
    public static void animateSlideDown(@NonNull View animatedView) {
        animatedView.clearAnimation();
        animatedView.setVisibility(View.VISIBLE);
        Animation downAnimation = AnimationUtils
                .loadAnimation(animatedView.getContext(), R.anim.slide_down);
        animatedView.startAnimation(downAnimation);
    }

    /**
     * method responsible for scrolling a scrollview for 60dp This is used for editexts that show in
     * a layout inside a toolbar
     */
    public static void scrollToViewByClick(final View scrollView, final View viewToDetectTouch) {
        viewToDetectTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollBy(0, dpToPx(60, scrollView.getContext().getResources()
                                    .getDisplayMetrics().scaledDensity));
                        }
                    }, 500);
                }
                return false;
            }
        });
    }

    public static SpannableString setSpan(String first, String second, int firstColor,
            int secondColor) {
        SpannableString spannableString = new SpannableString(first + second);
        spannableString.setSpan(new ForegroundColorSpan(firstColor), 0, first.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(secondColor), first.length(),
                first.length() + second.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
     */
    public static boolean drawableClicked(TextView view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if ((!ShopSelector.isRtl() && event.getX() >= view.getRight() - view
                    .getTotalPaddingRight())
                    || (ShopSelector.isRtl() && event.getX() <= view.getTotalPaddingLeft())) {
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
        int drawable = context.getResources()
                .getIdentifier(name, "drawable", context.getPackageName());
        setDrawableLeft(view, drawable);
    }

    /**
     * Fixes the checkbox state for Marshmallow
     */
    public static void checkBoxDrawableStateCompat(final CheckBox checkBox) {
        checkBox.setButtonDrawable(R.drawable._gen_selector_check_box);
        if (ShopSelector.isRtl()) {
            checkBox.setPadding(IntConstants.DEFAULT_POSITION, IntConstants.DEFAULT_POSITION,
                    IntConstants.PADDING_10, IntConstants.DEFAULT_POSITION);
        } else {
            checkBox.setPadding(IntConstants.PADDING_10, IntConstants.DEFAULT_POSITION,
                    IntConstants.DEFAULT_POSITION, IntConstants.DEFAULT_POSITION);
        }

        checkBox.setCompoundDrawables(null, null, null, null);
    }

    /**
     * Process the click on call to sendPurchaseRecommend
     */
    public static void onClickCallToOrder(@NonNull Activity activity) {
        // Tracking
        TrackerDelegator.trackCall(activity);
        // Get phone number
        SharedPreferences sharedPrefs = activity
                .getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // TODO: 9/26/2017 Change expiration policies
        String mPhone2Call = activity.getString(
                R.string.call_center_number); /*sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, null);*/ //In case phone # not fetched properly
        // Make a call
        if (mPhone2Call != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(mPhone2Call));
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
            }
        }
    }

    public static void onClickEmailToCS(@NonNull Activity activity) {
        // Tracking
        TrackerDelegator.trackCall(activity);

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        String appVersion = BuildConfig.VERSION_NAME;
        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        String deviceName = Build.MODEL;
        String deviceBrand = Build.BRAND;
        String description = "لطفا برای پیگیری بهتر، اطلاعات مندرج در انتهای ایمیل را پاک نکنید";

        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"application@bamilo.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "گزارش مشکل در برنامه");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\n\n\n "
                + description + "\n"
                + "android version : " + sdkVersion + "\n"
                + "application version : " + appVersion + "\n"
                + "device name : " + deviceBrand + "-" + deviceName

        );

        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } else {
            Toast.makeText(activity, "لطفا ایمیل را نصب کنید", Toast.LENGTH_LONG).show();
        }

    }

    public static String networkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return "unknown";
        }
        if (activeNetwork.getTypeName().equalsIgnoreCase("WIFI")) {
            return "wifi";
        } else if (activeNetwork.getTypeName().equalsIgnoreCase("MOBILE")) {
            return "cellular";
        }
        return "unknown";
    }

    public static void emailToCS(@NonNull Activity activity) {

        String appVersion = BuildConfig.VERSION_NAME;
        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        String deviceName = Build.MODEL;
        String deviceBrand = Build.BRAND;
        String description = "لطفا برای پیگیری بهتر، اطلاعات مندرج در انتهای ایمیل را پاک نکنید";

        String body = "\n\n\n\n\n\n\n "
                + description + "\n"
                + "android version : " + sdkVersion + "\n"
                + "application version : " + appVersion + "\n"
                + "device name : " + deviceBrand + "-" + deviceName;

        sendEmail(activity, new String[]{"support@bamilo.com"}, "", body);
    }

    public static void emailBugs(@NonNull Activity activity) {

        String appVersion = BuildConfig.VERSION_NAME; // e.g. myVersion := "1.6"
        String sdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        String deviceName = Build.MODEL;
        String deviceBrand = Build.BRAND;
        String description = "لطفا برای پیگیری بهتر، اطلاعات مندرج در انتهای ایمیل را پاک نکنید";

        String body = "\n\n\n\n\n\n\n "
                + description + "\n"
                + "android version : " + sdkVersion + "\n"
                + "application version : " + appVersion + "\n"
                + "device name : " + deviceBrand + "-" + deviceName;

        sendEmail(activity, new String[]{"application@bamilo.com"},
                "ارسال ایده\u200Cها و مشکلات برنامه", body);
    }

    private static void sendEmail(@NonNull Activity activity, String[] address, String subject,
            String body) {
        final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: " + address[0]));
//        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } else {
            Toast.makeText(activity, "لطفا ایمیل را نصب کنید", Toast.LENGTH_LONG).show();
        }
    }

    public static void rateApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context
                            .getPackageName())));
        }
    }

    public static void shareApp(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_link_title) + "\n\n" + context.getResources().getString(R.string.share_link));
        String url = context.getResources().getString(R.string.share_link);
        if (BamiloApplication.isCustomerLoggedIn()
                && BamiloApplication.CUSTOMER != null
                && BamiloApplication.CUSTOMER.getId() > 0) {
            url += "?label=" + String.valueOf(BamiloApplication.CUSTOMER.getId());
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_link_title) + "\n\n" + url);
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_app)));

        EventTracker.INSTANCE.inviteFriends();
    }
}