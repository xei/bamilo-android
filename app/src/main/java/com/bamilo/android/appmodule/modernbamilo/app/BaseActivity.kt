package com.bamilo.android.appmodule.modernbamilo.app

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.bamilo.android.appmodule.modernbamilo.util.locale.LocaleContextWrapper

open class BaseActivity: AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleContextWrapper.wrap(newBase!!,"fa"))
    }

}