package com.bamilo.android.appmodule.modernbamilo.product.descspec.desc.pojo

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName

data class DescriptionRow(
        @SerializedName("title") val title: String?,
        @SerializedName("hint") val hint: String?,
        @SerializedName("desc") val description: String?,
        @SerializedName("img") val image: String?
): BaseModel()