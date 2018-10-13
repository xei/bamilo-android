package com.bamilo.android.appmodule.modernbamilo.userreview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton
import com.bamilo.android.appmodule.modernbamilo.userreview.fragment.ReviewPageBaseFragment
import com.bamilo.android.appmodule.modernbamilo.userreview.fragment.ReviewPageTypeEssayFragment
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.SubmitSurveyResponse
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey.GetSurveyResponse
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey.Question
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey.Survey
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurveylist.GetSurveyListResponse
import com.bamilo.android.appmodule.modernbamilo.userreview.stepperview.StepperView
import com.bamilo.android.appmodule.modernbamilo.util.extension.replaceFragmentInActivity
import com.bamilo.android.appmodule.modernbamilo.util.extension.replaceFragmentInActivityWithAnim
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val KEY_EXTRA_REVIEW_TYPE = "KEY_EXTRA_REVIEW_TYPE"
const val KEY_EXTRA_USER_ID = "KEY_EXTRA_USER_ID"
const val KEY_EXTRA_ORDER_ID = "KEY_EXTRA_ORDER_ID"

class UserReviewActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mCloseBtnImageButton: ImageButton
    private lateinit var mSurveyTitleTextView: TextView
    private lateinit var mStepperView: StepperView
    private lateinit var mNextButton: BamiloActionButton

    private lateinit var mSurvey: Survey
    private val mPagesFragmentList = ArrayList<ReviewPageBaseFragment>()
    private var mPageNo = 0

    private var webApi = RetrofitHelper.makeWebApi(this, UserReviewWebApi::class.java)

    companion object {

        @JvmStatic val TYPE_USER_REVIEW_APP_INITIAL = 0
        @JvmStatic val TYPE_USER_REVIEW_AFTER_PURCHASE = 1

        @JvmStatic
        fun start(invokerContext: Context, reviewType: Int, userId: String?, orderId: String?) {
            return
            val intent = Intent(invokerContext, UserReviewActivity::class.java)
            intent.putExtra(KEY_EXTRA_REVIEW_TYPE, reviewType)
            intent.putExtra(KEY_EXTRA_USER_ID, userId)
            intent.putExtra(KEY_EXTRA_ORDER_ID, orderId)
            invokerContext.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_review)

        findViews()
        setOnClickListeners()

        when (intent.getIntExtra(KEY_EXTRA_REVIEW_TYPE, TYPE_USER_REVIEW_APP_INITIAL)) {
            TYPE_USER_REVIEW_APP_INITIAL -> {
                val call = webApi.getSurveysList(intent.getStringExtra(KEY_EXTRA_USER_ID))
                call.enqueue(object: Callback<ResponseWrapper<GetSurveyListResponse>> {
                    override fun onResponse(call: Call<ResponseWrapper<GetSurveyListResponse>>?, response: Response<ResponseWrapper<GetSurveyListResponse>>?) {
                        try {
                            mSurvey = response?.body()?.metadata?.data?.surveys!![0]
                            initViewModel()
                        } catch (npe: NullPointerException) {

                        }

                    }

                    override fun onFailure(call: Call<ResponseWrapper<GetSurveyListResponse>>?, t: Throwable?) {

                    }
                })
            }
            TYPE_USER_REVIEW_AFTER_PURCHASE -> {
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
        }

    }

    private fun initViewModel() {
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
            R.id.activityUserReview_imageButton_closeBtn -> {
                var call = webApi.cancelSurvey(userId = intent.getStringExtra(KEY_EXTRA_USER_ID))
                call.enqueue(object: Callback<ResponseWrapper<Any>> {
                    override fun onFailure(call: Call<ResponseWrapper<Any>>?, t: Throwable?) {
                        finish()                    }

                    override fun onResponse(call: Call<ResponseWrapper<Any>>?, response: Response<ResponseWrapper<Any>>?) {
                        finish()                    }

                })
            }
            R.id.activityUserReview_xeiButton_next -> {

                when (mPageNo) {
                    mPagesFragmentList.size - 1 -> submitReview()
                    mPagesFragmentList.size - 2 -> {
                        mStepperView.setCurrentPage(++mPageNo)
                        // TODO: submit first
                        replaceFragmentInActivityWithAnim(mPagesFragmentList[mPageNo], R.id.activityUserReview_frameLayout_reviewPage)
                        mNextButton.setText("پایان")
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
        val responses = makeResponses()
        val call = webApi.submitSurvey(device = "mobile_app", orderNumber = intent.getStringExtra(KEY_EXTRA_ORDER_ID), userId = intent.getStringExtra(KEY_EXTRA_USER_ID), responses = responses)
        call.enqueue(object: Callback<ResponseWrapper<SubmitSurveyResponse>> {

            override fun onResponse(call: Call<ResponseWrapper<SubmitSurveyResponse>>?, response: Response<ResponseWrapper<SubmitSurveyResponse>>?) {
                if (response?.isSuccessful != null && response?.isSuccessful && response?.body()?.success!!) {
                    Toast.makeText(this@UserReviewActivity, resources.getString(R.string.userReview_submit_msg_succeed), Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@UserReviewActivity, resources.getString(R.string.userReview_submit_msg_failed), Toast.LENGTH_LONG).show()
                }

            }
            override fun onFailure(call: Call<ResponseWrapper<SubmitSurveyResponse>>?, t: Throwable?) {
                Toast.makeText(this@UserReviewActivity, resources.getString(R.string.userReview_submit_msg_failed), Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun makeResponses(): Map<String, String> {
        val responses = HashMap<String, String>()
        for (i in 0 until mSurvey.pages[0].questions.size) {
            val question = mSurvey.pages[0].questions[i]
            for (j in 0 until question.options.size) {
                val option = mSurvey.pages[0].questions[i].options[j]
                if (option.isSelected) {
                    responses["[0][${question.id}][${option.id}]"] = option.id.toString()
                }

            }

            if (question.userInputText != null && !question.userInputText.isEmpty()) {
                responses["[0][${question.id}]"] = question.userInputText
            }

        }

        return responses
    }

    override fun onBackPressed() {
        super.onBackPressed()

        when (mPageNo) {
            mPagesFragmentList.size - 1 -> {
                mNextButton.setText("بعدی")
                mStepperView.setCurrentPage(--mPageNo)
            }
            else -> {
                if (mPageNo > 0) {
                    mStepperView.setCurrentPage(--mPageNo)
                }
            }
        }

    }

}
