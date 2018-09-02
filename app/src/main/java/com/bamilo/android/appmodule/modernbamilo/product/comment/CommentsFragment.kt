package com.bamilo.android.appmodule.modernbamilo.product.comment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.bamilo.android.R
import com.bamilo.android.appmodule.modernbamilo.util.logging.LogType
import com.bamilo.android.appmodule.modernbamilo.util.logging.Logger
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.ResponseWrapper
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG_DEBUG = "SubmitRateActivity"

private const val ARG_PRODUCT_ID = "ARG_EXTRA_PRODUCT_ID"
private const val ARG_RATE = "ARG_EXTRA_RATE"
private const val ARG_RATE_SUM = "ARG_EXTRA_RATE_SUM"
private const val ARG_COMMENTS_COUNT = "ARG_EXTRA_COMMENTS_COUNT"
private const val ARG_ONE_STARS_COUNT = "ARG_EXTRA_ONE_STARS_COUNT"
private const val ARG_TWO_STARS_COUNT = "ARG_EXTRA_TWO_STARS_COUNT"
private const val ARG_THREE_STARS_COUNT = "ARG_EXTRA_THREE_STARS_COUNT"
private const val ARG_FOUR_STARS_COUNT = "ARG_EXTRA_FOUR_STARS_COUNT"
private const val ARG_FIVE_STARS_COUNT = "ARG_EXTRA_FIVE_STARS_COUNT"
private const val ARG_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT = "ARG_EXTRA_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT"
private const val ARG_SERIALIZED_COMMENT_VIEWMODEL = "ARG_EXTRA_SERIALIZED_COMMENT_VIEWMODEL"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CommentsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CommentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CommentsFragment : Fragment(), View.OnClickListener {
    private lateinit var mProductId: String

    private lateinit var mWebApi: CommentsWebApi

    private lateinit var mViewModel: CommentsScreenViewModel

    private lateinit var mCloseBtnImageButton: ImageButton
    private lateinit var mToolbarTitleTextView: TextView
    private lateinit var mCommentsListRecyclerView: RecyclerView
    private lateinit var mSubmitCommentButton: Button

    private lateinit var mPaginationOnScrollListener: PaginationOnScrollListener
    private var mLoadedCommentsPage = 0

    private var mIsThisScreenJustForOneDistinctComment = false

    private var listener: OnSubmitCommentButtonClickListener? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommentsFragment.
         */
        @JvmStatic
        fun newInstance(productId: String,
                        rate: Float,
                        rateSum: Int,
                        commentsCount: Int,
                        oneStarsCount: Float,
                        twoStarsCount: Float,
                        threeStarsCount: Float,
                        fourStarsCount: Float,
                        fiveStarsCount: Float) =
                CommentsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PRODUCT_ID, productId)
                        putFloat(ARG_RATE, rate)
                        putInt(ARG_RATE_SUM, rateSum)
                        putInt(ARG_COMMENTS_COUNT, commentsCount)
                        putFloat(ARG_ONE_STARS_COUNT, oneStarsCount)
                        putFloat(ARG_TWO_STARS_COUNT, twoStarsCount)
                        putFloat(ARG_THREE_STARS_COUNT, threeStarsCount)
                        putFloat(ARG_FOUR_STARS_COUNT, fourStarsCount)
                        putFloat(ARG_FIVE_STARS_COUNT, fiveStarsCount)
                        putBoolean(ARG_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT, false)
                    }
                }

        @JvmStatic
        fun newInstanceForJustOneDistinctComment(productId: String,
                                                 rate: Float,
                                                 rateSum: Int,
                                                 commentsCount: Int,
                                                 oneStarsCount: Float,
                                                 twoStarsCount: Float,
                                                 threeStarsCount: Float,
                                                 fourStarsCount: Float,
                                                 fiveStarsCount: Float,
                                                 commentViewModelSerialized: String) =
                CommentsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PRODUCT_ID, productId)
                        putFloat(ARG_RATE, rate)
                        putInt(ARG_RATE_SUM, rateSum)
                        putInt(ARG_COMMENTS_COUNT, commentsCount)
                        putFloat(ARG_ONE_STARS_COUNT, oneStarsCount)
                        putFloat(ARG_TWO_STARS_COUNT, twoStarsCount)
                        putFloat(ARG_THREE_STARS_COUNT, threeStarsCount)
                        putFloat(ARG_FOUR_STARS_COUNT, fourStarsCount)
                        putFloat(ARG_FIVE_STARS_COUNT, fiveStarsCount)
                        putBoolean(ARG_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT, true)
                        putString(ARG_SERIALIZED_COMMENT_VIEWMODEL, commentViewModelSerialized)
                    }
                }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        createViewModel()
        context?.let {
            mWebApi = RetrofitHelper.makeWebApi(it, CommentsWebApi::class.java)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSubmitCommentButtonClickListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    private fun getArgs() {
        arguments?.let {
            mProductId = it.getString(ARG_PRODUCT_ID)
            mIsThisScreenJustForOneDistinctComment = it.getBoolean(ARG_IS_THIS_SCREEN_JUST_FOR_ONE_DISTINCT_COMMENT)

        }
    }

    private fun createViewModel() {
        arguments?.let {
            val commentsCount = it.getInt(ARG_COMMENTS_COUNT, 0)
            val comments = ArrayList<CommentViewModel>()
            comments.add(Gson().fromJson(it.getString(ARG_SERIALIZED_COMMENT_VIEWMODEL), CommentViewModel::class.java))

            mViewModel = if (!mIsThisScreenJustForOneDistinctComment)
                CommentsScreenViewModel(
                        rate = it.getFloat(ARG_RATE, 0f),
                        rateSum = it.getInt(ARG_RATE_SUM, 5),
                        commentsCount = commentsCount,
                        oneStarsAvg = it.getFloat(ARG_ONE_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        twoStarsAvg = it.getFloat(ARG_TWO_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        threeStarsAvg = it.getFloat(ARG_THREE_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        fourStarsAvg = it.getFloat(ARG_FOUR_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        fiveStarsAvg = it.getFloat(ARG_FIVE_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1)
                )
            else
                CommentsScreenViewModel(
                        rate = it.getFloat(ARG_RATE, 0f),
                        rateSum = it.getInt(ARG_RATE_SUM, 5),
                        commentsCount = commentsCount,
                        oneStarsAvg = it.getFloat(ARG_ONE_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        twoStarsAvg = it.getFloat(ARG_TWO_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        threeStarsAvg = it.getFloat(ARG_THREE_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        fourStarsAvg = it.getFloat(ARG_FOUR_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        fiveStarsAvg = it.getFloat(ARG_FIVE_STARS_COUNT, 0f) * 100 / (if (commentsCount != 0) commentsCount else 1),
                        comments = comments
                )

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.activity_comments, container, false)
        findViews(rootView)
        mToolbarTitleTextView.text = resources.getString(R.string.comment_title)
        setOnClickListeners()
        initRecyclerView()
        return rootView
    }

    private fun findViews(rootView: View) {
        mCloseBtnImageButton = rootView.findViewById(R.id.layoutToolbar_imageButton_close)
        mToolbarTitleTextView = rootView.findViewById(R.id.layoutToolbar_xeiTextView_title)
        mCommentsListRecyclerView = rootView.findViewById(R.id.activityComments_recyclerView_commentsList)
        mSubmitCommentButton = rootView.findViewById(R.id.activityComments_button_submitComment)
    }

    private fun setOnClickListeners() {
        mCloseBtnImageButton.setOnClickListener(this)
        mSubmitCommentButton.setOnClickListener(this)
    }

    private fun initRecyclerView() = mCommentsListRecyclerView.run {
        layoutManager = LinearLayoutManager(context)
        adapter = CommentsAdapter(mViewModel, mIsThisScreenJustForOneDistinctComment)

        if (!mIsThisScreenJustForOneDistinctComment) {
            mPaginationOnScrollListener = PaginationOnScrollListener(Runnable { loadCommentsNextPage() })
            addOnScrollListener(mPaginationOnScrollListener)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!mIsThisScreenJustForOneDistinctComment) {
            loadCommentsNextPage()
        }
    }

    private fun loadCommentsNextPage() {
        loadComments(mLoadedCommentsPage + 1)
    }

    private fun loadComments(page: Int) {
        val call = mWebApi.getComment(mProductId, page)
        call.enqueue(object: Callback<ResponseWrapper<GetCommentsResponse>> {

            override fun onResponse(call: Call<ResponseWrapper<GetCommentsResponse>>?, response: Response<ResponseWrapper<GetCommentsResponse>>?) {
                response?.body()?.metadata?.let {
                    if(it.comments != null) {
                        mViewModel.comments.addAll(it.comments)
                    }

                    mCommentsListRecyclerView.adapter?.notifyDataSetChanged()

                    it.pagination.run {
                        mLoadedCommentsPage++
                        if (currentPage == totalPagesCount) {
                            mPaginationOnScrollListener.isAllCommentsLoaded = true
                        }
                    }
                }
                mPaginationOnScrollListener.isCommentsPageLoading = false
            }

            override fun onFailure(call: Call<ResponseWrapper<GetCommentsResponse>>?, t: Throwable?) {
                mPaginationOnScrollListener.isCommentsPageLoading = false
                Logger.log("page $page of comments does not loaded: ${t?.message}", TAG_DEBUG, LogType.ERROR)
            }

        })
        mPaginationOnScrollListener.isCommentsPageLoading = true
        Logger.log("request comments page: $page")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClick(clickedView: View?) {
        when (clickedView?.id) {
            R.id.layoutToolbar_imageButton_close -> activity?.onBackPressed()
            R.id.activityComments_button_submitComment -> listener?.onSubmitCommentButtonClicked()
        }
    }

    /**
     * This class is responsible to implements pagination logic and load data lazily.
     */
    class PaginationOnScrollListener(private var loadRunnable: Runnable) : RecyclerView.OnScrollListener() {
        var isCommentsPageLoading: Boolean = false
        var isAllCommentsLoaded: Boolean = false

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val visibleItemCount: Int = recyclerView.layoutManager?.childCount!!
            val totalItemCount = recyclerView.layoutManager?.itemCount!!
            val firstVisibleItemPosition: Int = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (!isCommentsPageLoading && !isAllCommentsLoaded) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                /**&& totalItemCount >= PAGE_SIZE**/) {
                    loadRunnable.run()
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnSubmitCommentButtonClickListener {
        fun onSubmitCommentButtonClicked()
    }

}
