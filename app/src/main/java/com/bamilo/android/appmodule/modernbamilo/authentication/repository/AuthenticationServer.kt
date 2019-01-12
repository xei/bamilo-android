package com.bamilo.android.appmodule.modernbamilo.authentication.repository

import com.bamilo.android.appmodule.modernbamilo.authentication.forgetpassword.ResetPasswordRequestModel
import com.bamilo.android.appmodule.modernbamilo.authentication.repository.response.ForgetPasswordRequestModel
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthenticationServer {
    @FormUrlEncoded
    @POST("customer/forgotpasswordrequest/")
    fun forgetPasswordRequest(@Field(value = "identifier", encoded = false) identifier: String):
            Call<ResponseWrapper<ForgetPasswordRequestModel>>

    @FormUrlEncoded
    @POST("customer/forgotpasswordverify/")
    fun verifyPassword(@Field(value = "identifier", encoded = false) identifier: String,
                       @Field(value = "verification", encoded = false) verificationCode: String):
            Call<ResponseWrapper<ForgetPasswordRequestModel>>

    @FormUrlEncoded
    @POST("customer/forgotpasswordreset/")
    fun resetPassword(@Field(value = "identifier", encoded = false) identifier: String,
                      @Field(value = "new_password", encoded = false) newPassword: String):
            Call<ResponseWrapper<ForgetPasswordRequestModel>>
}