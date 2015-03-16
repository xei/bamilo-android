package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.forms.Form;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Customer;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.configs.GetSellerReviewFormHelper;
import com.mobile.helpers.products.RatingReviewProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

import de.akquinet.android.androlog.Log;

/**
 * This class represents the write seller review screen and manages all interactions about it's form.
 *
 * @author Paulo Carvalho
 *
 */
public class WriteSellerReviewFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(WriteSellerReviewFragment.class);

    private static final String NAME = "name";

    private static final String TITLE = "title";

    private static final String COMMENT = "comment";

    private static final String SELLER_ID = "sellerId";

    private static final String RATINGS = "ratings";

    private static WriteSellerReviewFragment mWriteSellerReviewFragment;

    private TextView mProductSellerName;

    private LinearLayout mReviewContainer;

    private DialogGenericFragment dialog_review_submitted;

    private boolean isExecutingSendReview = false;

    private View mMainContainer;

    private Form mSellerReviewForm;

    private DynamicForm mDynamicSellerReviewForm;

    private boolean nestedFragment = true;

    private ContentValues formValues;

    private String reviewName = "";

    private String reviewTitle = "";

    private String reviewComment = "";

    private String mSellerId = "";

    private String mSellerName = "";

    private int mSellerAverage = 0;

    private int mSellerCommentCount = 0;

    private RelativeLayout mSellerRatingContainer;

    private RatingBar mSellerRatingBar;

    private TextView mSellerRatingCount;

    private TextView mProductPriceNormal;

    private TextView mProductPriceSpecial;

    private TextView mReviewTitle;

    /**
     * Get instance
     *
     * @return
     */
    public static WriteSellerReviewFragment getInstance(Bundle bundle) {
        Log.i(TAG, "getInstance");
        mWriteSellerReviewFragment = new WriteSellerReviewFragment();
        if (bundle != null) {

            String sellerId = bundle.getString(ProductDetailsFragment.SELLER_ID);
            if(sellerId != null)
                mWriteSellerReviewFragment.mSellerId = sellerId;

            String sellerName = bundle.getString(ReviewsFragment.SELLER_NAME);
            if(sellerName != null)
                mWriteSellerReviewFragment.mSellerName = sellerName;

            mWriteSellerReviewFragment.mSellerAverage = bundle.getInt(ReviewsFragment.SELLER_COMMENT_COUNT);

            mWriteSellerReviewFragment.mSellerCommentCount = bundle.getInt(ReviewsFragment.SELLER_AVERAGE);

            mWriteSellerReviewFragment.setArguments(bundle);
        }
        return mWriteSellerReviewFragment;
    }

    /**
     * Empty constructor
     */
    public WriteSellerReviewFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.review_write_fragment,
                0,
                KeyboardState.ADJUST_CONTENT);
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
        JumiaApplication.setIsSellerReview(true);
        isExecutingSendReview = false;
        if(getArguments().containsKey(ProductDetailsFragment.SELLER_ID)){
            mSellerId = getArguments().getString(ProductDetailsFragment.SELLER_ID);
        }
        if(getArguments().containsKey(ReviewsFragment.SELLER_NAME)){
            mSellerName = getArguments().getString(ReviewsFragment.SELLER_NAME);
        }
        if(getArguments().containsKey(ReviewsFragment.SELLER_COMMENT_COUNT)){
            mSellerCommentCount = getArguments().getInt(ReviewsFragment.SELLER_COMMENT_COUNT);
        }
        if(getArguments().containsKey(ReviewsFragment.SELLER_AVERAGE)){
            mSellerAverage = getArguments().getInt(ReviewsFragment.SELLER_AVERAGE);
        }

        if(savedInstanceState != null){
            restoreSavedInstance(savedInstanceState);
        }
    }

    /**
     * restore saved instance
     * @param savedInstanceState
     */
    private void restoreSavedInstance(Bundle savedInstanceState){
        mSellerReviewForm = JumiaApplication.INSTANCE.mSellerReviewForm;
        if (savedInstanceState.containsKey(NAME))
            reviewName = savedInstanceState.getString(NAME);
        if (savedInstanceState.containsKey(TITLE))
            reviewTitle = savedInstanceState.getString(TITLE);
        if (savedInstanceState.containsKey(COMMENT))
            reviewComment = savedInstanceState.getString(COMMENT);
        if (savedInstanceState.containsKey(ProductDetailsFragment.SELLER_ID))
            mSellerId = savedInstanceState.getString(ProductDetailsFragment.SELLER_ID);
        if (savedInstanceState.containsKey(ReviewsFragment.SELLER_NAME))
            mSellerName = savedInstanceState.getString(ReviewsFragment.SELLER_NAME);
        if (savedInstanceState.containsKey(ReviewsFragment.SELLER_COMMENT_COUNT))
            mSellerCommentCount = savedInstanceState.getInt(ReviewsFragment.SELLER_COMMENT_COUNT);
        if (savedInstanceState.containsKey(ReviewsFragment.SELLER_AVERAGE))
            mSellerAverage = savedInstanceState.getInt(ReviewsFragment.SELLER_AVERAGE);
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        JumiaApplication.setIsSellerReview(true);
        mReviewContainer = (LinearLayout) view.findViewById(R.id.form_rating_container);
        mMainContainer = view.findViewById(R.id.product_rating_container);
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
        isExecutingSendReview = false;
        if(getArguments() != null){
            if (getArguments().containsKey(ReviewsFragment.CAME_FROM_POPULARITY)) {
                getView().findViewById(R.id.product_info_container).setVisibility(View.GONE);
                getView().findViewById(R.id.shadow).setVisibility(View.GONE);
            }
        }

     // Validate is service is available
        if (JumiaApplication.mIsBound) {

            if (TextUtils.isEmpty(mSellerId)) {
                if(getArguments().containsKey(ProductDetailsFragment.SELLER_ID)){
                    mSellerId = getArguments().getString(ProductDetailsFragment.SELLER_ID);
                    if(!TextUtils.isEmpty(mSellerId)){
                        triggerSellerReviewForm();
                    } else {
                        showRetryLayout();
                    }
                } else {
                    showRetryLayout();
                }
            } else {
                if(mSellerReviewForm != null){
                    loadReviewFormValues();
                    setReviewLayout(mSellerReviewForm);
                } else {
                    triggerSellerReviewForm();
                }
            }
        } else {
            showRetryLayout();
        }
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
        JumiaApplication.setIsSellerReview(true);
        if(mDynamicSellerReviewForm != null){
            saveTextReview(mDynamicSellerReviewForm);
            formValues = mDynamicSellerReviewForm.save();
            JumiaApplication.setSellerReviewValues(formValues);
            saveReview();

        }
        //duplicated here and on onSaveInstance because when this fragment is removed from the Reviews Landscape it doesn't pass on the onSaveInstance method
        JumiaApplication.INSTANCE.mSellerReviewForm = mSellerReviewForm;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
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

    /*
     * Set review name by user name if is logged.
     */
    private void setReviewName(DynamicForm reviewForm) {
        if(reviewForm != null && reviewForm.getItemByKey(NAME) != null && reviewForm.getItemByKey(NAME).getValue().equals("")){
            Customer customer = JumiaApplication.CUSTOMER;
            String firstname = (customer != null && !TextUtils.isEmpty(customer.getFirstName())) ? customer.getFirstName() : ""; 
            reviewForm.getItemByKey(NAME).setValue(firstname);
        }
    }
    
    
    private void showRetryLayout() {
        showFragmentErrorRetry();
    }
    
    /**
     * Set the Products layout using inflate
     */
    private void setReviewLayout(Form form) {

        mMainContainer.setVisibility(View.VISIBLE);

        if (getBaseActivity() == null) return;

        mDynamicSellerReviewForm = FormFactory.getSingleton().CreateForm(FormConstants.REVIEW_SELLER_FORM, getBaseActivity(), form);
        if (mReviewContainer.getChildCount() > 0)
            mReviewContainer.removeAllViews();

        mReviewContainer.addView(mDynamicSellerReviewForm.getContainer());

        loadReviewFormValues();
        setReviewName(mDynamicSellerReviewForm);
        restoreTextReview(mDynamicSellerReviewForm);

        setGenericLayout();
    }
    /**
     * save the information regarding the review form
     */
    private void saveTextReview(DynamicForm form){
        if(form != null && form.getItemByKey(NAME) != null){
            reviewName = form.getItemByKey(NAME).getValue().toString();
        }
        if(form != null && form.getItemByKey(TITLE) != null){
            reviewTitle = form.getItemByKey(TITLE).getValue().toString();
        }
        if(form != null && form.getItemByKey(COMMENT) != null){
            reviewComment = form.getItemByKey(COMMENT).getValue().toString();
        }
    }

    /**
     * clean fields after sending a review
     */
    private void cleanReviewText(){
        reviewName = "";
        reviewTitle = "";
        reviewComment = "";
    }

    /**
     * restore information related to the form edit texts
     * @param form
     */
    private void restoreTextReview(DynamicForm form){
        if(form != null && form.getItemByKey(NAME) != null){
            form.getItemByKey(NAME).setValue(reviewName);
        }
        if(form != null && form.getItemByKey(TITLE) != null){
            form.getItemByKey(TITLE).setValue(reviewTitle);
        }
        if(form != null && form.getItemByKey(COMMENT) != null){
            form.getItemByKey(COMMENT).setValue(reviewComment);
        }
    }
    
    /**
     * Set info of the product on write review screen
     * 
     */
    private void setGenericLayout() {

        mMainContainer.setVisibility(View.VISIBLE);

        mProductSellerName = (TextView) getView().findViewById(R.id.product_detail_name);
        mSellerRatingContainer = (RelativeLayout) getView().findViewById(R.id.seller_reviews_rating_container);
        mSellerRatingBar = (RatingBar) getView().findViewById(R.id.seller_reviews_item_rating);
        mSellerRatingCount = (TextView) getView().findViewById(R.id.seller_reviews_item_reviews);
        mProductPriceNormal = (TextView) getView().findViewById(R.id.product_price_normal);
        mProductPriceSpecial = (TextView) getView().findViewById(R.id.product_price_special);
        mReviewTitle = (TextView) getView().findViewById(R.id.write_title);


        mProductPriceSpecial.setVisibility(View.GONE);
        mProductPriceNormal.setVisibility(View.GONE);
        mSellerRatingContainer.setVisibility(View.VISIBLE);

        mReviewTitle.setText(R.string.review_this_seller);

        String reviewsString = getResources().getString(R.string.reviews);
        if(mSellerCommentCount == 1)
            reviewsString = getResources().getString(R.string.review);

        mSellerRatingCount.setText("" + mSellerCommentCount + " " + reviewsString);
        mProductSellerName.setText(mSellerName);
        mSellerRatingBar.setRating(mSellerAverage);
        ((Button) getView().findViewById(R.id.send_review)).setOnClickListener(this);

    }

    /**
     * Save rating and review form
     */
    private void saveReview() {
        if(mDynamicSellerReviewForm != null)
            JumiaApplication.setSellerReviewValues(mDynamicSellerReviewForm.save());
    }

    /**
     * Load rating and review form
     */
    private void loadReviewFormValues() {
        
        ContentValues savedReviewValues = new ContentValues();
        
        if(formValues == null){
            savedReviewValues = JumiaApplication.getSellerReviewValues();
        } else {
            savedReviewValues = formValues;
        }
        
            // Validate values
            if(savedReviewValues != null && mDynamicSellerReviewForm != null) {
                // Get dynamic form and update
                Iterator<DynamicFormItem> iter = mDynamicSellerReviewForm.getIterator();
                while (iter.hasNext()) {
                    DynamicFormItem item = iter.next();
                    try {
                        item.loadState(savedReviewValues);
                    } catch (NullPointerException e) {
                        Log.w(TAG, "LOAD STATE: NOT CONTAINS KEY " + item.getKey());
                    }
                }
            }
    }




    /**
     * function to clean form
     */
    private void cleanForm() {
        if(mDynamicSellerReviewForm != null)
            mDynamicSellerReviewForm.clear();
       
        JumiaApplication.cleanSellerReviewValues();
        if(formValues != null)
            formValues = null;
        
            setReviewLayout(mSellerReviewForm);

        cleanReviewText();
        setReviewName(mDynamicSellerReviewForm);
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        Log.i(TAG, "onSuccessEvent eventType : " + eventType);
        switch (eventType) {
        case REVIEW_RATING_PRODUCT_EVENT:

            Log.d(TAG, "review seller completed: success");
            // Clean options after success
            String buttonMessageText = getResources().getString(R.string.dialog_to_reviews);


            try {
                //Validate if fragment is nested
                if(this.getParentFragment() instanceof ReviewsFragment){
                    nestedFragment = true;
                } else {
                    nestedFragment = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                nestedFragment = true;
            }
            if(nestedFragment)
                buttonMessageText = getResources().getString(R.string.ok_label);


            dialog_review_submitted = DialogGenericFragment.newInstance(false, true, false,
                    getString(R.string.submit_title),
                    getResources().getString(R.string.submit_text),
                    buttonMessageText,
                    "",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_review_submitted.dismiss();
                            isExecutingSendReview = false;
                            if (getBaseActivity() != null) {
                                if(nestedFragment){
                                    cleanForm();
                                } else {
                                    getBaseActivity().onBackPressed();
                                }
                            }
                        }
                    });
            // Fixed back bug
            dialog_review_submitted.setCancelable(false);
            dialog_review_submitted.show(getActivity().getSupportFragmentManager(), null);
            hideActivityProgress();
            isExecutingSendReview = false;
            cleanForm();
            return false;
        case GET_FORM_SELLER_REVIEW_EVENT:
            Log.i(TAG, "GET_FORM_SELLER_REVIEW_EVENT");
            mSellerReviewForm = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            setReviewLayout(mSellerReviewForm);
            showFragmentContentContainer();
            return true;
        default:
            return false;
        }
    }

    protected boolean onErrorEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        // Hide progress
        hideActivityProgress();
        
        isExecutingSendReview = false;
        // Generic errors
        if(super.handleErrorEvent(bundle)) return true;
        
        switch (eventType) {
        case GET_FORM_SELLER_REVIEW_EVENT:
            showRetryLayout();
            return false;
        case REVIEW_RATING_PRODUCT_EVENT:
            dialog = DialogGenericFragment.createServerErrorDialog(getBaseActivity(),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isExecutingSendReview) {
                                formsValidation();
                            }
                            dismissDialogFragment();
                        }
                    }, false);
            dialog.setCancelable(false);
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
            hideActivityProgress();
            isExecutingSendReview = false;
            return false;
        default:
        }

        return false;
    }

    /**
     * request seller review form
     */
    private void triggerSellerReviewForm() {
        triggerContentEvent(new GetSellerReviewFormHelper(), null, mCallBack);
    }

    /**
     * CALLBACK
     * 
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
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if(id == R.id.send_review) formsValidation();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickErrorButton(android.view.View)
     */
    @Override
    protected void onClickErrorButton(View view) {
        super.onClickErrorButton(view);
        onResume();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onRetryRequest(com.mobile.framework.utils.EventType)
     */
    @Override
    protected void onRetryRequest(EventType eventType) {
        switch(eventType){
        case GET_FORM_SELLER_REVIEW_EVENT:
            triggerSellerReviewForm();
            return;
        case REVIEW_RATING_PRODUCT_EVENT:
            formsValidation();
            return;
        default:
            super.onRetryRequest(eventType);
            return;
        }
    }
    
    /**
     * function that validates if the form is correctly filled
     */
    private void formsValidation(){
        if(mDynamicSellerReviewForm != null){
            if(!mDynamicSellerReviewForm.validate())
                return;
        } else if (mSellerReviewForm != null) {
            mDynamicSellerReviewForm = FormFactory.getSingleton().CreateForm(FormConstants.REVIEW_SELLER_FORM, getBaseActivity(), mSellerReviewForm);
            
            if(!mDynamicSellerReviewForm.validate())
                return;
        } else {
            triggerSellerReviewForm();
        }
        isExecutingSendReview = false;
        if (!isExecutingSendReview) {
            isExecutingSendReview = true;
            executeSendReview(mSellerReviewForm.action, mDynamicSellerReviewForm);
        }
    }
    
    
    /**
     * function responsible for sending the rating/review to API
     * 
     * 
     * @param action
     * @param form
     */
    private void executeSendReview(String action, DynamicForm form) {
        
        Bundle bundle = new Bundle();
        
        form.getItemByKey(SELLER_ID).setValue(mSellerId);
        
        ContentValues values = form.save();
        
        getRatingFormValues(values,form);
        
        bundle.putString(RatingReviewProductHelper.ACTION, action);
        bundle.putParcelable(RatingReviewProductHelper.RATING_REVIEW_CONTENT_VALUES, values);
        
        triggerContentEventProgress(new RatingReviewProductHelper(), bundle, mCallBack);
        
    }
    
    
    /**
     * 
     * Function that retrieves info from rating bars form
     * 
     * @param values
     * @param form
     */
    private void getRatingFormValues(ContentValues values, DynamicForm form){
        
        String formName = form.getItemByKey(RATINGS).getName();
        
        Map<String, String> ratingMap = form.getItemByKey(RATINGS).getEntry().getDateSetRating();
        View  ratingFormContainer = form.getItemByKey(RATINGS).getEditControl();
        
        for (int i = 1; i < ratingMap.size()+1; i++) {
           int rate =  (int)((RatingBar)ratingFormContainer.findViewById(i).findViewById(R.id.option_stars)).getRating();
           String id =  ((RatingBar)ratingFormContainer.findViewById(i).findViewById(R.id.option_stars)).getTag().toString();
        
           String key =formName+"["+id+"]";
           values.put(key, rate);
           
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "  -----> ON SAVE INSTANCE STATE !!!!!!!!!");
        saveReview();

        outState.putString(NAME, reviewName);
        outState.putString(TITLE, reviewTitle);
        outState.putString(COMMENT, reviewComment);
        super.onSaveInstanceState(outState);
    }
    
}
