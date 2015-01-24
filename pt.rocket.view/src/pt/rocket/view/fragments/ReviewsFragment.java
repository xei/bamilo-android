/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.TextView;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.ErrorCode;
import pt.rocket.components.ScrollViewEx;
import pt.rocket.components.ScrollViewEx.OnScrollBottomReachedListener;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.ProductRatingPage;
import pt.rocket.framework.objects.ProductReviewComment;
import pt.rocket.framework.objects.RatingStar;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.DeviceInfoHelper;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.products.GetProductHelper;
import pt.rocket.helpers.products.GetProductReviewsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.Toast;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.LayoutDirection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * @modified manuelsilva
 */
public class ReviewsFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ReviewsFragment.class);
    
    private static final int MAX_REVIEW_COUNT = 10;

    private static ReviewsFragment sPopularityFragment;
    
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

    private boolean firstRequest = false;   
    
    private final int RATING_TYPE_BY_LINE = 3;
    
    private static boolean showRatingForm = true;

    private SharedPreferences sharedPrefs;

    /**
     * Get instance
     * 
     * @return
     */
    public static ReviewsFragment getInstance(Bundle bundle) {
        sPopularityFragment = new ReviewsFragment();
        sPopularityFragment.mProductRatingPage = null;
        String contentUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        sPopularityFragment.mSavedUrl = contentUrl != null ? contentUrl : "";
        sPopularityFragment.setArguments(bundle);
        return sPopularityFragment;
    }

    /**
     * Empty constructor
     */
    public ReviewsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.reviews_fragment,
                0,
                KeyboardState.NO_ADJUST_CONTENT);
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
        } else {
            
            // clean last saved review
            JumiaApplication.cleanReview();
            Log.e(TAG, "ERASE LAST REVIEW!");
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
            
            if(mSavedUrl == null && getArguments() != null && getArguments().containsKey(ConstantsIntentExtra.CONTENT_URL)){
                String contentUrl = getArguments().getString(ConstantsIntentExtra.CONTENT_URL);
                mSavedUrl = contentUrl != null ? contentUrl : "";
            }
            if (JumiaApplication.mIsBound && !mSavedUrl.equalsIgnoreCase("")) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mSavedUrl);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
                showFragmentRetry(this);
            }
        } else {
            showFragmentContent();    
        }
        
        
      
    }

    /**
     * show reviews content
     */
    private void showFragmentContent(){
        // Valdiate saved state
        if(!TextUtils.isEmpty(mSavedUrl) && !TextUtils.isEmpty(selectedProduct.getUrl()) && selectedProduct.getUrl().equals(mSavedUrl)) {
            pageNumber = mSavedPageNumber;
            mProductRatingPage = mSavedProductRatingPage;
        }
        // Set content
        setAppContentLayout();
        
        if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity())) {
            //Validate if country configs allows rating and review, only show write review fragment if both are allowed
            if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) || getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true) ){
                startWriteReviewFragment();
            }

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
        Log.i(TAG, "ON SAVE INSTANCE STATE: ");
        // Validate the current product
        if (selectedProduct != null) {
            String url = selectedProduct.getUrl();
            
            if (!TextUtils.isEmpty(url)) {
                // Save the url, page number and rating
                outState.putString("url", url);
                outState.putInt("page", pageNumber);
                outState.putParcelable("rate", mProductRatingPage);
                mSavedUrl = url;
            } else {
                url = "";
            }
            Log.i(TAG, "ON SAVE INSTANCE STATE: " + url + " " + pageNumber);
        } else {
            Log.i(TAG, "ON SAVE INSTANCE STATE: " + pageNumber);
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

        firstRequest = true;

        // Validate current rating page 
        if (mProductRatingPage != null) displayReviews(mProductRatingPage);
        else triggerReviews(selectedProduct.getUrl(), pageNumber);
        
        if (!DeviceInfoHelper.isTabletInLandscape(getBaseActivity())) {
            setCommentListener();
        }
    }

    private void startWriteReviewFragment() {
        mWriteReviewFragment = new ReviewWriteFragment();
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.CONTENT_URL, mSavedUrl);
        args.putBoolean(CAME_FROM_POPULARITY, true);
        
        
        args.putBoolean(ReviewWriteFragment.RATING_SHOW, showRatingForm);
        
        mWriteReviewFragment.setArguments(args);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_writereview, mWriteReviewFragment);
        ft.commit();
    }

    private void removeWriteReviewFragment() {
        if (mWriteReviewFragment != null) {
           
            if(mWriteReviewFragment instanceof ReviewWriteFragment)
                showRatingForm = ((ReviewWriteFragment)mWriteReviewFragment).getIsShowingRatingForm();
            
            FragmentManager fm = getChildFragmentManager();
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

        TextView productName = (TextView) getView().findViewById(R.id.product_detail_name);
        TextView productPriceNormal = (TextView) getView().findViewById(R.id.product_price_normal);
        TextView productPriceSpecial = (TextView) getView().findViewById(R.id.product_price_special);
        productName.setText(selectedProduct.getBrand()+" "+selectedProduct.getName());
        displayPriceInformation(productPriceNormal, productPriceSpecial);
        
        Bundle params = new Bundle();
        params.putParcelable(TrackerDelegator.PRODUCT_KEY,selectedProduct);
        params.putFloat(TrackerDelegator.RATING_KEY, selectedProduct.getRatingsAverage().floatValue());
        
        TrackerDelegator.trackViewReview(selectedProduct);
    }

    private void displayPriceInformation(TextView productPriceNormal, TextView productPriceSpecial) {
        String unitPrice = selectedProduct.getPrice();
        String specialPrice = selectedProduct.getSpecialPrice();
        /*--if (specialPrice == null) specialPrice = selectedProduct.getMaxSpecialPrice();*/
        displayPriceInfo(productPriceNormal, productPriceSpecial, unitPrice, specialPrice);
    }

    private void displayPriceInfo(TextView productPriceNormal, TextView productPriceSpecial, String unitPrice, String specialPrice) {
        if (specialPrice == null || (unitPrice.equals(specialPrice))) {
            // display only the special price
            productPriceSpecial.setText(unitPrice);
            productPriceNormal.setVisibility(View.GONE);
        } else {
            // display special and normal price
            productPriceSpecial.setText(specialPrice);
            productPriceNormal.setText(unitPrice);
            productPriceNormal.setVisibility(View.VISIBLE);
            productPriceNormal.setPaintFlags(productPriceNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    /**
     * This method sets the rating of a given product using the rating bar component.
     */
    private void setPopularity() {

        // Apply OnScrollBottomReachedListener to outer ScrollView, now that all page scrolls
        ((ScrollViewEx) getView().findViewById(R.id.reviews_scrollview_container)).setOnScrollBottomReached(new OnScrollBottomReachedListener() {

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
        
        //Validate if country configs allows rating and review, only show button if both are allowed
        if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) || getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true) ){
            writeComment.setVisibility(View.VISIBLE);
            writeComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeReview();
                }
            });
        } else {
            writeComment.setVisibility(View.GONE);

        }
       
    }

    /**
     * This method is invoked when the user wants to create a review.
     */
    private void writeReview() {
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.CONTENT_URL, mSavedUrl);
        getBaseActivity().onSwitchFragment(FragmentType.WRITE_REVIEW, args, FragmentController.ADD_TO_BACK_STACK);
    }
    

    protected void onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        switch (eventType) {
        case GET_PRODUCT_REVIEWS_EVENT:
            ProductRatingPage productRatingPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            
            // Validate the current rating page
            if(mProductRatingPage == null) mProductRatingPage = productRatingPage;
            // Append the new page to the current
            //XXX
//            else mProductRatingPage.appendPageRating(productRatingPage);
                
            showFragmentContentContainer();
            displayReviews(productRatingPage);
            break;
        case GET_PRODUCT_EVENT:
          if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
              Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
              getActivity().onBackPressed();
              return;
          } else {
              selectedProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
              showFragmentContent();
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
        return;
    }
    
    protected void onErrorEvent(Bundle bundle){
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic errors
        if(super.handleErrorEvent(bundle)) return;
        
        // Hide Loading from triggers
        showFragmentContentContainer();
        
        switch (eventType) {
        case GET_PRODUCT_REVIEWS_EVENT:
            ProductRatingPage productRatingPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            
            // Valdiate current rating page
            if(mProductRatingPage == null) mProductRatingPage = productRatingPage;
            // Append the new page to the current
            //XXX
//            else mProductRatingPage.appendPageRating(productRatingPage);
                
            displayReviews(productRatingPage);
            break;
        case GET_PRODUCT_EVENT:
            if (!errorCode.isNetworkError()) {
                Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();

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
    
    
    private void displayReviews(ProductRatingPage productRatingPage) {
        ArrayList<ProductReviewComment> reviews = (productRatingPage != null) ? productRatingPage.getReviewComments(): new ArrayList<ProductReviewComment>();
        
        LinearLayout reviewsLin = (LinearLayout) getView().findViewById(R.id.linear_reviews);
        
        if(reviewsLin.getChildCount() > 0)
            reviewsLin.removeAllViews();
        
        if(pageNumber == 1){
            try {
                reviewsLin.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            
        }
        
        LinearLayout productRatingContainer = (LinearLayout) getView().findViewById(R.id.product_ratings_container);
        productRatingContainer.setVisibility(View.VISIBLE);
        
        if(productRatingContainer.getChildCount() > 0)
            productRatingContainer.removeAllViews();
        
        
        insertRatingTypes(productRatingPage.getRatingTypes(), productRatingContainer, true);
        
        int numberReviews = reviews.size();
        // If there are reviews, list them
        // Otherwise, hide reviews list and show empty view
        if (numberReviews > 0) {
            firstRequest = false;
            for (int i = 0; i < numberReviews; i++) {
                final ProductReviewComment review = reviews.get(i);

                final View theInflatedView = inflater.inflate(R.layout.reviews_fragment_item, reviewsLin, false);

                // Hide first divider
                if (i == 0) {
                    theInflatedView.findViewById(R.id.top_review_line).setVisibility(View.GONE);
                }

                final TextView userName = (TextView) theInflatedView.findViewById(R.id.user_review);
                final TextView userDate = (TextView) theInflatedView.findViewById(R.id.date_review);
                final TextView textReview = (TextView) theInflatedView.findViewById(R.id.textreview);
                
                final TextView titleReview = (TextView) theInflatedView.findViewById(R.id.title_review);
                LinearLayout ratingsContainer = (LinearLayout) theInflatedView.findViewById(R.id.ratings_container);

                if(ratingsContainer.getChildCount() > 0)
                    ratingsContainer.removeAllViews();
                
                
                ArrayList<RatingStar> ratingOptionArray = new ArrayList<RatingStar>();
                ratingOptionArray = review.getRatingStars();

                insertRatingTypes(ratingOptionArray, ratingsContainer,false);
                
                final String[] stringCor = review.getDate().split(" ");
                userName.setText(review.getName() + ",");
                userDate.setText(stringCor[0]);
                textReview.setText(review.getComment());

                titleReview.setText(review.getTitle());

                theInflatedView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "review clicked: username = " + userName.getText().toString());

                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantsIntentExtra.REVIEW_TITLE, review.getTitle());
                        bundle.putString(ConstantsIntentExtra.REVIEW_NAME, review.getName());
                        bundle.putString(ConstantsIntentExtra.REVIEW_COMMENT, review.getComment());
                        bundle.putParcelableArrayList(ConstantsIntentExtra.REVIEW_RATING, review.getRatingStars());
                        bundle.putString(ConstantsIntentExtra.REVIEW_DATE, stringCor[0]);
                        getBaseActivity().onSwitchFragment(FragmentType.REVIEW, bundle, true);
                    }
                });

                reviewsLin.addView(theInflatedView);
                isLoadingMore = false;

            }
        } else {
            // Only hide reviews list and show empty on first request
            // Otherwise it was only a empty response for a page after the first 
            if (firstRequest) {
                reviewsLin.setVisibility(View.GONE);
                getView().findViewById(R.id.reviews_empty).setVisibility(View.VISIBLE);
            }
            firstRequest = false;
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
     * insert rate types on the review
     * @param ratingOptionArray
     * @param parent
     */
    private void insertRatingTypes(ArrayList<RatingStar> ratingOptionArray, LinearLayout parent, boolean isBigStar){
        if(ratingOptionArray != null && ratingOptionArray.size() > 0){
            
            // calculate how many lines of rate types the review will have, supossing 3 types for line;
            int rateCount = ratingOptionArray.size();
            int rest = rateCount % RATING_TYPE_BY_LINE;
            int numLines =(int) Math.ceil(rateCount / RATING_TYPE_BY_LINE);
            if(rest == 1)
                numLines = numLines + rest;
            
            int countType = 0;
            
            int starsLayout = R.layout.reviews_fragment_rating_samlltype_item;
            
            if(isBigStar)
                starsLayout = R.layout.reviews_fragment_rating_bigtype_item;
            
            
            for (int i = 0; i < numLines; i++) {
                
                LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,RATING_TYPE_BY_LINE);
                
                typeLine.setOrientation(LinearLayout.HORIZONTAL);
                //#RTL
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if(getResources().getBoolean(R.bool.is_bamilo_specific) && currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
                    typeLine.setLayoutDirection(LayoutDirection.LOCALE);
                }
               
                typeLine.setLayoutParams(params);
                parent.addView(typeLine);
                
                for (int j = countType; j < countType+RATING_TYPE_BY_LINE; j++) {

                    if(j < ratingOptionArray.size()){
                        final View rateTypeView = inflater.inflate(starsLayout, null, false);
                        
                        rateTypeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
                        
                        final TextView ratingTitle = (TextView) rateTypeView.findViewById(R.id.title_type);
                        final RatingBar userRating = (RatingBar) rateTypeView.findViewById(R.id.rating_value);
                          
                        userRating.setRating((float) ratingOptionArray.get(j).getRating());
                        ratingTitle.setText(ratingOptionArray.get(j).getTitle());
                       
                        typeLine.addView(rateTypeView);
                    }
                  
                }
                countType = countType + RATING_TYPE_BY_LINE;
            }
            

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

    
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.fragment_root_retry_button){
            getBaseActivity().onSwitchFragment(FragmentType.POPULARITY, getArguments(), FragmentController.ADD_TO_BACK_STACK);
        }
    }   

    private SharedPreferences getSharedPref(){
        if(sharedPrefs == null){
          //Validate if country configs allows rating and review, only show write review fragment if both are allowed
            sharedPrefs = JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE); 
        }
        return sharedPrefs;
    }
    
}
