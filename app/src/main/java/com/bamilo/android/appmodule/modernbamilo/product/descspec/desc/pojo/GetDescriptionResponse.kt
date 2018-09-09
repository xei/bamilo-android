package com.bamilo.android.appmodule.modernbamilo.product.descspec.desc.pojo

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName

data class GetDescriptionResponse (
        @SerializedName("description") val description: String
): BaseModel()