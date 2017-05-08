package com.mobile.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.mobile.app.BamiloApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.account.GetCustomerHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.PaymentMethodForm;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


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
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");

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
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_EXTERNAL_PAYMENT);
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
        Print.i(TAG, "ON VIEW CREATED");
        webview = (WebView) view.findViewById(R.id.webview);
        // TODO VALIDATE IF THIS TRIGGER IS NECESSARY
        triggerGetCustomer();
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
        webview.requestFocus();
        prepareCookieStore();
        setupWebView();
        startCheckout();
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
        if (webview == null) {
            Print.d(TAG, "onBackPressed");
        } else {
            Print.d(TAG, "onBackPressed: webview.canGoBackup = " + webview.canGoBack() + " webview.hasFocus() = " + webview.hasFocus());
        }
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
        Print.e(getTag(), "LOW MEM");
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
        Print.d(TAG, "Loading Url: " + paymentUrl);
        // Track
        String userId = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString() : "";
        String email = BamiloApplication.INSTANCE.getCustomerUtils().getEmail();
        String payment = mPaymentSubmitted.getName();
        TrackerDelegator.trackPaymentMethod(userId, email, payment);

        List<NameValuePair> parameters = new ArrayList<>();

        if (mPaymentSubmitted.getContentValues() != null && mPaymentSubmitted.getMethod() == PaymentMethodForm.POST) {
            Set<Entry<String, Object>> mValues = mPaymentSubmitted.getContentValues().valueSet();
            ResNum = (String) mPaymentSubmitted.getContentValues().get("ResNum");
           /* for (Entry<String, Object> entry : mValues) {
                if (entry.getKey().equalsIgnoreCase("tc")) {
                    parameters.add(new BasicNameValuePair(entry.getKey(), "1"));
                } else {
                    parameters.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
                }
            }*/




            Print.d(TAG, "Loading Url complete: " + ResNum + "  " );

            String uri = Uri.parse("http://" + getResources().getString(R.string.single_shop_country_url).concat("/androidpayment/sep/"))
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
        public void onReceivedError(WebView view, int errorCode, String description, final String failingUrl) {
            Print.e(TAG, "Received error: " + errorCode + " " + description + " " + failingUrl);
            failedPageRequest = failingUrl;
            webview.stopLoading();
            webview.clearView();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
            Print.i(TAG, "ON RECEIVED HTTP AUTH REQUEST");
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
            Print.d(TAG, "onPageFinished: url = " + url);
            if (wasLoadingErrorPage) {
                Print.d(TAG, "onPageFinished: resetting error page information");
                wasLoadingErrorPage = false;
                failedPageRequest = null;
            } else if (url.equals(failedPageRequest)) {
                Print.d(TAG, "onPageFinished: page was saved failed page");
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
                Print.d(TAG, "LOAD URL: JAVASCRIPT PROCESS");
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
            //Print.i(TAG, "code1payment : onLoadResource");
            try {
                Print.d(TAG, "onLoadResource: url = " + url);
            } catch (OutOfMemoryError e) {
                Print.d(TAG, "onLoadResource: url = OOF");
                e.printStackTrace();
            }
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
            Print.d(TAG, "onPageStarted: url = " + url);
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

        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebViewClient#onReceivedSslError(android.webkit.WebView ,
         * android.webkit.SslErrorHandler, android.net.http.SslError)
         */
        @Override
        public void onReceivedSslError(WebView view, @NonNull SslErrorHandler handler, SslError error) {
            //Print.i(TAG, "code1payment : onReceivedSslError : " + error);
            Print.w(TAG, "Received ssl error: " + error);
            if (error.getPrimaryError() == SslError.SSL_IDMISMATCH) {
                showWarningErrorMessage(getString(R.string.ssl_error_host_mismatch));
            } else {
                showWarningErrorMessage(getString(R.string.ssl_error_generic));
            }
            // Case in dev continue
            if (BamiloApplication.INSTANCE.isDebuggable()) {
                handler.proceed();
            } else {
                String url = view.getUrl();
                onReceivedError(view, error.getPrimaryError(), error.toString(), url);
                handler.cancel();
            }
        }
    }

    private class JavaScriptInterface {

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void processContent(String content) {
            try {
                Print.d(TAG, "Got checkout response: " + content);
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
                    if(getArguments() == null){
                        bundle = new Bundle();
                    } else {
                        bundle = getArguments();
                    }
                    bundle.putString(RestConstants.ORDER_NUMBER, orderNumber);
                    // Switch
                    getBaseActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK);
                        }
                    });
                }
            } catch (JSONException e) {
                Print.e(TAG, "json parse exception:", e);
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
                customer = (Customer)baseResponse.getContentData();
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
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

}