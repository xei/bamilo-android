package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.forms.Form;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.objects.Customer;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.configs.GetRatingFormHelper;
import com.mobile.helpers.configs.GetReviewFormHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.helpers.products.RatingReviewProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * @modified Paulo Carvalho
 * 
 */
public class ReviewWriteNestedFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ReviewWriteNestedFragment.class);
    
    private static final String SHOWING_FORM = "showingForm";
    
    private static final String NAME = "name";
    
    private static final String TITLE = "title";
    
    private static final String COMMENT = "comment";

    private static final String SKU = "sku";
    
    private static final String RATINGS = "ratings";

    private CompleteProduct completeProduct;

    private TextView productName;

    private LinearLayout ratingContainer;

    private DialogGenericFragment dialog_review_submitted;

    private boolean isExecutingSendReview = false;

    private String mCompleteProductUrl = "";
    
    private View mainContainer;

    private Form ratingForm;
    
    private Form reviewForm;
    
    private DynamicForm dynamicRatingForm;
    
    private boolean isShowingRatingForm = true;
    
    public static final String RATING_SHOW = "isShowingRating";
    
    private boolean nestedFragment = true;
    
    private ContentValues formValues;
    
    private String reviewName = "";
    private String reviewTitle = "";
    private String reviewComment = "";
    
    /**
     * Get instance
     * 
     * @return
     */
    public static ReviewWriteNestedFragment getInstance(Bundle bundle) {
        ReviewWriteNestedFragment fragment = new ReviewWriteNestedFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ReviewWriteNestedFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.review_write_fragment_nested,
                NO_TITLE,
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
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            String contentUrl = arguments.getString(ConstantsIntentExtra.CONTENT_URL);
            mCompleteProductUrl = !TextUtils.isEmpty(contentUrl) ? contentUrl : "";
        }

        completeProduct = JumiaApplication.INSTANCE.getCurrentProduct();
        isExecutingSendReview = false;
        if(savedInstanceState != null){
            ratingForm = JumiaApplication.INSTANCE.ratingForm;
            reviewForm =  JumiaApplication.INSTANCE.reviewForm;
            isShowingRatingForm = savedInstanceState.getBoolean(SHOWING_FORM);
            if(savedInstanceState.containsKey(NAME))
                reviewName = savedInstanceState.getString(NAME);
            if(savedInstanceState.containsKey(TITLE))
                reviewTitle = savedInstanceState.getString(TITLE);
            if(savedInstanceState.containsKey(COMMENT))
                reviewComment = savedInstanceState.getString(COMMENT);
        } 
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
        ratingContainer = (LinearLayout) view.findViewById(R.id.form_rating_container);
        mainContainer = view.findViewById(R.id.product_rating_container);
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
        
//        if (getArguments() != null && getArguments().containsKey(ReviewsFragment.CAME_FROM_POPULARITY)) {
//            getView().findViewById(R.id.product_info_container).setVisibility(View.GONE);
//            getView().findViewById(R.id.shadow).setVisibility(View.GONE);
//        } 
//        
        if(getArguments() != null && getArguments().containsKey(RATING_SHOW)) {
            isShowingRatingForm = getArguments().getBoolean(RATING_SHOW);
            ratingForm = JumiaApplication.INSTANCE.ratingForm;
            reviewForm =  JumiaApplication.INSTANCE.reviewForm;
        }
     // Validate is service is available
        if (JumiaApplication.mIsBound) {
            // load complete product URL
            if (mCompleteProductUrl.equalsIgnoreCase("") && getArguments() != null && getArguments().containsKey(ConstantsIntentExtra.CONTENT_URL)) {
                String contentUrl = getArguments().getString(ConstantsIntentExtra.CONTENT_URL);
                mCompleteProductUrl = contentUrl != null ? contentUrl : "";
            }
            
            if (completeProduct == null) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
                /* Commented due to unnecessary data being fetched
                triggerAutoLogin();
                triggerCustomer();*/
                if(ratingForm != null && reviewForm != null){
                    loadReviewAndRatingFormValues();
                    if(isShowingRatingForm){
                        setRatingLayout(ratingForm);
                    } else {
                        setRatingLayout(reviewForm);
                    }
                } else {
                    triggerRatingForm();
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
        saveReview();
        
        //duplicated here and on onSaveInstance because when this fragment is removed from the Reviews Landscape it doesn't pass on the onSaveInstance method
        JumiaApplication.INSTANCE.ratingForm = ratingForm;
        JumiaApplication.INSTANCE.reviewForm = reviewForm;
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
    private void setRatingLayout(Form form) {
        if (completeProduct == null) {
            if (!mCompleteProductUrl.equalsIgnoreCase("")) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
                showRetryLayout();
            }
            
        } else {
            mainContainer.setVisibility(View.VISIBLE);
            
            if (getBaseActivity() == null) return;
    
            dynamicRatingForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), form);
            if(ratingContainer.getChildCount() > 0)
                ratingContainer.removeAllViews();
            
            ratingContainer.addView(dynamicRatingForm.getContainer());
            
            loadReviewAndRatingFormValues();
            setReviewName(dynamicRatingForm);
            restoreTextReview(dynamicRatingForm);
            if(isShowingRatingForm)
                ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(false);
            else 
                ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(true);
            
            ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        if(reviewForm != null){
                            formValues = dynamicRatingForm.save();
                            JumiaApplication.setRatingReviewValues(formValues);
                            isShowingRatingForm = false;
                            setRatingLayout(reviewForm);
                        }
                      
                    } else {
                        if(ratingForm != null){
                            formValues = dynamicRatingForm.save();
                            JumiaApplication.setRatingReviewValues(formValues);
                            isShowingRatingForm = true;
                            hideKeyboard();
                            saveTextReview(dynamicRatingForm);
                            setRatingLayout(ratingForm);
                        }
                       
                    }
                }
            });
            
            setGenericLayout();
            
        }

    }

    private void saveTextReview(DynamicForm form){
        if(form != null && form.getItemByKey(NAME) != null){
            reviewName = form.getItemByKey(NAME).getValue();
        }
        if(form != null && form.getItemByKey(TITLE) != null){
            reviewTitle = form.getItemByKey(TITLE).getValue();
        }
        if(form != null && form.getItemByKey(COMMENT) != null){
            reviewComment = form.getItemByKey(COMMENT).getValue();
        }
    }
  
    private void cleanReviewText(){
        reviewName = "";
        reviewTitle = "";
        reviewComment = "";
    }
    
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
    private void setGenericLayout(){
        
        if (completeProduct == null) {
            if (!mCompleteProductUrl.equalsIgnoreCase("")) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
                showRetryLayout();
            }
            
        } else {
            mainContainer.setVisibility(View.VISIBLE);
            
//            productName = (TextView) getView().findViewById(R.id.product_detail_name);
//            TextView productPriceSpecial = (TextView) getView().findViewById(R.id.product_price_special);
//            TextView productPriceNormal = (TextView) getView().findViewById(R.id.product_price_normal);

            getView().findViewById(R.id.send_review).setOnClickListener(this);

//            productName.setText(completeProduct.getBrand() + " " + completeProduct.getName());
//            displayPriceInformation(productPriceNormal, productPriceSpecial);       
        }
    }
    
    /**
     * Save rating and review form
     */
    private void saveReview() {
        if(dynamicRatingForm != null)
            JumiaApplication.setRatingReviewValues(dynamicRatingForm.save());
    }

    /**
     * Load rating and review form
     */
    private void loadReviewAndRatingFormValues() {
        
        ContentValues savedRatingReviewValues = new ContentValues();
        
        if(formValues == null){
            savedRatingReviewValues = JumiaApplication.getRatingReviewValues();
        } else {
            savedRatingReviewValues = formValues;
        }
        
            // Validate values
            if(savedRatingReviewValues != null && dynamicRatingForm != null) {
                // Get dynamic form and update
                Iterator<DynamicFormItem> iter = dynamicRatingForm.getIterator();
                while (iter.hasNext()) {
                    DynamicFormItem item = iter.next();
                    try {
                        item.loadState(savedRatingReviewValues);
                    } catch (NullPointerException e) {
                        Log.w(TAG, "LOAD STATE: NOT CONTAINS KEY " + item.getKey());
                    }
                }
            }
    }

    private void displayPriceInformation(TextView productPriceNormal, TextView productPriceSpecial) {
        String unitPrice = completeProduct.getPrice();
        /*--if (unitPrice == null) unitPrice = completeProduct.getMaxPrice();*/
        String specialPrice = completeProduct.getSpecialPrice();
        /*--if (specialPrice == null) specialPrice = completeProduct.getMaxSpecialPrice();*/

        displayPriceInfo(productPriceNormal, productPriceSpecial, unitPrice, specialPrice);
    }

    private void displayPriceInfo(TextView productPriceNormal, TextView productPriceSpecial,
            String unitPrice, String specialPrice) {
        if (specialPrice == null || (unitPrice.equals(specialPrice))) {
            // display only the special price
            productPriceSpecial.setText(unitPrice);
            productPriceNormal.setVisibility(View.GONE);
        } else {
            // display special and normal price
            productPriceSpecial.setText(specialPrice);
            productPriceNormal.setText(unitPrice);
            productPriceNormal.setVisibility(View.VISIBLE);
            productPriceNormal.setPaintFlags(productPriceNormal.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }


    /**
     * function to clean form
     */
    private void cleanForm() {
        if(dynamicRatingForm != null)
            dynamicRatingForm.clear();
       
        JumiaApplication.cleanRatingReviewValues();
        if(formValues != null)
            formValues = null;
        
        if(isShowingRatingForm){
            setRatingLayout(ratingForm);
        } else {
            setRatingLayout(reviewForm);
        }
        cleanReviewText();
        setReviewName(dynamicRatingForm);
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

            Log.d(TAG, "review product completed: success");
            // Clean options after success
            Bundle params = new Bundle();
            params.putParcelable(TrackerDelegator.PRODUCT_KEY, completeProduct);
            
            //only needed for tracking purpose
            params.putSerializable(TrackerDelegator.RATINGS_KEY, getRatingsMapValues(dynamicRatingForm));
            
            TrackerDelegator.trackItemReview(params);
            String buttonMessageText = getResources().getString(R.string.dialog_to_reviews);
            
           
            //Validate if fragment is nested
            nestedFragment = this.getParentFragment() instanceof ReviewsFragment;

            if(nestedFragment)
                buttonMessageText = getResources().getString(R.string.ok_label);
            
            
            dialog_review_submitted = DialogGenericFragment.newInstance(true, false,
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

        case GET_FORM_RATING_EVENT:
            Log.i(TAG, "GET_FORM_RATING_EVENT");
            ratingForm = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            setRatingLayout(ratingForm);
            triggerReviewForm();
            return true;
            
        case GET_FORM_REVIEW_EVENT:
            Log.i(TAG, "GET_FORM_REVIEW_EVENT");
            reviewForm = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            if(ratingForm == null)
                setRatingLayout(reviewForm);
            showFragmentContentContainer();
            return true;

        case GET_PRODUCT_EVENT:
            Log.d(TAG, "GOT GET_PRODUCT_EVENT");
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return true;
            } else {
                completeProduct = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                JumiaApplication.INSTANCE.setCurrentProduct(completeProduct);
                // triggerAutoLogin();
                // triggerCustomer();
                triggerRatingForm();
                // Waiting for the fragment communication
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFragmentContentContainer();
                    }
                }, 300);
            }

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
        case GET_FORM_RATING_EVENT:
            triggerReviewForm();
            return false;
        case GET_FORM_REVIEW_EVENT:
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
            
            
        case GET_PRODUCT_EVENT:
            if (!errorCode.isNetworkError()) {
                Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();

                showFragmentContentContainer();

                try {
                    getBaseActivity().onBackPressed();
                } catch (IllegalStateException e) {
                    getBaseActivity().popBackStackUntilTag(FragmentType.HOME.toString());
                }
                return false;
            }
        default:
        }

        return false;
    }


    private void triggerRatingForm() {
        triggerContentEvent(new GetRatingFormHelper(), null, mCallBack);
    }
    
    private void triggerReviewForm() {
        triggerContentEvent(new GetReviewFormHelper(), null, mCallBack);
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
        case GET_FORM_RATING_EVENT:
            triggerRatingForm();
            break;
        case REVIEW_RATING_PRODUCT_EVENT:
            formsValidation();
            break;
        default:
            super.onRetryRequest(eventType);
            break;
        }
    }
    
    /**
     * function that validates if the form is correctly filled
     */
    private void formsValidation(){
        if(dynamicRatingForm != null){
            if(!dynamicRatingForm.validate())
                return;
        } else if (ratingForm != null) {
            if(isShowingRatingForm)
                dynamicRatingForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), ratingForm);
            else if(reviewForm != null)
                dynamicRatingForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), reviewForm);
            
            if(!dynamicRatingForm.validate())
                return;
        } else {
            triggerRatingForm();
        }
        isExecutingSendReview = false;
        if (!isExecutingSendReview) {
            isExecutingSendReview = true;
            if(isShowingRatingForm) executeSendReview(ratingForm.action, dynamicRatingForm);
            else executeSendReview(reviewForm.action, dynamicRatingForm);
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
        
        form.getItemByKey(SKU).setValue(completeProduct.getSku());
        
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
           String id =  ratingFormContainer.findViewById(i).findViewById(R.id.option_stars).getTag().toString();
        
           String key =formName+"["+id+"]";
           values.put(key, rate);
           
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "  -----> ON SAVE INSTANCE STATE !!!!!!!!!");
        saveReview();
        
        // TODO, fix Error on Parcer
//        outState.putParcelable(RATING_FORM, ratingForm);
//        outState.putParcelable(REVIEW_FORM, reviewForm);
        
//        JumiaApplication.INSTANCE.ratingForm = ratingForm;
//        JumiaApplication.INSTANCE.reviewForm = reviewForm;

        outState.putBoolean(SHOWING_FORM, isShowingRatingForm);
        outState.putString(NAME, reviewName);
        outState.putString(TITLE, reviewTitle);
        outState.putString(COMMENT, reviewComment);
        super.onSaveInstanceState(outState);
    }
    

    
    protected boolean getIsShowingRatingForm(){
        return isShowingRatingForm;
    }

    
    /**
     * function responsible for creating a map between the rating option name and it's value
     * needed for tracking only
     * @return
     */
    private HashMap<String, Long> getRatingsMapValues(DynamicForm form){
        HashMap<String, Long> values = new HashMap<>();
        
            if(form != null){
                Map<String, String> ratingMap = form.getItemByKey(RATINGS).getEntry().getDateSetRating();
                View  ratingFormContainer = form.getItemByKey(RATINGS).getEditControl();
                
                for (int i = 1; i < ratingMap.size()+1; i++) {
                   long rate =  (long)((RatingBar)ratingFormContainer.findViewById(i).findViewById(R.id.option_stars)).getRating();
                   String name =  ((TextView)ratingFormContainer.findViewById(i).findViewById(R.id.option_label)).getText().toString();
                   values.put(name, rate);
                }
            }
        
        return values;
    }
    
}
