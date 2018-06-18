package com.bamilo.modernbamilo.userreview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.userreview.fragment.ReviewPageBaseFragment
import com.bamilo.modernbamilo.userreview.fragment.ReviewPageTypeEssayFragment
import com.bamilo.modernbamilo.userreview.pojo.getsurvey.GetSurveyResponse
import com.bamilo.modernbamilo.userreview.pojo.getsurvey.Question
import com.bamilo.modernbamilo.userreview.pojo.getsurvey.Survey
import com.bamilo.modernbamilo.userreview.stepperview.StepperView
import com.bamilo.modernbamilo.util.extension.replaceFragmentInActivity
import com.bamilo.modernbamilo.util.extension.replaceFragmentInActivityWithAnim
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserReviewActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mCloseBtnImageButton: ImageButton
    private lateinit var mSurveyTitleTextView: TextView
    private lateinit var mStepperView: StepperView
    private lateinit var mNextButton: Button

    private lateinit var mSurvey: Survey
    private val mPagesFragmentList = ArrayList<ReviewPageBaseFragment>()
    private var mPageNo = 0

    private var webApi = RetrofitHelper.makeWebApi(this, UserReviewWebApi::class.java)

    companion object {
        @JvmStatic
        fun start(invokerContext: Context) {
            invokerContext.startActivity(Intent(invokerContext, UserReviewActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_review)

        findViews()
        setOnClickListeners()

        val call = webApi.getSurvey()
        call.enqueue(object: Callback<ResponseWrapper<GetSurveyResponse>> {
            override fun onResponse(call: Call<ResponseWrapper<GetSurveyResponse>>?, response: Response<ResponseWrapper<GetSurveyResponse>>?) {
                try {
                    mSurvey = response?.body()?.metadata?.survey!!
                    initViewModel()
                } catch (npe: NullPointerException) {

                }

            }

            override fun onFailure(call: Call<ResponseWrapper<GetSurveyResponse>>?, t: Throwable?) {

            }
        })

    }

    private fun initViewModel() {


//         TODO: get data from parcelable
//        mSurvey = createMockData().metadata.survey
        val surveyTitle = createMockData().metadata.survey.title
        mSurveyTitleTextView.text = mSurvey.title

        var i = 0
        mSurvey.pages[0].questions.forEach {page: Question ->
            if(!page.hidden && page.type != ReviewPageType.HIDDEN.name) {
                mPagesFragmentList.add(
                        ReviewPageBaseFragment.newInstance(ReviewPageType.valueOf(page.type),
                                i++,
                                mSurvey.product?.image))
            }
        }
        mPagesFragmentList.add(
                ReviewPageBaseFragment.newInstance(ReviewPageType.THANKS,
                        i,
                        null))

        mStepperView.setPagesCount(mPagesFragmentList.size)
        mStepperView.setCurrentPage(mPageNo)

        replaceFragmentInActivity(mPagesFragmentList[mPageNo], R.id.activityUserReview_frameLayout_reviewPage)
    }

    private fun findViews() {
        mCloseBtnImageButton = findViewById(R.id.activityUserReview_imageButton_closeBtn)
        mSurveyTitleTextView = findViewById(R.id.activityUserReview_textView_surveyTitle)
        mStepperView = findViewById(R.id.activityUserReview_stepperView_stepper)
        mNextButton = findViewById(R.id.activityUserReview_xeiButton_next)
    }

    private fun setOnClickListeners() {
        mCloseBtnImageButton.setOnClickListener(this)
        mNextButton.setOnClickListener(this)
    }

    fun getSurvey() = mSurvey

    override fun onClick(clickedView: View?) {

        if (mPagesFragmentList[mPageNo] is ReviewPageTypeEssayFragment) {
            (mPagesFragmentList[mPageNo] as ReviewPageTypeEssayFragment).storeUserInputText()
        }

        when(clickedView?.id) {
            R.id.activityUserReview_imageButton_closeBtn -> finish()
            R.id.activityUserReview_xeiButton_next -> {

                when (mPageNo) {
                    mPagesFragmentList.size - 1 -> finish()
                    mPagesFragmentList.size - 2 -> {
                        mStepperView.setCurrentPage(++mPageNo)
                        // TODO: submit first
                        replaceFragmentInActivityWithAnim(mPagesFragmentList[mPageNo], R.id.activityUserReview_frameLayout_reviewPage)
                        mNextButton.text = "بستن"
                    }
                    else -> {
                        mStepperView.setCurrentPage(++mPageNo)
                        // TODO: submit first
                        replaceFragmentInActivityWithAnim(mPagesFragmentList[mPageNo], R.id.activityUserReview_frameLayout_reviewPage)
                    }
                }

            }
        }
    }

    private fun submitReview() {
//         mSurvey
    }

    override fun onBackPressed() {
        super.onBackPressed()

        when (mPageNo) {
            mPagesFragmentList.size - 1 -> {
                mNextButton.text = "بعدی"
                mStepperView.setCurrentPage(--mPageNo)
            }
            else -> {
                if (mPageNo > 0) {
                    mStepperView.setCurrentPage(--mPageNo)
                }
            }
        }

    }



























    private fun createMockData(): ResponseWrapper<GetSurveyResponse> {

        //        mReviewPager.adapter = ReviewPagerAdapter(supportFragmentManager)
//        mStepperView.setViewPager(mReviewPager)

//        val userId = "32334"
//        val call = webApi.getSurveysList(userId)
//        call.enqueue(object : Callback<GetSurveyListResponse> {
//            override fun onResponse(call: Call<GetSurveyListResponse>?, response: Response<GetSurveyListResponse>?) {
//                val x = 2
//                try {
////                    listener.onAddressLookedUp(response!!.body()!!.results!![0].addressComponents!![0].longName)
//                } catch (ioobe: IndexOutOfBoundsException) {
//                }
//            }
//
//            override fun onFailure(call: Call<GetSurveyListResponse>?, t: Throwable?) {
//                val x = 2
////                listener.onErrorOccurred(t.toString())
//            }
//        })

        val str = "{\n" +
                "  \"success\": true,\n" +
                "  \"messages\": {\n" +
                "    \"success\": {\n" +
                "      \"message\": \"\",\n" +
                "      \"statusCode\": 200\n" +
                "    }\n" +
                "  },\n" +
                "  \"session\": {\n" +
                "    \"id\": \"v17osrjrfa5393dunomoq3m296\",\n" +
                "    \"expire\": null,\n" +
                "    \"YII_CSRF_TOKEN\": \"33f015aecd0b465a6d3e54ad1cb7401a172985f7\"\n" +
                "  },\n" +
                "  \"metadata\": {\n" +
                "    \"survey\": {\n" +
                "      \"alias\": \"journey_survey\",\n" +
                "      \"id\": 4179510,\n" +
                "      \"title\": \"ما را در بهبود فرایند خرید یاری کنید\",\n" +
                "      \"pages\": [\n" +
                "        {\n" +
                "          \"id\": 1,\n" +
                "          \"title\": \"(untitled)\",\n" +
                "          \"hidden\": false,\n" +
                "          \"questions\": [\n" +
                "            {\n" +
                "              \"id\": 2,\n" +
                "              \"title\": \"میزان رضایت شما از تجربه\u200Cی «خرید آنلاین» خود در بامیلو چقدر است؟\",\n" +
                "              \"type\": \"RADIO\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": [\n" +
                "                {\n" +
                "                  \"id\": 10001,\n" +
                "                  \"title\": \"خیلی زیاد\",\n" +
                "                  \"value\": 10001,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10002,\n" +
                "                  \"title\": \"زیاد\",\n" +
                "                  \"value\": 10002,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10003,\n" +
                "                  \"title\": \"متوسط\",\n" +
                "                  \"value\": 10003,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10004,\n" +
                "                  \"title\": \"کم\",\n" +
                "                  \"value\": 10004,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10005,\n" +
                "                  \"title\": \"اصلاً\",\n" +
                "                  \"value\": 10005,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 3,\n" +
                "              \"title\": \"چه عاملی باعث شد تا از ما خرید کنید؟\",\n" +
                "              \"type\": \"CHECKBOX\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": [\n" +
                "                {\n" +
                "                  \"id\": 10006,\n" +
                "                  \"title\": \"قیمت\",\n" +
                "                  \"value\": 10006,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10007,\n" +
                "                  \"title\": \"ارسال رایگان\",\n" +
                "                  \"value\": 10007,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10008,\n" +
                "                  \"title\": \"کد تخفیف\",\n" +
                "                  \"value\": 10008,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10009,\n" +
                "                  \"title\": \"بامیلو اولین نتیجه\u200Cی جست\u200Cوجوی من بود\",\n" +
                "                  \"value\": 10009,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10010,\n" +
                "                  \"title\": \"پیشنهاد دوستان\",\n" +
                "                  \"value\": 10010,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10011,\n" +
                "                  \"title\": \"نظرات کاربران\",\n" +
                "                  \"value\": 10011,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10012,\n" +
                "                  \"title\": \"۱۰۰ روز ضمانت بازگشت کالا\",\n" +
                "                  \"value\": 10012,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10013,\n" +
                "                  \"title\": \"تنوع محصولات و فروشندگان\",\n" +
                "                  \"value\": 10013,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10014,\n" +
                "                  \"title\": \"سایر موارد\",\n" +
                "                  \"value\": 10014,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": true\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 5,\n" +
                "              \"title\": \"آیا کالای مورد نظرتان را به راحتی پیدا کردید؟\",\n" +
                "              \"type\": \"IMAGE_SELECT\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": [\n" +
                "                {\n" +
                "                  \"id\": 10017,\n" +
                "                  \"title\": \"بله\",\n" +
                "                  \"value\": 10017,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10018,\n" +
                "                  \"title\": \"خیر\",\n" +
                "                  \"value\": 10018,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 6,\n" +
                "              \"title\": \"به نظر شما «چه عاملی» نیاز به بهبود دارد؟\",\n" +
                "              \"type\": \"NPS\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": [\n" +
                "                {\n" +
                "                  \"id\": 10019,\n" +
                "                  \"title\": \"۱\",\n" +
                "                  \"value\": 10019,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10020,\n" +
                "                  \"title\": \"۲\",\n" +
                "                  \"value\": 10020,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10021,\n" +
                "                  \"title\": \"۳\",\n" +
                "                  \"value\": 10021,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10022,\n" +
                "                  \"title\": \"۴\",\n" +
                "                  \"value\": 10022,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10023,\n" +
                "                  \"title\": \"۵\",\n" +
                "                  \"value\": 10023,\n" +
                "                  \"image\": \"http://www.free-icons-download.net/images/blue-digit-number-9-icon-24467.png\",\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10024,\n" +
                "                  \"title\": \"۶\",\n" +
                "                  \"value\": 10024,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10025,\n" +
                "                  \"title\": \"۷\",\n" +
                "                  \"value\": 10025,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10026,\n" +
                "                  \"title\": \"۸\",\n" +
                "                  \"value\": 10026,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": false\n" +
                "                },\n" +
                "                {\n" +
                "                  \"id\": 10027,\n" +
                "                  \"title\": \"۹\",\n" +
                "                  \"value\": 10027,\n" +
                "                  \"image\": null,\n" +
                "                  \"other\": true\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 7,\n" +
                "              \"title\": \"آیا نکته\u200Cای هست که مایل باشید با ما در میان بگذارید؟\",\n" +
                "              \"type\": \"ESSAY\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": []\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 8,\n" +
                "              \"title\": \"device\",\n" +
                "              \"type\": \"HIDDEN\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": []\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 9,\n" +
                "              \"title\": \"order_number\",\n" +
                "              \"type\": \"HIDDEN\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": []\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 10,\n" +
                "              \"title\": \"customer_id\",\n" +
                "              \"type\": \"HIDDEN\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": []\n" +
                "            },\n" +
                "            {\n" +
                "              \"id\": 11,\n" +
                "              \"title\": \"order_date\",\n" +
                "              \"type\": \"HIDDEN\",\n" +
                "              \"required\": false,\n" +
                "              \"hidden\": false,\n" +
                "              \"options\": []\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}\n"
        return Gson().fromJson(str, object : TypeToken<ResponseWrapper<GetSurveyResponse>>() {}.type)
    }

}
