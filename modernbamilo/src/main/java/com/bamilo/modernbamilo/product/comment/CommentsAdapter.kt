package com.bamilo.modernbamilo.product.comment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.customview.ExpandableXeiTextView
import com.bamilo.modernbamilo.util.extension.persianizeNumberString
import me.zhanghai.android.materialratingbar.MaterialRatingBar

private const val TYPE_VIEW_HEADER = 0
private const val TYPE_VIEW_COMMENT = 1

class CommentsAdapter(
        private val viewModel: CommentsScreenViewModel,
        private val isThisScreenJustForOneDistinctComment: Boolean)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mLayoutInflater: LayoutInflater? = null

    override fun getItemCount() = viewModel.comments.size + 1

    override fun getItemViewType(position: Int)
            = if (position == 0) TYPE_VIEW_HEADER else TYPE_VIEW_COMMENT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return if (viewType == TYPE_VIEW_HEADER) {
            RateViewHolder(mLayoutInflater!!.inflate(R.layout.row_comment_rate, parent, false))
        } else {
            CommentViewHolder(mLayoutInflater!!.inflate(R.layout.row_comment_comment, parent, false))
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        viewModel.run {
            when (position) {
                0 -> (viewHolder as RateViewHolder).run {
                    avgRateTextView.text = rate.toString().persianizeNumberString()
                    rateSumTextView.text = viewHolder.itemView.resources.getString(R.string.comment_header_rateSum, rateSum.toString().persianizeNumberString())
                    commentsCountTextView.text = viewHolder.itemView.resources.getString(R.string.comment_header_commentsCount, commentsCount.toString().persianizeNumberString())
                    oneStarProgressBar.setProgress(oneStarsAvg.toInt())
                    twoStarProgressBar.setProgress(twoStarsAvg.toInt())
                    threeStarProgressBar.setProgress(threeStarsAvg.toInt())
                    fourStarProgressBar.setProgress(fourStarsAvg.toInt())
                    fiveStarProgressBar.setProgress(fiveStarsAvg.toInt())

                } else -> (viewHolder as CommentViewHolder).run {
                    comments[position - 1].let {

                        if (it.title != null) {
                            titleTextView.text = it.title
                            titleTextView.visibility = View.VISIBLE
                        } else {
                            titleTextView.visibility = View.GONE
                        }
                        authorNameTextView.text = it.authorName
                        composedTimeTextView.text = it.composedTime
                        if (it.hasUserBeenBought) {
                            hasUserBeenBoughtImageView.visibility = View.VISIBLE
                            hasUserBeenBoughtTextView.visibility = View.VISIBLE
                        } else {
                            hasUserBeenBoughtImageView.visibility = View.GONE
                            hasUserBeenBoughtTextView.visibility = View.GONE
                        }
                        rateRatingBar.rating = it.rate
                        commentContentTextView.run {
                            if (isThisScreenJustForOneDistinctComment) {
                                commentContentTextView.onClick(mExpandIndicatorController.indicator)

                            } else {
                                mExpandIndicatorController.changeState(true)
                            }
                            text = it.commentContent
                        }

                        if (it.likesCount == -1 || it.dislikesCount == -1) {
                            // TODO: wrap these views inside a viewGroup
                            likeImageView.visibility = View.GONE
                            dislikeImageView.visibility = View.GONE
                            likeCountTextView.visibility = View.GONE
                            likeCountTextView.visibility = View.GONE
                        } else {
                            likeImageView.visibility = View.VISIBLE
                            dislikeImageView.visibility = View.VISIBLE
                            likeCountTextView.visibility = View.VISIBLE
                            likeCountTextView.visibility = View.VISIBLE

                            likeCountTextView.text = itemView.context.resources.getString(R.string.comment_comment_likeCount, it.likesCount.toString().persianizeNumberString())
                            dislikeCountTextView.text = itemView.context.resources.getString(R.string.comment_comment_likeCount, it.dislikesCount.toString().persianizeNumberString())
                        }

                    }
                }
            }
        }
    }


    class RateViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val avgRateTextView = itemView.findViewById(R.id.rowCommentRate_xeiTextView_avgRate) as TextView
        val rateSumTextView = itemView.findViewById(R.id.rowCommentRate_xeiTextView_rateSum) as TextView
        val commentsCountTextView = itemView.findViewById(R.id.rowCommentRate_xeiTextView_commentsCount) as TextView
        val oneStarProgressBar = itemView.findViewById(R.id.rowCommentRate_rateBarView_oneStar) as RateBarView
        val twoStarProgressBar = itemView.findViewById(R.id.rowCommentRate_rateBarView_twoStar) as RateBarView
        val threeStarProgressBar = itemView.findViewById(R.id.rowCommentRate_rateBarView_threeStar) as RateBarView
        val fourStarProgressBar = itemView.findViewById(R.id.rowCommentRate_rateBarView_fourStar) as RateBarView
        val fiveStarProgressBar = itemView.findViewById(R.id.rowCommentRate_rateBarView_fiveStar) as RateBarView
    }

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById(R.id.rowCommentComment_xeiTextView_title) as TextView
        val authorNameTextView = itemView.findViewById(R.id.rowCommentComment_xeiTextView_authorName) as TextView
        val composedTimeTextView = itemView.findViewById(R.id.rowCommentComment_xeiTextView_composedTime) as TextView
        val hasUserBeenBoughtImageView = itemView.findViewById(R.id.rowCommentComment_imageView_hasUserBeenBought) as ImageView
        val hasUserBeenBoughtTextView = itemView.findViewById(R.id.rowCommentComment_xeiTextView_hasUserBeenBought) as TextView
        val rateRatingBar = itemView.findViewById(R.id.rowCommentComment_materialRatingBar_ratingBar) as MaterialRatingBar
        val commentContentTextView = itemView.findViewById(R.id.rowCommentComment_expandableTextView_commentContent) as ExpandableXeiTextView
        val likeImageView = itemView.findViewById(R.id.rowCommentComment_imageView_like) as ImageView
        val dislikeImageView = itemView.findViewById(R.id.rowCommentComment_imageView_dislike) as ImageView
        val likeCountTextView = itemView.findViewById(R.id.rowCommentComment_xeiTextView_likeCount) as TextView
        val dislikeCountTextView = itemView.findViewById(R.id.rowCommentComment_xeiTextView_dislikeCount) as TextView
    }

}