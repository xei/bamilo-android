//package com.bamilo.android.appmodule.modernbamilo.product.descspec.desc
//
//
//import android.os.Bundle
//import android.support.v4.app.Fragment
//import android.support.v7.widget.DefaultItemAnimator
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.bamilo.android.R
//import com.bamilo.android.appmodule.modernbamilo.product.descspec.DescSpecWebApi
//import com.bamilo.android.appmodule.modernbamilo.product.descspec.desc.pojo.DescriptionRow
//import com.bamilo.android.appmodule.modernbamilo.product.descspec.desc.pojo.GetDescriptionResponse
//import com.bamilo.android.appmodule.modernbamilo.product.sellerslist.TAG_DEBUG
//import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
//import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//private const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"
//
///**
// * A simple [Fragment] subclass.
// * Use the [DescriptionFragment.newInstance] factory method to
// * create an instance of this fragment.
// *
// */
//class DescriptionFragment : Fragment() {
//
//    private lateinit var mRecyclerView: RecyclerView
//
//    private lateinit var mWebApi: DescSpecWebApi
//
//    private var mProductId: String? = null
//    private val mDescriptionRows = ArrayList<DescriptionRow>()
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment DescriptionFragment.
//         */
//        @JvmStatic
//        fun newInstance(productId: String) =
//                DescriptionFragment().apply {
//                    arguments = Bundle().apply {
//                        putString(ARG_PRODUCT_ID, productId)
//                    }
//                }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            mProductId = it.getString(ARG_PRODUCT_ID)
//        }
//
//        mWebApi = RetrofitHelper.makeWebApi(context!!, DescSpecWebApi::class.java)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//
//        val rootView = inflater.inflate(R.layout.fragment_description, container, false)
//        mRecyclerView = rootView.findViewById(R.id.fragmentDescription_recyclerView_descriptionRecyclerView)
//
//        loadDescription()
//        initRecyclerView()
//
//        return rootView
//    }
//
//    private fun loadDescription() {
//        val call = mWebApi.getDescription(mProductId!!)
//        call.enqueue(object: Callback<ResponseWrapper<GetDescriptionResponse>> {
//
//            override fun onResponse(call: Call<ResponseWrapper<GetDescriptionResponse>?, response: Response<ResponseWrapper<GetDescriptionResponse>>?) {
//
//                val descRow = response?.body()?.metadata
//                if (descRow != null && descRow.size != 0) {
//                    mDescriptionRows.clear()
//                    mDescriptionRows.removeAll(mDescriptionRows)
//                    mDescriptionRows.addAll(descRow)
//                }
//                initRecyclerView()
//            }
//
//            override fun onFailure(call: Call<ResponseWrapper<GetDescriptionResponse>>?, t: Throwable?) {
//                Log.e(TAG_DEBUG, t?.message)
//            }
//
//        })
//    }
//
//    private fun initRecyclerView() {
//        mRecyclerView.layoutManager = LinearLayoutManager(context)
//        mRecyclerView.itemAnimator = DefaultItemAnimator()
//        mRecyclerView.adapter = DescriptionRecyclerAdapter(mDescriptionRows)
//    }
//
//}
