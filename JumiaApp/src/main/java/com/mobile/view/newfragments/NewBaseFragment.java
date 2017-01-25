package com.mobile.view.newfragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewStub;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.ui.UITabLayoutUtils;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;
import com.mobile.view.fragments.CheckoutSummaryFragment;

/**
 * Created by Arash on 1/23/2017.
 */

public abstract class NewBaseFragment extends Fragment {

    protected static final String TAG = NewBaseFragment.class.getSimpleName();
    protected long mLoadTime = 0; // For tacking
    private BaseActivity mainActivity;
    private ViewStub mLoadingView;

    public NewBaseFragment()
    {

    }

    public static NewBaseFragment newInstance(@NonNull Context context, @NonNull Class<? extends NewBaseFragment> fragment, @Nullable Bundle arguments) {
        return (NewBaseFragment) Fragment.instantiate(context, fragment.getName(), arguments);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        mLoadTime = System.currentTimeMillis();
        if(mLoadTime == 0) mLoadTime = System.currentTimeMillis();
        // Set flag for requests
        //isOnStoppingProcess = false;
        // Exist order summary
        //isOrderSummaryPresent = view.findViewById(ORDER_SUMMARY_CONTAINER) != null;
        // Get loading layout
        mLoadingView = (ViewStub) view.findViewById(R.id.fragment_stub_loading);
       // mLoadingView.setOnInflateListener(this);
        // Get retry layout
       // mErrorView =  view.findViewById(R.id.fragment_stub_retry);
       // ((ViewStub) mErrorView).setOnInflateListener(this);
        // Get fall back layout
        //mFallBackView = (ViewStub) view.findViewById(R.id.fragment_stub_home_fall_back);
        //mFallBackView.setOnInflateListener(this);
        // Get maintenance layout
        //mMaintenanceView = (ViewStub) view.findViewById(R.id.fragment_stub_maintenance);
        //mMaintenanceView.setOnInflateListener(this);
        // Update base components, like items on action bar
        /*if (!isNestedFragment && enabledMenuItems != null) {
            Print.i(TAG, "UPDATE BASE COMPONENTS: " + enabledMenuItems + " " + action);
            getBaseActivity().updateBaseComponents(enabledMenuItems, action, titleResId, checkoutStep);
            // Method used to set a bottom margin
            UITabLayoutUtils.setViewWithoutNestedScrollView(mContentView, action);
        }*/

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

    /**
     * Send request showing the loading
     */
    protected final void triggerContentEvent(final SuperBaseHelper helper, Bundle args, final IResponseCallback responseCallback) {
        // Show loading
        showFragmentLoading();
        // Request
        JumiaApplication.INSTANCE.sendRequest(helper, args, responseCallback);
    }
    /**
     * Show the loading view from the root layout
     */
    protected void showFragmentLoading() {
        UIUtils.showOrHideViews(View.VISIBLE, mLoadingView);
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


}
