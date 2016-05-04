package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.components.webview.SuperWebView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.statics.StaticPage;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * @author Manuel Silva
 * 
 */
public class StaticWebViewPageFragment extends BaseFragmentRequester implements IResponseCallback {

    private static final String TAG = StaticWebViewPageFragment.class.getSimpleName();

    private SuperWebView mSuperWebViewView;
    private Bundle mStaticPageBundle;
    private String mTitle;
    private String mContentId;
    private String mContentHtml;

    /**
     * Empty constructor
     */
    public StaticWebViewPageFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.layout.static_webview_page_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }


    @Override
    protected void onCreateInstanceState(@NonNull Bundle bundle) {
        super.onCreateInstanceState(bundle);

        // Get static page key from arguments
        mStaticPageBundle = bundle;
        if (mStaticPageBundle != null) {
            mTitle = mStaticPageBundle.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mContentId = mStaticPageBundle.getString(ConstantsIntentExtra.CONTENT_ID);
            mContentHtml = mStaticPageBundle.getString(ConstantsIntentExtra.DATA);
        }
    }

    /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
         */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get title
        mTitle = TextUtils.isNotEmpty(mTitle) ? mTitle : getString(R.string.policy);
        // Title AB
        getBaseActivity().setActionBarTitle(mTitle);
        // Title Layout
        ((TextView) view.findViewById(R.id.terms_title)).setText(mTitle);
        // Content
        mSuperWebViewView = (SuperWebView) view.findViewById(R.id.static_webview);
        if(TextUtils.isNotEmpty(mContentHtml)){
            mSuperWebViewView.loadData(mContentHtml);
        } else {
            // Get static page
            triggerStaticPage();
        }

    }

    private void triggerStaticPage() {
        triggerContentEvent(new GetStaticPageHelper(), GetStaticPageHelper.createBundle(mContentId), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(ConstantsIntentExtra.FRAGMENT_BUNDLE, mStaticPageBundle);
        outState.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
        outState.putString(ConstantsIntentExtra.CONTENT_ID, mContentId);
        outState.putString(ConstantsIntentExtra.DATA, mContentHtml);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY");
    }


    @Override
    protected void onSuccessResponse(BaseResponse response) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (getBaseActivity() != null) {
            super.handleSuccessEvent(response);
        } else {
            return;
        }
        showFragmentContentContainer();
        mSuperWebViewView.loadData(TextUtils.stripHtml(mContentHtml));
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        if(!super.handleErrorEvent(response)){
            showContinueShopping();
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        triggerStaticPage();
    }
}
