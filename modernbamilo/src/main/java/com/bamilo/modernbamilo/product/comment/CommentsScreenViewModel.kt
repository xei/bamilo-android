package com.bamilo.modernbamilo.product.comment

import com.google.gson.annotations.SerializedName

data class CommentsScreenViewModel(
        val rate: Float,
        val rateSum: Int,
        val commentsCount: Int,
        val oneStarsAvg: Float,
        val twoStarsAvg: Float,
        val threeStarsAvg: Float,
        val fourStarsAvg: Float,
        val fiveStarsAvg: Float,
        val comments: ArrayList<CommentViewModel> = ArrayList()
)

data class GetCommentsResponse (
        @SerializedName("total") val totalCommentsCount: Long,
        @SerializedName("items") val comments: ArrayList<CommentViewModel>,
        @SerializedName("pagination") val pagination: Pagination
)

data class CommentViewModel(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String?,
        @SerializedName("date") val composedTime: String,
        @SerializedName("username") val authorName: String,
        @SerializedName("is_bought_by_user") val hasUserBeenBought: Boolean,
        @SerializedName("rate") val rate: Float,
        @SerializedName("comment") val commentContent: String,
        @SerializedName("like") val likesCount: Int,
        @SerializedName("dislike") val dislikesCount: Int
)

data class Pagination(
        @SerializedName("total_pages") val totalPagesCount: Int,
        @SerializedName("current_page")  val currentPage: Int,
        @SerializedName("per_page")  val pageSize: Int
)