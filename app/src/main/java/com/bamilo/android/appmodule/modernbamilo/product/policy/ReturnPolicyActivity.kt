package com.bamilo.android.appmodule.modernbamilo.product.policy

import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.SSLErrorAlertDialog
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val KEY_EXTRA_RETURN_POLICY_ID = "KEY_EXTRA_RETURN_POLICY_ID"
private const val KEY_EXTRA_RETURN_POLICY_TITLE = "KEY_EXTRA_RETURN_POLICY_TITLE"

class ReturnPolicyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mTitleTextView: TextView
    private lateinit var mCloseButton: ImageButton
    private lateinit var mCartImageView: AppCompatImageView
    private lateinit var mContentWebView: WebView

    private lateinit var mPolicyId: String
    private lateinit var mPolicyTitle: String

    private lateinit var mWebApi: ReturnPolicyWebApi

    companion object {
        fun startReturnPolicyActivity(invokerContext: Context, returnPolicyId: String, returnPolicyTitle: String) {
            val intent = Intent(invokerContext, ReturnPolicyActivity::class.java)
            intent.putExtra(KEY_EXTRA_RETURN_POLICY_ID, returnPolicyId)
            intent.putExtra(KEY_EXTRA_RETURN_POLICY_TITLE, returnPolicyTitle)
            invokerContext.startActivity(intent)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_policy)

        mPolicyId = intent.getStringExtra(KEY_EXTRA_RETURN_POLICY_ID)
        mPolicyTitle = intent.getStringExtra(KEY_EXTRA_RETURN_POLICY_TITLE)
        mWebApi = RetrofitHelper.makeWebApi(this, ReturnPolicyWebApi::class.java)

        findViews()
        mTitleTextView.text = mPolicyTitle
        setOnClickListeners()
        initWebView()
        loadContent()
    }

    private fun findViews() {
        mTitleTextView = findViewById<View>(R.id.activityReturnPolicy_toolbar_toolbar).findViewById(R.id.layoutToolbar_xeiTextView_title)
        mCloseButton = findViewById<View>(R.id.activityReturnPolicy_toolbar_toolbar).findViewById(R.id.layoutToolbar_imageButton_close)
        mCartImageView = findViewById(R.id.layoutToolbar_appCompatImageView_whiteCart)
        mCartImageView.visibility = View.GONE
        mContentWebView = findViewById(R.id.activityReturnPolicy_webView_contentWebView)
        mContentWebView.loadData(mPolicyId,"text/html; charset=UTF-8", null)
    }

    private fun setOnClickListeners() {
        mCloseButton.setOnClickListener(this)
    }

    private var webViewClient: WebViewClient = object : WebViewClient() {
        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            SSLErrorAlertDialog(this@ReturnPolicyActivity)
                    .show(getString(R.string.ssl_error_handler_title), getString(R.string.ssl_error_handler_message), View.OnClickListener { handler.proceed() },
                            View.OnClickListener { handler.cancel() })
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return true
        }

    }

    private fun initWebView() {
        mContentWebView.settings.run {
            loadWithOverviewMode = true
//            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        mContentWebView.webViewClient = webViewClient
    }

    private fun loadContent() {
        val call = mWebApi.getContent(mPolicyId)
        call.enqueue(object : Callback<ResponseWrapper<GetReturnPolicyResponse>> {

            override fun onResponse(call: Call<ResponseWrapper<GetReturnPolicyResponse>>?, response: Response<ResponseWrapper<GetReturnPolicyResponse>>?) {
                response?.body()?.metadata?.returnPolicy.let {

                    // Invalid characters was the cause of style (font) problem.
                    val formattedString = it
                            ?.replace("\u201c", "\"")
                            ?.replace("\u201d", "\"")

                    mContentWebView.loadData("\u200f$formattedString", "text/html; charset=UTF-8", null)
                }
            }

            override fun onFailure(call: Call<ResponseWrapper<GetReturnPolicyResponse>>?, t: Throwable?) {
//                Logger.log(t?.message.toString(), TAG_DEBUG, LogType.ERROR)
            }

        })
    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.layoutToolbar_imageButton_close -> finish()
//            R.id.layoutToolbar_appCompatImageView_whiteCart -> openCart()
        }
    }

}
