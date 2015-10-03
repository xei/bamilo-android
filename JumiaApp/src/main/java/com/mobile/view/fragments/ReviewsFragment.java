package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import android.widget.LinearLayout.LayoutParams;
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
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.product.ProductRatingPage;
import com.mobile.newFramework.objects.product.ProductReviewComment;
import com.mobile.newFramework.objects.product.RatingStar;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * @author sergiopereira
 * @modified manuelsilva
 */
public class ReviewsFragment extends BaseFragment {

    private static final String TAG = ReviewsFragment.class.getSimpleName();
    
    public static final String CAME_FROM_POPULARITY = "came_from_popularity";

    private ProductComplete selectedProduct;

    private LayoutInflater inflater;
    
    private Boolean isLoadingMore = false;
    
    private ProductRatingPage mProductRatingPage;
    
    private Fragment mWriteReviewFragment;

    private Fragment mSellerWriteReviewFragment;

    private String mProductSku;

    private boolean firstRequest = false;   
    
    public static final int RATING_TYPE_BY_LINE = 3;
    
    private boolean showRatingForm = true;

    private SharedPreferences sharedPrefs;
    
    private boolean isProductRating = true;

    private TextView productName;
    
    private TextView productPriceNormal;
    
    private TextView productPriceSpecial;
    
    private RelativeLayout sellerRatingContainer;
    
    private LinearLayout productRatingContainer;
    
    private LinearLayout reviewsContainer;
    
    private View marginLandscape;
    
    private RatingBar sellerRatingBar;
    
    private TextView sellerRatingCount;
    
    private TextView writeReviewTitle;
    
    private View centerPoint;
    
    private int pageNumber = 1;
    
    private int totalPages;

    private static final int REVIEWS_PER_PAGE = 18;
    
    private ArrayList<ProductReviewComment> reviews;
    
    private String mSellerId = "";

    public static final String SELLER_NAME = "sellerName";

    public static final String SELLER_COMMENT_COUNT = "sellerCommentCount";

    public static final String SELLER_AVERAGE = "sellerAverage";

    /**
     * Get instance
     */
    public static ReviewsFragment getInstance(Bundle bundle) {
        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(bundle);
        fragment.mProductRatingPage = null;
        fragment.showRatingForm = true;
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ReviewsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Product,
                R.layout.reviews_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
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
        if(arguments != null) {
            // Get review list type from arguments
            if(arguments.containsKey(ConstantsIntentExtra.REVIEW_TYPE)) {
                isProductRating = arguments.getBoolean(ConstantsIntentExtra.REVIEW_TYPE);
            }
            mProductSku = arguments.getString(ConstantsIntentExtra.PRODUCT_SKU);
            mSellerId = arguments.getString(ProductDetailsFragment.SELLER_ID);

            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if(parcelableProduct instanceof ProductComplete){
                selectedProduct = (ProductComplete)parcelableProduct;
            }
        }
        // Load saved state
        if(savedInstanceState != null) {
            // TODO
            Print.i(TAG, "ON LOAD SAVED STATE");
            mProductSku = savedInstanceState.getString("url");
            pageNumber = savedInstanceState.getInt("page", 1);
            totalPages = savedInstanceState.getInt("current_page", -1);
            mProductRatingPage = savedInstanceState.getParcelable("rate");
            reviews = savedInstanceState.getParcelableArrayList("reviews");
            isProductRating =  savedInstanceState.getBoolean(ConstantsIntentExtra.REVIEW_TYPE);
            mSellerId =  savedInstanceState.getString(ProductDetailsFragment.SELLER_ID);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get views
        productName = (TextView) view.findViewById(R.id.product_detail_name);
        productPriceNormal = (TextView) view.findViewById(R.id.pdv_text_price);
        productPriceSpecial = (TextView) view.findViewById(R.id.product_price_special);
        productRatingContainer = (LinearLayout) view.findViewById(R.id.product_ratings_container);
        
        centerPoint = view.findViewById(R.id.center_point);
        //seller
        sellerRatingContainer = (RelativeLayout) view.findViewById(R.id.seller_reviews_rating_container);
        sellerRatingBar = (RatingBar) view.findViewById(R.id.seller_reviews_item_rating);
        sellerRatingCount = (TextView) view.findViewById(R.id.seller_reviews_item_reviews);

        TextView emptyScreenText = (TextView) view.findViewById(R.id.fragment_root_empty_text);
        if(isProductRating){
            emptyScreenText.setText(getResources().getString(R.string.reviews_empty));
        } else {
            emptyScreenText.setText(getResources().getString(R.string.reviews_empty_seller));
        }
        
        reviewsContainer = (LinearLayout) view.findViewById(R.id.linear_reviews);
        marginLandscape = view.findViewById(R.id.margin_landscape);
        if(reviews == null) {
            reviews = new ArrayList<>();
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
        Print.i(TAG, "ON START");

        inflater = LayoutInflater.from(getActivity());
        if (selectedProduct == null) {
            if(mProductSku == null && getArguments() != null && getArguments().containsKey(ConstantsIntentExtra.PRODUCT_SKU)){
                String sku = getArguments().getString(ConstantsIntentExtra.PRODUCT_SKU);
                mProductSku = sku != null ? sku : "";
            }
            if (!TextUtils.isEmpty(mProductSku)) {
                ContentValues values = new ContentValues();
                values.put(GetProductHelper.SKU_TAG, mProductSku);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);

            } else {
                showFragmentErrorRetry();
            }
        } else {
            checkReviewsTypeVisibility();
            showFragmentContent();
            showFragmentContentOfSeller();
        }
    }

    /**
     * show reviews content
     */
    private void showFragmentContent(){
        // Set content
        setAppContentLayout();
        
        if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity())) {

            if(isProductRating){
                //Validate if country configs allows rating and review, only show write review fragment if both are allowed
                if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) || getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)){
                    centerPoint.setVisibility(View.VISIBLE);
                    marginLandscape.setVisibility(View.GONE);
                    startWriteReviewFragment();
                } else {
                    //hide center point in order to list fill all the screen
                    centerPoint.setVisibility(View.GONE);
                    marginLandscape.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * show nested write review fragment if this is seller reviews
     */
    private void showFragmentContentOfSeller(){
        if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) && mProductRatingPage != null && !isProductRating) {
            startSellerWriteReviewFragment();
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
        outState.putBoolean(ConstantsIntentExtra.REVIEW_TYPE, isProductRating);
        outState.putString(ProductDetailsFragment.SELLER_ID, mSellerId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        removeWriteReviewFragment();
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

        // Validate current rating page 
        if (mProductRatingPage != null){
            if(pageNumber == 1){
                triggerReviews(selectedProduct.getSku() , pageNumber);
            } else {
                displayReviews(mProductRatingPage, false);
            }
        } else {
            triggerReviews(selectedProduct.getSku(), pageNumber);
        }
    }

    /**
     * instanciates and replace fragment in order to show write seller review
     */
    private void startWriteReviewFragment() {
        if(isProductRating){
            Bundle args = new Bundle();
            args.putString(ConstantsIntentExtra.PRODUCT_SKU, mProductSku);
            args.putBoolean(CAME_FROM_POPULARITY, true);
            args.putBoolean(ReviewWriteNestedFragment.RATING_SHOW, showRatingForm);
            mWriteReviewFragment = ReviewWriteNestedFragment.getInstance(args);
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_writereview, mWriteReviewFragment);
            ft.commit();
        }
        
    }

    /**
     * instanciate and replace fragment in order to show write seller review
     */
    private void startSellerWriteReviewFragment() {
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.PRODUCT_SKU, mProductSku);
        args.putString(ProductDetailsFragment.SELLER_ID, mSellerId);
        args.putString(SELLER_NAME, mProductRatingPage.getSellerName());
        args.putInt(SELLER_COMMENT_COUNT, mProductRatingPage.getCommentsCount());
        args.putInt(SELLER_AVERAGE, mProductRatingPage.getAverage());
        //create seller review nested fragment

        mSellerWriteReviewFragment = WriteSellerReviewNestedFragment.getInstance(args);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_writereview, mSellerWriteReviewFragment);
        ft.commit();

    }
    
    private void removeWriteReviewFragment() {
        if (mWriteReviewFragment != null) {
           
            if(mWriteReviewFragment instanceof ReviewWriteNestedFragment)
                showRatingForm = ((ReviewWriteNestedFragment)mWriteReviewFragment).getIsShowingRatingForm();
            
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(mWriteReviewFragment);
            ft.commit();
        } else if (mSellerWriteReviewFragment != null){

            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(mSellerWriteReviewFragment);
            ft.commit();

        }
    }
    

    private void displayPriceInformation(TextView productPriceNormal, TextView productPriceSpecial) {
        if (selectedProduct.hasDiscount()) {
            // display special and normal price
            productPriceSpecial.setText(CurrencyFormatter.formatCurrency(selectedProduct.getSpecialPrice()));
            productPriceNormal.setText(CurrencyFormatter.formatCurrency(selectedProduct.getPrice()));
            productPriceNormal.setVisibility(View.VISIBLE);
            productPriceNormal.setPaintFlags(productPriceNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // display only the special price
            productPriceSpecial.setText(CurrencyFormatter.formatCurrency(selectedProduct.getPrice()));
            productPriceNormal.setVisibility(View.GONE);
        }
    }

    /**
     * This method sets the content view to allow the user to create a review, and checks if the
     * fields are filled before sending a review to the server.
     */
    private void setCommentListener() {
        
        final Button writeComment = (Button) getView().findViewById(R.id.write_btn);
        final TextView writeReviewTitle = (TextView) getView().findViewById(R.id.write_title);
        
        //Validate if country configs allows rating and review, only show button if both are allowed
        if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) || getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true) && isProductRating ){
            writeComment.setVisibility(View.VISIBLE);
            writeReviewTitle.setVisibility(View.VISIBLE);
            writeComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // clean last saved review
                    JumiaApplication.cleanRatingReviewValues();
                    JumiaApplication.cleanSellerReviewValues();
                    JumiaApplication.INSTANCE.setFormReviewValues(null);
                    writeReview();
                }
            });
        } else if(!isProductRating){

            writeComment.setVisibility(View.VISIBLE);
            writeReviewTitle.setVisibility(View.VISIBLE);
            writeComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // clean last saved review
                    JumiaApplication.cleanRatingReviewValues();
                    JumiaApplication.cleanSellerReviewValues();
                    JumiaApplication.INSTANCE.setFormReviewValues(null);
                    writeReview();
                }
            });

        }else {
            writeReviewTitle.setVisibility(View.GONE);
            writeComment.setVisibility(View.GONE);

        }
       
    }
    
    /**
     * This method is invoked when the user wants to create a review.
     */
    private void writeReview() {
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.PRODUCT_SKU, mProductSku);
        if(isProductRating){
            args.putParcelable(ConstantsIntentExtra.PRODUCT, selectedProduct);
            getBaseActivity().onSwitchFragment(FragmentType.WRITE_REVIEW, args, FragmentController.ADD_TO_BACK_STACK);
        } else {
            args.putString(ProductDetailsFragment.SELLER_ID, mSellerId);
            args.putString(SELLER_NAME, mProductRatingPage.getSellerName());
            args.putInt(SELLER_COMMENT_COUNT, mProductRatingPage.getCommentsCount());
            args.putInt(SELLER_AVERAGE, mProductRatingPage.getAverage());

            getBaseActivity().onSwitchFragment(FragmentType.WRITE_REVIEW_SELLER, args, FragmentController.ADD_TO_BACK_STACK);
        }
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
                    mLoadingLayout = getView().findViewById(R.id.catalog_loading_more);
                    mLoadingLayout.setVisibility(View.VISIBLE);
//                    mLoadingLayout.refreshDrawableState();

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
            Print.d(TAG, "getMoreRevies: pageNumber = " + pageNumber);
            pageNumber++;
            triggerReviews(selectedProduct.getSku(), pageNumber);
        }

    }

    protected void onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        switch (eventType) {
        case GET_PRODUCT_REVIEWS:
            ProductRatingPage productRatingPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            
            // Validate the current rating page
            if(mProductRatingPage == null) mProductRatingPage = productRatingPage;
            
            if(productRatingPage.getCurrentPage() != 0 && productRatingPage.getTotalPages() != 0){
                pageNumber = productRatingPage.getCurrentPage();
                totalPages = productRatingPage.getTotalPages();
            }
            showFragmentContentOfSeller();
            // Append the new page to the current
            displayReviews(productRatingPage, true);
            showFragmentContentContainer();
            break;
        case GET_PRODUCT_DETAIL:
          if (((ProductComplete) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
              Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
              getActivity().onBackPressed();
              return;
          } else {
              selectedProduct = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
              setHeaderReviews();
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
    }
    
    protected void onErrorEvent(Bundle bundle){
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic errors
        if(super.handleErrorEvent(bundle)) return;
        
        // Hide Loading from triggers
        showFragmentContentContainer();
        
        switch (eventType) {
        case GET_PRODUCT_REVIEWS:
            ProductRatingPage productRatingPage = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            
            // Valdiate current rating page
            if(mProductRatingPage == null) mProductRatingPage = productRatingPage;
            // Append the new page to the current
            displayReviews(productRatingPage, true);
            break;
        case GET_PRODUCT_DETAIL:
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
    
    
    private void displayReviews(ProductRatingPage productRatingPage, boolean isFromApi) {
        if(!isFromApi){
            if(productRatingPage != null && reviews != null){
                Print.d("POINT", "DO NOTHING");
            } else if(productRatingPage == null ){
                reviews = new ArrayList<>();
                triggerReviews(selectedProduct.getSku(), 1);
            } else { // reviews == null
                reviews = new ArrayList<>();
                triggerReviews(selectedProduct.getSku(), pageNumber);
            }
        } else {
            if(productRatingPage != null){
                if(reviews != null && pageNumber == 1){
                    reviews.clear();
                    reviews =  productRatingPage.getReviewComments();
                } else if(reviews != null){
                    if(reviews.size() != productRatingPage.getCommentsCount()){
                        reviews.addAll(productRatingPage.getReviewComments());
                    }
                } else {
                    reviews = new ArrayList<>();
                }
            } else {
                reviews = new ArrayList<>();
            }
        }
        
        
        if(reviewsContainer == null){
            reviewsContainer = (LinearLayout) getView().findViewById(R.id.linear_reviews);
            pageNumber = 1;
        }

        if(reviewsContainer.getChildCount() > 0)
            reviewsContainer.removeAllViews();

        if(productRatingContainer.getChildCount() > 0)
            productRatingContainer.removeAllViews();


        if(!isProductRating){
            String reviewsString = getResources().getString(R.string.reviews);
            //alexandrapires: mobapi 1.8 change: productRatingPage can come null (reviews/ratings/comments)
            if(productRatingPage!= null && productRatingPage.getCommentsCount() == 1)
                reviewsString = getResources().getString(R.string.review);

            sellerRatingCount.setText(""+productRatingPage.getCommentsCount()+" "+reviewsString);
            productName.setText(productRatingPage.getSellerName());
            sellerRatingBar.setRating(productRatingPage.getAverage());

        } else {
            //alexandrapires: mobapi 1.8 correction: productRatingPage can come null (reviews/ratings/comments)
            if(productRatingPage != null)
                insertRatingTypes(productRatingPage.getRatingTypes(), productRatingContainer, true, productRatingPage.getAverage());
        }


        // set the number of grid columns depending on the screen size
        int numColumns = getBaseActivity().getResources().getInteger(R.integer.catalog_list_num_columns);
        // means there's write fragment attached so the reviews list must be only one column
        if(reviews != null && reviews.size() > 0 && getBaseActivity() != null &&
                DeviceInfoHelper.isTabletInLandscape(getBaseActivity()) &&
                (getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) || getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true))){
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
            int group =(int) Math.ceil(numberReviews / numColumns);
            if(rest > 0)
                group = group + 1;

            for (int j = 0; j < group; j++) {
                LinearLayout gridElement = new LinearLayout(getActivity().getApplicationContext());
                gridElement.setOrientation(LinearLayout.HORIZONTAL);
                LayoutParams gridParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, numColumns);
                //#RTL
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if(ShopSelector.isRtl() && currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
                    gridElement.setLayoutDirection(LayoutDirection.LOCALE);
                }
                gridElement.setLayoutParams(gridParams);
                for (int i = startPoint; i < startPoint+numColumns; i++) {
                    if(i < reviews.size()){

                        final ProductReviewComment review = reviews.get(i);

                        final View theInflatedView = inflater.inflate(R.layout.reviews_fragment_item, reviewsContainer, false);
                        // Hide last divider
                        if (j == group - 1) {
                            theInflatedView.findViewById(R.id.top_review_line).setVisibility(View.GONE);
                        }

                        if(i == startPoint+numColumns -1){
                            theInflatedView.findViewById(R.id.horizintal_review_line).setVisibility(View.GONE);
                        }

//                        theInflatedView.setBackgroundColor(Color.CYAN);
                        final TextView userName = (TextView) theInflatedView.findViewById(R.id.user_review);
                        final TextView userDate = (TextView) theInflatedView.findViewById(R.id.date_review);
                        final TextView textReview = (TextView) theInflatedView.findViewById(R.id.textreview);

                        final TextView titleReview = (TextView) theInflatedView.findViewById(R.id.title_review);
                        LinearLayout ratingsContainer = (LinearLayout) theInflatedView.findViewById(R.id.ratings_container);

                        if(ratingsContainer.getChildCount() > 0)
                            ratingsContainer.removeAllViews();
                        
                        
                        ArrayList<RatingStar> ratingOptionArray = review.getRatingStars();

                        insertRatingTypes(ratingOptionArray, ratingsContainer,false,review.getAverage());

                        final String[] stringCor = review.getDate().split(" ");
                        userName.setText(review.getName() + ",");
                        userDate.setText(stringCor[0]);
                        textReview.setText(review.getComment());

                        titleReview.setText(review.getTitle());

                        theInflatedView.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Print.d(TAG, "review clicked: username = " + userName.getText().toString());

                                Bundle bundle = new Bundle();
                                bundle.putString(ConstantsIntentExtra.REVIEW_TITLE, review.getTitle());
                                bundle.putString(ConstantsIntentExtra.REVIEW_NAME, review.getName());
                                bundle.putString(ConstantsIntentExtra.REVIEW_COMMENT, review.getComment());
                                if(isProductRating){
                                    bundle.putParcelableArrayList(ConstantsIntentExtra.REVIEW_RATING, review.getRatingStars());
                                } else {
                                    bundle.putInt(ConstantsIntentExtra.REVIEW_RATING, review.getAverage());
                                }
                                bundle.putString(ConstantsIntentExtra.REVIEW_DATE, stringCor[0]);
                                getBaseActivity().onSwitchFragment(FragmentType.REVIEW, bundle, true);
                            }
                        });

                        gridElement.addView(theInflatedView);

                    } else {
//                        final View theInflatedView = inflater.inflate(R.layout.reviews_fragment_item, reviewsContainer, false);
//
//                        final TextView postedBy = (TextView) theInflatedView.findViewById(R.id.posted_by);
//                        postedBy.setText("");

                        LinearLayout emptyView = new LinearLayout(getActivity().getApplicationContext());
                        LayoutParams emptyParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
//                        emptyView.setBackgroundColor(Color.YELLOW);
                        emptyView.setLayoutParams(emptyParams);
                        gridElement.addView(emptyView);


                    }

                }
                startPoint = startPoint+numColumns;
                reviewsContainer.addView(gridElement);
                isLoadingMore = false;

            }


        } else {
            // Only hide reviews list and show empty on first request
            // Otherwise it was only a empty response for a page after the first
            if (firstRequest) {
                reviewsContainer.setVisibility(View.GONE);
                getView().findViewById(R.id.reviews_empty).setVisibility(View.VISIBLE);
            }
            firstRequest = false;
        }

        View loadingLayout = getView().findViewById(R.id.catalog_loading_more);
        loadingLayout.setVisibility(View.GONE);
        loadingLayout.refreshDrawableState();

        // Validate if the current request size is < MAX_REVIEW_COUNT
        // Or from saved values the current size == comments max count

        // FIXME commented only for testing purpose
        if (reviews.size() < REVIEWS_PER_PAGE || (reviews.size() > REVIEWS_PER_PAGE && reviews.size() == mProductRatingPage.getCommentsCount())) {
            isLoadingMore = true;
        }

    }
    
    /**
     * insert rate types on the review
     * @param ratingOptionArray
     * @param parent
     */
    private void insertRatingTypes(ArrayList<RatingStar> ratingOptionArray, LinearLayout parent, boolean isBigStar, int average){
        
        
        int starsLayout = R.layout.reviews_fragment_rating_samlltype_item;
        
        if(isBigStar)
            starsLayout = R.layout.reviews_fragment_rating_bigtype_item;
        
        if(ratingOptionArray != null && ratingOptionArray.size() > 0){
            
            // calculate how many lines of rate types the review will have, supossing 3 types for line;
            int rateCount = ratingOptionArray.size();
            int rest = rateCount % RATING_TYPE_BY_LINE;
            int numLines =(int) Math.ceil(rateCount / RATING_TYPE_BY_LINE);
            if(rest >= 1)
                numLines = numLines + rest;
            
            int countType = 0;
            
            for (int i = 0; i < numLines; i++) {
                
                LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,RATING_TYPE_BY_LINE);
                
                typeLine.setOrientation(LinearLayout.HORIZONTAL);
                //#RTL
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if(ShopSelector.isRtl() && currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
                    typeLine.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
//                    typeLine.setLayoutDirection(View.LaLayoutDirection.LOCALE);
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
            

        } else {
        //if rating Options == null then its a seller review
            
            if(parent.getChildCount() > 0){
                parent.removeAllViews();
            }
            
            LinearLayout typeLine = new LinearLayout(getActivity().getApplicationContext());
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,ReviewsFragment.RATING_TYPE_BY_LINE);
            
            typeLine.setOrientation(LinearLayout.HORIZONTAL);
            
            
            View rateTypeView = inflater.inflate(starsLayout, null, false);
            
            rateTypeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
            
            final TextView ratingTitle = (TextView) rateTypeView.findViewById(R.id.title_type);
            final RatingBar userRating = (RatingBar) rateTypeView.findViewById(R.id.rating_value);
              
            userRating.setRating(average);
            ratingTitle.setVisibility(View.GONE);
           
            typeLine.addView(rateTypeView);
            parent.addView(typeLine);
        }
    }
    
    
    /**
     * TRIGGERS
     * @author sergiopereira
     */
    private void triggerReviews(String sku, int pageNumber) {
        ContentValues values = new ContentValues();
        //mobapi 1.8 change
   /*     values.put(GetProductReviewsHelper.SKU, sku);
        values.put(GetProductReviewsHelper.PAGE, pageNumber);
        values.put(GetProductReviewsHelper.PER_PAGE, REVIEWS_PER_PAGE);
        values.put(GetProductReviewsHelper.REST_PARAM_SELLER_RATING, !isProductRating);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        // Show loading layout for first time
        if(pageNumber == 1){
            triggerContentEvent(new GetProductReviewsHelper(), bundle, mCallBack);
        } else {
            triggerContentEventNoLoading(new GetProductReviewsHelper(), bundle, mCallBack);
        }*/

        values.put(GetReviewsHelper.SKU, sku);
        values.put(GetReviewsHelper.PAGE, pageNumber);
        values.put(GetReviewsHelper.PER_PAGE, REVIEWS_PER_PAGE);

        if(isProductRating)
            values.put(GetReviewsHelper.REST_PARAM_RATING, isProductRating);
        else    //seller
            values.put(GetReviewsHelper.REST_PARAM_SELLER_RATING, true);

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        // Show loading layout for first time
        if(pageNumber == 1){
            triggerContentEvent(new GetReviewsHelper(), bundle, mCallBack);
        } else {
            triggerContentEventNoLoading(new GetReviewsHelper(), bundle, mCallBack);
        }
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

    private SharedPreferences getSharedPref(){
        if(sharedPrefs == null){
          //Validate if country configs allows rating and review, only show write review fragment if both are allowed
            sharedPrefs = JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return sharedPrefs;
    }
 
    private void setHeaderReviews(){
        if(isProductRating){
            productName.setText(selectedProduct.getBrand() + " " + selectedProduct.getName());
            displayPriceInformation(productPriceNormal, productPriceSpecial);
        }
       
    }
 
    /**
     * method that controls what to show or hide based on what type of reviews it is
     */
    private void checkReviewsTypeVisibility(){
        
        if(isProductRating){
            productRatingContainer.setVisibility(View.VISIBLE);
            sellerRatingContainer.setVisibility(View.GONE);
            productPriceSpecial.setVisibility(View.VISIBLE);
            productPriceNormal.setVisibility(View.VISIBLE);
            productName.setText(selectedProduct.getBrand() + " " + selectedProduct.getName());
            displayPriceInformation(productPriceNormal, productPriceSpecial);
            
            productRatingContainer.setVisibility(View.VISIBLE);
            sellerRatingContainer.setVisibility(View.GONE);
            
            if(!DeviceInfoHelper.isTabletInLandscape(getActivity().getApplicationContext())){
                centerPoint.setVisibility(View.VISIBLE);
//                marginLandscape.setVisibility(View.GONE);
                writeReviewTitle = (TextView) getView().findViewById(R.id.write_title);
                Button writeComment = (Button) getView().findViewById(R.id.write_btn);
                writeReviewTitle.setVisibility(View.VISIBLE);
                writeComment.setVisibility(View.VISIBLE); 
                setWriteButtonTitle();
                setCommentListener();
            } else {
                marginLandscape.setVisibility(View.GONE);
                centerPoint.setVisibility(View.VISIBLE);
                
            }
            
        } else {
            productRatingContainer.setVisibility(View.GONE);
            sellerRatingContainer.setVisibility(View.VISIBLE);
            productPriceSpecial.setVisibility(View.GONE);
            productPriceNormal.setVisibility(View.GONE);
            
            if(!DeviceInfoHelper.isTabletInLandscape(getActivity().getApplicationContext())){
                centerPoint.setVisibility(View.GONE);
//                marginLandscape.setVisibility(View.GONE);
                writeReviewTitle = (TextView) getView().findViewById(R.id.write_title);
                Button writeComment = (Button) getView().findViewById(R.id.write_btn);
                setWriteButtonTitle();
                setCommentListener();
                writeReviewTitle.setVisibility(View.VISIBLE);
                writeComment.setVisibility(View.VISIBLE);

            } else {
                marginLandscape.setVisibility(View.GONE);
                centerPoint.setVisibility(View.VISIBLE);
                
            }
            
        }
        setScrollListener();
        
        // TRACKER
        Bundle params = new Bundle();
        params.putParcelable(TrackerDelegator.PRODUCT_KEY,selectedProduct);
        params.putFloat(TrackerDelegator.RATING_KEY, (float) selectedProduct.getAvgRating());
        
        TrackerDelegator.trackViewReview(selectedProduct);
        
    }
    
    /**
     * set title above write review button
     */
    private void setWriteButtonTitle(){
        writeReviewTitle = (TextView) getView().findViewById(R.id.write_title);
        
        if(isProductRating){
            writeReviewTitle.setText(getResources().getString(R.string.rating_question));
        } else {
            writeReviewTitle.setText(getResources().getString(R.string.review_this_seller));
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

}
