package com.bamilo.modernbamilo.userreview.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.userreview.UserReviewActivity
import com.bamilo.modernbamilo.userreview.optionview.RadioOptionView
import com.bamilo.modernbamilo.userreview.pojo.getsurvey.Question
import com.bamilo.modernbamilo.util.extension.loadImageFromNetwork
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * A simple [Fragment] subclass.
 * Use the [ReviewPageTypeRadioFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReviewPageTypeRadioFragment : ReviewPageBaseFragment() {

    private lateinit var mPageTitleTextView: TextView
    private lateinit var mProductImageImageView: AppCompatImageView
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
                ReviewPageTypeRadioFragment().apply {
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

        val rootView =  inflater.inflate(R.layout.fragment_review_page_type_radio, container, false)
        mPageTitleTextView = rootView.findViewById(R.id.fragmentReviewPageTypeRadio_xeiTextView_title)
        mProductImageImageView = rootView.findViewById(R.id.fragmentReviewPageTypeRadio_imageView_productImage)
        mOptionsContainerLinearLayout = rootView.findViewById(R.id.fragmentReviewPageTypeRadio_linearLayout_optionsContainer)


        mPageTitleTextView.text = mViewModel.title
        if (mProductImageUrl != null) {
            mProductImageImageView.loadImageFromNetwork(mProductImageUrl!!)
            mProductImageImageView.visibility = View.VISIBLE
        }
        val rainbow = RadioOptionView.getRainbow(mViewModel.options.size)
        for (i in 0 until mViewModel.options.size) {
            val optionButton = RadioOptionView(context!!, 300, 56, 16, 16, 16, 16, rainbow[i])
            optionButton.setText(mViewModel.options[i].title)
            optionButton.setOnClickListener {
                if (optionButton.isOptionSelected()) {
                    optionButton.deselect()
                    // TODO update model
                } else {
                    optionButton.select()
                    // TODO update model
                }
            }
            mOptionsContainerLinearLayout.addView(optionButton)
        }

        return rootView
    }

}
