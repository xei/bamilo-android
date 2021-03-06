package com.bamilo.android.appmodule.modernbamilo.product.comment.submit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.app.BaseActivity
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker
import com.bamilo.android.appmodule.modernbamilo.util.extension.persianizeNumberString
import com.bamilo.android.appmodule.modernbamilo.util.logging.LogType
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.willy.ratingbar.ScaleRatingBar
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG_DEBUG = "SubmitRateActivity"

private const val KEY_EXTRA_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"
private const val RATING_DEFAULT = 0

fun startSubmitRateActivity(invokerContext: Context, productId: String) {
    val intent = Intent(invokerContext, SubmitRateActivity::class.java)
    intent.putExtra(KEY_EXTRA_PRODUCT_ID, productId)
    invokerContext.startActivity(intent)

    Logger.log("SubmitRateActivity has started for product: $productId", TAG_DEBUG)
}

class SubmitRateActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mCloseBtnImageButton: ImageButton
    private lateinit var mToolbarTitleTextView: TextView
    private lateinit var mRateRatingBar: ScaleRatingBar
    private lateinit var mRateTextView: TextView
    private lateinit var mCommentTitleEditText: EditText
    private lateinit var mCommentContentEditText: EditText
    private lateinit var mSubmitRateButton: BamiloActionButton
    private lateinit var mSubmitRateProgressBar: MaterialProgressBar
    private lateinit var mMessageBox: View

    private var mRateIsSubmiting = false

    private lateinit var mProductId: String

    private val mWebApi = RetrofitHelper.makeWebApi(this, SubmitRateWebApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_rate)

        mProductId = intent.getStringExtra(KEY_EXTRA_PRODUCT_ID)

        findViews()
        mToolbarTitleTextView.text = resources.getString(R.string.submitRate_title)
        setOnClickListeners()
        initRatingBar()

    }

    private fun findViews() {
        mCloseBtnImageButton = findViewById(R.id.layoutToolbar_imageButton_close)
        mToolbarTitleTextView = findViewById(R.id.layoutToolbar_xeiTextView_title)
        mRateRatingBar = findViewById(R.id.activitySubmitRate_scaleRatingBar_ratingBar)
        mRateTextView = findViewById(R.id.activitySubmitRate_xeiTextView_rate)
        mCommentTitleEditText = findViewById(R.id.activitySubmitRate_xeiEditText_title)
        mCommentContentEditText = findViewById(R.id.activitySubmitRate_xeiEditText_comment)
        mSubmitRateButton = findViewById(R.id.activitySubmitRate_xeiButton_submitRateBtn)
        mSubmitRateProgressBar = findViewById(R.id.activitySubmitRate_materialProgressBar_submitCommentLoading)
        mMessageBox = findViewById(R.id.activitySubmitRate_layout_warningLayout)
    }

    private fun setOnClickListeners() {
        mCloseBtnImageButton.setOnClickListener(this)
        mSubmitRateButton.setOnClickListener(this)
    }

    private fun initRatingBar() {
        mRateRatingBar.stepSize = 1f
        mRateRatingBar.rating = RATING_DEFAULT.toFloat()
        setRateTextView(RATING_DEFAULT)

        mRateRatingBar.setOnRatingChangeListener { _, rate -> setRateTextView(rate.toInt()) }
    }

    private fun setRateTextView(rating: Int) {
        mRateTextView.text = resources.getString(R.string.submitRate_rate, rating.toString().persianizeNumberString())
    }

    private fun submitRate() {
        val rate = mRateRatingBar.rating.toInt()

        if (rate == 0) {
            Toast.makeText(this, resources.getString(R.string.submitRate_selectStarError), Toast.LENGTH_LONG).show()
            return
        }

        val title = mCommentTitleEditText.text.toString()
        val content = mCommentContentEditText.text.toString()

        val call = mWebApi.submit(mProductId, rate, title, content)
        call.enqueue(object: Callback<ResponseWrapper<Boolean>> {

            override fun onResponse(call: Call<ResponseWrapper<Boolean>>?, response: Response<ResponseWrapper<Boolean>>?) {
                if (response?.body()?.success != null && response.body()?.success!!) {
                    Toast.makeText(this@SubmitRateActivity, resources.getText(R.string.submitRate_succeed), Toast.LENGTH_LONG).show()
                    EventTracker.addProductReview(
                            id = mProductId,
                            title = "",
                            amount = 0,
                            categoryId = ""
                    )
                    Logger.log("SubmitRate request succeed!", TAG_DEBUG, LogType.INFO)
                    finish()
                } else {
                    mMessageBox.visibility = View.VISIBLE
//                    Toast.makeText(this@SubmitRateActivity, resources.getText(R.string.submitRate_submitError), Toast.LENGTH_LONG).show()

                    Logger.log("SubmitRate request failed!", TAG_DEBUG, LogType.ERROR)
                }
                mRateIsSubmiting = false
                mSubmitRateProgressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<ResponseWrapper<Boolean>>?, t: Throwable?) {
                mMessageBox.visibility = View.VISIBLE
//                Toast.makeText(this@SubmitRateActivity, resources.getText(R.string.submitRate_submitError), Toast.LENGTH_LONG).show()
                mRateIsSubmiting = false
                mSubmitRateProgressBar.visibility = View.GONE
                Logger.log("SubmitRate request failed!", TAG_DEBUG, LogType.ERROR)
            }

        })
    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.layoutToolbar_imageButton_close -> finish()
            R.id.activitySubmitRate_xeiButton_submitRateBtn ->
                if(!mRateIsSubmiting) {
                    if (mRateRatingBar.rating.toInt() == 0) {
                        Toast.makeText(this, resources.getString(R.string.submitRate_selectStarError), Toast.LENGTH_LONG).show()
                    } else {
                        submitRate()
                        mRateIsSubmiting = true
                        mSubmitRateProgressBar.visibility = View.VISIBLE
                        mMessageBox.visibility = View.GONE
                    }
            }
        }
    }

}
