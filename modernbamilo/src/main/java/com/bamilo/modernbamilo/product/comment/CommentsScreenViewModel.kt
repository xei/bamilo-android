package com.bamilo.modernbamilo.product.comment

data class CommentsScreenViewModel(
        val rate: Float,
        val rateSum: Int,
        val commentsCount: Int,
        val oneStarsAvg: Float,
        val twoStarsAvg: Float,
        val threeStarsAvg: Float,
        val fourStarsAvg: Float,
        val fiveStarsAvg: Float,
        val comments: ArrayList<CommentViewModel> = ArrayList<CommentViewModel>()
)

data class CommentViewModel(
        val title: String,
        val composedTime: String,
        val authorName: String,
        val hasUserBeenBought: Boolean,
        val rate: Float,
        val commentContent: String,
        val likesCount: Int,
        val dislikesCount: Int
)