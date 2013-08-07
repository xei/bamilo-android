package pt.rocket.view;

import java.net.Proxy.Type;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;

import org.apache.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetShoppingCartItemsEvent;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.rest.RestContract;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.fragments.FragmentType;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
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
 * 
 * @author Ralph Holland-Moritz
 * 
 */
public class CheckoutWebActivity extends BaseActivity {

    private static final String TAG = LogTagHelper.create(CheckoutWebActivity.class);

    private static final String CHECKOUT_URL_WITH_PARAM = "/checkout/multistep/?setDevice=mobileApi&iosApp=1";

    private WebView webview;
    
    private String checkoutUrl;

    private String failedPageRequest;
    private boolean isRequestedPage;

    private Customer customer;
            
    private Handler handler = new Handler();
    

    public CheckoutWebActivity() {
        super(NavigationAction.Basket,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_SHOPPING_CART_ITEMS_EVENT,
                        EventType.GET_CUSTOMER),
                EnumSet.noneOf(EventType.class),
                0,
                R.layout.checkoutweb);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWebView();
        init();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init();
    }
    

    private void init() {
        prepareCookieStore();
        EventManager.getSingleton().triggerRequestEvent(new RequestEvent(EventType.GET_CUSTOMER));
        EventManager.getSingleton().triggerRequestEvent(GetShoppingCartItemsEvent.FORCE_API_CALL);
        startCheckout();
    }

    @Override
    public void onStart() {
        super.onStart();
        
    }

    @Override
    public void onResume() {
        super.onResume();
        // Needed for 2.3 problem with not showing keyboard by tapping in webview
        webview.requestFocus();
        AnalyticsGoogle.get().trackPage(R.string.gcheckoutbegin);
    }

    @Override
    public void onStop() {
        super.onStop();
        
    }
    
    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webview = (WebView) findViewById(R.id.webview);
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
        webview.setWebViewClient(customWebViewClient);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSaveFormData(false);
        webview.getSettings().setSavePassword(false);
        webview.addJavascriptInterface(new JavaScriptInterface(), "INTERFACE");
        

    }
    
    private void startCheckout() {
        showLoading();
        webview.clearView();
        checkoutUrl = "https://" + RestContract.REQUEST_HOST + CHECKOUT_URL_WITH_PARAM;
        setProxy( checkoutUrl );
        Log.d(TAG, "Loading Url: " + checkoutUrl);
        android.util.Log.e("#################", ":"+checkoutUrl);
        webview.loadUrl(checkoutUrl);
        isRequestedPage = true;
    }
    
    private void setProxy( String url ) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            ProxyConfiguration conf = null;
            try {
                conf = ProxySettings.getCurrentProxyConfiguration(getApplicationContext(), new URI(url));
            } catch (Exception e) {
                Log.e( TAG, "ProxyConfigurationException:", e);
            }
            if ( conf != null && conf.getProxyType() != Type.DIRECT) {
                ProxyUtils.setWebViewProxy(getApplicationContext(), conf);
            }
        }
    }
    
    private void prepareCookieStore() {
        List<Cookie> cookies = RestClientSingleton.getSingleton()
                .getCookieStore().getCookies();
        CookieManager cookieManager = CookieManager.getInstance();
        if (!cookies.isEmpty()) {
            CookieSyncManager.createInstance(this);
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
       TrackerDelegator.trackPurchase(getApplicationContext(), result, customer);
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
            showError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d( TAG, "showError: onClick:");
                    showLoading();
                    isRequestedPage = true;
                    webview.requestFocus();
                    webview.loadUrl( failingUrl );
                }
            });
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
                showContentContainer();
                isRequestedPage = false;
            } else {
                showContentContainer();
            }
            
            if (url.contains(SUCCESS_URL_TAG)) {
                view.loadUrl(JAVASCRIPT_PROCESS);
            }            
        }
        
        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#onLoadResource(android.webkit.WebView, java.lang.String)
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.d(TAG, "onLoadResource: url = " + url);
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
            
            showLoading();
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
                        CheckoutWebActivity.this,
                        "The host name does not match the certificate: "
                                + error, Toast.LENGTH_LONG).show();
                handler.proceed();
            } else {
                Toast.makeText(CheckoutWebActivity.this,
                        "An SSL error occured: " + error, Toast.LENGTH_LONG)
                        .show();
            }
        }

    }
    
    private class JavaScriptInterface extends Object {
        @SuppressWarnings("unused")
        public void processContent(String content) {
            try {
                Log.d(TAG, "Got checkout response: " + content);
                final JSONObject result = new JSONObject(content);
                if (result.optBoolean("success")) {
                    // Measure to escape the webview thread
                     EventManager.getSingleton().triggerRequestEvent(
                             GetShoppingCartItemsEvent.FORCE_API_CALL);
                    handler.post( new Runnable() {
                        
                        @Override
                        public void run() {
                            trackPurchase(result);                            
                        }
                    });
                    ActivitiesWorkFlow.checkoutActivity(
                            CheckoutWebActivity.this, ConstantsCheckout.CHECKOUT_THANKS);
                }
            } catch (ParseException e) {
                Log.e(TAG, "parse exception:", e);
            } catch (JSONException e) {
                Log.e(TAG, "json parse exception:", e);
            }
            finish();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.type) {
        case GET_CUSTOMER:
            customer = (Customer) event.result;
            break;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            if (((ShoppingCart) event.result).getCartCount() == 0) {
                Toast.makeText(this, getString(R.string.shoppingcart_alert_message_no_items),
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            break;
        }
        return false;
    }
    
    public void onBackPressed() {
        Log.d( TAG, "onBackPressed: webview.canGoBackup = " + webview.canGoBack() + " webview.hasFocus() = " + webview.hasFocus());
        if (webview.canGoBack() && webview.hasFocus()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

}
