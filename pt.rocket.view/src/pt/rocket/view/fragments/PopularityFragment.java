/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.components.ScrollViewEx;
import pt.rocket.framework.components.ScrollViewEx.OnScrollBottomReachedListener;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetProductReviewsEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.ProductRatingPage;
import pt.rocket.framework.objects.RatingOption;
import pt.rocket.framework.objects.ProductReviewComment;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class PopularityFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(PopularityFragment.class);
    
    private static final int MAX_REVIEW_COUNT = 10;

    private static PopularityFragment popularityFragment;

    private Context context;

    private CompleteProduct selectedProduct;

    private LayoutInflater inflater;
    
    private int pageNumber = 1;
    
    private Boolean isLoadingMore = false;

    /**
     * Get instance
     * 
     * @return
     */
    public static PopularityFragment getInstance() {
        if (popularityFragment == null)
            popularityFragment = new PopularityFragment();
        return popularityFragment;
    }

    /**
     * Empty constructor
     */
    public PopularityFragment() {
        super(EnumSet.of(EventType.GET_PRODUCT_REVIEWS_EVENT), EnumSet.noneOf(EventType.class));
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
        context = getActivity().getApplicationContext();
        selectedProduct = ServiceManager.SERVICES.get(ProductService.class).getCurrentProduct();
        inflater = LayoutInflater.from(getActivity());
        triggerContentEvent(new GetProductReviewsEvent(selectedProduct.getUrl(), pageNumber));
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.popularity, container, false);
        return view;
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
        ((BaseActivity) getActivity()).updateActivityHeader(NavigationAction.Products, R.string.reviews);
        setAppContentLayout();
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
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
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
        Log.i(TAG, "ON DESTROY");
    }
    
    
    /**
     * This method inflates the current activity layout into the main template.
     */
    private void setAppContentLayout() {
        setViewContent();
        setPopularity();
        setCommentListener();
    }

    /**
     * This method initializes the views with the product details such as name price and discount
     * price, in case the product has one.
     */
    private void setViewContent() {

        TextView namePop = (TextView) getView().findViewById(R.id.product_info_name_p);
        TextView pricePop = (TextView) getView().findViewById(R.id.price_normal_p);
        TextView discountPop = (TextView) getView().findViewById(R.id.price_w_discount_p);
        namePop.setText(selectedProduct.getName());
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
        Log.i(TAG, "code1rating : "+ selectedProduct.getRatingsAverage().floatValue());
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
            EventManager.getSingleton().triggerRequestEvent(
                    new GetProductReviewsEvent(selectedProduct.getUrl(), pageNumber));
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
        ActivitiesWorkFlow.writeReviewActivity(getActivity());
    }

    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        displayReviews((ProductRatingPage) event.result);
        return true;
    }
    
    
    private void displayReviews(ProductRatingPage productRatingPage) {
        ArrayList<ProductReviewComment> reviews = productRatingPage.getReviewComments();
        LinearLayout reviewsLin = (LinearLayout) getView().findViewById(R.id.linear_reviews);
        // Log.i("REVIEW COUNT", " IS " + review.size());
        if (productRatingPage.getCommentsCount() > 0) {
            TextView reviewsPop = (TextView) getView().findViewById(R.id.reviews);
            reviewsPop.setText("" + productRatingPage.getCommentsCount());
        }
        for (final ProductReviewComment review : reviews) {
            final View theInflatedView = inflater.inflate(R.layout.popularityreview, reviewsLin,
                    false);

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
                    ActivitiesWorkFlow.reviewActivity(getActivity(), review.getTitle(),
                            review.getComments(), review.getName(), review.getRating(),
                            stringCor[0]);
                }
            });

            reviewsLin.addView(theInflatedView);
            isLoadingMore = false;

        }

        if (reviews.size() < MAX_REVIEW_COUNT) {
            View loadingLayout = getView().findViewById(R.id.loadmore);
            loadingLayout.setVisibility(View.GONE);
            loadingLayout.refreshDrawableState();
            isLoadingMore = true;
        }
    }

}
