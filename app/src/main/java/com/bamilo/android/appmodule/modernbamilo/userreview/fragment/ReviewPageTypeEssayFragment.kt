package com.bamilo.android.appmodule.modernbamilo.userreview.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.userreview.UserReviewActivity
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey.Question
import com.bamilo.android.appmodule.modernbamilo.util.extension.loadImageFromNetwork
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_review_page_type_essay.*


/**
 * A simple [Fragment] subclass.
 * Use the [ReviewPageTypeRadioFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReviewPageTypeEssayFragment : ReviewPageBaseFragment() {

    private lateinit var mPageTitle: TextView
    private lateinit var mProductImageImageView: ImageView
    private lateinit var mUserInputTextEditText: EditText

    private lateinit var mViewModel: Question
    private var mFragmentIndex = -1
    private var mProductImageUrl: String? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment ReviewPageTypeRadioFragment.
         */
        @JvmStatic
        fun newInstance(fragmentIndex: Int, productImageUrl: String?) =
                ReviewPageTypeEssayFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM_FRAGMENT_INDEX, fragmentIndex)
                        putString(ARG_PARAM_PRODUCT_IMAGE_URL, productImageUrl)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mFragmentIndex = it.getInt(ARG_PARAM_FRAGMENT_INDEX)
            mProductImageUrl = it.getString(ARG_PARAM_PRODUCT_IMAGE_URL)
        }

        mViewModel = (activity as UserReviewActivity)
                .getSurvey().pages[0].questions[mFragmentIndex]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView =  inflater.inflate(R.layout.fragment_review_page_type_essay, container, false)
        mPageTitle = rootView.findViewById(R.id.fragmentReviewPageTypeEssay_xeiTextView_title)
        mProductImageImageView = rootView.findViewById(R.id.fragmentReviewPageTypeEssay_imageView_productImage)
        mUserInputTextEditText = rootView.findViewById(R.id.fragmentReviewPageTypeEssay_xeiEditText_userInputText)


        mPageTitle.text = mViewModel.title
        if (mProductImageUrl != null) {
            mProductImageImageView.loadImageFromNetwork(mProductImageUrl!!)
            mProductImageImageView.visibility = View.VISIBLE
        }

        return rootView
    }

    fun storeUserInputText() {
        mViewModel.userInputText = mUserInputTextEditText.text.toString()
    }

}
