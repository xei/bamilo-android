/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.components.ScrollViewEx;
import pt.rocket.framework.components.ScrollViewEx.OnScrollBottomReachedListener;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.ProductRatingPage;
import pt.rocket.framework.objects.ProductReviewComment;
import pt.rocket.framework.objects.RatingOption;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetProductReviewsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * @modified manuelsilva
 */
public class PopularityFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(PopularityFragment.class);
    
    private static final int MAX_REVIEW_COUNT = 10;

    private static PopularityFragment sPopularityFragment;
    
    public static final String CAME_FROM_POPULARITY = "came_from_popularity";

    private CompleteProduct selectedProduct;

    private LayoutInflater inflater;
    
    private int pageNumber = 1;
    
    private Boolean isLoadingMore = false;
    
    private ProductRatingPage mProductRatingPage;
    
    private Fragment mWriteReviewFragment;

    private String mSavedUrl;

    private int mSavedPageNumber;

    private ProductRatingPage mSavedProductRatingPage;

    /**
     * Get instance
     * 
     * @return
     */
    public static PopularityFragment getInstance() {
        sPopularityFragment = new PopularityFragment();
        sPopularityFragment.mProductRatingPage = null;
        return sPopularityFragment;
    }

    /**
     * Empty constructor
     */
    public PopularityFragment() {
        super(EnumSet.of(EventType.GET_PRODUCT_REVIEWS_EVENT),
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.popularity,
                R.string.reviews,
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        
        // Load saved state
        if(savedInstanceState != null) {
            Log.i(TAG, "ON LOAD SAVED STATE");    
            mSavedUrl = savedInstanceState.getString("url");
            mSavedPageNumber = savedInstanceState.getInt("page", 1);
            mSavedProductRatingPage = savedInstanceState.getParcelable("rate");
            //Log.i(TAG, "ON LOAD SAVED STATE: " + mSavedUrl + " " + mSavedPageNumber);
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
        
        selectedProduct = JumiaApplication.INSTANCE.getCurrentProduct();
        inflater = LayoutInflater.from(getActivity());
        if (selectedProduct == null) {
            Log.e(TAG, "NO CURRENT PRODUCT - SWITCHING TO HOME");
            restartAllFragments();
            return;
        }
        
        // Valdiate saved state
        if(!TextUtils.isEmpty(mSavedUrl) && !TextUtils.isEmpty(selectedProduct.getUrl()) && selectedProduct.getUrl().equals(mSavedUrl)) {
            pageNumber = mSavedPageNumber;
            mProductRatingPage = mSavedProductRatingPage;
        }
        // Set content
        setAppContentLayout();
        
        if (BaseActivity.isTabletInLandscape(getBaseActivity())) {
            Log.i(TAG, "startWriteReviewFragment : ");
            startWriteReviewFragment();
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
        Log.i(TAG, "ON RESUME");
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVED STATE: " + selectedProduct.getUrl() + " " + pageNumber);
        // Validate the current product
        if(selectedProduct != null && !TextUtils.isEmpty(selectedProduct.getUrl())) {
            // Save the url, page number and rating
            outState.putString("url", selectedProduct.getUrl());
            outState.putInt("page", pageNumber);
            outState.putParcelable("rate", mProductRatingPage);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        removeWriteReviewFragment();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    /**
     * This method inflates the current activity layout into the main template.
     */
    private void setAppContentLayout() {
        setViewContent();
        setPopularity();
        
        // Validate current rating page 
        if (mProductRatingPage != null) displayReviews(mProductRatingPage);
        else triggerReviews(selectedProduct.getUrl(), pageNumber);
        
        if (!BaseActivity.isTabletInLandscape(getBaseActivity())) {
            setCommentListener();    
        }
        
    }
    
    private void startWriteReviewFragment(){
        mWriteReviewFragment = new WriteReviewFragment();
        Bundle args = new Bundle();
        args.putBoolean(CAME_FROM_POPULARITY, true);
        mWriteReviewFragment.setArguments(args);
        FragmentManager     fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_writereview, mWriteReviewFragment);
        ft.commit();
    }
    
    private void removeWriteReviewFragment(){
        if(mWriteReviewFragment != null){
            FragmentManager     fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(mWriteReviewFragment);
            ft.commit();
        }

    }
    
    /**
     * This method initializes the views with the product details such as name price and discount
     * price, in case the product has one.
     */
    private void setViewContent() {

        TextView namePop = (TextView) getView().findViewById(R.id.product_info_name_p);
        TextView pricePop = (TextView) getView().findViewById(R.id.price_normal_p);
        TextView discountPop = (TextView) getView().findViewById(R.id.price_w_discount_p);
        namePop.setText(selectedProduct.getBrand()+" "+selectedProduct.getName());
        if (!selectedProduct.getSpecialPrice().equals(selectedProduct.getPrice())) {
            pricePop.setText("" + selectedProduct.getPrice());
            pricePop.setPaintFlags(pricePop.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            pricePop.setTextColor(getResources().getColor(R.color.grey_light));
            pricePop.setTextAppearance(getActivity(), R.style.text_normal);
            discountPop.setText("" + selectedProduct.getSpecialPrice());
            discountPop.setVisibility(View.VISIBLE);
        } else {
            pricePop.setText("" + selectedProduct.getPrice());
            discountPop.setVisibility(View.GONE);
        }
        
        TrackerDelegator.trackViewReview(getActivity().getApplicationContext(), selectedProduct, selectedProduct.getRatingsAverage().floatValue());
    }

    /**
     * This method sets the rating of a given product using the rating bar component.
     */
    private void setPopularity() {

        RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.rating);
        
        ratingBar.setRating(selectedProduct.getRatingsAverage().floatValue());

        ScrollViewEx reviewsScroll = (ScrollViewEx) getView().findViewById(R.id.reviews_scroller);
        reviewsScroll.setOnScrollBottomReached(new OnScrollBottomReachedListener() {

            private View mLoadingLayout;

            @Override
            public void OnScrollBottomReached() {

                Log.i(TAG, "onScrollBottomReached: isLoadingMore = " + isLoadingMore);
                if (!isLoadingMore) {
                    isLoadingMore = true;
                    mLoadingLayout = getView().findViewById(R.id.loadmore);
                    mLoadingLayout.setVisibility(View.VISIBLE);
                    mLoadingLayout.refreshDrawableState();

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
        if (selectedProduct.getUrl() != null) {
            Log.d(TAG, "getMoreRevies: pageNumber = " + pageNumber);
            pageNumber++;
            triggerReviews(selectedProduct.getUrl(), pageNumber);
        }

    }
    
    
    /**
     * This method sets the content view to allow the user to create a review, and checks if the
     * fields are filled before sending a review to the server.
     */
    private void setCommentListener() {
        final Button writeComment = (Button) getView().findViewById(R.id.write_btn);
        writeComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                writeReview();
            }
        });
    }

    /**
     * This method is invoked when the user wants to create a review.
     */
    private void writeReview() {
        getBaseActivity().onSwitchFragment(FragmentType.WRITE_REVIEW, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    

    protected boolean onSuccessEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        ProductRatingPage productRatingPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        
        // Valdiate current rating page
        if(mProductRatingPage == null) mProductRatingPage = productRatingPage;
        // Append the new page to the current
        else mProductRatingPage.appendPageRating(productRatingPage);
            
        showFragmentContentContainer();
        displayReviews(productRatingPage);
        return true;
    }
    
    protected void onErrorEvent(Bundle bundle){
        if(!isVisible()){
            return;
        }
        
        showFragmentContentContainer();
        
        if(getBaseActivity().handleErrorEvent(bundle)){
            return;
        }    
    }
    
    
    private void displayReviews(ProductRatingPage productRatingPage) {
        ArrayList<ProductReviewComment> reviews = productRatingPage.getReviewComments();
        LinearLayout reviewsLin = (LinearLayout) getView().findViewById(R.id.linear_reviews);
        if(pageNumber == 1){
            try {
                reviewsLin.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            
        }
        // Log.i("REVIEW COUNT", " IS " + review.size());
        if (productRatingPage.getCommentsCount() >= 0) {
            TextView reviewsPop = (TextView) getView().findViewById(R.id.reviews);
            reviewsPop.setText("" + productRatingPage.getCommentsCount());
        }
        for (final ProductReviewComment review : reviews) {
            final View theInflatedView = inflater.inflate(R.layout.popularityreview, reviewsLin, false);

            final TextView userName = (TextView) theInflatedView.findViewById(R.id.user_review);
            final TextView userDate = (TextView) theInflatedView.findViewById(R.id.date_review);
            final TextView textReview = (TextView) theInflatedView.findViewById(R.id.textreview);
            final RatingBar userRating = (RatingBar) theInflatedView.findViewById(R.id.quality_rating);
            final TextView titleReview = (TextView) theInflatedView.findViewById(R.id.title_review);
            
            final TextView optionTitle = (TextView) theInflatedView.findViewById(R.id.quality_title_option);
            final TextView appearenceTitle = (TextView) theInflatedView.findViewById(R.id.appearence_title_option);
            final TextView priceTitle = (TextView) theInflatedView.findViewById(R.id.price_title_option);
            
            final RatingBar appearenceRating = (RatingBar) theInflatedView.findViewById(R.id.appearence_rating);
            final RatingBar priceRating = (RatingBar) theInflatedView.findViewById(R.id.price_rating);
            
            
            ArrayList<RatingOption> ratingOptionArray= new ArrayList<RatingOption>();
            ratingOptionArray = review.getRatingOptions();

            final String[] stringCor = review.getDate().split(" ");
            userName.setText(review.getName() + ",");
            userDate.setText(stringCor[0]);
            textReview.setText(review.getComments());
            
            if(ratingOptionArray.size()>0){
                priceRating.setRating((float) ratingOptionArray.get(0).getRating());
                priceTitle.setText(ratingOptionArray.get(0).getTitle());
            }
            
            if(ratingOptionArray.size()>1){
                appearenceRating.setRating((float) ratingOptionArray.get(1).getRating());
                appearenceTitle.setText(ratingOptionArray.get(1).getTitle());
            } else {
            	appearenceRating.setVisibility(View.GONE);
            	appearenceTitle.setVisibility(View.GONE);
            }
            
            if(ratingOptionArray.size()>2){
                userRating.setRating((float) ratingOptionArray.get(2).getRating());
                optionTitle.setText(ratingOptionArray.get(2).getTitle());
            } else {
            	userRating.setVisibility(View.GONE);
            	optionTitle.setVisibility(View.GONE);
            }
        
            titleReview.setText(review.getTitle());
            
            theInflatedView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d(TAG, "review clicked: username = " + userName.getText().toString());
                    
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.REVIEW_TITLE, review.getTitle());
                    bundle.putString(ConstantsIntentExtra.REVIEW_NAME, review.getName());
                    bundle.putString(ConstantsIntentExtra.REVIEW_COMMENT, review.getComments());
                    bundle.putDouble(ConstantsIntentExtra.REVIEW_RATING, review.getRating());
                    bundle.putString(ConstantsIntentExtra.REVIEW_DATE, stringCor[0]);
                    ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.REVIEW, bundle, true);
                }
            });

            reviewsLin.addView(theInflatedView);
            isLoadingMore = false;

        }
        View loadingLayout = getView().findViewById(R.id.loadmore);
        loadingLayout.setVisibility(View.GONE);
        loadingLayout.refreshDrawableState();
        
        // Validate if the current request size is < MAX_REVIEW_COUNT
        // Or from saved values the current size == comments max count
        if (reviews.size() < MAX_REVIEW_COUNT || (reviews.size() > MAX_REVIEW_COUNT && reviews.size() == mProductRatingPage.getCommentsCount())) {
            isLoadingMore = true;
        }

    }
    
    /**
     * TRIGGERS
     * @author sergiopereira
     */
    private void triggerReviews(String url, int pageNumber) {
        Bundle bundle = new Bundle();
        bundle.putString(GetProductReviewsHelper.PRODUCT_URL, url);
        bundle.putInt(GetProductReviewsHelper.PAGE_NUMBER, pageNumber);
        // Show laoding layout for first time
        if(pageNumber == 1) triggerContentEvent(new GetProductReviewsHelper(), bundle, mCallBack);
        else triggerContentEventWithNoLoading(new GetProductReviewsHelper(), bundle, mCallBack);
    }
    
    /**
     * CALLBACK
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {
        
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {                
            onSuccessEvent(bundle);
        }
    };

}
