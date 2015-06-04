package com.mobile.view.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.objects.TeaserCampaign;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.TextUtils;
import com.mobile.helpers.teasers.GetShopHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

import com.mobile.framework.output.Print;

/**
 * Shops in shop Fragment. Created by Sergio Pereira on 3/4/15.
 *
 * @author sergiopereira
 */
public class InnerShopFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = InnerShopFragment.class.getSimpleName();

    private static final String HTML_TYPE = "text/html";

    private static final String HTML_ENCODING = "utf-8";

    private static final int DEEP_LINK_SIZE = 2;

    private static final int DEEP_LINK_TARGET_POSITION = 0;

    private static final int DEEP_LINK_URL_POSITION = 1;

    private static final String DEEP_LINK_DELIMITER = "::";

    private static final String TARGET_TYPE_PDV = "pdv";

    private static final String TARGET_TYPE_CATALOG = "catalog";

    private static final String TARGET_TYPE_CAMPAIGN = "campaign";

    private static final int WEB_VIEW_SCROLL_DELAY = 100;

    private String mTitle;

    private String mUrl;

    private String mHtml;

    private WebView mWebView;

    private int mWebViewScrollPosition = 0;

    /**
     * Get a instance of InnerShopFragment.
     *
     * @param bundle - the arguments
     * @return InnerShopFragment
     */
    public static InnerShopFragment getInstance(Bundle bundle) {
        InnerShopFragment fragment = new InnerShopFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor.
     */
    public InnerShopFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Unknown,
                R.layout.shop_fragment_main,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /*
     * ############ LIFE CYCLE ############
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get data from arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTitle = arguments.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mUrl = arguments.getString(ConstantsIntentExtra.CONTENT_URL);
            Print.i(TAG, "RECEIVED DATA: " + mTitle + " " + mUrl);
        }
        // Get data from saved instance
        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mUrl = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_URL);
            mHtml = savedInstanceState.getString(ConstantsIntentExtra.CONTENT_DATA);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get web view
        mWebView = (WebView) view.findViewById(R.id.shop_web_view);
        // Set the client
        mWebView.setWebViewClient(InnerShopWebClient);
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
        outState.putString(ConstantsIntentExtra.CONTENT_URL, mUrl);
        outState.putString(ConstantsIntentExtra.CONTENT_DATA, mHtml);
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        /*
         * Save the web view scroll position only for back workflow and not for rotation.
         * On rotation some devices need a different delay to scroll until the saved position.
         */
        mWebViewScrollPosition = mWebView != null ? mWebView.getScrollY() : 0;
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

    /*
     * ############ LAYOUT ############
     */

    /**
     * Validate the current data.
     */
    private void onValidateDataState() {
        // Show saved data
        if (!TextUtils.isEmpty(mHtml)) {
            onLoadShopData(mHtml);
        }
        // Get data
        else if (!TextUtils.isEmpty(mUrl)) {
            triggerGetShop(mUrl);
        }
        // Case unexpected error
        else {
            showContinueShopping();
        }
    }

    /**
     * Load the escaped html.<br> The method used to load is the loadDataWithBaseURL, because the loadData not works correctly for FROYO version.<br>
     *
     * @param html The escaped html
     * @see <a href="http://stackoverflow.com/questions/3961589/android-webview-and-loaddata?answertab=active#tab-top">http://stackoverflow.com/android-webview-and-loaddata</a>
     */
    private void onLoadShopData(String html) {
        // Set title
        getBaseActivity().setTitle(mTitle);
        // Strip html response two times
        String displayableHtml = stripHtml(html);
        // Load data
        mWebView.loadDataWithBaseURL(null, displayableHtml, HTML_TYPE, HTML_ENCODING, null);
        // Show container
        showFragmentContentContainer();
    }

    /**
     * Strip the escaped html two times to return a displayable html.
     *
     * @param html The escaped html
     * @return String
     */
    public String stripHtml(String html) {
        return Html.fromHtml(Html.fromHtml(html).toString()).toString();
    }

    /*
     * ############ TRIGGERS ############
     */

    /**
     * Trigger to get the CMS block for shop.
     *
     * @param url The shop service
     */
    private void triggerGetShop(String url) {
        Bundle bundle = new Bundle();
        String staticPageKey = getStaticPageKey(url);
        bundle.putString(Constants.BUNDLE_URL_KEY, staticPageKey);
        triggerContentEvent(new GetShopHelper(), bundle, this);
    }

    /**
     * extract static page key from the static page url
     */
    private String getStaticPageKey(String url){
        if(!com.mobile.framework.utils.TextUtils.isEmpty(url)){
            Uri myUri = Uri.parse(url);
            return myUri.getQueryParameter(GetShopHelper.INNER_SHOP_TAG);
        }
        return "";
    }

    /*
     * ############## LISTENERS ##############
     */
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
     * The web client to intercept the clicks in the deep links to show the respective view:<br> - Case product: the link is pdv::http://... - Case catalog: the
     * link is catalog::http://... - Case campaign: the link is campaign::http://...
     */
    private WebViewClient InnerShopWebClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Print.i(TAG, "ON PAGE STARTED: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Print.i(TAG, "ON PAGE FINISHED: " + url);
            // Restore the saved position
            if (mWebViewScrollPosition > 0) {
                Print.i(TAG, "ON SCROLL TO SAVED POSITION: " + mWebViewScrollPosition);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.scrollTo(0, mWebViewScrollPosition);
                    }
                }, WEB_VIEW_SCROLL_DELAY);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Print.i(TAG, "ON PAGE RECEIVED ERROR: " + failingUrl);
            showContinueShopping();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Print.i(TAG, "SHOULD OVERRIDE URL LOADING: " + url);
            // Parse, validate and goto the deep link
            processDeepLink(url);
            // Return link processed
            return true;
        }
    };

    /**
     * Process the deep link with this structure: TARGET::URL<br> Supported targets: pdv, catalog and campaign
     *
     * @param link The deep link
     */
    private void processDeepLink(String link) {
        // Split pdv::http or catalog::http or campaign::http
        String[] deepLink = TextUtils.split(link, DEEP_LINK_DELIMITER);
        // Validate deep link
        if (deepLink.length == DEEP_LINK_SIZE) {
            // Target
            String target = deepLink[DEEP_LINK_TARGET_POSITION];
            // Link
            String url = deepLink[DEEP_LINK_URL_POSITION];
            // Case pdv
            if (TextUtils.equals(TARGET_TYPE_PDV, target)) {
                gotoProduct(url);
            }
            // Case catalog
            else if (TextUtils.equals(TARGET_TYPE_CATALOG, target)) {
                gotoCatalog(url);
            }
            // Case campaign
            else if (TextUtils.equals(TARGET_TYPE_CAMPAIGN, target)) {
                gotoCampaign(url);
            }
            // Case unknown
            else {
                Print.w(TAG, "WARNING UNKNOWN TARGET: " + target + " " + url);
            }
        }
    }

    /**
     * Goto Product.
     *
     * @param url The product url
     */
    private void gotoProduct(String url) {
        Print.i(TAG, "PDV: " + url);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, url);
        bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Goto Catalog.
     *
     * @param url The catalog url
     */
    private void gotoCatalog(String url) {
        Print.i(TAG, "CATALOG: " + url);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, url);
        bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
        getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Goto Campaign.
     *
     * @param url The campaign url
     */
    private void gotoCampaign(String url) {
        Print.i(TAG, "CAMPAIGN: " + url);
        Bundle bundle = new Bundle();
        ArrayList<TeaserCampaign> teaserCampaigns = new ArrayList<>();
        TeaserCampaign campaign = new TeaserCampaign();
        campaign.setTitle(mTitle);
        campaign.setUrl(url);
        teaserCampaigns.add(campaign);
        bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, teaserCampaigns);
        bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
        getBaseActivity().onSwitchFragment(FragmentType.CAMPAIGNS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Handles the success request
     *
     * @param bundle The response
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Print.i(TAG, "ON SUCCESS");
        // Validate fragment state
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get the html
        mHtml = bundle.getString(Constants.BUNDLE_RESPONSE_KEY);
        //  Case valid success response
        if (!TextUtils.isEmpty(mHtml)) {
            onLoadShopData(mHtml);
        }
        // Case invalid success response
        else {
            showContinueShopping();
        }
    }

    /**
     * Handles the error request
     *
     * @param bundle The response
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Print.i(TAG, "ON ERROR");
        // Validate fragment state
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Case network errors
        if (super.handleErrorEvent(bundle)) {
            Print.i(TAG, "RECEIVED NETWORK ERROR!");
        }
        // Case other errors
        else {
            showContinueShopping();
        }
    }
}