package com.bamilo.android.appmodule.modernbamilo.authentication.repository

import android.content.ContentValues
import android.content.Context
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginAutoHelper
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginHelper
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback
import com.bamilo.android.appmodule.modernbamilo.authentication.forgetpassword.ResetPasswordRequestModel
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.response.ForgetPasswordRequestModel
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Callback

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

    public fun forgetPasswordRequest(context: Context, identifier: String, callBack: Callback<ResponseWrapper<ForgetPasswordRequestModel>>) {
        RetrofitHelper.makeWebApi(context, AuthenticationServer::class.java)
                .forgetPasswordRequest(identifier)
                .enqueue(callBack)
    }

    public fun resetPassword(context: Context, identifier: String, newPass: String, callBack: Callback<ResponseWrapper<ForgetPasswordRequestModel>>) {
        RetrofitHelper.makeWebApi(context, AuthenticationServer::class.java)
                .resetPassword(identifier, newPass)
                .enqueue(callBack)
    }

    public fun verifyPassword(context: Context, identifier: String, verificationCode: String, callBack: Callback<ResponseWrapper<ForgetPasswordRequestModel>>) {
        RetrofitHelper.makeWebApi(context, AuthenticationServer::class.java)
                .verifyPassword(identifier, verificationCode)
                .enqueue(callBack)
    }
}