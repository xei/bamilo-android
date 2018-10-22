package com.bamilo.android.appmodule.modernbamilo.authentication.repository

import android.content.ContentValues
import android.content.Context
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginAutoHelper
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginHelper
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback

/**
 * Created by Farshid
 * since 10/17/2018.
 * contact farshidabazari@gmail.com
 */
object AuthenticationRepo {
    public fun login(context: Context?, identifier: String, password: String, response: IResponseCallback) {
        context?.let {
            val values = ContentValues()
            values.put("login[identifier]", identifier)
            values.put("login[password]", password)
            BamiloApplication.INSTANCE.sendRequest(LoginHelper(it),
                    LoginHelper.createLoginBundle(values), response)
        }
    }

    public fun autoLogin(context: Context?, response: IResponseCallback) {
        context?.let {
            BamiloApplication.INSTANCE.sendRequest(LoginAutoHelper(it),
                    LoginAutoHelper.createAutoLoginBundle(), response)
        }
    }
}