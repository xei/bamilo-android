package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.configs.GetRatingFormHelper;
import com.mobile.helpers.configs.GetReviewFormHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.helpers.products.RatingReviewProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
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

/**
 * This class represents the write product review screen and manages all interactions about it's form.
 *
 * @author sergiopereira
 * @modified Paulo Carvalho
 * 
 */
public class ReviewWriteFragment extends BaseFragment {

    private static final String TAG = ReviewWriteFragment.class.getSimpleName();
    
    private static final String SHOWING_FORM = "showingForm";
    
    private static final String NAME = "name";
    
    private static final String TITLE = "title";
    
    private static final String COMMENT = "comment";

    private static final String SKU = "sku";
    
    private static final String RATINGS = "ratings";

    private ProductComplete completeProduct;

    private LinearLayout ratingContainer;

    private DialogGenericFragment dialog_review_submitted;

    private boolean isExecutingSendReview = false;

    private String mCompleteProductSku = "";
    
    private View mainContainer;

    private Form ratingForm;
    
    private Form reviewForm;
    
    private DynamicForm dynamicRatingForm;
    
    private boolean isShowingRatingForm = true;
    
    public static final String RATING_SHOW = "isShowingRating";
    
    private boolean nestedFragment = true;
    
    private ContentValues formValues;

    private SharedPreferences mSharedPrefs;

    private HashMap<String,String> mFormReviewValues = new HashMap<>();

    /**
     * Get instance
     * 
     * @return
     */
    public static ReviewWriteFragment getInstance(Bundle bundle) {
        ReviewWriteFragment fragment = new ReviewWriteFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public ReviewWriteFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Product,
                R.layout.review_write_fragment,
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

        if(savedInstanceState != null){
            ratingForm = JumiaApplication.INSTANCE.ratingForm;
            reviewForm =  JumiaApplication.INSTANCE.reviewForm;
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
        Print.i(TAG, "ON VIEW CREATED");
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
        Print.i(TAG, "ON START");
        
    }

    /**
     * set control flag to know which form is showing
     */
    private void setRatingReviewFlag(){
        if(!getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) && getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)){
            isShowingRatingForm = false;
        } else if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) && !getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)){
            isShowingRatingForm = true;
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

        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCompleteProductSku = arguments.getString(ConstantsIntentExtra.PRODUCT_SKU);
            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if(parcelableProduct instanceof ProductComplete){
                completeProduct = (ProductComplete)parcelableProduct;
            }

        }

        isExecutingSendReview = false;
        
        if (getArguments() != null && getArguments().containsKey(ReviewsFragment.CAME_FROM_POPULARITY)) {
            getView().findViewById(R.id.product_info_container).setVisibility(View.GONE);
            getView().findViewById(R.id.shadow).setVisibility(View.GONE);
        } 
        
        if(getArguments() != null && getArguments().containsKey(RATING_SHOW)) {
            isShowingRatingForm = getArguments().getBoolean(RATING_SHOW);
            ratingForm = JumiaApplication.INSTANCE.ratingForm;
            reviewForm =  JumiaApplication.INSTANCE.reviewForm;
        }
        setRatingReviewFlag();
            // load complete product URL
            if (mCompleteProductSku.equalsIgnoreCase("") && getArguments() != null && getArguments().containsKey(ConstantsIntentExtra.PRODUCT_SKU)) {
                String sku = getArguments().getString(ConstantsIntentExtra.PRODUCT_SKU);
                mCompleteProductSku = sku != null ? sku : "";
            }
            
            if (completeProduct == null) {
                ContentValues values = new ContentValues();
                values.put(GetProductHelper.SKU_TAG, mCompleteProductSku);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
                /* Commented due to unnecessary data being fetched
                triggerAutoLogin();
                triggerCustomer();*/
                if(ratingForm != null || reviewForm != null){
                    loadReviewAndRatingFormValues();
                    if(isShowingRatingForm){
                        setRatingLayout(ratingForm);
                    } else {
                        setRatingLayout(reviewForm);
                    }
                } else {
                    if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true)){
                        triggerRatingForm();
                    } else if(!getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) && getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)) {
                        triggerReviewForm();
                    }

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
        Print.i(TAG, "ON DESTROY");
    }

    /*
     * Set review name by user name if is logged.
     */
    private void setReviewName(DynamicForm reviewForm) {
        if(reviewForm != null && reviewForm.getItemByKey(NAME) != null && reviewForm.getItemByKey(NAME).getValue().equals("")){
            Customer customer = JumiaApplication.CUSTOMER;
            if(customer != null && !TextUtils.isEmpty(customer.getFirstName())){
                reviewForm.getItemByKey(NAME).setValue(customer.getFirstName());
            }
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
            if (!mCompleteProductSku.equalsIgnoreCase("")) {
                ContentValues values = new ContentValues();
                values.put(GetProductHelper.SKU_TAG, mCompleteProductSku);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
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
            restoreTextReview(dynamicRatingForm);
            setReviewName(dynamicRatingForm);

            //Validate if both reviews and ratings are enabled on country configuration
            if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) && getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)){
                if(isShowingRatingForm)
                    ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(false);
                else
                    ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(true);

                ((CheckBox) dynamicRatingForm.getContainer().findViewById(R.id.checkbox_form)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (reviewForm != null) {
                                formValues = dynamicRatingForm.save();
                                JumiaApplication.setRatingReviewValues(formValues);
                                isShowingRatingForm = false;
                                setRatingLayout(reviewForm);
                            }

                        } else {
                            if (ratingForm != null) {
                                formValues = dynamicRatingForm.save();
                                JumiaApplication.setRatingReviewValues(formValues);
                                hideKeyboard();
                                saveTextReview(dynamicRatingForm);
                                isShowingRatingForm = true;
                                setRatingLayout(ratingForm);
                            }

                        }
                    }
                });
            }

            setGenericLayout();
            
        }

    }

    /**
     * save the information regarding the review form
     */
    private void saveTextReview(DynamicForm form){
        if(!isShowingRatingForm){
            mFormReviewValues = new HashMap<>();
            if(form != null && form.getItemByKey(NAME) != null){
                mFormReviewValues.put(NAME,form.getItemByKey(NAME).getValue());
            }
            if(form != null && form.getItemByKey(TITLE) != null){
                mFormReviewValues.put(TITLE, form.getItemByKey(TITLE).getValue());
            }
            if(form != null && form.getItemByKey(COMMENT) != null){
                mFormReviewValues.put(COMMENT,form.getItemByKey(COMMENT).getValue());
            }
            JumiaApplication.INSTANCE.setFormReviewValues(mFormReviewValues);
        }

    }

    /**
     * clean  fields after sending a review
     */
    private void cleanReviewText(){
        JumiaApplication.INSTANCE.setFormReviewValues(null);
    }

    /**
     * restore information related to the form edit texts
     * @param form
     */
    private void restoreTextReview(DynamicForm form){
        mFormReviewValues = JumiaApplication.INSTANCE.getFormReviewValues();
        if(form != null && form.getItemByKey(NAME) != null){
            if(mFormReviewValues != null)
                form.getItemByKey(NAME).setValue(mFormReviewValues.get(NAME));
        }
        if(form != null && form.getItemByKey(TITLE) != null){
            if(mFormReviewValues != null)
                form.getItemByKey(TITLE).setValue(mFormReviewValues.get(TITLE));
        }
        if(form != null && form.getItemByKey(COMMENT) != null){
            if(mFormReviewValues != null)
                form.getItemByKey(COMMENT).setValue(mFormReviewValues.get(COMMENT));
        }
    }
    
    /**
     * Set info of the product on write review screen
     * 
     */
    private void setGenericLayout(){
        
        if (completeProduct == null) {
            if (!mCompleteProductSku.equalsIgnoreCase("")) {
                ContentValues values = new ContentValues();
                values.put(GetProductHelper.SKU_TAG, mCompleteProductSku);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
                showRetryLayout();
            }
            
        } else {
            mainContainer.setVisibility(View.VISIBLE);

            TextView productName = (TextView) getView().findViewById(R.id.product_detail_name);
            TextView productPriceSpecial = (TextView) getView().findViewById(R.id.product_price_special);
            TextView productPriceNormal = (TextView) getView().findViewById(R.id.pdv_text_price);

            getView().findViewById(R.id.send_review).setOnClickListener(this);

            productName.setText(completeProduct.getBrand() + " " + completeProduct.getName());
            displayPriceInformation(productPriceNormal, productPriceSpecial);       
        }
    }
    
    /**
     * Save rating and review form
     */
    private void saveReview() {
        if(dynamicRatingForm != null){
            JumiaApplication.setRatingReviewValues(dynamicRatingForm.save());
            formValues = dynamicRatingForm.save();
            saveTextReview(dynamicRatingForm);
        }
    }

    /**
     * Load rating and review form
     */
    private void loadReviewAndRatingFormValues() {
        
        ContentValues savedRatingReviewValues = formValues == null ? JumiaApplication.getRatingReviewValues() : formValues;
        
        // Validate values
        if(savedRatingReviewValues != null && dynamicRatingForm != null) {
            // Get dynamic form and update
            Iterator<DynamicFormItem> iter = dynamicRatingForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                try {
                    item.loadState(savedRatingReviewValues);
                } catch (NullPointerException e) {
                    Print.w(TAG, "LOAD STATE: NOT CONTAINS KEY " + item.getKey());
                }
            }
        }
    }

    /**
     * method to display the header price info
     * @param productPriceNormal
     * @param productPriceSpecial
     */
    private void displayPriceInformation(TextView productPriceNormal, TextView productPriceSpecial) {
        String unitPrice = String.valueOf(completeProduct.getPrice());
        /*--if (unitPrice == null) unitPrice = completeProduct.getMaxPrice();*/
        String specialPrice = String.valueOf(completeProduct.getSpecialPrice());
        /*--if (specialPrice == null) specialPrice = completeProduct.getMaxSpecialPrice();*/

        displayPriceInfo(productPriceNormal, productPriceSpecial, unitPrice, specialPrice);
    }

    private void displayPriceInfo(TextView productPriceNormal, TextView productPriceSpecial,
            String unitPrice, String specialPrice) {
        if (specialPrice == null || (unitPrice.equals(specialPrice))) {
            // display only the special price
            productPriceSpecial.setText(CurrencyFormatter.formatCurrency(unitPrice));
            productPriceNormal.setVisibility(View.GONE);
        } else {
            // display special and normal price
            productPriceSpecial.setText(CurrencyFormatter.formatCurrency(specialPrice));
            productPriceNormal.setText(CurrencyFormatter.formatCurrency(unitPrice));
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
        JumiaApplication.INSTANCE.setFormReviewValues(null);
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
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        Print.i(TAG, "onSuccessEvent eventType : " + eventType);
        switch (eventType) {
        case REVIEW_RATING_PRODUCT_EVENT:

            Print.d(TAG, "review product completed: success");
            // Clean options after success
            Bundle params = new Bundle();
            params.putParcelable(TrackerDelegator.PRODUCT_KEY, completeProduct);
            
            //only needed for tracking purpose
            params.putSerializable(TrackerDelegator.RATINGS_KEY, getRatingsMapValues(dynamicRatingForm));

            TrackerDelegator.trackItemReview(params, isShowingRatingForm);
            String buttonMessageText = getResources().getString(R.string.dialog_to_product);

            //Validate if fragment is nested
            nestedFragment = getParentFragment() instanceof ReviewsFragment;

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
                                    getBaseActivity().onBackPressed();
                                } else {
                                    // Remove entries until specific tag
                                    getBaseActivity().popBackStackUntilTag(FragmentType.PRODUCT_DETAILS.toString());
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
            Print.i(TAG, "GET_FORM_RATING_EVENT");
            ratingForm = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            setRatingLayout(ratingForm);
            if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)){
                triggerReviewForm();
            } else {
                showFragmentContentContainer();
            }

            return true;
            
        case GET_FORM_REVIEW_EVENT:
            Print.i(TAG, "GET_FORM_REVIEW_EVENT");
            reviewForm = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            if(ratingForm == null)
                setRatingLayout(reviewForm);
            showFragmentContentContainer();
            return true;

        case GET_PRODUCT_DETAIL:
            Print.d(TAG, "GOT GET_PRODUCT_EVENT");
            if (((ProductComplete) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return true;
            } else {
                completeProduct = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
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
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        // Hide progress
        hideActivityProgress();
        
        isExecutingSendReview = false;
        // Generic errors
        if(super.handleErrorEvent(bundle)) return true;
        
        switch (eventType) {
        case GET_FORM_RATING_EVENT:
            if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)){
                triggerReviewForm();
            } else {
                showRetryLayout();
            }

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
            
            
        case GET_PRODUCT_DETAIL:
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


    /**
     * request the ratign form
     */
    private void triggerRatingForm() {
        triggerContentEvent(new GetRatingFormHelper(), null, mCallBack);
    }

    /**
     * request the review form
     */
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
        if(id == R.id.send_review){
            formsValidation();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
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
            if(isShowingRatingForm){
                if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_REQUIRED_LOGIN, true) && JumiaApplication.CUSTOMER == null){
                    Bundle bundle = getArguments();
                    bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.WRITE_REVIEW);
                    bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProductSku);
                    getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
                } else {
                    executeSendReview(ratingForm.getAction(), dynamicRatingForm);
                }
            } else {
                if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_REQUIRED_LOGIN, true) && JumiaApplication.CUSTOMER == null){
                    Bundle bundle = getArguments();
                    bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.WRITE_REVIEW);
                    bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProductSku);
                    getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
                } else {
                    executeSendReview(reviewForm.getAction(), dynamicRatingForm);
                }
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
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        
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
        Print.d(TAG, "  -----> ON SAVE INSTANCE STATE !!!!!!!!!");
        saveReview();

        outState.putBoolean(SHOWING_FORM, isShowingRatingForm);
        super.onSaveInstanceState(outState);
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

    private SharedPreferences getSharedPref(){
        if(mSharedPrefs == null){
            //Validate if country configs allows rating and review, only show write review fragment if both are allowed
            mSharedPrefs = JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return mSharedPrefs;
    }

}