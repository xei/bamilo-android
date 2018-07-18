package com.bamilo.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName

data class ResponseWrapper<T> (

        @SerializedName("success") val success: Boolean,
//        @SerializedName("messages") val messages: Messages,   // TODO: duo to some backend issues
        @SerializedName("session") val session: Session,
        @SerializedName("metadata") val metadata: T

)