/**
 * 
 */
package pt.rocket.view.fragments;

import java.net.Proxy.Type;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.forms.PaymentMethodForm;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetCustomerHelper;
import pt.rocket.helpers.GetShoppingCartItemsHelper;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.cookie.Cookie;

import com.shouldit.proxy.lib.ProxyConfiguration;
import com.shouldit.proxy.lib.ProxySettings;
import com.shouldit.proxy.lib.ProxyUtils;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutWebFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(CheckoutWebFragment.class);

    private static final String CHECKOUT_URL_WITH_PARAM = "/checkout/multistep/?setDevice=mobileApi&iosApp=1";

//    private FrameLayout mWebContainer;
    private WebView webview;
    
    private String checkoutUrl;

    private String failedPageRequest;
    private boolean isRequestedPage;

    private Customer customer;
            
    private Handler handler = new Handler();
    
    private static CheckoutWebFragment checkoutWebFragment;

    /**
     * Get instance
     * 
     * @return
     */
    public static CheckoutWebFragment getInstance() {
        if (checkoutWebFragment == null)
            checkoutWebFragment = new CheckoutWebFragment();
        
        checkoutWebFragment.webview = null;
        checkoutWebFragment.checkoutUrl = null;
        checkoutWebFragment.failedPageRequest = null;
        checkoutWebFragment.customer = null;
        return checkoutWebFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutWebFragment() {
        super(EnumSet.of(EventType.GET_SHOPPING_CART_ITEMS_EVENT, EventType.GET_CUSTOMER),
        EnumSet.noneOf(EventType.class),EnumSet.noneOf(MyMenuItem.class),NavigationAction.Unknown, 0);
        this.setRetainInstance(true);
    }
    
    @Override
    public boolean allowBackPressed() {
        Log.d( TAG, "onBackPressed: webview.canGoBackup = " + webview.canGoBack() + " webview.hasFocus() = " + webview.hasFocus());
        if ( webview != null && webview.canGoBack() && webview.hasFocus()) {
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
        triggerGetShoppingCartItems();
    }
    
    private void triggerGetCustomer(){
        
        triggerContentEventWithNoLoading(new GetCustomerHelper(), null, mCallback);
    }
    
    private void triggerGetShoppingCartItems(){
        
        triggerContentEventWithNoLoading(new GetShoppingCartItemsHelper(), null, mCallback);
//        EventManager.getSingleton().triggerRequestEvent(GetShoppingCartItemsEvent.FORCE_API_CALL);
    }
    
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

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.checkoutweb, container, false);
        
        webview = (WebView) view.findViewById(R.id.webview);
//        webview = new WebView(getActivity());
//        mWebContainer.addView(webview);
        
        return view;    
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
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
            webview.loadUrl("about:blank");    
        }
        

        // Needed for 2.3 problem with not showing keyboard by tapping in webview
        webview.requestFocus();
//        webview.setHttpAuthUsernamePassword("https://" + RestContract.REQUEST_HOST, "", "rocket", "rock4me");
        prepareCookieStore();
        setupWebView();
        // XXX
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
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
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
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
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
        if(webview != null) {
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
    
//    @SuppressLint("SetJavaScriptEnabled")
//    private void setupWebView() {
//       
//        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
//        webview.setWebViewClient(customWebViewClient);
//        webview.getSettings().setJavaScriptEnabled(true);
//        webview.getSettings().setSaveFormData(false);
//        webview.getSettings().setSavePassword(false);
//        webview.addJavascriptInterface(new JavaScriptInterface(), "INTERFACE");
//        checkoutUrl = "https://" + RestContract.REQUEST_HOST + CHECKOUT_URL_WITH_PARAM;
//        Log.d(TAG, "Loading Url: " + checkoutUrl);
//        isRequestedPage = true;
//        webview.loadUrl(checkoutUrl);
//    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        //webview = (WebView) findViewById(R.id.webview);
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        webview.setWebViewClient(customWebViewClient);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSaveFormData(false);
        webview.getSettings().setSavePassword(false);
        webview.addJavascriptInterface(new JavaScriptInterface(), "INTERFACE");
    }
    
    private void startCheckout() {
        ((BaseActivity) getActivity()).showLoading(true);
        webview.clearView();
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
            webview.loadUrl("about:blank");
        }
        checkoutUrl = "https://" + RestContract.REQUEST_HOST + CHECKOUT_URL_WITH_PARAM;
        setProxy( checkoutUrl );
        Log.d(TAG, "Loading Url: " + checkoutUrl);
        webview.loadUrl(checkoutUrl);
        isRequestedPage = true;
    }
    
    private void setProxy( String url ) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            ProxyConfiguration conf = null;
            try {
                conf = ProxySettings.getCurrentProxyConfiguration(getActivity(), new URI(url));
            } catch (Exception e) {
                Log.e( TAG, "ProxyConfigurationException:", e);
            }
            if ( conf != null && conf.getProxyType() != Type.DIRECT) {
                ProxyUtils.setWebViewProxy(getActivity(), conf);
            }
        }
    }
    
    private void prepareCookieStore() {
        List<Cookie> cookies = RestClientSingleton.getSingleton(getBaseActivity()).getCookieStore().getCookies();
        CookieManager cookieManager = CookieManager.getInstance();
        if (!cookies.isEmpty()) {
            CookieSyncManager.createInstance(getActivity());
            // sync all the cookies in the httpclient with the webview by
            // generating cookie string
            
            for (Cookie cookie : cookies) {
                String normDomain = prepareCookie( cookie );
                String cookieString = cookie.getName() + "=" + cookie.getValue() + "; Domain=" + cookie.getDomain();
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
    
    private void trackPurchase( final JSONObject result ) {
       TrackerDelegator.trackPurchase(getActivity().getApplicationContext(), result, customer);
    }
    
    
    private class CustomWebViewClient extends WebViewClient {

        private static final String SUCCESS_URL_TAG = "checkout/success";
        private static final String JAVASCRIPT_PROCESS = "javascript:window.INTERFACE.processContent" + 
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
//            mCallbackCheckoutWebFragment.sendClickListenerToActivity(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d( TAG, "showError: onClick:");
//                    ((BaseActivity) getActivity()).showLoading();
//                    isRequestedPage = true;
//                    webview.requestFocus();
//                    webview.loadUrl( failingUrl );
//                }
//            });
           
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
            Log.d(TAG, "onPageFinished: url = " + url );
            if ( wasLoadingErrorPage ) {
                Log.d( TAG, "onPageFinished: resetting error page inforamtion");
                wasLoadingErrorPage = false;
                failedPageRequest = null;
            } else if ( url.equals( failedPageRequest )) {
                Log.d( TAG ,"onPageFinished: page was saved failed page" );
                wasLoadingErrorPage = true;
            } else if ( isRequestedPage ) {
                if(getActivity() != null)
                    ((BaseActivity) getActivity()).showContentContainer();
                isRequestedPage = false;
            } else if (getActivity() != null) {
                ((BaseActivity) getActivity()).showContentContainer();
            }
            
            if (url.contains(SUCCESS_URL_TAG)) {
            	/**
            	 * This line causes a JNI exception only in the emulators 2.3.X.
            	 * @see http://code.google.com/p/android/issues/detail?id=12987
            	 */
            	Log.d(TAG, "LOAD URL: JAVASCRIPT PROCESS");
                view.loadUrl(JAVASCRIPT_PROCESS);  
                //view.loadUrl(JAVASCRIPT_PRINT);
            }            
        }
        
        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#onLoadResource(android.webkit.WebView, java.lang.String)
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            try {
                Log.d(TAG, "onLoadResource: url = " + url);
            } catch (OutOfMemoryError e) {
                Log.d(TAG, "onLoadResource: url = OOF");
                e.printStackTrace();
            }
           
        }
        
        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#onPageStarted(android.webkit.WebView, java.lang.String, android.graphics.Bitmap)
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "onPageStarted: url = " + url);
            if ( url.equals(failedPageRequest)) {
                return;
            }
            
            if (getActivity() != null)
                ((BaseActivity) getActivity()).showLoading(true);
            
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
            Log.w(TAG, "Received ssl error: " + error);
            if (error.getPrimaryError() == SslError.SSL_IDMISMATCH) {
                Toast.makeText(
                        getActivity(),
                        "The host name does not match the certificate: "
                                + error, Toast.LENGTH_LONG).show();
                handler.proceed();
            } else {
                Toast.makeText(getActivity(),
                        "An SSL error occured: " + error, Toast.LENGTH_LONG)
                        .show();
            }
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
                    triggerContentEventWithNoLoading(new GetShoppingCartItemsHelper(), null, mCallback);
                    
                    handler.post( new Runnable() {
                        
                        @Override
                        public void run() {
                            trackPurchase(result);                            
                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.SUCESS_INFORMATION, content);
                    bundle.putString(ConstantsIntentExtra.CUSTOMER_EMAIL, (customer != null ) ? customer.getEmail() : "");
                    JumiaApplication.INSTANCE.setPaymentMethodForm(new PaymentMethodForm());
                    
					String order_number = result.optString("orderNr");
                    String grandTotal = result.optString("grandTotal");
                    JumiaApplication.INSTANCE.getPaymentMethodForm().setOrderNumber(order_number);
                    JumiaApplication.INSTANCE.getPaymentMethodForm().setCustomerFirstName((customer != null ) ? customer.getFirstName() : "");
                    JumiaApplication.INSTANCE.getPaymentMethodForm().setCustomerFirstName((customer != null ) ? customer.getLastName() : "");
					bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, order_number);                   
					((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK);
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
            customer = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            break;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            break;
        }
        return false;
    }
    
}
