package com.bamilo.android.appmodule.modernbamilo.authentication.forgetpassword

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel

data class ResetPasswordRequestModel(
        var identifier: String = "",
        var new_password: String = ""
) : BaseModel()