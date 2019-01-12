package com.bamilo.android.appmodule.modernbamilo.authentication.repository.response

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel

data class ForgetPasswordRequestModel(
        val nextStepVerification: Boolean,
        var verification: String
) : BaseModel()