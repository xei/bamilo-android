package com.bamilo.android.appmodule.modernbamilo.product.policy

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ReturnPolicyWebApi {

    @GET("catalog/returnpolicy/category/{returnPolicyKey}")
    fun getContent(@Path("returnPolicyKey") returnPolicyKey: String): Call<ResponseWrapper<GetReturnPolicyResponse>>

}

data class GetReturnPolicyResponse(val returnPolicy: String) : BaseModel()