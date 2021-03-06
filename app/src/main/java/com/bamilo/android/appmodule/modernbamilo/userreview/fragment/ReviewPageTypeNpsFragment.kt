package com.bamilo.android.appmodule.modernbamilo.userreview.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.userreview.UserReviewActivity
import com.bamilo.android.appmodule.modernbamilo.userreview.optionview.NpsNumberPicker
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey.Question
import com.bamilo.android.appmodule.modernbamilo.util.extension.loadImageFromNetwork

private const val TAG_DEBUG = "ReviewPageTypeNps"


/**
 * A simple [Fragment] subclass.
 * Use the [ReviewPageTypeNpsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReviewPageTypeNpsFragment : ReviewPageBaseFragment() {

    private lateinit var mPageTitleTextView: TextView
    private lateinit var mProductImageImageView: AppCompatImageView
    private lateinit var mOptionsContainerLinearLayout: LinearLayout
    private lateinit var mNpsOptionsNumberPicker: NpsNumberPicker
    private lateinit var mNpsOptionImageImageView: ImageView

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
                ReviewPageTypeNpsFragment().apply {
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

        val rootView =  inflater.inflate(R.layout.fragment_review_page_type_nps, container, false)
        mPageTitleTextView = rootView.findViewById(R.id.fragmentReviewPageTypeNps_xeiTextView_title)
        mProductImageImageView = rootView.findViewById(R.id.fragmentReviewPageTypeNps_imageView_productImage)
        mOptionsContainerLinearLayout = rootView.findViewById(R.id.fragmentReviewPageTypeNps_linearLayout_optionsContainer)
        mNpsOptionsNumberPicker = rootView.findViewById(R.id.fragmentReview_PageTypeNps_NpsNumberPicker_npsOptions)
        mNpsOptionImageImageView = rootView.findViewById(R.id.fragmentReviewPageTypeNps_imageView_NpsImage)


        mPageTitleTextView.text = mViewModel.title
        if (mProductImageUrl != null) {
            mProductImageImageView.loadImageFromNetwork(mProductImageUrl!!)
            mProductImageImageView.visibility = View.VISIBLE
        }

        mNpsOptionsNumberPicker.setOnNpsOptionChangeListener(object: NpsNumberPicker.OnNpsOptionChangeListener {
            override fun changeImage(imageUrl: String?) {
                try {
                    mNpsOptionImageImageView.loadImageFromNetwork(imageUrl!!)
                } catch (npe: NullPointerException) {
                    Log.e(TAG_DEBUG, "image url is null")
                }

            }

        })

        mNpsOptionsNumberPicker.setNpsOptions(mViewModel.options)

        return rootView
    }

}
