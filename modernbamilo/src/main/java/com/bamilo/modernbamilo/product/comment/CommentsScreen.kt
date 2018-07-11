package com.bamilo.modernbamilo.product.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.util.logging.Logger
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper

private const val TAG_DEBUG = "SubmitRateActivity"

private const val KEY_EXTRA_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"
private const val KEY_EXTRA_RATE = "KEY_EXTRA_RATE"
private const val KEY_EXTRA_RATE_SUM = "KEY_EXTRA_RATE_SUM"
private const val KEY_EXTRA_COMMENTS_COUNT = "KEY_EXTRA_COMMENTS_COUNT"
private const val KEY_EXTRA_ONE_STARS_AVG = "KEY_EXTRA_ONE_STARS_AVG"
private const val KEY_EXTRA_TWO_STARS_AVG = "KEY_EXTRA_TWO_STARS_AVG"
private const val KEY_EXTRA_THREE_STARS_AVG = "KEY_EXTRA_THREE_STARS_AVG"
private const val KEY_EXTRA_FOUR_STARS_AVG = "KEY_EXTRA_FOUR_STARS_AVG"
private const val KEY_EXTRA_FIVE_STARS_AVG = "KEY_EXTRA_FOUR_STARS_AVG"

fun startActivity(context: Context,
                  productId: String,
                  rate: Float,
                  rateSum: Int,
                  commentsCount: Int,
                  oneStarsAvg: Float,
                  twoStarsAvg: Float,
                  threeStarsAvg: Float,
                  fourStarsAvg: Float) {
    val intent = Intent(context, CommentsActivity::class.java)
    intent.putExtra(KEY_EXTRA_PRODUCT_ID, productId)
    intent.putExtra(KEY_EXTRA_RATE, rate)
    intent.putExtra(KEY_EXTRA_RATE_SUM, rateSum)
    intent.putExtra(KEY_EXTRA_COMMENTS_COUNT, commentsCount)
    intent.putExtra(KEY_EXTRA_ONE_STARS_AVG, oneStarsAvg)
    intent.putExtra(KEY_EXTRA_TWO_STARS_AVG, twoStarsAvg)
    intent.putExtra(KEY_EXTRA_THREE_STARS_AVG, threeStarsAvg)
    intent.putExtra(KEY_EXTRA_FOUR_STARS_AVG, fourStarsAvg)
    intent.putExtra(KEY_EXTRA_FIVE_STARS_AVG, fourStarsAvg)
    context.startActivity(intent)

    Logger.log("CommentActivity has started for product: $productId", TAG_DEBUG)
}

class CommentsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mProductId: String

    private val mWebApi = RetrofitHelper.makeWebApi(this, CommentWebApi::class.java)

    private lateinit var mViewModel: CommentsScreenViewModel

    private lateinit var mCloseBtnImageButton: ImageButton
    private lateinit var mToolbarTitleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        mProductId = intent.getStringExtra(KEY_EXTRA_PRODUCT_ID)

        createViewModel()
        findViews()
        mToolbarTitleTextView.text = resources.getString(R.string.comment_title)
        setOnClickListeners()
    }

    private fun createViewModel() {
        mViewModel = CommentsScreenViewModel(
                rate = intent.getFloatExtra(KEY_EXTRA_RATE, 0f),
                rateSum = intent.getIntExtra(KEY_EXTRA_RATE_SUM, 5),
                commentsCount = intent.getIntExtra(KEY_EXTRA_COMMENTS_COUNT, 0),
                oneStarsAvg = intent.getFloatExtra(KEY_EXTRA_ONE_STARS_AVG, 0f),
                twoStarsAvg = intent.getFloatExtra(KEY_EXTRA_TWO_STARS_AVG, 0f),
                threeStarsAvg = intent.getFloatExtra(KEY_EXTRA_THREE_STARS_AVG, 0f),
                fourStarsAvg = intent.getFloatExtra(KEY_EXTRA_FOUR_STARS_AVG, 0f),
                fiveStarsAvg = intent.getFloatExtra(KEY_EXTRA_ONE_STARS_AVG, 0f)
        )
    }

    private fun findViews() {
        mCloseBtnImageButton = findViewById(R.id.layoutToolbar_imageButton_close)
        mToolbarTitleTextView = findViewById(R.id.layoutToolbar_xeiTextView_title)
    }

    private fun setOnClickListeners() {
        mCloseBtnImageButton.setOnClickListener(this)
    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.layoutToolbar_imageButton_close -> finish()
        }
    }
}
