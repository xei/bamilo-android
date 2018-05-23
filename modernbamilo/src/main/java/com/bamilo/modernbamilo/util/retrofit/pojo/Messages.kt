package com.bamilo.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName
data class Messages(
    @SerializedName("error") val error: List<Error>
)