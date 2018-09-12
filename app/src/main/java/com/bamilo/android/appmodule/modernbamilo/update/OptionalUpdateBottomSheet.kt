package com.bamilo.android.appmodule.modernbamilo.update

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 9/12/2018.
 * contact farshidabazari@gmail.com
 */
class OptionalUpdateBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mOnDialogDismissListener: OnDialogDismissListener

    fun setDismissListener(onDialogDismissListener: OnDialogDismissListener): OptionalUpdateBottomSheet {
        this.mOnDialogDismissListener = onDialogDismissListener
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.bottomsheet_update, container, false)



        return view
    }

}