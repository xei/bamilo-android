/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.LayoutDirection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.ScrollViewReachable;
import com.mobile.components.ScrollViewReachable.OnScrollBottomReachedListener;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.helpers.products.GetReviewsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.objects.product.ProductReviewComment;
import com.mobile.newFramework.objects.product.RatingStar;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * @author sergiopereira
 * @modified manuelsilva
 */
public class ReviewsFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = ReviewsFragment.class.getSimpleName();

    public static final String CAME_FROM_POPULARITY = "came_from_popularity";

    private ProductComplete selectedProduct;

    private LayoutInflater inflater;

    private Boolean isLoadingMore = false;

    private ProductRatingPage mProductRatingPage;

    private String mProductSku;

    private boolean firstRequest = false;

    public static final int RATING_TYPE_BY_LINE = 3;

    private SharedPreferences sharedPrefs;

    private LinearLayout reviewsContainer;

    private int pageNumber = 1;

    private int totalPages;

    private static final int REVIEWS_PER_PAGE = 18;

    private ArrayList<ProductReviewComment> reviews;
    //new
    public TextView txHeaderRatings;

    public TextView txHeaderUsers;

    public TextView txAverageRatings;


    public LinearLayout mRatingsBoard;

    public RelativeLayout mProgressBoard;

    public LinearLayout mWriteReview;

    private Resources resources;

    private ProgressBar progressBarFive, progressBarFour, progressBarThree, progressBarTwo, progressBarOne;

    private static final String FIVE_STAR_PROGRESS = "5";
    private static final String FOUR_STAR_PROGRESS = "4";
    private static final String THREE_STAR_PROGRESS = "3";
    private static final String TWO_STAR_PROGRESS = "2";
    private static final String ONE_STAR_PROGRESS = "1";


    /**
     * Get instance
     */
    public static ReviewsFragment getInstance(Bundle bundle) {
        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(bundle);
        fragment.mProductRatingPage = null;
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ReviewsFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.reviews_fragment);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mProductSku = arguments.getString(ConstantsIntentExtra.PRODUCT_SKU);

            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if (parcelableProduct instanceof ProductComplete) {
                selectedProduct = (ProductComplete) parcelableProduct;

                if (mProductSku == null)
                    mProductSku = selectedProduct.getSku();
            }
        }
        // Load saved state
        if (savedInstanceState != null) {
            // TODO
            Print.i(TAG, "ON LOAD SAVED STATE");
            mProductSku = savedInstanceState.getString("url");
            pageNumber = savedInstanceState.getInt("page", 1);
            totalPages = savedInstanceState.getInt("current_page", -1);
            mProductRatingPage = savedInstanceState.getParcelable("rate");
            reviews = savedInstanceState.getParcelableArrayList("reviews");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

        resources = getResources();
        //fill headers
        txHeaderRatings = (TextView) view.findViewById(R.id.ratingsHeader);
        String ratingsHeader = resources.getString(R.string.average_raings_header);
        txHeaderRatings.setText(ratingsHeader + " (" + String.valueOf(selectedProduct.getTotalRatings()) + ")");

        txHeaderUsers = (TextView) view.findViewById(R.id.usersHeader);
        String usersReviewsHeader = resources.getString(R.string.average_reviews_header);
        txHeaderUsers.setText(usersReviewsHeader + " (" + String.valueOf(selectedProduct.getTotalReviews()) + ")");


        //ratings board
        mRatingsBoard = (LinearLayout) view.findViewById(R.id.ratingsBoard);

        //progress bars board
        mProgressBoard = (RelativeLayout) mRatingsBoard.findViewById(R.id.progressBoard);

        //reviews content
        reviewsContainer = (LinearLayout) view.findViewById(R.id.linear_reviews);

        mWriteReview = (LinearLayout) view.findViewById(R.id.lWriteReview);
        mWriteReview.setOnClickListener(this);
    }


    /**
     * Fill average rating and total of ratings in ratingsBoard
     */
    private void fillAverageRatingData(ProductRatingPage productRatingPage) {
        ArrayList<RatingStar> ratingTypes = productRatingPage.getRatingTypes();
        int basedOn = productRatingPage.getmBasedOn();

        txAverageRatings = (TextView) mRatingsBoard.findViewById(R.id.averageValue);

        String averageStar;
        if (ratingTypes == null || ratingTypes.size() == 0)
            averageStar = String.valueOf(selectedProduct.getAvgRating());
        else
            averageStar = String.valueOf(ratingTypes.get(0).getRating());


        String average = String.valueOf(averageStar) + " / " + String.valueOf(basedOn);

        if (ShopSelector.isRtl()) {

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                average = String.valueOf(basedOn) + " \\ " + String.valueOf(averageStar);
            } else {
                average = String.valueOf(averageStar) + " \\ " + String.valueOf(basedOn);
                txAverageRatings.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        txAverageRatings.setText(average);


        TextView txTotalCustomersMessage = (TextView) mRatingsBoard.findViewById(R.id.badgeValue);
        String from = resources.getString(R.string.from_avg_rat);
        String customers = resources.getString(R.string.from_avg_cus);
        txTotalCustomersMessage.setText(from + " " + selectedProduct.getTotalRatings() + " " + customers);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");

        inflater = LayoutInflater.from(getActivity());
        if (selectedProduct == null) {
            if (mProductSku == null && getArguments() != null && getArguments().containsKey(ConstantsIntentExtra.PRODUCT_SKU)) {
                String sku = getArguments().getString(ConstantsIntentExtra.PRODUCT_SKU);
                mProductSku = sku != null ? sku : "";
            }
            if (!TextUtils.isEmpty(mProductSku)) {
                ContentValues values = new ContentValues();
                values.put(GetProductHelper.SKU_TAG, mProductSku);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
                triggerContentEvent(new GetProductHelper(), bundle, this);

            } else {
                showFragmentErrorRetry();
            }
        } else {
            setAppContentLayout();
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE: ");
        // TODO
        outState.putString("url", mProductSku);
        outState.putInt("page", pageNumber);
        outState.putInt("current_page", totalPages);
        outState.putParcelable("rate", mProductRatingPage);
        outState.putParcelableArrayList("reviews", reviews);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    /**
     * This method inflates the current activity layout into the main template.
     */
    private void setAppContentLayout() {
        firstRequest = true;
        setScrollListener();
        // Validate current rating page
        if (mProductRatingPage != null) {
            if (pageNumber == 1) {
                triggerReviews(selectedProduct.getSku(), pageNumber);
            } else {
                setProgressRating(selectedProduct.getTotalRatings());
                fillAverageRatingData(mProductRatingPage);
                displayReviews(mProductRatingPage, false);
            }
        } else {
            triggerReviews(selectedProduct.getSku(), pageNumber);
        }
    }


    /**
     * This method is invoked when the user wants to create a review.
     */
    private void writeReview() {
        // Clean previous write review form values
        JumiaApplication.cleanRatingReviewValues();
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.PRODUCT_SKU, mProductSku);
        args.putParcelable(ConstantsIntentExtra.PRODUCT, selectedProduct);
        getBaseActivity().onSwitchFragment(FragmentType.WRITE_REVIEW, args, FragmentController.ADD_TO_BACK_STACK);
    }


    /**
     * This listener controls whether to load more products
     */
    private void setScrollListener() {

        // Apply OnScrollBottomReachedListener to outer ScrollView, now that all page scrolls
        ((ScrollViewReachable) getView().findViewById(R.id.reviews_scrollview_container)).setOnScrollBottomReached(new OnScrollBottomReachedListener() {

            private View mLoadingLayout;

            @Override
            public void OnScrollBottomReached() {

                Print.i(TAG, "onScrollBottomReached: isLoadingMore = " + isLoadingMore);
                if (!isLoadingMore && pageNumber < totalPages) {

                    isLoadingMore = true;
//                    mLoadingLayout = getView().findViewById(R.id.catalog_loading_more);
//                    mLoadingLayout.setVisibility(View.VISIBLE);
                    getMoreReviews();
                }
            }
        });
    }


    /**
     * This method gets more product reviews by triggering the respective event to get them from the
     * framework.
     */
    private void getMoreReviews() {
        if (selectedProduct.getSku() != null) {
            Print.d(TAG, "getMoreReviews: pageNumber = " + pageNumber);
            pageNumber++;
            triggerReviews(selectedProduct.getSku(), pageNumber);
        }

    }




    private void displayReviews(ProductRatingPage productRatingPage, boolean isFromApi) {
        if (!isFromApi) {
            if (productRatingPage != null && reviews != null) {
                Print.d("POINT", "DO NOTHING");
            } else if (productRatingPage == null) {
                reviews = new ArrayList<>();
                triggerReviews(selectedProduct.getSku(), 1);
            } else { // reviews == null
                reviews = new ArrayList<>();
                triggerReviews(selectedProduct.getSku(), pageNumber);
            }
        } else {
            if (productRatingPage != null) {
                if (reviews != null && pageNumber == 1) {
                    reviews.clear();
                    reviews = productRatingPage.getReviewComments();
                } else if (reviews != null) {
                    if (reviews.size() != productRatingPage.getCommentsCount()) {
                        reviews.addAll(productRatingPage.getReviewComments());
                    }
                } else {
                    reviews = productRatingPage.getReviewComments();
                }
            } else {
                reviews = new ArrayList<>();
            }
        }


        if (reviewsContainer == null) {
            reviewsContainer = (LinearLayout) getView().findViewById(R.id.linear_reviews);
            pageNumber = 1;
        }

        if (reviewsContainer.getChildCount() > 0)
            reviewsContainer.removeAllViews();

        // set the number of grid columns depending on the screen size
        int numColumns = getBaseActivity().getResources().getInteger(R.integer.catalog_list_num_columns);
        // means there's write fragment attached so the reviews list must be only one column
        if (reviews != null && reviews.size() > 0 && getBaseActivity() != null &&
                DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) &&
                (getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) || getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true))) {
            numColumns = 1;
        }

        int numberReviews = reviews.size();
        // If there are reviews, list them
        // Otherwise, hide reviews list and show empty view
        if (numberReviews > 0) {
            firstRequest = false;
            int startPoint = 0;
            //calculate how many empty fields it needs, and add them
            int rest = numberReviews % numColumns;
            int group = (int) Math.ceil(numberReviews / numColumns);
            if (rest > 0)
                group = group + 1;

            for (int j = 0; j < group; j++) {
                LinearLayout gridElement = new LinearLayout(getActivity().getApplicationContext());
                gridElement.setOrientation(LinearLayout.HORIZONTAL);
                LayoutParams gridParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, numColumns);
                //#RTL
                int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                if (ShopSelector.isRtl() && currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    gridElement.setLayoutDirection(LayoutDirection.LOCALE);
                }
                gridElement.setLayoutParams(gridParams);
                for (int i = startPoint; i < startPoint + numColumns; i++) {
                    if (i < reviews.size()) {

                        final ProductReviewComment review = reviews.get(i);

                        final View theInflatedView = inflater.inflate(R.layout.review_list, reviewsContainer, false);

                        final TextView userName = (TextView) theInflatedView.findViewById(R.id.user_review);
                        final TextView userDate = (TextView) theInflatedView.findViewById(R.id.date_review);
                        final TextView textReview = (TextView) theInflatedView.findViewById(R.id.textreview);

                        final TextView titleReview = (TextView) theInflatedView.findViewById(R.id.title_review);

                        LinearLayout ratingsContainer = (LinearLayout) theInflatedView.findViewById(R.id.ratings_container);

                        ArrayList<RatingStar> ratingOptionArray = review.getRatingStars();
                        insertRatingTypes(ratingOptionArray, ratingsContainer, false, review.getAverage());


                        final String[] stringCor = review.getDate().split(" ");
                        userName.setText(review.getName() + ",");
                        userDate.setText(stringCor[0]);
                        textReview.setText(review.getComment());

                        titleReview.setText(review.getTitle());

                        theInflatedView.setOnClickListener(this);
                        theInflatedView.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Print.d(TAG, "review clicked: username = " + userName.getText());
                                goToReview(review, stringCor[0]);
                            }
                        });

                        gridElement.addView(theInflatedView);

                    } else {

                        LinearLayout emptyView = new LinearLayout(getActivity().getApplicationContext());
                        LayoutParams emptyParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
                        emptyView.setLayoutParams(emptyParams);
                        gridElement.addView(emptyView);

                    }

                }
                startPoint = startPoint + numColumns;
                reviewsContainer.addView(gridElement);
                isLoadingMore = false;

            }


        } else {
            // Only hide reviews list and show empty on first request
            // Otherwise it was only a empty response for a page after the first
            if (firstRequest) {
                reviewsContainer.setVisibility(View.GONE);
            }
            firstRequest = false;
        }

        // Validate if the current request size is < MAX_REVIEW_COUNT
        // Or from saved values the current size == comments max count

        // FIXME commented only for testing purpose
        if (reviews.size() < REVIEWS_PER_PAGE || (reviews.size() > REVIEWS_PER_PAGE && reviews.size() == mProductRatingPage.getCommentsCount())) {
            isLoadingMore = true;
        }


        // TRACKER
        Bundle params = new Bundle();
        params.putParcelable(TrackerDelegator.PRODUCT_KEY,selectedProduct);
        params.putFloat(TrackerDelegator.RATING_KEY, (float) selectedProduct.getAvgRating());

        TrackerDelegator.trackViewReview(selectedProduct);


    }


    /**
     * insert rate types on the review
     *
     * @param ratingOptionArray
     * @param parent
     */
    private void insertRatingTypes(ArrayList<RatingStar> ratingOptionArray, LinearLayout parent, boolean isBigStar, int average) {


        int starsLayout = R.layout.reviews_fragment_rating_samlltype_item;

        if (isBigStar)
            starsLayout = R.layout.reviews_fragment_rating_bigtype_item;

        if (ratingOptionArray != null && ratingOptionArray.size() > 0) {

            // calculate how many lines of rate types the review will have, supossing 3 types for line;
            int rateCount = ratingOptionArray.size();
            int rest = rateCount % RATING_TYPE_BY_LINE;
            int numLines = (int) Math.ceil(rateCount / RATING_TYPE_BY_LINE);
            if (rest >= 1)
                numLines = numLines + rest;

            int countType = 0;

            for (int i = 0; i < numLines; i++) {

                LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, RATING_TYPE_BY_LINE);

                typeLine.setOrientation(LinearLayout.HORIZONTAL);
                //#RTL
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (ShopSelector.isRtl() && currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    typeLine.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                }

                typeLine.setLayoutParams(params);
                parent.addView(typeLine);

                for (int j = countType; j < countType + RATING_TYPE_BY_LINE; j++) {

                    if (j < ratingOptionArray.size()) {
                        final View rateTypeView = inflater.inflate(starsLayout, null, false);

                        rateTypeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                        final TextView ratingTitle = (TextView) rateTypeView.findViewById(R.id.title_type);
                        final RatingBar userRating = (RatingBar) rateTypeView.findViewById(R.id.rating_value);

                        userRating.setRating((float) ratingOptionArray.get(j).getRating());
                        if (numLines > 1)    //just show title if has more than 1 rating type
                            ratingTitle.setText(ratingOptionArray.get(j).getTitle());
                        else
                            ratingTitle.setVisibility(View.GONE);

                        typeLine.addView(rateTypeView);
                    }

                }
                countType = countType + RATING_TYPE_BY_LINE;
            }


        } else {
            //if rating Options == null then its a seller review

            if (parent.getChildCount() > 0) {
                parent.removeAllViews();
            }

            LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,ReviewsFragment.RATING_TYPE_BY_LINE);

            typeLine.setOrientation(LinearLayout.HORIZONTAL);

            View rateTypeView = inflater.inflate(starsLayout, null, false);

            rateTypeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            final TextView ratingTitle = (TextView) rateTypeView.findViewById(R.id.title_type);
            final RatingBar userRating = (RatingBar) rateTypeView.findViewById(R.id.rating_value);

            userRating.setRating(average);
            ratingTitle.setVisibility(View.GONE);

            typeLine.addView(rateTypeView);
            parent.addView(typeLine);
        }
    }


    /**
     * Set progressbar progress and it's value number with the info coming from byStar
     *
     * @param maxTotal - total of ratings
     */
    private void setProgressRating(int maxTotal) {
        //get progress bars and value numbers

        progressBarFive = (ProgressBar) mProgressBoard.findViewById(R.id.progressBarFive);
        progressBarFour = (ProgressBar) mProgressBoard.findViewById(R.id.progressBarFour);
        progressBarThree = (ProgressBar) mProgressBoard.findViewById(R.id.progressBarThree);
        progressBarTwo = (ProgressBar) mProgressBoard.findViewById(R.id.progressBarTwo);
        progressBarOne = (ProgressBar) mProgressBoard.findViewById(R.id.progressBarOne);


        progressBarFive.setMax(maxTotal);
        progressBarFour.setMax(maxTotal);
        progressBarThree.setMax(maxTotal);
        progressBarTwo.setMax(maxTotal);
        progressBarOne.setMax(maxTotal);


        //if is rtl, the progress bars shows up with inverted progression
        if (ShopSelector.isRtl() && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            progressBarFive.setProgress(maxTotal - Integer.parseInt(mProductRatingPage.getByStarValue(FIVE_STAR_PROGRESS)));
            progressBarFive.setProgressDrawable(getResources().getDrawable(R.drawable.ratings_progress_inverted));

            progressBarFour.setProgress(maxTotal - Integer.parseInt(mProductRatingPage.getByStarValue(FOUR_STAR_PROGRESS)));
            progressBarFour.setProgressDrawable(getResources().getDrawable(R.drawable.ratings_progress_inverted));

            progressBarThree.setProgress(maxTotal - Integer.parseInt(mProductRatingPage.getByStarValue(THREE_STAR_PROGRESS)));
            progressBarThree.setProgressDrawable(getResources().getDrawable(R.drawable.ratings_progress_inverted));

            progressBarTwo.setProgress(maxTotal - Integer.parseInt(mProductRatingPage.getByStarValue(TWO_STAR_PROGRESS)));
            progressBarTwo.setProgressDrawable(getResources().getDrawable(R.drawable.ratings_progress_inverted));

            progressBarOne.setProgress(maxTotal - Integer.parseInt(mProductRatingPage.getByStarValue(ONE_STAR_PROGRESS)));
            progressBarOne.setProgressDrawable(getResources().getDrawable(R.drawable.ratings_progress_inverted));


        } else {
            progressBarFive.setProgress(Integer.parseInt(mProductRatingPage.getByStarValue(FIVE_STAR_PROGRESS)));
            progressBarFour.setProgress(Integer.parseInt(mProductRatingPage.getByStarValue(FOUR_STAR_PROGRESS)));
            progressBarThree.setProgress(Integer.parseInt(mProductRatingPage.getByStarValue(THREE_STAR_PROGRESS)));
            progressBarTwo.setProgress(Integer.parseInt(mProductRatingPage.getByStarValue(TWO_STAR_PROGRESS)));
            progressBarOne.setProgress(Integer.parseInt(mProductRatingPage.getByStarValue(ONE_STAR_PROGRESS)));
        }

        TextView txValueFive = (TextView) mProgressBoard.findViewById(R.id.fiveValue);
        txValueFive.setText(mProductRatingPage.getByStarValue(FIVE_STAR_PROGRESS));

        TextView txValueFour = (TextView) mProgressBoard.findViewById(R.id.fourValue);
        txValueFour.setText(mProductRatingPage.getByStarValue(FOUR_STAR_PROGRESS));

        TextView txValueThree = (TextView) mProgressBoard.findViewById(R.id.threeValue);
        txValueThree.setText(mProductRatingPage.getByStarValue(THREE_STAR_PROGRESS));


        TextView txValueTwo = (TextView) mProgressBoard.findViewById(R.id.twoValue);
        txValueTwo.setText(mProductRatingPage.getByStarValue(TWO_STAR_PROGRESS));


        TextView txValueOne = (TextView) mProgressBoard.findViewById(R.id.oneValue);
        txValueOne.setText(mProductRatingPage.getByStarValue(ONE_STAR_PROGRESS));

    }

    /**
     * TRIGGERS
     *
     * @author sergiopereira
     */
    private void triggerReviews(String sku, int pageNumber) {

        // Show loading layout for first time
        if (pageNumber == 1) {
            triggerContentEvent(new GetReviewsHelper(), GetReviewsHelper.createBundle(sku,pageNumber), this);
        } else {
            triggerContentEventNoLoading(new GetReviewsHelper(), GetReviewsHelper.createBundle(sku,pageNumber), this);
        }
    }

    /**
     * CALLBACK
     *
     * @author sergiopereira
     */



    private SharedPreferences getSharedPref() {
        if (sharedPrefs == null) {
            //Validate if country configs allows rating and review, only show write review fragment if both are allowed
            sharedPrefs = JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return sharedPrefs;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onStart();
    }

    /**
     * method to go to specific review
     *
     * @param review
     * @param date
     */
    private void goToReview(ProductReviewComment review, String date) {

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.REVIEW_TITLE, review.getTitle());
        bundle.putString(ConstantsIntentExtra.REVIEW_NAME, review.getName());
        bundle.putString(ConstantsIntentExtra.REVIEW_COMMENT, review.getComment());
        bundle.putParcelableArrayList(ConstantsIntentExtra.REVIEW_RATING, review.getRatingStars());
        bundle.putString(ConstantsIntentExtra.REVIEW_DATE, date);
        getBaseActivity().onSwitchFragment(FragmentType.REVIEW, bundle, true);

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Buy button
        if (id == R.id.lWriteReview) {
            writeReview();
        }

    }



    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT type= "+ eventType);
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            if(eventType == EventType.GET_PRODUCT_REVIEWS){
                pageNumber--;
            }
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }



        switch (eventType) {
            case GET_PRODUCT_REVIEWS:
                ProductRatingPage productRatingPage = (ProductRatingPage) baseResponse.getMetadata().getData();

                // Validate the current rating page
                if (mProductRatingPage == null) mProductRatingPage = productRatingPage;

                if (productRatingPage.getCurrentPage() != 0 && productRatingPage.getTotalPages() != 0) {
                    pageNumber = productRatingPage.getCurrentPage();
                    totalPages = productRatingPage.getTotalPages();
                }
                //fill header after getting RatingPage object
                fillAverageRatingData(productRatingPage);
                //added: fill progress bars
                setProgressRating(selectedProduct.getTotalRatings());
                // Append the new page to the current
                displayReviews(productRatingPage, true);
                showFragmentContentContainer();
                break;
            case GET_PRODUCT_DETAIL:
                if (((ProductComplete) baseResponse.getMetadata().getData()).getName() == null) {
                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.product_could_not_retrieved));
                    getActivity().onBackPressed();
                    return;
                } else {
                    selectedProduct = (ProductComplete) baseResponse.getMetadata().getData();
                    setAppContentLayout();
                    // Waiting for the fragment comunication
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFragmentContentContainer();
                        }
                    }, 300);
                }

            default:
                break;
        }
    }



    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.w(TAG, "ON ERROR EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic errors
        if (super.handleErrorEvent(baseResponse)) return;

        // Hide Loading from triggers
        showFragmentContentContainer();

        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "onErrorEvent: type = " + eventType + " code = " + errorCode);

        if(eventType == EventType.GET_PRODUCT_REVIEWS){
            pageNumber--;
        }

        switch (eventType) {
            case GET_PRODUCT_REVIEWS:
                ProductRatingPage productRatingPage = (ProductRatingPage) baseResponse.getMetadata().getData();

                // Validate current rating page
                if (mProductRatingPage == null) mProductRatingPage = productRatingPage;
                // Append the new page to the current
                displayReviews(productRatingPage, true);
                break;
            case GET_PRODUCT_DETAIL:
                if (!ErrorCode.isNetworkError(errorCode)) {
                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.product_could_not_retrieved));

                    try {
                        getBaseActivity().onBackPressed();
                    } catch (IllegalStateException e) {
                        getBaseActivity().popBackStackUntilTag(FragmentType.HOME.toString());
                    }
                    return;
                }
            default:
                break;
        }
    }
}
