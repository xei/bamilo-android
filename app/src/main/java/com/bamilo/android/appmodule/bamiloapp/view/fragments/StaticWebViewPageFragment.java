package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentSwitcher;
import com.bamilo.android.appmodule.bamiloapp.helpers.configs.GetStaticPageHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.SSLErrorAlertDialog;
import com.bamilo.android.framework.components.webview.SuperWebView;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import java.util.EnumSet;

/**
 * @author Manuel Silva
 */
public class StaticWebViewPageFragment extends BaseFragmentRequester implements IResponseCallback {

    private static final String TAG = StaticWebViewPageFragment.class.getSimpleName();

    private SuperWebView mSuperWebViewView;
    private Bundle mStaticPageBundle;
    private String mContentHtml;

    /**
     * Empty constructor
     */
    public StaticWebViewPageFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET,
                MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.layout.static_webview_page_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    protected void onCreateInstanceState(@NonNull Bundle bundle) {
        super.onCreateInstanceState(bundle);

        mStaticPageBundle = bundle.getBundle(FragmentSwitcher.DATA);
        if (mStaticPageBundle != null) {
            mContentHtml = mStaticPageBundle.getString(ConstantsIntentExtra.DATA);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ShopSelector.setLocaleOnOrientationChanged(BamiloApplication.INSTANCE);
        // Get title
        mArgTitle = TextUtils.isNotEmpty(mArgTitle) ? mArgTitle : getString(R.string.policy);
        // Title AB
        getBaseActivity().setActionBarTitle(mArgTitle);
        // Title Layout
        ((TextView) view.findViewById(R.id.terms_title)).setText(mArgTitle);
        // Content
        mSuperWebViewView = view.findViewById(R.id.static_webview);
        mSuperWebViewView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                new SSLErrorAlertDialog(getContext())
                        .show(getString(R.string.ssl_error_handler_title),
                                getString(R.string.ssl_error_handler_message),
                                v -> handler.proceed(),
                                v -> handler.cancel());
            }
        });

        if (TextUtils.isNotEmpty(mContentHtml)) {
            mSuperWebViewView.loadData(mContentHtml);
        } else {
            // Get static page
            triggerStaticPage();
        }
    }

    private void triggerStaticPage() {
        triggerContentEvent(new GetStaticPageHelper(), GetStaticPageHelper.createBundle(mArgId),
                this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(ConstantsIntentExtra.FRAGMENT_BUNDLE, mStaticPageBundle);
        outState.putString(ConstantsIntentExtra.CONTENT_TITLE, mArgTitle);
        outState.putString(ConstantsIntentExtra.CONTENT_ID, mArgId);
        outState.putString(ConstantsIntentExtra.DATA, mContentHtml);
    }

    @Override
    public void onDestroyView() {
        mSuperWebViewView.removeAllViews();
        mSuperWebViewView.destroy();

        super.onDestroyView();
    }

    @Override
    protected void onSuccessResponse(BaseResponse response) {
        if (isOnStoppingProcess) {
            return;
        }

        if (getBaseActivity() != null) {
            super.handleSuccessEvent(response);
        } else {
            return;
        }
        showFragmentContentContainer();
        mSuperWebViewView.loadData(TextUtils.stripHtml(mContentHtml));
    }

    @Override
    protected void onErrorResponse(BaseResponse response) {
        if (isOnStoppingProcess) {
            return;
        }
        if (!super.handleErrorEvent(response)) {
            showContinueShopping();
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        triggerStaticPage();
    }
}