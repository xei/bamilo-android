package com.bamilo.android.appmodule.modernbamilo.launch.model.webservice

import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel
import com.google.gson.annotations.SerializedName

data class GetStartupConfigsResponse(
        @SerializedName("currency_symbol") val currency: String,
        @SerializedName("ga_android_id") val googleAnalyticsID: String,
        @SerializedName("phone_number") val bamiloPhoneNo: String,
        @SerializedName("cs_email") val bamiloEmailAddress: String,
        @SerializedName("version") val versionStatus: VersionStatus
): BaseModel()

const val STATE_VALID_VERSION = 0
const val STATE_OPTIONAL_UPDATE = 1
const val STATE_FORCED_UPDATE = 2

data class VersionStatus(
        @SerializedName("status") val state: Int,
        @SerializedName("message_title") val title: String?,
        @SerializedName("message_content") val message: String?,
        @SerializedName("store_url") val latestApkUrl: String?
): BaseModel()