package com.bamilo.modernbamilo.product.descspec.spec


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.product.descspec.DescSpecWebApi
import com.bamilo.modernbamilo.product.descspec.spec.pojo.GetSpecificationResponse
import com.bamilo.modernbamilo.product.descspec.spec.pojo.SpecificationRow
import com.bamilo.modernbamilo.util.logging.LogType
import com.bamilo.modernbamilo.util.logging.Logger
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.emilsjolander.stickylistheaders.StickyListHeadersListView

private const val TAG_DEBUG = "SpecificationFragment"
private const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [SpecificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SpecificationFragment : Fragment(), View.OnClickListener {

    private lateinit var mTitleTextView: TextView
    private lateinit var mCloseButton: ImageButton
    private lateinit var mSpecificationTableStickyHeadersListView: StickyListHeadersListView

    private lateinit var mWebApi: DescSpecWebApi

    private var mProductId: String? = null
    private val mSpecificationRows = ArrayList<SpecificationRow>()

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SpecificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(productId: String) =
                SpecificationFragment().apply {
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

        val rootView = inflater.inflate(R.layout.fragment_specification, container, false)
        findViews(rootView)
        mTitleTextView.text = resources.getString(R.string.decSpec_tab_specification)
        setOnClickListeners()
        loadSpecification()
        return rootView
    }

    private fun findViews(rootView: View) {
        mTitleTextView = rootView.findViewById<View>(R.id.activitySellersList_toolbar_toolbar).findViewById(R.id.layoutToolbar_xeiTextView_title)
        mCloseButton = rootView.findViewById<View>(R.id.activitySellersList_toolbar_toolbar).findViewById(R.id.layoutToolbar_imageButton_close)
        mSpecificationTableStickyHeadersListView = rootView.findViewById(R.id.fragmentSpecification_stickyListHeadersListView_specificationTable)
    }

    private fun setOnClickListeners() {
        mCloseButton.setOnClickListener(this)
    }

    private fun loadSpecification() {
        val call = mWebApi.getSpecification(mProductId!!)
        call.enqueue(object: Callback<ResponseWrapper<GetSpecificationResponse>> {
            override fun onResponse(call: Call<ResponseWrapper<GetSpecificationResponse>>?, response: Response<ResponseWrapper<GetSpecificationResponse>>?) {

                response?.body()?.metadata?.specification?.let {
                    if (it.size != 0) {
                        mSpecificationRows.clear()
                        mSpecificationRows.removeAll(mSpecificationRows)
                        mSpecificationRows.addAll(it)
                    }
                }

                initStickyListHeadersListView()
            }

            override fun onFailure(call: Call<ResponseWrapper<GetSpecificationResponse>>?, t: Throwable?) {
                Logger.log(t?.message.toString(), TAG_DEBUG, LogType.ERROR)
            }

        })
    }

    /**
     * https://github.com/emilsjolander/StickyListHeaders
     */
    private fun initStickyListHeadersListView() {
        mSpecificationTableStickyHeadersListView.adapter = SpecificationTableAdapter(mSpecificationRows)
    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.layoutToolbar_imageButton_close -> activity?.onBackPressed()
        }
    }

}
