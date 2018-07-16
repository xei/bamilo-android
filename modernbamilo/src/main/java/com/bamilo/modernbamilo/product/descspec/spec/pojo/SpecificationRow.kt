package com.bamilo.modernbamilo.product.descspec.spec.pojo

import com.google.gson.annotations.SerializedName

data class SpecificationRow (
        @SerializedName("header") val headerTitle: String,
        @SerializedName("content") val content: ArrayList<SpecificationTuple>
)