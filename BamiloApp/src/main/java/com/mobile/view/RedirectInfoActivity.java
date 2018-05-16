package com.mobile.view;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mobile.app.BamiloApplication;
import com.mobile.components.webview.SuperWebView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.service.objects.configs.RedirectPage;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.rest.RestUrlUtils;
import com.mobile.service.utils.shop.ShopSelector;

public class RedirectInfoActivity extends AppCompatActivity {

    private RedirectPage redirect = new RedirectPage();
    private SuperWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopSelector.setLocaleOnOrientationChanged(BamiloApplication.INSTANCE);
        // Set layout
        setContentView(R.layout._def_redirect_info_page);
        // Get data
        if (getIntent().hasExtra(ConstantsIntentExtra.DATA)) {
            redirect = getIntent().getExtras().getParcelable(ConstantsIntentExtra.DATA);
        }
        // Set html info
        webView = findViewById(R.id.redirect_info_web);
        // Enable java script
        webView.enableJavaScript();
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.clearCache(false);
        webView.setWebViewClient(webViewClient);
        // Load html
        webView.loadData(redirect.getHtml());
        // Set button link
        findViewById(R.id.redirect_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitiesWorkFlow.startMarketActivity(RedirectInfoActivity.this, RestUrlUtils.getQueryValue(redirect.getLink(), RestConstants.ID), redirect.getLink());
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
//            handler.proceed();
        }
    };
}
