/**
 * 
 */
package pt.rocket.view.fragments;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.account.GetCustomerHelper;
import pt.rocket.helpers.cart.GetShoppingCartItemsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import de.akquinet.android.androlog.Log;

/**
 * Webview to execute an external Payment
 * 
 * @author Manuel Silva
 * 
 */
public class CheckoutExternalPaymentFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(CheckoutExternalPaymentFragment.class);

    // private static final String CHECKOUT_URL_WITH_PARAM =
    // "/checkout/multistep/?setDevice=mobileApi&iosApp=1";

    private WebView webview;

    private String paymentUrl;

    private String failedPageRequest;
    private boolean isRequestedPage;

    private Handler handler = new Handler();

    private static CheckoutExternalPaymentFragment checkoutWebFragment;

    private Customer customer;
    /**
     * Get instance
     * 
     * @return
     */
    public static CheckoutExternalPaymentFragment getInstance() {
        if (checkoutWebFragment == null)
            checkoutWebFragment = new CheckoutExternalPaymentFragment();

        checkoutWebFragment.webview = null;
        checkoutWebFragment.paymentUrl = null;
        checkoutWebFragment.failedPageRequest = null;
        return checkoutWebFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutExternalPaymentFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.layout.checkoutweb,
                0,
                KeyboardState.NO_ADJUST_CONTENT);
        this.setRetainInstance(true);
    }

    @Override
    public boolean allowBackPressed() {
        if (webview == null) {
            Log.d(TAG, "onBackPressed");
        } else {
            Log.d(TAG, "onBackPressed: webview.canGoBackup = " + webview.canGoBack() + " webview.hasFocus() = " + webview.hasFocus());
        }
        if (webview != null && webview.canGoBack() && webview.hasFocus()) {
            webview.goBack();
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");

         triggerGetCustomer();
         Bundle params = new Bundle();        
         params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
         params.putInt(TrackerDelegator.GA_STEP_KEY, R.string.gcheckoutExternalPayment);
         params.putInt(TrackerDelegator.ADX_STEP_KEY, R.string.xcheckoutexternalpayment);
         params.putInt(TrackerDelegator.MIXPANEL_STEP_KEY, R.string.mixprop_checkout_external_payment);        
         
         TrackerDelegator.trackCheckoutStep(params);
    }

    private void triggerGetCustomer() {

        triggerContentEventWithNoLoading(new GetCustomerHelper(), null, mCallback);
    }

//    private void triggerGetShoppingCartItems() {
//
//        triggerContentEventWithNoLoading(new GetShoppingCartItemsHelper(), null, mCallback);
//    }

    IResponseCallback mCallback = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            getBaseActivity().handleErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);

        }
    };

//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
//     * android.view.ViewGroup, android.os.Bundle)
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        Log.i(TAG, "ON CREATE VIEW");
//        View view = inflater.inflate(R.layout.checkoutweb, container, false);
//
//        webview = (WebView) view.findViewById(R.id.webview);
//
//        return view;
//    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        webview = (WebView) view.findViewById(R.id.webview);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            webview.loadUrl("about:blank");
        }

        // Needed for 2.3 problem with not showing keyboard by tapping in webview
        webview.requestFocus();
        prepareCookieStore();
        setupWebView();

        startCheckout();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            webview.loadUrl("about:blank");
        }
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            webview.loadUrl("about:blank");
        }
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.i(TAG, "ON DESTROY");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webview != null) {
            webview.setWebViewClient(null);
            try {
                webview.removeAllViews();
            } catch (IllegalArgumentException e) {
                // TODO: handle exception
            }

            webview.destroy();
            webview = null;
        }
        System.gc();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(getTag(), "LOW MEM");
        System.gc();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        webview.setWebViewClient(customWebViewClient);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSaveFormData(true);
        webview.getSettings().setSavePassword(false);
        webview.addJavascriptInterface(new JavaScriptInterface(), "INTERFACE");
    }

    private void startCheckout() {
        showFragmentLoading();
        webview.clearView();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            webview.loadUrl("about:blank");
        }
        if(JumiaApplication.INSTANCE.getPaymentMethodForm() != null){
            paymentUrl = JumiaApplication.INSTANCE.getPaymentMethodForm().getAction();    
        } else {
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "NO PAYMENT METHOD DEFINED");
            return;
        }
        
        Log.i(TAG, "trackPaymentMethod : payment method : "+JumiaApplication.INSTANCE.getPaymentMethodForm().getName() + " email : "+JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        Bundle params = new Bundle();
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putString(TrackerDelegator.PAYMENT_METHOD_KEY,JumiaApplication.INSTANCE.getPaymentMethodForm().getName());    
        
        TrackerDelegator.trackPaymentMethod(params);
        
        Log.d(TAG, "Loading Url: " + paymentUrl);

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        if (JumiaApplication.INSTANCE.getPaymentMethodForm() != null
                && JumiaApplication.INSTANCE.getPaymentMethodForm().getContentValues() != null
                && JumiaApplication.INSTANCE.getPaymentMethodForm().getMethod() == RequestType.POST) {
            Set<Entry<String, Object>> mValues = JumiaApplication.INSTANCE.getPaymentMethodForm()
                    .getContentValues().valueSet();
            for (Entry<String, Object> entry : mValues) {
                if(entry.getKey().equalsIgnoreCase("tc")){
                    parameters.add(new BasicNameValuePair(entry.getKey(), "1"));
                } else {
                    parameters.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
                }
                
            }

            Log.i(TAG, "code1content : " + parameters.toString());
            UrlEncodedFormEntity entity;
            try {
                entity = new UrlEncodedFormEntity(parameters);
                Log.d(TAG, "Loading Url complete: " + paymentUrl + "  " + parameters.toString());
                setProxy(paymentUrl);
                webview.postUrl(paymentUrl, EntityUtils.toByteArray(entity));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (JumiaApplication.INSTANCE.getPaymentMethodForm().getContentValues() != null) {
            Set<Entry<String, Object>> mValues = JumiaApplication.INSTANCE.getPaymentMethodForm()
                    .getContentValues().valueSet();
            setProxy(paymentUrl);
            for (Entry<String, Object> entry : mValues) {

                if (!paymentUrl.contains("?")) {
                    paymentUrl += "?" + entry.getKey() + "=" + (String) entry.getValue();
                } else {
                    paymentUrl += "&" + entry.getKey() + "=" + (String) entry.getValue();
                }
            }
            
            webview.loadUrl(paymentUrl);
        } else {
            setProxy(paymentUrl);
            webview.loadUrl(paymentUrl);
        }

        isRequestedPage = true;
    }

    private void setProxy(String url) {
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
//            ProxyConfiguration conf = null;
//            try {
//                conf = ProxySettings.getCurrentProxyConfiguration(getActivity(), new URI(url));
//            } catch (Exception e) {
//                Log.e(TAG, "ProxyConfigurationException:", e);
//            }
//            if (conf != null && conf.getProxyType() != Type.DIRECT) {
//                ProxyUtils.setWebViewProxy(getActivity(), conf);
//            }
//        }
    }

    private void prepareCookieStore() {
        List<Cookie> cookies = RestClientSingleton.getSingleton(getBaseActivity()).getCookieStore()
                .getCookies();
        CookieManager cookieManager = CookieManager.getInstance();
        if (!cookies.isEmpty()) {
            CookieSyncManager.createInstance(getActivity());
            // sync all the cookies in the httpclient with the webview by
            // generating cookie string

            for (Cookie cookie : cookies) {
                String normDomain = prepareCookie(cookie);
                String cookieString = cookie.getName() + "=" + cookie.getValue() + "; Domain="
                        + cookie.getDomain();
                // Log.d( TAG, "prepareCookieStore: adding cookie = " + cookieString);
                cookieManager.setCookie(normDomain, cookieString);
            }
            CookieSyncManager.getInstance().sync();
        }
    }

    private String prepareCookie(Cookie cookie) {
        String transDomain = cookie.getDomain();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (cookie.getDomain().startsWith(".")) {
                transDomain = transDomain.substring(1);
                Log.d(TAG, "prepareCookie: transform domain = " + cookie.getDomain() + " result = "
                        + transDomain);
            } else {
                Log.d(TAG, "prepareCookie: cookie is fine: result = " + transDomain);
            }
        }
        return transDomain;
    }
    
    private void trackPurchase(final JSONObject result) {
        Bundle params = new Bundle();
        params.putString(TrackerDelegator.PURCHASE_KEY, result.toString());
        params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);        
        TrackerDelegator.trackPurchase(params);
    }

    private class CustomWebViewClient extends WebViewClient {

        private static final String SUCCESS_URL_TAG = "checkout/success";
        private static final String JAVASCRIPT_PROCESS = "javascript:window.INTERFACE.processContent"
                +
                "(document.getElementById('jsonAppObject').innerHTML);";
        private boolean wasLoadingErrorPage;

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, final String failingUrl) {
            Log.e(TAG, "Received error: " + errorCode + " " + description + " "
                    + failingUrl);
            failedPageRequest = failingUrl;
            webview.stopLoading();
            webview.clearView();

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.webkit.WebViewClient#onReceivedHttpAuthRequest(android.webkit .WebView,
         * android.webkit.HttpAuthHandler, java.lang.String, java.lang.String)
         */
        @Override
        public void onReceivedHttpAuthRequest(WebView view,
                HttpAuthHandler handler, String host, String realm) {
            Log.i(TAG, "code1payment : onReceivedHttpAuthRequest");
            handler.proceed(RestContract.AUTHENTICATION_USER,
                    RestContract.AUTHENTICATION_PASS);
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
            Log.i(TAG, "code1payment : onPageFinished");
            Log.d(TAG, "onPageFinished: url = " + url);
            if (wasLoadingErrorPage) {
                Log.d(TAG, "onPageFinished: resetting error page inforamtion");
                wasLoadingErrorPage = false;
                failedPageRequest = null;
            } else if (url.equals(failedPageRequest)) {
                Log.d(TAG, "onPageFinished: page was saved failed page");
                wasLoadingErrorPage = true;
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
                Log.d(TAG, "LOAD URL: JAVASCRIPT PROCESS");
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
            Log.i(TAG, "code1payment : onLoadResource");
            try {
                Log.d(TAG, "onLoadResource: url = " + url);
            } catch (OutOfMemoryError e) {
                Log.d(TAG, "onLoadResource: url = OOF");
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
            Log.i(TAG, "code1payment : onPageStarted : " + url);
            Log.d(TAG, "onPageStarted: url = " + url);
            if (url.equals(failedPageRequest)) {
                return;
            }

            showFragmentLoading();

            if (url.contains("checkout/success")) {
                view.getSettings().setBlockNetworkImage(true);
                if (Build.VERSION.SDK_INT >= 8) {
                    view.getSettings().setBlockNetworkLoads(true);
                }
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
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                SslError error) {
            Log.i(TAG, "code1payment : onReceivedSslError : " + error);
            Log.w(TAG, "Received ssl error: " + error);
            if (error.getPrimaryError() == SslError.SSL_IDMISMATCH) {
                Toast.makeText(
                        getActivity(),
                        "The host name does not match the certificate: "
                                + error, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getActivity(),
                        "An SSL error occured: " + error, Toast.LENGTH_LONG)
                        .show();
            }
            handler.proceed();
        }

    }

    private class JavaScriptInterface extends Object {

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void processContent(String content) {
            try {
                Log.d(TAG, "Got checkout response: " + content);
                final JSONObject result = new JSONObject(content);
                if (result.optBoolean("success")) {
                    // Measure to escape the webview thread
                    triggerContentEventWithNoLoading(new GetShoppingCartItemsHelper(), null,
                            mCallback);

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            trackPurchase(result);
                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.SUCESS_INFORMATION, content);
                    /**
                     * TODO: Verify if we need to send customer email
                     */
                    // bundle.putString(ConstantsIntentExtra.CUSTOMER_EMAIL, (customer != null ) ?
                    // customer.getEmail() : "");
                    String order_number = "";
                    String grandTotal = "";
                    if (result.has(RestConstants.JSON_ORDER_NUMBER_TAG)) {
                        order_number = result.optString(RestConstants.JSON_ORDER_NUMBER_TAG);
                    } else if (result.has("orderNr")) {
                        order_number = result.optString("orderNr");
                    }

                    if (result.has(RestConstants.JSON_ORDER_GRAND_TOTAL_TAG)) {
                        grandTotal = result.optString(RestConstants.JSON_ORDER_GRAND_TOTAL_TAG);
                    } else if (result.has("grandTotal")) {
                        grandTotal = result.optString("grandTotal");
                    }

                    bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, order_number);
                    ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CHECKOUT_THANKS,
                            bundle, FragmentController.ADD_TO_BACK_STACK);
                }
            } catch (ParseException e) {
                Log.e(TAG, "parse exception:", e);
            } catch (JSONException e) {
                Log.e(TAG, "json parse exception:", e);
            }
        }
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_CUSTOMER:
            /**
             * TODO: Verify if we need to fill customer
             */
             customer = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
             JumiaApplication.CUSTOMER = customer;
            break;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            break;
        default:
            break;
        }
        return false;
    }

}
