package com.bamilo.modernbamilo.userreview.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.userreview.optionview.NpsNumberPicker
import com.bamilo.modernbamilo.userreview.optionview.RadioOptionView
import com.bamilo.modernbamilo.userreview.pojo.getsurvey.Question
import com.bamilo.modernbamilo.util.extension.loadImageFromNetwork
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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

        mViewModel = createMockData()

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

//        mViewModel.options = arrayOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۱۰")

        mNpsOptionsNumberPicker.setOnNpsOptionChangeListener(object: NpsNumberPicker.OnNpsOptionChangeListener {
            override fun changeImage(imageUrl: String) {
                mNpsOptionImageImageView.loadImageFromNetwork(imageUrl)
            }

        })

        mNpsOptionsNumberPicker.setNpsOptions(mViewModel.options)

        return rootView
    }
















    private fun createMockData(): Question {

        val str = "{\n" +
                "\"id\":3,\n" +
                "\"title\":\"چه عاملی باعث شد تا از ما خرید کنید؟\",\n" +
                "\"type\":\"CHECKBOX\",\n" +
                "\"required\":false,\n" +
                "\"hidden\":false,\n" +
                "\"options\":[\n" +
                "{\"id\":10011,\n" +
                "\"title\":\"۱\",\n" +
                "\"value\":10011,\n" +
                "\"image\":\"https://www.colourbox.com/preview/4911064-wooden-numeric-1.jpg\",\n" +
                "\"other\":false\n" +
                "},\n" +
                "{\n" +
                "\"id\":10012,\n" +
                "\"title\":\"۲\",\n" +
                "\"value\":10012,\n" +
                "\"image\":\"https://www.colourbox.com/preview/4911077-wooden-numeric-2.jpg\",\n" +
                "\"other\":false\n" +
                "},\n" +
                "{\n" +
                "\"id\":10013,\n" +
                "\"title\":\"۳\",\n" +
                "\"value\":10013,\n" +
                "\"image\":\"https://www.colourbox.com/preview/4911081-wooden-numeric-3.jpg\",\n" +
                "\"other\":false\n" +
                "},\n" +
                "{\n" +
                "\"id\":10014,\n" +
                "\"title\":\"۴\",\n" +
                "\"value\":10014,\n" +
                "\"image\":\"https://www.colourbox.com/preview/4911092-wooden-numeric-4.jpg\",\n" +
                "\"other\":true\n" +
                "}\n" +
                "]\n" +
                "}"
        return Gson().fromJson(str, object : TypeToken<Question>() {}.type)
    }
}
