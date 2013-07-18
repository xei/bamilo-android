package pt.rocket.view;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.components.ScrollViewEx;
import pt.rocket.framework.components.ScrollViewEx.OnScrollBottomReachedListener;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetProductReviewsEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.ProductRatingPage;
import pt.rocket.framework.objects.ProductReviewComment;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class shows all reviews and rating of a specified product.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential. Written by josedourado, 30/06/2012.
 * </p>
 * 
 * 
 * 
 * 
 * @version 1.01
 * 
 * @author paulocarvalho, josedourado
 * 
 * 
 * @description
 * 
 */

public class PopularityActivity extends BaseActivity {

    /**
     * 
     */
    private static final int MAX_REVIEW_COUNT = 10;
    protected final static String TAG = LogTagHelper.create(PopularityActivity.class);
    private CompleteProduct selectedProduct;
    private LayoutInflater inflater;
    private int pageNumber = 1;
    private Boolean isLoadingMore = false;

    /**
	 * 
	 */
    public PopularityActivity() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SHARE),
                EnumSet.of(EventType.GET_PRODUCT_REVIEWS_EVENT),
                EnumSet.noneOf(EventType.class),
                R.string.reviews, R.layout.popularity);
    }

    /*
     * ####################### # Activity Life Cycle # #######################
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedProduct = ServiceManager.SERVICES.get(ProductService.class).getCurrentProduct();
        inflater = LayoutInflater.from(PopularityActivity.this); // 1
        setAppContentLayout();
        triggerContentEvent(new GetProductReviewsEvent(selectedProduct.getUrl(), pageNumber));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsGoogle.get().trackPage(R.string.gproductreviews);
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

        TextView namePop = (TextView) findViewById(R.id.product_info_name_p);
        TextView pricePop = (TextView) findViewById(R.id.price_normal_p);
        TextView discountPop = (TextView) findViewById(R.id.price_w_discount_p);
        namePop.setText(selectedProduct.getName());
        if (!selectedProduct.getSpecialPrice().equals(selectedProduct.getPrice())) {
            pricePop.setText("" + selectedProduct.getPrice());
            pricePop.setPaintFlags(pricePop.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            pricePop.setTextColor(getResources().getColor(R.color.grey_light));
            pricePop.setTextAppearance(getApplicationContext(), R.style.text_normal);
            discountPop.setText("" + selectedProduct.getSpecialPrice());
            discountPop.setVisibility(View.VISIBLE);
        } else {
            pricePop.setText("" + selectedProduct.getPrice());
            discountPop.setVisibility(View.GONE);
        }
    }

    /**
     * This method sets the rating of a given product using the rating bar component.
     */
    private void setPopularity() {

        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating);
        ratingBar.setRating(selectedProduct.getRatingsAverage().floatValue());

        ScrollViewEx reviewsScroll = (ScrollViewEx) findViewById(R.id.reviews_scroller);
        reviewsScroll.setOnScrollBottomReached(new OnScrollBottomReachedListener() {

            private View mLoadingLayout;

            @Override
            public void OnScrollBottomReached() {

                Log.i(TAG, "onScrollBottomReached: isLoadingMore = " + isLoadingMore);
                if (!isLoadingMore) {
                    isLoadingMore = true;
                    mLoadingLayout = findViewById(R.id.loadmore);
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

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == maskRequestCodeId(R.id.request_login) && resultCode == RESULT_OK) {
            writeReview();
        }
    }

    /**
     * This method sets the content view to allow the user to create a review, and checks if the
     * fields are filled before sending a review to the server.
     */
    private void setCommentListener() {
        final Button writeComment = (Button) findViewById(R.id.write_btn);
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
        ActivitiesWorkFlow.writeReviewActivity(this);
    }

    private void displayReviews(ProductRatingPage productRatingPage) {
        ArrayList<ProductReviewComment> reviews = productRatingPage.getReviewComments();
        LinearLayout reviewsLin = (LinearLayout) findViewById(R.id.linear_reviews);
        // Log.i("REVIEW COUNT", " IS " + review.size());
        if (productRatingPage.getCommentsCount() > 0) {
            TextView reviewsPop = (TextView) findViewById(R.id.reviews);
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

            final String[] stringCor = review.getDate().split(" ");
            userName.setText(review.getName() + ",");
            userDate.setText(stringCor[0]);
            textReview.setText(review.getComments());
            userRating.setRating((float) review.getRating());
            titleReview.setText(review.getTitle());
            theInflatedView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d(TAG, "review clicked: username = " + userName.getText().toString());
                    ActivitiesWorkFlow.reviewActivity(PopularityActivity.this, review.getTitle(),
                            review.getComments(), review.getName(), review.getRating(),
                            stringCor[0]);
                }
            });

            reviewsLin.addView(theInflatedView);
            isLoadingMore = false;

        }

        if (reviews.size() < MAX_REVIEW_COUNT) {
            View loadingLayout = findViewById(R.id.loadmore);
            loadingLayout.setVisibility(View.GONE);
            loadingLayout.refreshDrawableState();
            isLoadingMore = true;
        }
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // Log.i("GOT PRODUCT REVIEW", " " + reviewEvent.getSuccess());
        displayReviews((ProductRatingPage) event.result);
        return true;
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

}
