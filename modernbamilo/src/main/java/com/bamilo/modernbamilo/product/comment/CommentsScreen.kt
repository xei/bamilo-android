//package com.bamilo.modernbamilo.product.comment
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.view.View
//import android.widget.Button
//import android.widget.ImageButton
//import android.widget.TextView
//import com.bamilo.modernbamilo.R
//import com.bamilo.modernbamilo.app.BaseActivity
//import com.bamilo.modernbamilo.product.comment.submit.startSubmitRateActivity
//import com.bamilo.modernbamilo.util.logging.LogType
//import com.bamilo.modernbamilo.util.logging.Logger
//import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
//import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
//import com.google.gson.Gson
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//
//private const val TAG_DEBUG = "SubmitRateActivity"
//
//private const val KEY_EXTRA_PRODUCT_ID = "KEY_EXTRA_PRODUCT_ID"
//private const val KEY_EXTRA_RATE = "KEY_EXTRA_RATE"
//private const val KEY_EXTRA_RATE_SUM = "KEY_EXTRA_RATE_SUM"
//private const val KEY_EXTRA_COMMENTS_COUNT = "KEY_EXTRA_COMMENTS_COUNT"
//private const val KEY_EXTRA_ONE_STARS_COUNT = "KEY_EXTRA_ONE_STARS_COUNT"
//private const val KEY_EXTRA_TWO_STARS_COUNT = "KEY_EXTRA_TWO_STARS_COUNT"
//private const val KEY_EXTRA_THREE_STARS_COUNT = "KEY_EXTRA_THREE_STARS_COUNT"
//private const val KEY_EXTRA_FOUR_STARS_COUNT = "KEY_EXTRA_FOUR_STARS_COUNT"
//private const val KEY_EXTRA_FIVE_STARS_COUNT = "KEY_EXTRA_FIVE_STARS_COUNT"
//private const val KEY_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT = "KEY_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT"
//private const val KEY_EXTRA_SERIALIZED_COMMENT_VIEWMODEL = "KEY_EXTRA_SERIALIZED_COMMENT_VIEWMODEL"
//
//fun startCommentsActivity(context: Context,
//                  productId: String,
//                  rate: Float,
//                  rateSum: Int,
//                  commentsCount: Int,
//                  oneStarsCount: Float,
//                  twoStarsCount: Float,
//                  threeStarsCount: Float,
//                  fourStarsCount: Float,
//                  fiveStarsCount: Float) {
//
//    val intent = Intent(context, CommentsActivity::class.java).apply {
//        putExtra(KEY_EXTRA_PRODUCT_ID, productId)
//        putExtra(KEY_EXTRA_RATE, rate)
//        putExtra(KEY_EXTRA_RATE_SUM, rateSum)
//        putExtra(KEY_EXTRA_COMMENTS_COUNT, commentsCount)
//        putExtra(KEY_EXTRA_ONE_STARS_COUNT, oneStarsCount)
//        putExtra(KEY_EXTRA_TWO_STARS_COUNT, twoStarsCount)
//        putExtra(KEY_EXTRA_THREE_STARS_COUNT, threeStarsCount)
//        putExtra(KEY_EXTRA_FOUR_STARS_COUNT, fourStarsCount)
//        putExtra(KEY_EXTRA_FIVE_STARS_COUNT, fiveStarsCount)
//        putExtra(KEY_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT, false)
//    }
//
//    context.startActivity(intent).also {
//        Logger.log("CommentActivity has started for product: $productId", TAG_DEBUG)
//    }
//}
//
//fun startCommentsActivityForJustOneDistinctComment(context: Context,
//                                                   productId: String,
//                                                   rate: Float,
//                                                   rateSum: Int,
//                                                   commentsCount: Int,
//                                                   oneStarsCount: Float,
//                                                   twoStarsCount: Float,
//                                                   threeStarsCount: Float,
//                                                   fourStarsCount: Float,
//                                                   fiveStarsCount: Float,
//                                                   commentViewModelSerialized: String) {
//
//    val intent = Intent(context, CommentsActivity::class.java).apply {
//        putExtra(KEY_EXTRA_PRODUCT_ID, productId)
//        putExtra(KEY_EXTRA_RATE, rate)
//        putExtra(KEY_EXTRA_RATE_SUM, rateSum)
//        putExtra(KEY_EXTRA_COMMENTS_COUNT, commentsCount)
//        putExtra(KEY_EXTRA_ONE_STARS_COUNT, oneStarsCount)
//        putExtra(KEY_EXTRA_TWO_STARS_COUNT, twoStarsCount)
//        putExtra(KEY_EXTRA_THREE_STARS_COUNT, threeStarsCount)
//        putExtra(KEY_EXTRA_FOUR_STARS_COUNT, fourStarsCount)
//        putExtra(KEY_EXTRA_FIVE_STARS_COUNT, fiveStarsCount)
//        putExtra(KEY_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT, true)
//        putExtra(KEY_EXTRA_SERIALIZED_COMMENT_VIEWMODEL, commentViewModelSerialized)
//    }
//
//    context.startActivity(intent).also {
//        Logger.log("CommentActivity has started for product: $productId", TAG_DEBUG)
//    }
//}
//
//class CommentsActivity : BaseActivity(), View.OnClickListener {
//
//    private lateinit var mProductId: String
//
//    private val mWebApi = RetrofitHelper.makeWebApi(this, CommentsWebApi::class.java)
//
//    private lateinit var mViewModel: CommentsScreenViewModel
//
//    private lateinit var mCloseBtnImageButton: ImageButton
//    private lateinit var mToolbarTitleTextView: TextView
//    private lateinit var mCommentsListRecyclerView: RecyclerView
//    private lateinit var mSubmitCommentButton: Button
//
//    private lateinit var mPaginationOnScrollListener: PaginationOnScrollListener
//    private var mLoadedCommentsPage = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_comments)
//
//        mProductId = intent.getStringExtra(KEY_EXTRA_PRODUCT_ID)
//
//        createViewModel()
//        findViews()
//        mToolbarTitleTextView.text = resources.getString(R.string.comment_title)
//        setOnClickListeners()
//        initRecyclerView()
//
//        if (!intent.getBooleanExtra(KEY_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT, false)) {
//            loadCommentsNextPage()
//        }
//    }
//
//    private fun createViewModel() {
//        val commentsCount = intent.getIntExtra(KEY_EXTRA_COMMENTS_COUNT, 0)
//        val comments = ArrayList<CommentViewModel>()
//        comments.add(Gson().fromJson(intent.getStringExtra(KEY_EXTRA_SERIALIZED_COMMENT_VIEWMODEL), CommentViewModel::class.java))
//
//        mViewModel = if (!intent.getBooleanExtra(KEY_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT, false))
//            CommentsScreenViewModel(
//                    rate = intent.getFloatExtra(KEY_EXTRA_RATE, 0f),
//                    rateSum = intent.getIntExtra(KEY_EXTRA_RATE_SUM, 5),
//                    commentsCount = commentsCount,
//                    oneStarsAvg = intent.getFloatExtra(KEY_EXTRA_ONE_STARS_COUNT, 0f) / (if (commentsCount != 0) commentsCount else 1),
//                    twoStarsAvg = intent.getFloatExtra(KEY_EXTRA_TWO_STARS_COUNT, 0f) / (if (commentsCount != 0) commentsCount else 1),
//                    threeStarsAvg = intent.getFloatExtra(KEY_EXTRA_THREE_STARS_COUNT, 0f) / (if (commentsCount != 0) commentsCount else 1),
//                    fourStarsAvg = intent.getFloatExtra(KEY_EXTRA_FOUR_STARS_COUNT, 0f) / (if (commentsCount != 0) commentsCount else 1),
//                    fiveStarsAvg = intent.getFloatExtra(KEY_EXTRA_FIVE_STARS_COUNT, 0f / (if (commentsCount != 0) commentsCount else 1))
//            )
//        else
//            CommentsScreenViewModel(
//                    rate = intent.getFloatExtra(KEY_EXTRA_RATE, 0f),
//                    rateSum = intent.getIntExtra(KEY_EXTRA_RATE_SUM, 5),
//                    commentsCount = commentsCount,
//                    oneStarsAvg = intent.getFloatExtra(KEY_EXTRA_ONE_STARS_COUNT, 0f) / (if (commentsCount != 0) commentsCount else 1),
//                    twoStarsAvg = intent.getFloatExtra(KEY_EXTRA_TWO_STARS_COUNT, 0f) / (if (commentsCount != 0) commentsCount else 1),
//                    threeStarsAvg = intent.getFloatExtra(KEY_EXTRA_THREE_STARS_COUNT, 0f) / (if (commentsCount != 0) commentsCount else 1),
//                    fourStarsAvg = intent.getFloatExtra(KEY_EXTRA_FOUR_STARS_COUNT, 0f) / (if (commentsCount != 0) commentsCount else 1),
//                    fiveStarsAvg = intent.getFloatExtra(KEY_EXTRA_FIVE_STARS_COUNT, 0f / (if (commentsCount != 0) commentsCount else 1)),
//                    comments = comments
//            )
//
//    }
//
//    private fun findViews() {
//        mCloseBtnImageButton = findViewById(R.id.layoutToolbar_imageButton_close)
//        mToolbarTitleTextView = findViewById(R.id.layoutToolbar_xeiTextView_title)
//        mCommentsListRecyclerView = findViewById(R.id.activityComments_recyclerView_commentsList)
//        mSubmitCommentButton = findViewById(R.id.activityComments_button_submitComment)
//    }
//
//    private fun setOnClickListeners() {
//        mCloseBtnImageButton.setOnClickListener(this)
//        mSubmitCommentButton.setOnClickListener(this)
//    }
//
//    private fun initRecyclerView() = mCommentsListRecyclerView.run {
//        layoutManager = LinearLayoutManager(this@CommentsActivity)
//        adapter = CommentsAdapter(mViewModel, intent.getBooleanExtra(KEY_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT, false))
//
//        if (!intent.getBooleanExtra(KEY_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT, false)) {
//            mPaginationOnScrollListener = PaginationOnScrollListener(Runnable { loadCommentsNextPage() })
//            addOnScrollListener(mPaginationOnScrollListener)
//        }
//    }
//
//    private fun loadCommentsNextPage() {
//        loadComments(mLoadedCommentsPage + 1)
//    }
//
//    private fun loadComments(page: Int) {
//        val call = mWebApi.getComment(mProductId, page)
//        call.enqueue(object: Callback<ResponseWrapper<GetCommentsResponse>> {
//
//            override fun onResponse(call: Call<ResponseWrapper<GetCommentsResponse>>?, response: Response<ResponseWrapper<GetCommentsResponse>>?) {
//                response?.body()?.metadata?.let {
//                    if(it.comments != null) {
//                        mViewModel.comments.addAll(it.comments)
//                    }
//
//                    mCommentsListRecyclerView.adapter?.notifyDataSetChanged()
//
//                    it.pagination.run {
//                        mLoadedCommentsPage++
//                        if (currentPage == totalPagesCount) {
//                            mPaginationOnScrollListener.isAllCommentsLoaded = true
//                        }
//                    }
//                }
//                mPaginationOnScrollListener.isCommentsPageLoading = false
//            }
//
//            override fun onFailure(call: Call<ResponseWrapper<GetCommentsResponse>>?, t: Throwable?) {
//                mPaginationOnScrollListener.isCommentsPageLoading = false
//                Logger.log("page $page of comments does not loaded: ${t?.message}", TAG_DEBUG, LogType.ERROR)
//            }
//
//        })
//        mPaginationOnScrollListener.isCommentsPageLoading = true
//        Logger.log("request comments page: $page")
//    }
//
//    override fun onClick(clickedView: View?) {
//        when (clickedView?.id) {
//            R.id.layoutToolbar_imageButton_close -> finish()
//            R.id.activityComments_button_submitComment -> startSubmitRateActivity(this, mProductId)
//        }
//    }
//
//    /**
//     * This class is responsible to implements pagination logic and load data lazily.
//     */
//    class PaginationOnScrollListener(private var loadRunnable: Runnable) : RecyclerView.OnScrollListener() {
//        var isCommentsPageLoading: Boolean = false
//        var isAllCommentsLoaded: Boolean = false
//
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//
//            val visibleItemCount: Int = recyclerView.layoutManager?.childCount!!
//            val totalItemCount = recyclerView.layoutManager?.itemCount!!
//            val firstVisibleItemPosition: Int = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
//
//            if (!isCommentsPageLoading && !isAllCommentsLoaded) {
//                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
//                        && firstVisibleItemPosition >= 0
//                /**&& totalItemCount >= PAGE_SIZE**/) {
//                    loadRunnable.run()
//                }
//            }
//        }
//    }
//
//}
