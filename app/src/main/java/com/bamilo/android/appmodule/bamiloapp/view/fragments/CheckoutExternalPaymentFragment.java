package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.account.GetCustomerHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.SSLErrorAlertDialog;
import com.bamilo.android.framework.service.forms.PaymentMethodForm;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.rest.AigHttpClient;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.EventType;
import java.net.HttpCookie;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Web view to execute an external Payment
 *
 * @author Manuel Silva
 * @edit Shahrooz Jahanshah
 */
public class CheckoutExternalPaymentFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = CheckoutExternalPaymentFragment.class.getSimpleName();

    private WebView webview;

    private String paymentUrl;

    private String failedPageRequest;

    private boolean isRequestedPage;

    private final Handler handler = new Handler();

    private Customer customer;

    private PaymentMethodForm mPaymentSubmitted;

    String ResNum = null;

    /**
     * Empty constructor
     */
    public CheckoutExternalPaymentFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.checkoutweb,
                R.string.checkout_label,
                NO_ADJUST_CONTENT);
        this.setRetainInstance(true);
        this.webview = null;
        this.paymentUrl = null;
        this.failedPageRequest = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPaymentSubmitted = arguments.getParcelable(ConstantsIntentExtra.DATA);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webview = view.findViewById(R.id.webview);
        // TODO VALIDATE IF THIS TRIGGER IS NECESSARY
        triggerGetCustomer();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        webview.requestFocus();
        prepareCookieStore();
        setupWebView();
        startCheckout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webview != null) {
            webview.setWebViewClient(null);
            try {
                webview.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            webview.destroy();
            webview = null;
        }
        System.gc();
    }

    @Override
    public boolean allowBackPressed() {
        boolean result = false;
        if (webview != null && webview.canGoBack() && webview.hasFocus()) {
            webview.goBack();
            result = true;
        }
        return result;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setupWebView() {
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        webview.setWebViewClient(customWebViewClient);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSaveFormData(true);
        webview.getSettings().setSavePassword(false);
        webview.addJavascriptInterface(new JavaScriptInterface(), "INTERFACE");
    }

    @SuppressWarnings("deprecation")
    private void startCheckout() {

        showFragmentLoading();
        webview.clearView();
        if (mPaymentSubmitted != null) {
            paymentUrl = mPaymentSubmitted.getAction();
        } else {
            super.showFragmentErrorRetry();
            return;
        }
        // Track
        String userId =
                BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString()
                        : "";
        String email = BamiloApplication.INSTANCE.getCustomerUtils().getEmail();
        String payment = mPaymentSubmitted.getName();

        if (mPaymentSubmitted.getContentValues() != null
                && mPaymentSubmitted.getMethod() == PaymentMethodForm.POST) {
            Set<Entry<String, Object>> mValues = mPaymentSubmitted.getContentValues().valueSet();
            ResNum = (String) mPaymentSubmitted.getContentValues().get("ResNum");
           /* for (Entry<String, Object> entry : mValues) {
                if (entry.getKey().equalsIgnoreCase("tc")) {
                    parameters.add(new BasicNameValuePair(entry.getKey(), "1"));
                } else {
                    parameters.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
                }
            }*/

            String uri = Uri
                    .parse("http://" + getResources().getString(R.string.single_shop_country_url)
                            .concat("/androidpayment/sep/"))
                    .buildUpon()
                    .appendQueryParameter("ResNum", ResNum)
                    .appendQueryParameter("setDevice", "mobile")
                    .build().toString();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(uri)));
            startActivity(browserIntent);


        } /*else if (mPaymentSubmitted.getContentValues() != null) {
            Set<Entry<String, Object>> mValues = mPaymentSubmitted.getContentValues().valueSet();
            //setProxy();
            for (Entry<String, Object> entry : mValues) {
                if (!paymentUrl.contains("?")) {
                    paymentUrl += "?" + entry.getKey() + "=" + entry.getValue();
                } else {
                    paymentUrl += "&" + entry.getKey() + "=" + entry.getValue();
                }
            }
            webview.loadUrl(paymentUrl);
        } else {
            //setProxy();
            webview.loadUrl(paymentUrl);
        }
*/
        isRequestedPage = true;
    }

    private void prepareCookieStore() {

        if (DeviceInfoHelper.isPosLollipop()) {
            // AppRTC requires third party cookies to work
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webview, true);
        }
        // GET COOKIES FROM FRAMEWORK
        List<HttpCookie> cookies = AigHttpClient.getInstance().getCookies();

        CookieManager cookieManager = CookieManager.getInstance();
        if (!cookies.isEmpty()) {
            CookieSyncManager.createInstance(getActivity());
            // sync all the cookies in the httpclient with the webview by
            // generating cookie string

            for (HttpCookie cookie : cookies) {
                String normDomain = prepareCookie(cookie);
                String cookieString = cookie.getName() + "=" + cookie.getValue() + "; Domain="
                        + cookie.getDomain();
                // Log.d( TAG, "prepareCookieStore: adding cookie = " + cookieString);
                cookieManager.setCookie(normDomain, cookieString);
            }
            CookieSyncManager.getInstance().sync();
        }
    }

    private String prepareCookie(HttpCookie cookie) {
        return cookie.getDomain();
    }

    private void trackPurchase(final JSONObject result) {
        Bundle params = new Bundle();
        params.putString(TrackerDelegator.PURCHASE_KEY, result.toString());
        params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
        TrackerDelegator.trackPurchase(params);
    }

    private class CustomWebViewClient extends WebViewClient {

        private static final String SUCCESS_URL_TAG = "checkout/success";
        private static final String JAVASCRIPT_PROCESS = "javascript:window.INTERFACE.processContent(document.getElementById('jsonAppObject').innerHTML);";
        private boolean wasLoadingErrorPage;
        private long beginTransaction;

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                final String failingUrl) {
            failedPageRequest = failingUrl;
            webview.stopLoading();
            webview.clearView();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,
                String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView,
         * java.lang.String)
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //Print.i(TAG, "code1payment : onPageFinished");
            if (wasLoadingErrorPage) {
                wasLoadingErrorPage = false;
                failedPageRequest = null;
            } else if (url.equals(failedPageRequest)) {
                wasLoadingErrorPage = true;
                getBaseActivity().removeAllNativeCheckoutFromBackStack();
                showFragmentSSLError();
            } else if (isRequestedPage) {
                showFragmentContentContainer();
                isRequestedPage = false;
            } else {
                showFragmentContentContainer();
            }

            if (url.contains(SUCCESS_URL_TAG)) {
                /**
                 * This line cause s a JNI exception only in the emulators 2.3.X.
                 *
                 * @see http://code.google.com/p/android/issues/detail?id=12987
                 */
                view.loadUrl(JAVASCRIPT_PROCESS);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebViewClient#onLoadResource(android.webkit.WebView,
         * java.lang.String)
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebViewClient#onPageStarted(android.webkit.WebView, java.lang.String,
         * android.graphics.Bitmap)
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //Print.i(TAG, "code1payment : onPageStarted : " + url);
            if (url.equals(failedPageRequest)) {
                return;
            }
            beginTransaction = System.currentTimeMillis();
            showFragmentLoading();
            if (url.contains(SUCCESS_URL_TAG)) {
                view.getSettings().setBlockNetworkImage(true);
                view.getSettings().setBlockNetworkLoads(true);
                view.getSettings().setLoadsImagesAutomatically(false);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, @NonNull SslErrorHandler handler,
                SslError error) {
            if (error.getPrimaryError() == SslError.SSL_IDMISMATCH) {
                showWarningErrorMessage(getString(R.string.ssl_error_host_mismatch));
            } else {
                showWarningErrorMessage(getString(R.string.ssl_error_generic));
            }

            new SSLErrorAlertDialog(getContext())
                    .show(getString(R.string.ssl_error_handler_title),
                            getString(R.string.ssl_error_handler_message), v -> {
                                if (BamiloApplication.INSTANCE.isDebuggable()) {
                                    handler.proceed();
                                }
                            }
                            , v -> {
                                String url = view.getUrl();
                                onReceivedError(view, error.getPrimaryError(), error.toString(),
                                        url);
                                handler.cancel();
                            });
        }
    }

    private class JavaScriptInterface {

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void processContent(String content) {
            try {
                final JSONObject result = new JSONObject(content);
                if (result.optBoolean("success")) {
                    // TODO VALIDATE IF THIS TRIGGER IS NECESSARY
                    triggerGetCustomer();
                    // Measure to escape the web view thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            trackPurchase(result);
                        }
                    });

                    // Get order number
                    String orderNumber = "";
                    if (result.has(RestConstants.ORDER_NR)) {
                        orderNumber = result.optString(RestConstants.ORDER_NR);
                    } else if (result.has(RestConstants.ORDERNr)) {
                        orderNumber = result.optString(RestConstants.ORDERNr);
                    }

                    // Create bundle for last checkout step
                    final Bundle bundle;
                    if (getArguments() == null) {
                        bundle = new Bundle();
                    } else {
                        bundle = getArguments();
                    }
                    bundle.putString(RestConstants.ORDER_NUMBER, orderNumber);
                    // Switch
                    getBaseActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle,
                                    FragmentController.ADD_TO_BACK_STACK);
                        }
                    });
                }
            } catch (JSONException e) {
            }
        }
    }

    private void triggerGetCustomer() {
        triggerContentEventNoLoading(new GetCustomerHelper(), null, this);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        CheckoutExternalPaymentFragment.super.handleErrorEvent(baseResponse);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_CUSTOMER:
                customer = (Customer) baseResponse.getContentData();
                BamiloApplication.CUSTOMER = customer;
                break;
            case GET_SHOPPING_CART_ITEMS_EVENT:
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickErrorButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        if (null != BamiloApplication.CUSTOMER) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE,
                    FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle,
                    FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle,
                    FragmentController.ADD_TO_BACK_STACK);
        }
    }

}