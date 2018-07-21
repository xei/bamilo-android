package com.bamilo.modernbamilo.product.descspec.tempdesc


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.product.descspec.DescSpecWebApi
import com.bamilo.modernbamilo.product.descspec.desc.pojo.GetDescriptionResponse
import com.bamilo.modernbamilo.util.logging.LogType
import com.bamilo.modernbamilo.util.logging.Logger
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
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
class TemporaryDescriptionFragment : Fragment() {

    private lateinit var mWebView: WebView

    private lateinit var mWebApi: DescSpecWebApi

    private var mProductId: String? = null

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
        mWebView = rootView.findViewById(R.id.fragmentDescription_webView_descriptionWebView)

        loadDescription()

        return rootView
    }

    private fun loadDescription() {
        val call = mWebApi.getDescription(mProductId!!)
        call.enqueue(object: Callback<ResponseWrapper<GetDescriptionResponse>> {

            override fun onResponse(call: Call<ResponseWrapper<GetDescriptionResponse>>?, response: Response<ResponseWrapper<GetDescriptionResponse>>?) {
                response?.body()?.metadata?.description.let {
                    mWebView.loadData("\u200f$it", "text/html", "UTF-8")
                }
            }

            override fun onFailure(call: Call<ResponseWrapper<GetDescriptionResponse>>?, t: Throwable?) {
                Logger.log(t?.message.toString(), TAG_DEBUG, LogType.ERROR)
            }

        })
    }

}
