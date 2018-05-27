package com.bamilo.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName
data class Messages(
        @SerializedName("successes") val successes: MessageItem,
        @SerializedName("messageItem") val messageItem: MessageItem,
        @SerializedName("validate") val validate: MessageItem
)