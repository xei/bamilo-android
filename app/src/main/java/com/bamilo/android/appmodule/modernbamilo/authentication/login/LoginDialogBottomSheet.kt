package com.bamilo.android.appmodule.modernbamilo.authentication.login

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bamilo.android.R

/**
 * Created by Farshid
 * since 9/26/2018.
 * contact farshidabazari@gmail.com
 */
class LoginDialogBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.bottomsheet_login, container, false).apply {
        }
    }
}
