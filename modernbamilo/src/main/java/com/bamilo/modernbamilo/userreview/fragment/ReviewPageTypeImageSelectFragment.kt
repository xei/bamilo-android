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
import com.bamilo.modernbamilo.userreview.pojo.getsurvey.Question
import com.bamilo.modernbamilo.util.dpToPx
import com.bamilo.modernbamilo.util.extension.loadImageFromNetwork
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    private lateinit var mOptionsContainer: LinearLayout

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
        mOptionsContainer = rootView.findViewById(R.id.fragmentReviewPageTypeImageselect_linearLayout_optionsContainer)

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
            mOptionsContainer.addView(option)
        }


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
                "\"title\":\"نظرات کاربران\",\n" +
                "\"value\":10011,\n" +
                "\"image\":\"https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Noto_Emoji_Oreo_1f9df_200d_2640.svg/128px-Noto_Emoji_Oreo_1f9df_200d_2640.svg.png\",\n" +
                "\"other\":false\n" +
                "},\n" +
                "{\n" +
                "\"id\":10012,\n" +
                "\"title\":\"۱۰۰ روز ضمانت بازگشت کالا\",\n" +
                "\"value\":10012,\n" +
                "\"image\":\"https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Noto_Emoji_KitKat_263a.svg/128px-Noto_Emoji_KitKat_263a.svg.png\",\n" +
                "\"other\":false\n" +
                "},\n" +
                "{\n" +
                "\"id\":10013,\n" +
                "\"title\":\"تنوع محصولات و فروشندگان\",\n" +
                "\"value\":10013,\n" +
                "\"image\":\"https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Noto_Emoji_Oreo_1f37a.svg/128px-Noto_Emoji_Oreo_1f37a.svg.png\",\n" +
                "\"other\":false\n" +
                "},\n" +
                "{\n" +
                "\"id\":10014,\n" +
                "\"title\":\"سایر موارد\",\n" +
                "\"value\":10014,\n" +
                "\"image\":\"https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Noto_Emoji_Oreo_1f4a3.svg/128px-Noto_Emoji_Oreo_1f4a3.svg.png\",\n" +
                "\"other\":true\n" +
                "}\n" +
                "]\n" +
                "}"
        return Gson().fromJson(str, object : TypeToken<Question>() {}.type)
    }
}
