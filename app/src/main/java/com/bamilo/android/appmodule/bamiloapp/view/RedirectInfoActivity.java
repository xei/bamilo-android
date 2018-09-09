package com.bamilo.android.appmodule.bamiloapp.view;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.framework.components.webview.SuperWebView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.ActivitiesWorkFlow;
import com.bamilo.android.framework.service.objects.configs.RedirectPage;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.rest.RestUrlUtils;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;

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
            //super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    };
}
