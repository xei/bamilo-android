package com.bamilo.android.appmodule.modernbamilo.product.comment

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.util.logging.LogType
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger
import me.zhanghai.android.materialratingbar.MaterialRatingBar

private const val TAG_DEBUG = "RateBarView"

class RateBarView : RelativeLayout {

    private lateinit var mProgressBar: ProgressBar

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        addView(LayoutInflater.from(context).inflate(R.layout.layout_rate_bar, this, false))

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RateBarView)
        try {

            findViewById<MaterialRatingBar>(R.id.activitySubmitRate_materialRatingBar_ratingBar).run {
                rating = typedArray.getInt(R.styleable.RateBarView_rating, 0).toFloat()
            }

            mProgressBar = findViewById<ProgressBar>(R.id.activitySubmitRate_progressBar_progressBar).apply {
                rotation = 180f
                progressDrawable = ContextCompat.getDrawable(context, R.drawable.progress_comments_rate)
                progress = typedArray.getFloat(R.styleable.RateBarView_percentage, 0f).toInt()
            }

        } catch (e: Exception) {
            Logger.log(e.message.toString(), TAG_DEBUG, LogType.ERROR)
        } finally {
            typedArray.recycle()
        }
    }

    fun setProgress(progress: Int) {
        mProgressBar.progress = progress
    }

}
