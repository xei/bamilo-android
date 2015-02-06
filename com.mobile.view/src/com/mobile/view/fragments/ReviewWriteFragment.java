package com.mobile.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.forms.Form;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.objects.Customer;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.DeviceInfoHelper;
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
import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * @modified Paulo Carvalho
 * 
 */
public class ReviewWriteFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ReviewWriteFragment.class);
    
    private static final String SHOWING_FORM = "showingForm";
    
    private static final String NAME = "name";

    private static final String SKU = "sku";
    
    private static final String RATINGS = "ratings";

    private static ReviewWriteFragment writeReviewFragment;

    private CompleteProduct completeProduct;

    private TextView productName;

    private LinearLayout reviewContainer;

    private LinearLayout ratingContainer;

    private DialogGenericFragment dialog_review_submitted;

    private boolean isExecutingSendReview = false;

    private String mCompleteProductUrl = "";
    
    private LinearLayout mainContainer;

    private Form ratingForm;
    
    private Form reviewForm;
    
    private DynamicForm dynamicRatingForm;
    
    private DynamicForm dynamicReviewForm;
    
    private boolean failedRatingForm = false;
    
    private boolean failedReviewForm = false;
    
    private boolean isShowingRatingForm = true;
    
    public static final String RATING_SHOW = "isShowingRating";
    
    private static final String RATING_FORM = "rating_form";
    private static final String REVIEW_FORM = "review_form";
    
    private boolean nestedFragment = true;
    
    /**
     * Get instance
     * 
     * @return
     */
    public static ReviewWriteFragment getInstance(Bundle bundle) {
        Log.i(TAG, "getInstance");
        writeReviewFragment = new ReviewWriteFragment();
        if (bundle != null) {
            String contentUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
            writeReviewFragment.mCompleteProductUrl = contentUrl != null ? contentUrl : "";
        }
        return writeReviewFragment;
    }

    /**
     * Empty constructor
     */
    public ReviewWriteFragment() {
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
        completeProduct = JumiaApplication.INSTANCE.getCurrentProduct();
        isExecutingSendReview = false;
        if(savedInstanceState != null){
            ratingForm = JumiaApplication.INSTANCE.ratingForm;
            reviewForm =  JumiaApplication.INSTANCE.reviewForm;
//            ratingForm = savedInstanceState.getParcelable(RATING_FORM);
//            reviewForm =  savedInstanceState.getParcelable(REVIEW_FORM);
            isShowingRatingForm = savedInstanceState.getBoolean(SHOWING_FORM);
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
        reviewContainer = (LinearLayout) view.findViewById(R.id.form_review_container);
        ratingContainer = (LinearLayout) view.findViewById(R.id.form_rating_container);
        mainContainer = (LinearLayout) view.findViewById(R.id.product_rating_container);
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
        // Validate is service is available
        if (JumiaApplication.mIsBound) {
            // load complete product URL
            if (mCompleteProductUrl.equalsIgnoreCase("") && getArguments() != null && getArguments().containsKey(ConstantsIntentExtra.CONTENT_URL)) {
                String contentUrl = getArguments().getString(ConstantsIntentExtra.CONTENT_URL);
                mCompleteProductUrl = contentUrl != null ? contentUrl : "";
            }

        } else {
            showFragmentRetry(this);
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
        isExecutingSendReview = false;
        if (getArguments() != null
                && getArguments().containsKey(ReviewsFragment.CAME_FROM_POPULARITY)) {
            getView().findViewById(R.id.product_info_container).setVisibility(View.GONE);
            getView().findViewById(R.id.shadow).setVisibility(View.GONE);
        } 
        if(getArguments() != null
                && getArguments().containsKey(RATING_SHOW)) {
            isShowingRatingForm = getArguments().getBoolean(RATING_SHOW);
            
            ratingForm = JumiaApplication.INSTANCE.ratingForm;
            reviewForm =  JumiaApplication.INSTANCE.reviewForm;

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
                setRatingLayout(ratingForm); 
                setReviewLayout(reviewForm);
                loadReviewAndRatingFormValues();
                if(isShowingRatingForm){
                    ratingContainer.setVisibility(View.VISIBLE);
                    reviewContainer.setVisibility(View.GONE);
                } else {
                    ratingContainer.setVisibility(View.GONE);
                    reviewContainer.setVisibility(View.VISIBLE);
                }
            } else {
                triggerRatingForm();
            }
            
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
        if(ratingContainer.isShown()){
            isShowingRatingForm = true;
        } else {
            isShowingRatingForm = false;
        }
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
                showFragmentRetry(this);
            }
            
        } else {
            mainContainer.setVisibility(View.VISIBLE);
            
            if (getBaseActivity() == null) return;
    
            dynamicRatingForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), form);
            if(ratingContainer.getChildCount() > 0)
                ratingContainer.removeAllViews();
            
            ratingContainer.addView(dynamicRatingForm.getContainer());
            
            ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(false);
            ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        JumiaApplication.setRatingReviewValues(dynamicRatingForm.save());
                        loadReviewAndRatingFormValues();
                        isShowingRatingForm = false;
                        ratingContainer.setVisibility(View.GONE);
                        reviewContainer.setVisibility(View.VISIBLE);
                        
                        ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(false);
                        
                        
                    }
                }
            });
            
            setGenericLayout();
            
        }

    }

    /**
     * Set the Products layout using inflate
     */
    private void setReviewLayout(Form form) {
        if (completeProduct == null) {
            if (!mCompleteProductUrl.equalsIgnoreCase("")) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
                showFragmentRetry(this);
            }
            
        } else {
            mainContainer.setVisibility(View.VISIBLE);
            
            if (getBaseActivity() == null) return;
    
            dynamicReviewForm = FormFactory.getSingleton().CreateForm(FormConstants.REVIEW_FORM, getBaseActivity(), form);
            if(reviewContainer.getChildCount() > 0)
            reviewContainer.removeAllViews();
            
            reviewContainer.addView(dynamicReviewForm.getContainer());
            
            ((CheckBox) dynamicReviewForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(true);
            
            ((CheckBox) dynamicReviewForm.getContainer().findViewById(R.id.checkbox_form)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        JumiaApplication.setRatingReviewValues(dynamicReviewForm.save());
                        loadReviewAndRatingFormValues();
                        isShowingRatingForm = true;
                        ratingContainer.setVisibility(View.VISIBLE);
                        reviewContainer.setVisibility(View.GONE);
                        ((CheckBox) dynamicReviewForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(true);
                    }
                }
            });

            setGenericLayout();
            
            setReviewName(dynamicReviewForm);
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
                showFragmentRetry(this);
            }
            
        } else {
            mainContainer.setVisibility(View.VISIBLE);
            
            productName = (TextView) getView().findViewById(R.id.product_detail_name);
            TextView productPriceSpecial = (TextView) getView().findViewById(R.id.product_price_special);
            TextView productPriceNormal = (TextView) getView().findViewById(R.id.product_price_normal);

            ((Button) getView().findViewById(R.id.send_review)).setOnClickListener(this);

            productName.setText(completeProduct.getBrand() + " " + completeProduct.getName());
            displayPriceInformation(productPriceNormal, productPriceSpecial);       
        }
    }
    
    /**
     * Save rating and review form
     */
    private void saveReview() {

        if(isShowingRatingForm){
            if(dynamicRatingForm != null)
                JumiaApplication.setRatingReviewValues(dynamicRatingForm.save());
        } else {
            if(dynamicReviewForm != null)
                JumiaApplication.setRatingReviewValues(dynamicReviewForm.save());
        }

    }

    /**
     * Load rating and review form
     */
    private void loadReviewAndRatingFormValues() {
        
        ContentValues savedRatingReviewValues = JumiaApplication.getRatingReviewValues();
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
        
        // Validate values
        if(savedRatingReviewValues != null && dynamicReviewForm != null) {
            // Get dynamic form and update
            Iterator<DynamicFormItem> iter = dynamicReviewForm.getIterator();
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
        if(dynamicReviewForm != null){
            dynamicReviewForm.clear();
            setReviewName(dynamicReviewForm);
        }
        
        JumiaApplication.cleanRatingReviewValues();
        
        if(ratingForm != null)
        setRatingLayout(ratingForm);
        if(reviewForm != null)
        setReviewLayout(reviewForm);
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
            
            if(isShowingRatingForm){
                params.putSerializable(TrackerDelegator.RATINGS_KEY, getRatingsMapValues(dynamicRatingForm));
            } else {
                params.putSerializable(TrackerDelegator.RATINGS_KEY, getRatingsMapValues(dynamicReviewForm));
            }
            
            TrackerDelegator.trackItemReview(params);
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

        case GET_FORM_RATING_EVENT:
            Log.i(TAG, "GET_FORM_RATING_EVENT");
            ratingForm = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            setRatingLayout(ratingForm);
            triggerReviewForm();
            return true;
            
        case GET_FORM_REVIEW_EVENT:
            Log.i(TAG, "GET_FORM_REVIEW_EVENT");
            reviewForm = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            setReviewLayout(reviewForm);
            showFragmentContentContainer();
            return true;

        case GET_PRODUCT_EVENT:
            Log.d(TAG, "GOT GET_PRODUCT_EVENT");
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return true;
            } else {
                completeProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
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
        isExecutingSendReview = false;
        // Generic errors
        super.handleErrorEvent(bundle);
        
        
        
        switch (eventType) {
        // case GET_CUSTOMER:
        // List<String> errors = event.errorMessages.get( Errors.JSON_ERROR_TAG);
        // if ( errors.contains( Errors.CODE_CUSTOMER_NOT_LOGGED_ID )) {
        // return true;
        // }
        // return false;
        // case LOGIN_EVENT:
        // // don't care
        // return true;
        //
        // case GET_CUSTOMER:
        // // don't care
        // // customerCred = null;
        // // Log.i("DIDNT GET CUSTOMER"," HERE ");
        // return true;
        case GET_FORM_RATING_EVENT:

            failedRatingForm = true;
            triggerReviewForm();

            return false;
        case GET_FORM_REVIEW_EVENT:
            failedReviewForm = true;
            showFragmentContentContainer();
            showFragmentRetry(this);
            return false;
        case REVIEW_RATING_PRODUCT_EVENT:
            dialog = DialogGenericFragment.createServerErrorDialog(getBaseActivity(),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isExecutingSendReview) {
                                formsValidation();
                            }
                            dialog.dismiss();
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fragment_root_retry_button) {
            getBaseActivity().onSwitchFragment(FragmentType.WRITE_REVIEW, getArguments(), FragmentController.ADD_TO_BACK_STACK);

        } else if(id == R.id.send_review){
            formsValidation();
        }
    }
    
    /**
     * function that validates if the form is correctly filled
     */
    private void formsValidation(){
        if(isShowingRatingForm){
            if(dynamicRatingForm != null){
                if(!dynamicRatingForm.validate())
                    return;
            } else if (ratingForm != null) {
                dynamicRatingForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), ratingForm);
                if(!dynamicRatingForm.validate())
                    return;
            } else {
                triggerRatingForm();
            }
            isExecutingSendReview = false;
            if (!isExecutingSendReview) {
                isExecutingSendReview = true;
                executeSendReview(ratingForm.action, dynamicRatingForm);
            }
                
        } else {
            if(dynamicReviewForm != null){
                if(!dynamicReviewForm.validate())
                    return;
            } else if (reviewForm != null) {
                dynamicReviewForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), reviewForm);
                if(!dynamicReviewForm.validate())
                    return;
            } else {
                triggerRatingForm();
               
            }
            isExecutingSendReview = false;
            if (!isExecutingSendReview) {
                isExecutingSendReview = true;
                executeSendReview(reviewForm.action, dynamicReviewForm);
            }
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
           String id =  ((RatingBar)ratingFormContainer.findViewById(i).findViewById(R.id.option_stars)).getTag().toString();
        
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
        
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRetryRequest(EventType eventType) {
        switch(eventType){
        case GET_FORM_RATING_EVENT:
            triggerRatingForm();
            return;
        case REVIEW_RATING_PRODUCT_EVENT:
            formsValidation();
            return;
        default:
            super.onRetryRequest(eventType);
            return;
        }
        
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
        HashMap<String, Long> values = new HashMap<String, Long>();
        
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
