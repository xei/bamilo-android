package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;

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
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.CollectionUtils;
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
public class ReviewsFragment extends BaseFragment implements IResponseCallback, OnScrollBottomReachedListener {

    private static final String TAG = ReviewsFragment.class.getSimpleName();

    public static final int RATING_TYPE_BY_LINE = 3;

    private ProductComplete mProduct;

    private LayoutInflater inflater;

    private Boolean isLoadingMore = false;

    private ProductRatingPage mProductRatingPage;

    private String mProductSku;

    private boolean firstRequest = false;

    private SharedPreferences sharedPrefs;

    private int pageNumber = IntConstants.FIRST_PAGE;

    private int totalPages;

    private ArrayList<ProductReviewComment> mReviews;

    private ViewGroup mRatingInfoGroup;
    private ViewGroup mRatingBarGroup;
    private ViewGroup mReviewsGroup;
    private ScrollViewReachable mScrollableView;

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
        super(IS_NESTED_FRAGMENT, R.layout._def_reviews_fragment);
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
                mProduct = (ProductComplete) parcelableProduct;
                if (mProductSku == null)
                    mProductSku = mProduct.getSku();
            }
        }
        // Load saved state
        if (savedInstanceState != null) {
            Print.i(TAG, "ON LOAD SAVED STATE");
            mProductSku = savedInstanceState.getString(RestConstants.URL);
            pageNumber = savedInstanceState.getInt(RestConstants.PAGE, 1);
            totalPages = savedInstanceState.getInt(RestConstants.TOTAL_PAGES, -1);
            mProductRatingPage = savedInstanceState.getParcelable(RestConstants.RATING);
            mReviews = savedInstanceState.getParcelableArrayList(RestConstants.REVIEWS);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Scroll
        mScrollableView = (ScrollViewReachable) view.findViewById(R.id.reviews_scrollview_container);
        // Get rating header
        TextView ratingHeader = (TextView) view.findViewById(R.id.rating_header);
        ratingHeader.setText(getString(R.string.rating_placeholder, mProduct.getTotalRatings()));
        // Get reviews header
        TextView reviewsHeader = (TextView) view.findViewById(R.id.reviews_header);
        reviewsHeader.setText(getString(R.string.reviews_placeholder, mProduct.getTotalReviews()));
        // Get info group
        mRatingInfoGroup = (ViewGroup) view.findViewById(R.id.rate_info_group);
        // Get bar group
        mRatingBarGroup = (ViewGroup) view.findViewById(R.id.rate_bar_group);
        // Get reviews group
        mReviewsGroup = (ViewGroup) view.findViewById(R.id.reviews_group);
        // Write Button
        view.findViewById(R.id.rate_write_button).setOnClickListener(this);
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
        if (mProduct == null) {
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
        outState.putString(RestConstants.URL, mProductSku);
        outState.putInt(RestConstants.PAGE, pageNumber);
        outState.putInt(RestConstants.TOTAL_PAGES, totalPages);
        outState.putParcelable(RestConstants.RATING, mProductRatingPage);
        outState.putParcelableArrayList(RestConstants.REVIEWS, mReviews);
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
                triggerReviews(mProduct.getSku(), pageNumber);
            } else {
                fillRatingBarGroup(mProductRatingPage);
                fillAverageRatingData(mProductRatingPage);
                displayReviews(mProductRatingPage, false);
            }
        } else {
            triggerReviews(mProduct.getSku(), pageNumber);
        }
    }


    /**
     * This method is invoked when the user wants to create a review.
     */
    private void writeReview() {
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.PRODUCT_SKU, mProductSku);
        args.putParcelable(ConstantsIntentExtra.PRODUCT, mProduct);
        getBaseActivity().onSwitchFragment(FragmentType.WRITE_REVIEW, args, FragmentController.ADD_TO_BACK_STACK);
    }


    /**
     * This listener controls whether to load more products
     */
    private void setScrollListener() {
        // Apply OnScrollBottomReachedListener to outer ScrollView, now that all page scrolls
        if(mScrollableView != null) {
            mScrollableView.setOnScrollBottomReached(this);
        }
    }

    @Override
    public void onScrollBottomReached() {
        Print.i(TAG, "onScrollBottomReached: isLoadingMore = " + isLoadingMore);
        if (!isLoadingMore && pageNumber < totalPages) {
            isLoadingMore = true;
            if (mProduct.getSku() != null) {
                Print.d(TAG, "getMoreReviews: pageNumber = " + pageNumber);
                pageNumber++;
                triggerReviews(mProduct.getSku(), pageNumber);
            }
        }
    }


    private void displayReviews(ProductRatingPage productRatingPage, boolean isFromApi) {
        if (!isFromApi) {
            if (productRatingPage != null && mReviews != null) {
                Print.d("POINT", "DO NOTHING");
            } else if (productRatingPage == null) {
                mReviews = new ArrayList<>();
                triggerReviews(mProduct.getSku(), 1);
            } else { // reviews == null
                mReviews = new ArrayList<>();
                triggerReviews(mProduct.getSku(), pageNumber);
            }
        } else {
            if (productRatingPage != null) {
                if (mReviews != null && pageNumber == 1) {
                    mReviews.clear();
                    mReviews = productRatingPage.getReviewComments();
                } else if (mReviews != null) {
                    if (mReviews.size() != productRatingPage.getCommentsCount()) {
                        mReviews.addAll(productRatingPage.getReviewComments());
                    }
                } else {
                    mReviews = productRatingPage.getReviewComments();
                }
            } else {
                mReviews = new ArrayList<>();
            }
        }
        if (mReviewsGroup.getChildCount() > 0) {
            mReviewsGroup.removeAllViews();
        }
        // set the number of grid columns depending on the screen size
        int numColumns = getBaseActivity().getResources().getInteger(R.integer.catalog_list_num_columns);
        // means there's write fragment attached so the reviews list must be only one column
        if (mReviews.size() > 0 && getBaseActivity() != null &&
                DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) &&
                (getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) || getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true))) {
            numColumns = 1;
        }
        int numberReviews = mReviews.size();
        // If there are reviews, list them
        // Otherwise, hide reviews list and show empty view
        if (numberReviews > 0) {
            firstRequest = false;
            int startPoint = 0;
            //calculate how many empty fields it needs, and add them
            int rest = numberReviews % numColumns;
            int group = (int) Math.ceil(numberReviews / numColumns);
            if (rest > 0) {
                group = group + 1;
            }
            for (int j = 0; j < group; j++) {
                LinearLayout gridElement = new LinearLayout(getActivity().getApplicationContext());
                gridElement.setOrientation(LinearLayout.HORIZONTAL);
                LayoutParams gridParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, numColumns);
                gridElement.setLayoutParams(gridParams);
                for (int i = startPoint; i < startPoint + numColumns; i++) {
                    if (i < mReviews.size()) {
                        final ProductReviewComment review = mReviews.get(i);
                        final View theInflatedView = inflater.inflate(R.layout.review_list, mReviewsGroup, false);
                        final TextView userName = (TextView) theInflatedView.findViewById(R.id.review_item_user);
                        final TextView userDate = (TextView) theInflatedView.findViewById(R.id.review_item_date);
                        final TextView textReview = (TextView) theInflatedView.findViewById(R.id.review_item_text);
                        final TextView titleReview = (TextView) theInflatedView.findViewById(R.id.review_item_title);
                        LinearLayout ratingsContainer = (LinearLayout) theInflatedView.findViewById(R.id.review_item_ratings_container);
                        ArrayList<RatingStar> ratingOptionArray = review.getRatingStars();
                        insertRatingTypes(ratingOptionArray, ratingsContainer, false, review.getAverage());
                        userDate.setText(review.getDate());
                        titleReview.setText(review.getTitle());
                        userName.setText(getString(R.string.by_placeholder, review.getName()));
                        textReview.setText(review.getComment());
                        theInflatedView.setOnClickListener(this);
                        theInflatedView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Print.d(TAG, "review clicked: username = " + userName.getText());
                                goToReview(review);
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
                mReviewsGroup.addView(gridElement);
                isLoadingMore = false;
            }
        } else {
            // Only hide reviews list and show empty on first request
            // Otherwise it was only a empty response for a page after the first
            if (firstRequest) {
                mReviewsGroup.setVisibility(View.GONE);
            }
            firstRequest = false;
        }
        // Validate if the current request size is < MAX_REVIEW_COUNT
        // Or from saved values the current size == comments max count
        if (mReviews.size() < IntConstants.MAX_ITEMS_PER_PAGE || (mReviews.size() > IntConstants.MAX_ITEMS_PER_PAGE && mReviews.size() == mProductRatingPage.getCommentsCount())) {
            isLoadingMore = true;
        }
        // Tracking
        TrackerDelegator.trackViewReview(mProduct);
    }


    /**
     * insert rate types on the review
     */
    private void insertRatingTypes(ArrayList<RatingStar> ratingOptionArray, LinearLayout parent, boolean isBigStar, int average) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        int starsLayout = R.layout.reviews_fragment_rating_samlltype_item;
        if (isBigStar) {
            starsLayout = R.layout.reviews_fragment_rating_bigtype_item;
        }
        if (ratingOptionArray != null && ratingOptionArray.size() > 0) {
            // calculate how many lines of rate types the review will have, supossing 3 types for line;
            int rateCount = ratingOptionArray.size();
            int rest = rateCount % RATING_TYPE_BY_LINE;
            int numLines = (int) Math.ceil(rateCount / RATING_TYPE_BY_LINE);
            if (rest >= 1) {
                numLines = numLines + rest;
            }
            int countType = 0;
            for (int i = 0; i < numLines; i++) {
                LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, RATING_TYPE_BY_LINE);
                typeLine.setOrientation(LinearLayout.HORIZONTAL);
                typeLine.setLayoutParams(params);
                parent.addView(typeLine);
                for (int j = countType; j < countType + RATING_TYPE_BY_LINE; j++) {
                    if (j < ratingOptionArray.size()) {
                        final View rateTypeView = inflater.inflate(starsLayout, null, false);
                        rateTypeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        final TextView ratingTitle = (TextView) rateTypeView.findViewById(R.id.title_type);
                        final RatingBar userRating = (RatingBar) rateTypeView.findViewById(R.id.rating_value);
                        if (ShopSelector.isRtl() && currentapiVersion < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            userRating.setRating(userRating.getMax() - (float) ratingOptionArray.get(j).getRating());
                        } else {
                            userRating.setRating((float) ratingOptionArray.get(j).getRating());
                        }
                        //just show title if has more than 1 rating type
                        if (numLines > 1) {
                            ratingTitle.setText(ratingOptionArray.get(j).getTitle());
                        } else {
                            ratingTitle.setVisibility(View.GONE);
                        }
                        typeLine.addView(rateTypeView);
                    }
                }
                countType = countType + RATING_TYPE_BY_LINE;
            }
        } else {
            if (parent.getChildCount() > 0) {
                parent.removeAllViews();
            }
            LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
            typeLine.setOrientation(LinearLayout.HORIZONTAL);
            View rateTypeView = inflater.inflate(starsLayout, null, false);
            rateTypeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            final TextView ratingTitle = (TextView) rateTypeView.findViewById(R.id.title_type);
            final RatingBar userRating = (RatingBar) rateTypeView.findViewById(R.id.rating_value);
            if (ShopSelector.isRtl() && currentapiVersion < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                userRating.setRating(userRating.getMax() - average);
            } else {
                userRating.setRating(average);
            }
            userRating.setRating(average);
            ratingTitle.setVisibility(View.GONE);
            typeLine.addView(rateTypeView);
            parent.addView(typeLine);
        }
    }

    /**
     * Fill average rating and total of ratings in ratingsBoard
     */
    private void fillAverageRatingData(@NonNull  ProductRatingPage productRatingPage) {
        // Get average
        TextView average = (TextView) mRatingInfoGroup.findViewById(R.id.rate_info_average);
        // Get total
        TextView total = (TextView) mRatingInfoGroup.findViewById(R.id.rate_info_total);
        // Get avg and max
        ArrayList<RatingStar> types = productRatingPage.getRatingTypes();
        double avg = CollectionUtils.isEmpty(types) ? mProduct.getAvgRating(): types.get(IntConstants.DEFAULT_POSITION).getRating();
        int max = productRatingPage.getMaxStarSize();
        // Set average
        average.setText(getString(R.string.first_slash_second_placeholder, avg, max));
        // Set total
        total.setText(getString(R.string.rating_average_placeholder, productRatingPage.getBasedOn()));
    }

    /**
     * Fill the rating bar group
     */
    private void fillRatingBarGroup(@NonNull ProductRatingPage productRatingPage) {
        for (int rate = 1; rate <= 5; rate++) {
            int viewId;
            switch (rate) {
                case 1: viewId = R.id.rate_item_one; break;
                case 2: viewId = R.id.rate_item_two; break;
                case 3: viewId = R.id.rate_item_three; break;
                case 4: viewId = R.id.rate_item_four; break;
                case 5: default: viewId = R.id.rate_item_five; break;
            }
            ViewGroup rateBarItem = (ViewGroup) mRatingBarGroup.findViewById(viewId);
            setRatingBar(productRatingPage, rateBarItem, String.valueOf(rate));
        }
    }

    /**
     * Set progressbar progress and it's value number with the info coming from byStar
     */
    private void setRatingBar(@NonNull ProductRatingPage ratingPage, ViewGroup rateBar, String rate) {
        // Get label
        ((TextView) rateBar.findViewById(R.id.rate_item_label)).setText(rate);
        // Get bar
        ProgressBar bar = (ProgressBar) rateBar.findViewById(R.id.rate_item_bar);
        // Case RTL|V8 show base over accent
        if (ShopSelector.isRtl() && DeviceInfoHelper.isPreJellyBeanMR1()) {
            // To show the reverse progress is used max - value
            // Case max empty fill the bar to show the reverse style
            int max = ratingPage.getBasedOn();
            int value = ratingPage.getByStarValue(rate);
            int progress = max == IntConstants.EMPTY ? max = IntConstants.FULL : max - value;
            bar.setMax(max);
            bar.setProgress(progress);
        }
        // Case LTR show accent over base
        else {
            bar.setMax(ratingPage.getBasedOn());
            bar.setProgress(ratingPage.getByStarValue(rate));
        }
        // Get value
        ((TextView) rateBar.findViewById(R.id.rate_item_value)).setText(getString(R.string.parenthesis_placeholder, ratingPage.getByStarValue(rate)));
    }

    private SharedPreferences getSharedPref() {
        if (sharedPrefs == null) {
            //Validate if country configs allows rating and review, only show write review fragment if both are allowed
            sharedPrefs = JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return sharedPrefs;
    }

    /**
     * method to go to specific review
     */
    private void goToReview(ProductReviewComment review) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.REVIEW_TITLE, review.getTitle());
        bundle.putString(ConstantsIntentExtra.REVIEW_NAME, review.getName());
        bundle.putString(ConstantsIntentExtra.REVIEW_COMMENT, review.getComment());
        bundle.putParcelableArrayList(ConstantsIntentExtra.REVIEW_RATING, review.getRatingStars());
        bundle.putString(ConstantsIntentExtra.REVIEW_DATE, review.getDate());
        getBaseActivity().onSwitchFragment(FragmentType.REVIEW, bundle, true);
    }

    /*
     * ########## TRIGGERS ##########
     */

    /**
     * Get reviews
     */
    private void triggerReviews(String sku, int pageNumber) {
        if (pageNumber == IntConstants.FIRST_PAGE) {
            triggerContentEvent(new GetReviewsHelper(), GetReviewsHelper.createBundle(sku, pageNumber), this);
        } else {
            triggerContentEventNoLoading(new GetReviewsHelper(), GetReviewsHelper.createBundle(sku, pageNumber), this);
        }
    }

    /*
     * ########## LISTENERS ##########
     */

    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Buy button
        if (id == R.id.rate_write_button) {
            writeReview();
        }
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

    /*
     * ########## RESPONSE ##########
     */

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
                ProductRatingPage productRatingPage = (ProductRatingPage) baseResponse.getContentData();
                // Validate the current rating page
                if (mProductRatingPage == null) mProductRatingPage = productRatingPage;

                if (productRatingPage.getCurrentPage() != 0 && productRatingPage.getTotalPages() != 0) {
                    pageNumber = productRatingPage.getCurrentPage();
                    totalPages = productRatingPage.getTotalPages();
                }
                //fill header after getting RatingPage object
                fillAverageRatingData(productRatingPage);
                //added: fill progress bars
                fillRatingBarGroup(productRatingPage);
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
                    mProduct = (ProductComplete) baseResponse.getContentData();
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
                ProductRatingPage productRatingPage = (ProductRatingPage) baseResponse.getContentData();
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
