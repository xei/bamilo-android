package com.bamilo.modernbamilo.userreview.optionview

import android.content.Context
import android.widget.ImageView
import com.bamilo.modernbamilo.R

class ImageOptionView(context: Context) : ImageView(context) {

    private var isOptionSelected: Boolean = false

    init {
        setBackgroundResource(R.drawable.background_imageoption_survey_deselected)
    }

    fun isOptionSelected() = isOptionSelected

    fun select() {
        isOptionSelected = true
        setBackgroundResource(R.drawable.background_imageoption_survey_selected)
    }

    fun deselect() {
        isOptionSelected = false
        setBackgroundResource(R.drawable.background_imageoption_survey_deselected)
    }

}