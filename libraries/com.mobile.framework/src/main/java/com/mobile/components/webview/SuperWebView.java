package com.mobile.components.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

/**
 * Class used to represent a web view
 * @author spereira
 */
public class SuperWebView extends WebView {

    public SuperWebView(Context context) {
        super(context);
    }

    public SuperWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SuperWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SuperWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    /**
     * Tells the WebView to enable JavaScript execution. <b>The default is false.</b><br>
     * <b>WARNING: Using setJavaScriptEnabled can introduce XSS vulnerabilities into you application, review carefully.</b><br>
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void enableJavaScript() {
        /*
         * Crash: In some devices with OS 4.2.2<br>
         *  - https://rink.hockeyapp.net/manage/apps/33641/app_versions/173/crash_reasons/116945743?type=overview
         */
        try {
            // Enable java script
            getSettings().setJavaScriptEnabled(true);
        } catch (NullPointerException e) {
            Print.w("WARNING: NPE ON ENABLE JAVA SCRIPT");
        }
    }

    /**
     * Load html data.
     */
    public void loadData(@Nullable String html) {
        if (TextUtils.isNotEmpty(html)) {
            loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        } else {
            loadUrl(html);
        }
    }
}
