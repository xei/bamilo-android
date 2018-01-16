package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.mobile.classes.models.BaseScreenModel;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.statics.StaticPage;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * @author Manuel Silva
 * 
 */
public class StaticPageFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = StaticPageFragment.class.getSimpleName();

    private TextView textView;
    private Bundle mStaticPageBundle;
    private String mTitle;
    private String mContentId;
    //DROID-10
    private long mGABeginRequestMillis;
    private boolean pageTracked = false;
    /**
     * Empty constructor
     */
    public StaticPageFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.TERMS,
                R.layout.static_page_fragment,
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
        mGABeginRequestMillis = System.currentTimeMillis();
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
        // Get static page key from arguments
        mStaticPageBundle = savedInstanceState != null ? savedInstanceState : getArguments();
        if (mStaticPageBundle != null) {
            mTitle = mStaticPageBundle.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mContentId = mStaticPageBundle.getString(ConstantsIntentExtra.CONTENT_ID);
        }

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.STATIC_PAGE.getName()), getString(R.string.gaScreen), "", getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
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
        textView = (TextView) view.findViewById(R.id.terms_text);
        // Get static page
        triggerStaticPage();
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
    public void onRequestComplete(BaseResponse baseResponse) {

        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (getBaseActivity() != null) {
            super.handleSuccessEvent(baseResponse);
        } else {
            return;
        }
        showFragmentContentContainer();
        textView.setText(((StaticPage)baseResponse.getMetadata().getData()).getHtml());
        if (!pageTracked) {
            // Track screen timing
            BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.STATIC_PAGE.getName()), getString(R.string.gaScreen),
                    "" /*The API isn't sending the name of the static page*/,
                    getLoadTime());
            TrackerManager.trackScreenTiming(getContext(), screenModel);
            pageTracked = true;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        if(!super.handleErrorEvent(baseResponse)){
            showContinueShopping();
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        triggerStaticPage();
    }
}
