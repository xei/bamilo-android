package com.bamilo.android.appmodule.modernbamilo.customview

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.util.logging.LogType
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger

private const val TAG_DEBUG = "BamiloActionButton"

class BamiloActionButton : RelativeLayout {

    private lateinit var mTitleTextView: TextView
    private lateinit var mIconImageView: AppCompatImageView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        setBackgroundResource(R.drawable.background_btn_orange)

        val layout = LayoutInflater
                .from(context)
                .inflate(R.layout.layout_action_btn_bamilo, this, false)

        mTitleTextView = layout.findViewById(R.id.layoutActionBtnBamilo_xeiTextView_title)
        mIconImageView = layout.findViewById(R.id.layoutActionBtnBamilo_imageView_icon)

        addView(layout)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BamiloActionButton)

        try {
            val titleText = typedArray.getString(R.styleable.BamiloActionButton_text)
            val iconResId = typedArray.getResourceId(R.styleable.BamiloActionButton_iconDrawable, 0)

            setText(titleText)
            if (iconResId != 0) { setIcon(iconResId) }

        } catch (e: Exception) {
            Logger.log(message = e.message.toString(), tag = TAG_DEBUG, logType = LogType.ERROR)
        } finally {
            typedArray.recycle()
        }
    }

    fun setText(text: String?) = text?.let {mTitleTextView.text = it }.also { mTitleTextView.visibility = View.VISIBLE }

    fun setIcon(resId: Int) = mIconImageView.apply {
        visibility = View.VISIBLE
        setImageResource(resId)
    }

}
