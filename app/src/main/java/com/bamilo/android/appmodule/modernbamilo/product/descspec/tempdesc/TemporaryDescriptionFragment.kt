package com.bamilo.android.appmodule.modernbamilo.product.descspec.tempdesc


import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType
import com.bamilo.android.appmodule.bamiloapp.view.MainFragmentActivity
import com.bamilo.android.appmodule.modernbamilo.product.descspec.DescSpecWebApi
import com.bamilo.android.appmodule.modernbamilo.product.descspec.desc.pojo.GetDescriptionResponse
import com.bamilo.android.appmodule.modernbamilo.util.logging.LogType
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG_DEBUG = "TAG_DEBUG"
private const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [DescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TemporaryDescriptionFragment : Fragment(), View.OnClickListener {

    private lateinit var mTitleTextView: TextView
    private lateinit var mCartBadgeTextView: TextView
    private lateinit var mCloseButton: ImageButton
    private lateinit var mCartButton: AppCompatImageView
    private lateinit var mWebView: WebView

    private lateinit var mWebApi: DescSpecWebApi

    private var mProductId: String? = null

    private var mDefaultCartItemCount = 0

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DescriptionFragment.
         */
        @JvmStatic
        fun newInstance(productId: String) =
                TemporaryDescriptionFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PRODUCT_ID, productId)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mProductId = it.getString(ARG_PRODUCT_ID)
        }

        mWebApi = RetrofitHelper.makeWebApi(context!!, DescSpecWebApi::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_description_temp, container, false)

        findViews(rootView)

        if (mDefaultCartItemCount > 0) {
            mCartBadgeTextView.text = mDefaultCartItemCount.toString()
            mCartBadgeTextView.visibility = View.VISIBLE
        }

        mTitleTextView.text = resources.getString(R.string.decSpec_tab_description)
        initWebView()
        setOnClickListeners()
        loadDescription()

        return rootView
    }

    private fun findViews(rootView: View) {
        mTitleTextView = rootView.findViewById<View>(R.id.activitySellersList_toolbar_toolbar).findViewById(R.id.layoutToolbar_xeiTextView_title)
        mCartBadgeTextView = rootView.findViewById<View>(R.id.activitySellersList_toolbar_toolbar).findViewById(R.id.layoutToolbar_xeiTextView_cartBadge)
        mCloseButton = rootView.findViewById<View>(R.id.activitySellersList_toolbar_toolbar).findViewById(R.id.layoutToolbar_imageButton_close)
        mCartButton = rootView.findViewById(R.id.layoutToolbar_appCompatImageView_whiteCart)
        mWebView = rootView.findViewById(R.id.fragmentDescription_webView_descriptionWebView)
    }

    private fun setOnClickListeners() {
        mCloseButton.setOnClickListener(this)
        mCartButton.setOnClickListener(this)
    }

    private fun initWebView() {
        mWebView.settings.run {
            loadWithOverviewMode = true
//            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        mWebView.webViewClient = webViewClient
    }

    private fun loadDescription() {
        val call = mWebApi.getDescription(mProductId!!)
        call.enqueue(object : Callback<ResponseWrapper<GetDescriptionResponse>> {

            override fun onResponse(call: Call<ResponseWrapper<GetDescriptionResponse>>?, response: Response<ResponseWrapper<GetDescriptionResponse>>?) {
                response?.body()?.metadata?.description.let {

                    // Invalid characters was the cause of style (font) problem.
                    val formattedString = it
                            ?.replace("\u201c", "\"")
                            ?.replace("\u201d", "\"")

                    mWebView.loadData("\u200f$formattedString", "text/html", "UTF-8")
                }
            }

            override fun onFailure(call: Call<ResponseWrapper<GetDescriptionResponse>>?, t: Throwable?) {
                Logger.log(t?.message.toString(), TAG_DEBUG, LogType.ERROR)
            }

        })
    }

    private fun openCart() = startActivity(
            Intent(context, MainFragmentActivity::class.java).apply {
                putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.SHOPPING_CART)
                putExtra(ConstantsIntentExtra.FRAGMENT_INITIAL_COUNTRY, false)
            })

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.layoutToolbar_imageButton_close -> activity?.onBackPressed()
            R.id.layoutToolbar_appCompatImageView_whiteCart -> openCart()
        }
    }

    fun updateCartBadge(cartItemsCount: Int?) {
        cartItemsCount?.let {
            try {
                if (cartItemsCount > 0) {
                    mCartBadgeTextView.text = cartItemsCount.toString()
                    mCartBadgeTextView.visibility = View.VISIBLE
                } else {
                    mCartBadgeTextView.visibility = View.GONE
                }
            } catch (e: Exception) {
                mDefaultCartItemCount = cartItemsCount
            }
        }
    }

    private var webViewClient: WebViewClient = object : WebViewClient() {
        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed()
        }
    }
}
