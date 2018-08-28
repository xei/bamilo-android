package com.bamilo.android.appmodule.modernbamilo.customview

import android.content.Context
import android.support.v7.widget.AppCompatCheckBox
import android.util.AttributeSet
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.util.typography.TypeFaceHelper
import java.util.*

/**
 * This is a custom TextView that render fonts based on locale set in the layout XML file.
 *
 * Created by hamidreza on 2/14/16.
 */
open class XeiCheckBox : AppCompatCheckBox {

    companion object {

        private const val LOCALE_AUTO = 0
        private const val LOCALE_EN_US = 1
        private const val LOCALE_FA_IR = 2
        private const val LOCALE_HYBRID = 3

        private const val STYLE_NORMAL = 0
        private const val STYLE_BOLD = 1

        private const val LANGUAGE_ENGLISH = "en"
        private const val LANGUAGE_PERSIAN = "fa"
    }


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            setLocaleTypeface(context, attrs)
        }
    }

    private fun setLocaleTypeface(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.XeiLocaledView)

        try {
            val localeEnum = typedArray.getInt(R.styleable.XeiLocaledView_locale, LOCALE_AUTO)
            val styleFlag = typedArray.getInt(R.styleable.XeiLocaledView_style, STYLE_NORMAL)
            val typeFaceHelper = TypeFaceHelper.getInstance(getContext())

            when (localeEnum) {
                LOCALE_AUTO -> {
                    val language = Locale.getDefault().language
                    when (language) {
                        LANGUAGE_ENGLISH -> when (styleFlag) {
                            STYLE_NORMAL -> typeface = typeFaceHelper.getTypeFace(TypeFaceHelper.FONT_ROBOTO_REGULAR)
                            STYLE_BOLD -> typeface = typeFaceHelper.getTypeFace(TypeFaceHelper.FONT_ROBOTO_BOLD)
                        }
                        LANGUAGE_PERSIAN -> when (styleFlag) {
                            STYLE_NORMAL -> typeface = typeFaceHelper.getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_REGULAR)
                            STYLE_BOLD -> typeface = typeFaceHelper.getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_BOLD)
                        }
                        else -> {
                        }
                    }// Using default typeface for other languages.
                }
                LOCALE_EN_US -> when (styleFlag) {
                    STYLE_NORMAL -> typeface = typeFaceHelper.getTypeFace(TypeFaceHelper.FONT_ROBOTO_REGULAR)
                    STYLE_BOLD -> typeface = typeFaceHelper.getTypeFace(TypeFaceHelper.FONT_ROBOTO_BOLD)
                }
                LOCALE_FA_IR -> when (styleFlag) {
                    STYLE_NORMAL -> typeface = typeFaceHelper.getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_REGULAR)
                    STYLE_BOLD -> typeface = typeFaceHelper.getTypeFace(TypeFaceHelper.FONT_IRAN_SANS_BOLD)
                }
                LOCALE_HYBRID -> {
                }
            }// TODO: handle this with Android Data Binding Support Library.
        } catch (e: Exception) {

        } finally {
            typedArray.recycle()
        }

    }

}
