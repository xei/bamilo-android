package com.bamilo.android.appmodule.modernbamilo.product.descspec.spec.pojo

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName

class SpecificationTuple (
        @SerializedName("key") val title: String,
        @SerializedName("value") val value: String?
): BaseModel()