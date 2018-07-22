package com.bamilo.modernbamilo.product.descspec.spec.pojo

import com.google.gson.annotations.SerializedName

data class GetSpecificationResponse (
    @SerializedName("specifications") val specification: ArrayList<SpecificationRow>
)