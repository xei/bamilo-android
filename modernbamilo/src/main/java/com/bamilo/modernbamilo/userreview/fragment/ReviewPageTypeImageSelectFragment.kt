package com.bamilo.modernbamilo.userreview.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.userreview.UserReviewActivity
import com.bamilo.modernbamilo.userreview.optionview.ImageOptionView
import com.bamilo.modernbamilo.userreview.optionview.RadioOptionView
import com.bamilo.modernbamilo.userreview.pojo.getsurvey.Question
import com.bamilo.modernbamilo.util.dpToPx
import com.bamilo.modernbamilo.util.extension.loadImageFromNetwork

private const val TAG_DEBUG = "ReviewPageTypeImage"


/**
 * A simple [Fragment] subclass.
 * Use the [ReviewPageTypeRadioFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReviewPageTypeImageSelectFragment : ReviewPageBaseFragment() {

    private lateinit var mPageTitle: TextView
    private lateinit var mProductImageImageView: ImageView
    private lateinit var mOptionsContainerLinearLayout: LinearLayout

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
                ReviewPageTypeImageSelectFragment().apply {
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

        val rootView =  inflater.inflate(R.layout.fragment_review_page_type_imageselect, container, false)
        mPageTitle = rootView.findViewById(R.id.fragmentReviewPageTypeImageselect_xeiTextView_title)
        mProductImageImageView = rootView.findViewById(R.id.fragmentReviewPageTypeImageselect_imageView_productImage)
        mOptionsContainerLinearLayout = rootView.findViewById(R.id.fragmentReviewPageTypeImageselect_linearLayout_optionsContainer)

        mPageTitle.text = mViewModel.title
        if (mProductImageUrl != null) {
            mProductImageImageView.loadImageFromNetwork(mProductImageUrl!!)
            mProductImageImageView.visibility = View.VISIBLE
        }

        for (i in 0 until mViewModel.options.size) {
            val option = ImageOptionView(context!!)
            val param = LinearLayout.LayoutParams(dpToPx(context!!, 48f), dpToPx(context!!, 48f), 1f)
            param.setMargins(8,8,8,8)
            option.layoutParams = param
            try {
                if (!mViewModel.options[i].image.isEmpty()) {
                    option.loadImageFromNetwork(mViewModel.options[i].image)
                }
            } catch (npe: NullPointerException) {
                Log.e(TAG_DEBUG, npe.message)
            }
            option.setOnClickListener {
                option.select()
                mViewModel.options[i].isSelected = true

                for (j in 0 until mOptionsContainerLinearLayout.childCount) {
                    if (it != mOptionsContainerLinearLayout.getChildAt(j)) {
                        (mOptionsContainerLinearLayout.getChildAt(j) as ImageOptionView).deselect()
                        mViewModel.options[j].isSelected = false
                    }
                }
            }
            mOptionsContainerLinearLayout.addView(option)
        }


        return rootView
    }

}
