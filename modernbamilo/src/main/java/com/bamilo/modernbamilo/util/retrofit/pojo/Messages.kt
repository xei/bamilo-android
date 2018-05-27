package com.bamilo.modernbamilo.util.retrofit.pojo

import com.google.gson.annotations.SerializedName
data class Messages(
        @SerializedName("successes") val successes: List<MessageItem>,
        @SerializedName("messageItem") val messageItem: List<MessageItem>,
        @SerializedName("validate") val validate: List<MessageItem>
)