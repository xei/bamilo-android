package com.bamilo.android.appmodule.modernbamilo.userreview.optionview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.bamilo.android.appmodule.modernbamilo.util.typography.TypeFaceHelper
import com.bamilo.android.appmodule.modernbamilo.userreview.pojo.getsurvey.Option
import com.shawnlin.numberpicker.NumberPicker

private const val TAG_DEBUG = "NPSNumberPicker"

class NpsNumberPicker : NumberPicker {

    private lateinit var mOptions: List<Option>

    private lateinit var mOnNpsOptionChangeListener: OnNpsOptionChangeListener

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        typeface = TypeFaceHelper.getInstance(context).getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_BOLD)
        setOnValueChangedListener { _, _, newVal ->

            mOptions[newVal - 1].isSelected = true
            for (i in 0 until mOptions.size) {
                if (i != newVal - 1) {
                    mOptions[i].isSelected = false
                }
            }

            mOnNpsOptionChangeListener.changeImage(mOptions[newVal - 1].image)
        }
    }

    fun setNpsOptions(options: List<Option>) {
        mOptions = options
        minValue = 1
        maxValue = options.size
        val optionsLabel : Array<String> = Array(options.size) { "it = $it" }
        for (i in 0 until options.size) {
            optionsLabel[i] = options[i].title
        }
        displayedValues = optionsLabel
        value = maxValue / 2

        mOnNpsOptionChangeListener.changeImage(mOptions[value - 1].image)
    }

    fun setOnNpsOptionChangeListener(onNpsOptionChangeListener: OnNpsOptionChangeListener) {
        try {
            this.mOnNpsOptionChangeListener = onNpsOptionChangeListener
        } catch (npe: NullPointerException) {
            Log.e(TAG_DEBUG, npe.message)
        }
    }

    interface OnNpsOptionChangeListener {
        fun changeImage(imageUrl: String?)
    }

}