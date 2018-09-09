package com.bamilo.android.appmodule.modernbamilo.product.descspec.spec.pojo

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName

data class SpecificationRow (
        @SerializedName("header") val headerTitle: String,
        @SerializedName("body") val content: ArrayList<SpecificationTuple>?
): BaseModel()