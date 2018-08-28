package com.bamilo.android.appmodule.modernbamilo.product.descspec.desc.pojo

import com.google.gson.annotations.SerializedName

data class GetDescriptionResponse (
        @SerializedName("description") val description: String
)