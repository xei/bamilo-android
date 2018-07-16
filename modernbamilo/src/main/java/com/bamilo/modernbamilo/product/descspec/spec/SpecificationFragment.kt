package com.bamilo.modernbamilo.product.descspec.spec


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.product.descspec.DescSpecWebApi
import com.bamilo.modernbamilo.product.descspec.desc.pojo.DescriptionRow
import com.bamilo.modernbamilo.product.descspec.spec.pojo.SpecificationRow
import com.bamilo.modernbamilo.product.descspec.spec.pojo.SpecificationTuple
import com.bamilo.modernbamilo.product.sellerslist.TAG_DEBUG
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.emilsjolander.stickylistheaders.StickyListHeadersListView

private const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [SpecificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SpecificationFragment : Fragment() {

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
        loadSpecification()
        return rootView
    }

    private fun findViews(rootView: View) {
        mSpecificationTableStickyHeadersListView = rootView.findViewById(R.id.fragmentSpecification_stickyListHeadersListView_specificationTable)
    }

    private fun loadSpecification() {
        val call = mWebApi.getSpecification(mProductId!!)
        call.enqueue(object: Callback<ResponseWrapper<ArrayList<SpecificationRow>>> {
            override fun onResponse(call: Call<ResponseWrapper<ArrayList<SpecificationRow>>>?, response: Response<ResponseWrapper<ArrayList<SpecificationRow>>>?) {

                val descRows = response?.body()?.metadata
                if (descRows != null && descRows.size != 0) {
                    mSpecificationRows.clear()
                    mSpecificationRows.removeAll(mSpecificationRows)
                    mSpecificationRows.addAll(descRows)
                }

                initStickyListHeadersListView()
            }

            override fun onFailure(call: Call<ResponseWrapper<ArrayList<SpecificationRow>>>?, t: Throwable?) {
                Log.e(TAG_DEBUG, t?.message)
            }

        })
    }

    /**
     * https://github.com/emilsjolander/StickyListHeaders
     */
    private fun initStickyListHeadersListView() {
        mSpecificationTableStickyHeadersListView.adapter = SpecificationTableAdapter(mSpecificationRows)
    }

}
