package com.mobile.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mobile.components.webview.SuperWebView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.service.objects.configs.RedirectPage;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.rest.RestUrlUtils;

public class RedirectInfoActivity extends AppCompatActivity {

    private RedirectPage redirect = new RedirectPage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set layout
        setContentView(R.layout._def_redirect_info_page);
        // Get data
        if (getIntent().hasExtra(ConstantsIntentExtra.DATA)) {
            redirect = getIntent().getExtras().getParcelable(ConstantsIntentExtra.DATA);
        }
        // Set html info
        SuperWebView webView = (SuperWebView) findViewById(R.id.redirect_info_web);
        // Enable java script
        webView.enableJavaScript();
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

}
