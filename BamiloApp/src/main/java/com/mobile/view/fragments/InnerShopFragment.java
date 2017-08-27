package com.mobile.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;

import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.recycler.HorizontalListView;
import com.mobile.components.webview.SuperWebView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.objects.home.type.TeaserGroupType;
import com.mobile.service.objects.statics.StaticFeaturedBox;
import com.mobile.service.objects.statics.StaticPage;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.tracking.AbcBaseTracker;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.home.holder.HomeTopSellersTeaserAdapter;
import com.mobile.view.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.EnumSet;

/**
 * Shop in shop Fragment.
 * @author sergiopereira
 */
public class InnerShopFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = InnerShopFragment.class.getSimpleName();

    private static final int WEB_VIEW_LOAD_DELAY = 400;

    private String mTitle;

    private String mPageId;

    private ViewGroup mMainContainer;

    private SuperWebView mWebView;

    private ScrollView mScrollView;

    private int mWebViewScrollPosition = 0;

    //DROID-10
    private long mGABeginRequestMillis;

    /**
     * Empty constructor.
     */
    public InnerShopFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.UNKNOWN,
                R.layout.shop_fragment_main,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
    }

    /*
     * ############ LIFE CYCLE ############
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get data from arguments
        mGABeginRequestMillis = System.currentTimeMillis();
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if (arguments != null) {
            mTitle = arguments.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mPageId = arguments.getString(ConstantsIntentExtra.CONTENT_ID);
            Print.i(TAG, "LOAD DATA: " + mTitle + " " + mPageId);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        ShopSelector.setLocaleOnOrientationChanged(BamiloApplication.INSTANCE);
        // Get scroll
        mScrollView = (ScrollView) view.findViewById(R.id.shop_scroll);
        // Get main container
        mMainContainer = (ViewGroup) view.findViewById(R.id.shop_main_container);
        // Get web view
        mWebView = (SuperWebView) view.findViewById(R.id.shop_web_view);
        // Set the client
        mWebView.setWebViewClient(mInnerShopWebClient);
        // Enable java script
        mWebView.enableJavaScript();
        // Validate the data (load/request/continue)
        onValidateDataState();
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVED INSTANCE STATE");
        outState.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
        outState.putString(ConstantsIntentExtra.CONTENT_ID, mPageId);
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        /*
         * Save the web view scroll position only for back workflow and not for rotation.
         * On rotation some devices need a different delay to scroll until the saved position.
         */
        mWebViewScrollPosition = mScrollView != null ? mScrollView.getScrollY() : 0;
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    @Override
    public void onDestroyView() {
        mWebView.destroy();
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /*
     * ############ LAYOUT ############
     */

    /**
     * Validate the current data.
     */
    private void onValidateDataState() {
        // Get data
        if (!TextUtils.isEmpty(mPageId)) {
            triggerGetShop(mPageId);
        }
        // Case unexpected error
        else {
            showContinueShopping();
        }
    }

    /**
     * Load the escaped html.<br> The method used to load is the loadDataWithBaseURL, because the loadData not works correctly for FROYO version.<br>
     *
     * @param staticPage The static page for inner shop
     * @see <a href="http://stackoverflow.com/questions/3961589/android-webview-and-loaddata?answertab=active#tab-top">http://stackoverflow.com/android-webview-and-loaddata</a>
     */
    private void onLoadShopData(StaticPage staticPage) {
        // Set title
        getBaseActivity().setTitle(mTitle);
        // Validate
        if (staticPage.hasHtml() || staticPage.hasFeaturedBoxes()) {
            // Load featured box
            loadFeaturedBox(staticPage);
            // Load html and show container
            mWebView.requestFocus(View.FOCUS_DOWN);
            loadHtml(staticPage);
            // Show container after load delay
            mScrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Restore the saved scroll position
                    mScrollView.scrollTo(0, mWebViewScrollPosition);
                    // Show container
                    showFragmentContentContainer();
                }
            }, WEB_VIEW_LOAD_DELAY);

        } else {
            // Show continue shopping
            showContinueShopping();
        }
    }

    /**
     * Load the Html.<br/>
     */
    private void loadHtml(StaticPage staticPage) {
        // Validate html
        if (staticPage.hasHtml()) {
            // Load the html response, striped two times
            mWebView.requestFocus(View.FOCUS_DOWN);
            mWebView.loadData(TextUtils.stripHtml(staticPage.getHtml()));
        } else {
            // Hide web view
            mWebView.requestFocus(View.FOCUS_DOWN);
            mWebView.setVisibility(View.GONE);
        }
    }

    /**
     * Load featured boxes.
     */
    private void loadFeaturedBox(StaticPage staticPage) {
        // Validate html
        if (staticPage.hasFeaturedBoxes()) {
            Context context = mMainContainer.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (StaticFeaturedBox featuredBox : staticPage.getFeaturedBoxes()) {
                View inflated = inflater.inflate(R.layout._def_shop_fragment_featured_box, mMainContainer, false);
                ((TextView) inflated.findViewById(R.id.shop_featured_box_title)).setText(featuredBox.getTitle());
                HorizontalListView horizontalListView = (HorizontalListView) inflated.findViewById(R.id.shop_featured_box_horizontal_list);        // Validate orientation
                horizontalListView.setHasFixedSize(true);
                horizontalListView.enableRtlSupport(ShopSelector.isRtl());
                horizontalListView.setAdapter(new HomeTopSellersTeaserAdapter(featuredBox.getItems(), this));
                mMainContainer.addView(inflated);
            }
        }
    }

    /*
     * ############ TRIGGERS ############
     */

    /**
     * Trigger to get the CMS block for shop.
     *
     * @param page The shop service
     */
    private void triggerGetShop(String page) {
        triggerContentEvent(new GetStaticPageHelper(), GetStaticPageHelper.createBundle(page), this);
    }

    /*
     * ############## LISTENERS ##############
     */

    @Override
    public void onClick(View view) {
        Print.i(TAG, "ON CLICK");
        // Get featured box item type
        String sku = (String) view.getTag(R.id.target_link);
        // Validate target
        if (TextUtils.isNotEmpty(sku)) {
            // Get url
            gotoProduct(view);
        } else {
            super.onClick(view);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onValidateDataState();
    }

    /**
     * The web client to intercept the clicks in the deep links to show the respective view:<br>
     * - Case product: the link is pdv::http://...  <br>
     * - Case catalog: the link is catalog::http://... <br>
     * - Case campaign: the link is campaign::http://... <br>
     */
    private final WebViewClient mInnerShopWebClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Print.i(TAG, "ON PAGE STARTED: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Print.i(TAG, "ON PAGE FINISHED: " + url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Print.i(TAG, "ON PAGE RECEIVED ERROR: " + failingUrl);
            showContinueShopping();
        }

        /**
         * https://developer.android.com/guide/webapps/migrating.html#URLs
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Print.i("SHOULD OVERRIDE URL LOADING: " + url);
            try {
                url = URLDecoder.decode(url, "utf-8");
            } catch (UnsupportedEncodingException e) {
                Print.w("WARNING ON DECODE URL", e);
            }
            // Parse, validate and goto the deep link
            if (!processDeepLink(url)) {
                // Or external link
                ActivitiesWorkFlow.startExternalWebActivity(getBaseActivity(), url, AbcBaseTracker.NOT_AVAILABLE);
            }
            // Return link processed
            return true;
        }
    };

    /**
     * Process the deep link with this structure: TARGET::URL<br> Supported targets: pdv, catalog and campaign
     *
     * @param link The deep link
     */
    private boolean processDeepLink(String link) {
        // Parse target link
        return new TargetLink(getWeakBaseActivity(), link)
                .addTitle(mTitle)
                .setOrigin(mGroupType)
                .retainBackStackEntries()
                .run();
    }

    /**
     * Goto Product.
     */
    private void gotoProduct(View view) {
        // Get title
        String title = (String) view.getTag(R.id.target_title);
        // Get target link
        @TargetLink.Type String link = (String) view.getTag(R.id.target_link);
        // Get origin id
        int id = (int) view.getTag(R.id.target_teaser_origin);
        // Get teaser group type
        TeaserGroupType origin = TeaserGroupType.values()[id];
        // Parse target link
        new TargetLink(getWeakBaseActivity(), link)
                .addTitle(title)
                .setOrigin(origin)
                .retainBackStackEntries()
                .run();
    }

    /**
     * Handles the success request
     *
     * @param baseResponse The response
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "ON SUCCESS");
        // Validate fragment state
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get static page
        StaticPage mShopPage = (StaticPage) baseResponse.getContentData();
        //DROID-10
        TrackerDelegator.trackScreenLoadTiming(R.string.gaStaticPage, mGABeginRequestMillis, "");
        //  Case valid success response
        if (mShopPage != null) {
            onLoadShopData(mShopPage);
        }
        // Case invalid success response
        else {
            showContinueShopping();
        }
    }

    /**
     * Handles the error request
     *
     * @param baseResponse The response
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR");
        // Validate fragment state
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Case network errors
        if (super.handleErrorEvent(baseResponse)) {
            Print.i(TAG, "RECEIVED NETWORK ERROR!");
        }
        // Case other errors
        else {
            showContinueShopping();
        }
    }

}