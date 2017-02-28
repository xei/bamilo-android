package com.mobile.view.newfragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.UITabLayoutUtils;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;
import com.mobile.view.fragments.CheckoutSummaryFragment;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arash on 1/23/2017.
 */

public abstract class NewBaseFragment extends BaseFragment {

    protected static final String TAG = NewBaseFragment.class.getSimpleName();
    protected long mLoadTime = 0; // For tacking
    private BaseActivity mainActivity;
    private ViewStub mLoadingView;
    private View mErrorView;
    private ErrorLayoutFactory mErrorLayoutFactory;

    public NewBaseFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState);
    }

    public NewBaseFragment(Set<MyMenuItem> enabledMenuItems, @NavigationAction.Type int action, @LayoutRes int layoutResId, @StringRes int titleResId, @KeyboardState int adjustState, @ConstantsCheckout.CheckoutType int titleCheckout) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjustState, titleCheckout);
    }

    public NewBaseFragment(Boolean isNestedFragment, @LayoutRes int layoutResId) {
        super(isNestedFragment, layoutResId);
    }


    public BaseActivity getBaseActivity() {
        if (mainActivity == null) {
            mainActivity = (BaseActivity) getActivity();
        }
        return mainActivity;
    }

    /*
     * ############# LIFE CYCLE #############
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
       *//* // Get current time
        mLoadTime = System.currentTimeMillis();
       // if (hasLayoutToInflate()) {
            //Print.i(TAG, "ON CREATE VIEW: HAS LAYOUT TO INFLATE");
            View view = inflater.inflate(R.layout.fragment_root_layout, container, false);

            return view;
        *//**//*} else {
            Print.i(TAG, "ON CREATE VIEW: HAS NO LAYOUT TO INFLATE");
            return super.onCreateView(inflater, container, savedInstanceState);
        }*//*
    }*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

    }


    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Get addresses
        /*if(mAddresses == null || onlyHasTitleChild(mShippingView)) {
            triggerGetForm();
        } else {
            showFragmentContentContainer();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    protected long getLoadTime() {
        return mLoadTime;
    }
    /**
     * Process the click in continue shopping
     * @param view The clicked view
     * @author sergiopereira
     */
    protected void onClickRetryButton(View view) {
        // ...
    }





    /**
     * Show the summary order if the view is present
     * On rotate, tries use the old fragment from ChildFragmentManager.
     * @author sergiopereira
     */
    /*public void showOrderSummaryIfPresent(int checkoutStep, PurchaseEntity orderSummary) {
        // Get order summary
        if (isOrderSummaryPresent) {
            Print.i(TAG, "ORDER SUMMARY IS PRESENT");
            CheckoutSummaryFragment fragment = (CheckoutSummaryFragment) getChildFragmentManager().findFragmentByTag(CheckoutSummaryFragment.TAG + "_" + checkoutStep);
            if(fragment == null) {
                fragment = CheckoutSummaryFragment.getInstance(checkoutStep, orderSummary);
            } else {
                // Used to update when is applied a voucher
                fragment.onUpdate(checkoutStep, orderSummary);
            }
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(ORDER_SUMMARY_CONTAINER, fragment, CheckoutSummaryFragment.TAG + "_" + checkoutStep);
            ft.commit();
        } else {
            Print.i(TAG, "ORDER SUMMARY IS NOT PRESENT");
        }
    }*/

/*
     * (non-Javadoc)
     *
     * @see com.mobile.utils.BaseActivity.OnActivityFragmentInteraction#allowBackPressed()
     */
    public boolean allowBackPressed() {

        // No intercept the back pressed
        return false;
    }

    /**
     * Show the retry view from the root layout
     * @author sergiopereira
     */
    protected void showFragmentErrorRetry() {
        showErrorFragment(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, null);
    }

    /**
     * Show error layout based on type. if the view is not inflated, it will be in first place.
     */



    protected void setCheckoutStep(View view, int step)
    {
        TextView tvStep1 = (TextView) view.findViewById(R.id.step1);
        TextView tvStep2 = (TextView) view.findViewById(R.id.step2);
        TextView tvStep3 = (TextView) view.findViewById(R.id.step3);
        TextView tvStep1_title = (TextView) view.findViewById(R.id.step1_title);
        TextView tvStep2_title = (TextView) view.findViewById(R.id.step2_title);
        TextView tvStep3_title = (TextView) view.findViewById(R.id.step3_title);
        tvStep1.setText("1");
        tvStep2.setText("2");
        tvStep3.setText("3");
        switch (step)
        {
            case 1:
                tvStep1_title.setVisibility(View.VISIBLE);
                tvStep2_title.setVisibility(View.GONE);
                tvStep3_title.setVisibility(View.GONE);
                tvStep1.setBackgroundResource(R.drawable.new_checkout_steps_current);
                tvStep2.setBackgroundResource(R.drawable.new_checkout_steps_next);
                tvStep3.setBackgroundResource(R.drawable.new_checkout_steps_next);
                break;
            case 2:
                tvStep1.setText("");
                tvStep1_title.setVisibility(View.GONE);
                tvStep2_title.setVisibility(View.VISIBLE);
                tvStep3_title.setVisibility(View.GONE);
                tvStep1.setBackgroundResource(R.drawable.new_checkout_steps_passed);
                tvStep2.setBackgroundResource(R.drawable.new_checkout_steps_current);
                tvStep3.setBackgroundResource(R.drawable.new_checkout_steps_next);
                break;
            case 3:
                tvStep1.setText("");
                tvStep2.setText("");
                tvStep1_title.setVisibility(View.GONE);
                tvStep2_title.setVisibility(View.GONE);
                tvStep3_title.setVisibility(View.VISIBLE);
                tvStep1.setBackgroundResource(R.drawable.new_checkout_steps_passed);
                tvStep2.setBackgroundResource(R.drawable.new_checkout_steps_passed);
                tvStep3.setBackgroundResource(R.drawable.new_checkout_steps_current);
                break;
        }
    }

    public boolean validateStringToPattern(Context context, int label, EditText editText, String text, boolean isRequired, int min, int max, int regex, String errorMessage, int textViewId) {

            return validateStringToPattern(context, context.getResources().getString(label), editText, text, isRequired, min, max, context.getResources().getString(regex), errorMessage, textViewId);
        }

        public boolean validateStringToPattern(Context context, String label, EditText editText, String text, boolean isRequired, int min, int max, String regex, String errorMessage, int textViewId) {
        boolean result = true;
        TextView textView = (TextView) ((Activity)context).findViewById(textViewId);
            textView.setVisibility(View.GONE);
        String space = " ";
        // Case empty
        if (isRequired && android.text.TextUtils.isEmpty(text)) {
            errorMessage = context.getString(R.string.error_isrequired);
            textView.setText(errorMessage + space);
            textView.setVisibility(View.VISIBLE);
            //editText.setError(errorMessage + space);
            //setErrorText(errorMessage + space);
            result=false;
        }
        // Case too short
        else if (min > 0 && text.length() < min) {
            textView.setText(label + " " + String.format(context.getResources().getString(R.string.form_textminlen), min) + space);
            textView.setVisibility(View.VISIBLE);
            result = false;
        }
        // Case too long
        else if (max > 0 && text.length() > max) {
            textView.setText(label + " " + String.format(context.getResources().getString(R.string.form_textmaxlen), max) + space);
            textView.setVisibility(View.VISIBLE);
            result = false;
        }
        // Case no match regex
        else {

            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            // = ""; //this.entry.getValidation().getRegexErrorMessage();
            /**
             * This is a fallback in case API don't return the error message
             * for the Regex. Will be fixed in https://jira.africainternetgroup.com/browse/NAFAMZ-16927
             */
            if(com.mobile.newFramework.utils.TextUtils.isEmpty(errorMessage)){
                errorMessage = context.getString(R.string.error_ismandatory) + " " + label;
            }

            Matcher matcher = pattern.matcher(text);
            result = matcher.find();
            if (!result)
            {
                textView.setText(errorMessage + space);
                textView.setVisibility(View.VISIBLE);
            }
        }
        return result;
    }

    public void onCheckoutCircleClick(int currentstep, int nextstep)
    {
        for(int s=0;s<currentstep-nextstep;s++) {
            getBaseActivity().onBackPressed();
        }
        

    }
}
