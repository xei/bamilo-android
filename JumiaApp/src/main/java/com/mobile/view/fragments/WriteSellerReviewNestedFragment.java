package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.configs.GetSellerReviewFormHelper;
import com.mobile.helpers.products.RatingReviewProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents the write seller review screen framgent when the user uses landscape view on Review fragment and manages all interactions about it's form.
 *
 * @author Paulo Carvalho
 *
 */
public class WriteSellerReviewNestedFragment extends BaseFragment {

    private static final String TAG = WriteSellerReviewNestedFragment.class.getSimpleName();

    private static final String NAME = "name";

    private static final String TITLE = "title";

    private static final String COMMENT = "comment";

    private static final String SELLER_ID = "sellerId";

    private static final String RATINGS = "ratings";

    private LinearLayout mReviewContainer;

    private DialogGenericFragment dialog_review_submitted;

    private boolean isExecutingSendReview = false;

    private View mMainContainer;

    private Form mSellerReviewForm;

    private DynamicForm mDynamicSellerReviewForm;

    private boolean nestedFragment = true;

    private ContentValues formValues;

    private String mSellerId = "";

    private HashMap<String,String> mFormReviewValues = new HashMap<>();

    /**
     * Get instance
     *
     * @return
     */
    public static WriteSellerReviewNestedFragment getInstance(Bundle bundle) {
        WriteSellerReviewNestedFragment mWriteSellerReviewFragment = new WriteSellerReviewNestedFragment();
        mWriteSellerReviewFragment.setArguments(bundle);
        return mWriteSellerReviewFragment;
    }

    /**
     * Empty constructor
     */
    public WriteSellerReviewNestedFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Product,
                R.layout.review_write_fragment_nested,
                IntConstants.ACTION_BAR_NO_TITLE,
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
        isExecutingSendReview = false;
        JumiaApplication.setIsSellerReview(true);
        if(getArguments() != null){
            if(getArguments().containsKey(ProductDetailsFragment.SELLER_ID)){
                mSellerId = getArguments().getString(ProductDetailsFragment.SELLER_ID);
            }
        }

        if(savedInstanceState != null){
          restoreSavedInstance(savedInstanceState);
        }
    }

    private void restoreSavedInstance(Bundle savedInstanceState){

        mSellerReviewForm =  JumiaApplication.INSTANCE.mSellerReviewForm;

        if(savedInstanceState.containsKey(ProductDetailsFragment.SELLER_ID))
            mSellerId = savedInstanceState.getString(ProductDetailsFragment.SELLER_ID);

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
        Print.i(TAG, "ON START");
        
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
        isExecutingSendReview = false;

        if (TextUtils.isEmpty(mSellerId)) {
            if (getArguments().containsKey(ProductDetailsFragment.SELLER_ID)) {
                mSellerId = getArguments().getString(ProductDetailsFragment.SELLER_ID);
                if (!TextUtils.isEmpty(mSellerId)) {
                    triggerSellerReviewForm();
                } else {
                    showRetryLayout();
                }
            } else {
                showRetryLayout();
            }
        } else {
            mSellerReviewForm = JumiaApplication.INSTANCE.mSellerReviewForm;
            if (mSellerReviewForm != null) {
                loadReviewFormValues();
                setReviewLayout(mSellerReviewForm);
            } else {
                triggerSellerReviewForm();
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
        JumiaApplication.setIsSellerReview(true);
        if(mDynamicSellerReviewForm != null){
            saveTextReview(mDynamicSellerReviewForm);
            formValues = mDynamicSellerReviewForm.save();
            JumiaApplication.setSellerReviewValues(formValues);
        }
        //duplicated here and on onSaveInstance because when this fragment is removed from the Reviews Landscape it doesn't pass on the onSaveInstance method
        JumiaApplication.INSTANCE.mSellerReviewForm = mSellerReviewForm;
        saveReview();

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
        Print.i(TAG, "ON DESTROY");
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

    private void saveTextReview(DynamicForm form){
        mFormReviewValues = new HashMap<>();
        if(form != null && form.getItemByKey(NAME) != null){
            mFormReviewValues.put(NAME,form.getItemByKey(NAME).getValue());
        }
        if(form != null && form.getItemByKey(TITLE) != null){
            mFormReviewValues.put(TITLE,form.getItemByKey(TITLE).getValue());
        }
        if(form != null && form.getItemByKey(COMMENT) != null){
            mFormReviewValues.put(COMMENT,form.getItemByKey(COMMENT).getValue());
        }
        JumiaApplication.INSTANCE.setFormReviewValues(mFormReviewValues);
    }
  
    private void cleanReviewText(){
        JumiaApplication.INSTANCE.setFormReviewValues(null);
    }
    
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
    private void setGenericLayout() {

        mMainContainer.setVisibility(View.VISIBLE);

        TextView mReviewTitle = (TextView) getView().findViewById(R.id.write_title);

        mReviewTitle.setText(R.string.review_this_seller);

        getView().findViewById(R.id.send_review).setOnClickListener(this);

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
        
        ContentValues savedReviewValues;
        
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
                        Print.w(TAG, "LOAD STATE: NOT CONTAINS KEY " + item.getKey());
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
        JumiaApplication.INSTANCE.setFormReviewValues(null);
        if(formValues != null)
            formValues = null;
        
            setReviewLayout(mSellerReviewForm);

        cleanReviewText();
        setReviewName(mDynamicSellerReviewForm);
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

            Print.d(TAG, "review seller completed: success");
            // Clean options after success
            String buttonMessageText = getResources().getString(R.string.dialog_to_product);


            try {
                //Validate if fragment is nested
                nestedFragment = this.getParentFragment() instanceof ReviewsFragment;
            } catch (Exception e) {
                e.printStackTrace();
                nestedFragment = true;
            }

            dialog_review_submitted = DialogGenericFragment.newInstance(false, true,
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
                                if(nestedFragment) {
                                    cleanForm();
                                }
                                getBaseActivity().popBackStackUntilTag(FragmentType.PRODUCT_DETAILS.toString());
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
            Print.i(TAG, "GET_FORM_SELLER_REVIEW_EVENT");
            mSellerReviewForm = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
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
            executeSendReview(mSellerReviewForm.getAction(), mDynamicSellerReviewForm);
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
        super.onSaveInstanceState(outState);
    }
    
}
