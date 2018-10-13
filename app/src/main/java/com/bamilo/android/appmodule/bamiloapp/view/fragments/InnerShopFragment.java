package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.ActivitiesWorkFlow;
import com.bamilo.android.appmodule.bamiloapp.helpers.configs.GetStaticPageHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.SSLErrorAlertDialog;
import com.bamilo.android.appmodule.bamiloapp.utils.home.holder.HomeTopSellersTeaserAdapter;
import com.bamilo.android.framework.components.recycler.HorizontalListView;
import com.bamilo.android.framework.components.webview.SuperWebView;
import com.bamilo.android.framework.service.objects.home.type.TeaserGroupType;
import com.bamilo.android.framework.service.objects.statics.StaticFeaturedBox;
import com.bamilo.android.framework.service.objects.statics.StaticPage;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.tracking.AbcBaseTracker;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.EnumSet;

/**
 * Shop in shop Fragment.
 *
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
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET,
                MyMenuItem.MY_PROFILE),
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
        // Get data from arguments
        mGABeginRequestMillis = System.currentTimeMillis();
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if (arguments != null) {
            mTitle = arguments.getString(ConstantsIntentExtra.CONTENT_TITLE);
            mPageId = arguments.getString(ConstantsIntentExtra.CONTENT_ID);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ShopSelector.setLocaleOnOrientationChanged(BamiloApplication.INSTANCE);
        // Get scroll
        mScrollView = view.findViewById(R.id.shop_scroll);
        // Get main container
        mMainContainer = view.findViewById(R.id.shop_main_container);
        // Get web view
        mWebView = view.findViewById(R.id.shop_web_view);
        // Set the client
//        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // Enable java script
        mWebView.enableJavaScript();
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(mInnerShopWebClient);
        mWebView.clearCache(false);
        // Validate the data (load/request/continue)
        onValidateDataState();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
        outState.putString(ConstantsIntentExtra.CONTENT_ID, mPageId);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*
         * Save the web view scroll position only for back workflow and not for rotation.
         * On rotation some devices need a different delay to scroll until the saved position.
         */
        mWebViewScrollPosition = mScrollView != null ? mScrollView.getScrollY() : 0;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mMainContainer.removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
     * Load the escaped html.<br> The method used to load is the loadDataWithBaseURL, because the
     * loadData not works correctly for FROYO version.<br>
     *
     * @param staticPage The static page for inner shop
     * @see <a href="http://stackoverflow.com/questions/3961589/android-webview-and-loaddata?answertab=active#tab-top">http://stackoverflow.com/android-webview-and-loaddata</a>
     */
    private void onLoadShopData(StaticPage staticPage) {
        // Set title
        if (mTitle != null && !mTitle.trim().isEmpty()) {
            getBaseActivity().setActionBarTitle(mTitle);
        }
        // Validate
        if (staticPage.hasHtml() || staticPage.hasFeaturedBoxes()) {
            // Load featured box
            loadFeaturedBox(staticPage);
            // Load html and show container
            mWebView.requestFocus(View.FOCUS_DOWN);
            loadHtml(staticPage);
            // Show container after load delay
            mScrollView.postDelayed(() -> {
                // Restore the saved scroll position
                mScrollView.scrollTo(0, mWebViewScrollPosition);
                // Show container
                showFragmentContentContainer();
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
                View inflated = inflater
                        .inflate(R.layout._def_shop_fragment_featured_box, mMainContainer, false);
                ((TextView) inflated.findViewById(R.id.shop_featured_box_title))
                        .setText(featuredBox.getTitle());
                HorizontalListView horizontalListView = inflated.findViewById(
                        R.id.shop_featured_box_horizontal_list);        // Validate orientation
                horizontalListView.setHasFixedSize(true);
                horizontalListView.enableRtlSupport(ShopSelector.isRtl());
                horizontalListView
                        .setAdapter(new HomeTopSellersTeaserAdapter(featuredBox.getItems(), this));
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
        triggerContentEvent(new GetStaticPageHelper(), GetStaticPageHelper.createBundle(page),
                this);
    }

    /*
     * ############## LISTENERS ##############
     */

    @Override
    public void onClick(View view) {
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
     * The web client to intercept the clicks in the deep links to show the respective view:<br> -
     * Case product: the link is pdv::http://...  <br> - Case catalog: the link is
     * catalog::http://... <br> - Case campaign: the link is campaign::http://... <br>
     */
    private final WebViewClient mInnerShopWebClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            showContinueShopping();
        }

        /**
         * https://developer.android.com/guide/webapps/migrating.html#URLs
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                url = URLDecoder.decode(url, "utf-8");
            } catch (UnsupportedEncodingException e) {
            }
            // Parse, validate and goto the deep link
            if (!processDeepLink(url)) {
                // Or external link
                ActivitiesWorkFlow.startExternalWebActivity(getBaseActivity(), url,
                        AbcBaseTracker.NOT_AVAILABLE);
            }
            // Return link processed
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            new SSLErrorAlertDialog(getContext())
                    .show(getString(R.string.ssl_error_handler_title),
                            getString(R.string.ssl_error_handler_message),
                            v -> handler.proceed(),
                            v -> handler.cancel());
        }
    };

    /**
     * Process the deep link with this structure: TARGET::URL<br> Supported targets: pdv, catalog
     * and campaign
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
        // Validate fragment state
        if (isOnStoppingProcess) {
            return;
        }
        // Get static page
        StaticPage mShopPage = (StaticPage) baseResponse.getContentData();
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
        // Validate fragment state
        if (isOnStoppingProcess) {
            return;
        }
        // Case network errors
        if (super.handleErrorEvent(baseResponse)) {
        }
        // Case other errors
        else {
            showContinueShopping();
        }
    }
}