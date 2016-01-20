package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.ui.KeyboardUtils;
import com.mobile.utils.ui.ProductUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the write product review screen and manages all interactions about it's form.
 *
 * @author sergiopereira
 * @modified Paulo Carvalho
 *
 */
public class ReviewWriteFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = ReviewWriteFragment.class.getSimpleName();

    private static final String SHOWING_FORM = "showingForm";

    public static final String RATING_SHOW = "isShowingRating";

    private ProductComplete completeProduct;

    private LinearLayout ratingContainer;

    private DialogGenericFragment dialog_review_submitted;

    /**
     * flag used to avoid sending more than one rating/review on double click
     */
    private boolean isExecutingSendReview = false;

    private String mCompleteProductSku = "";

    private View mainContainer;

    private Form ratingForm;

    private Form reviewForm;

    private DynamicForm mDynamicForm;

    private boolean isShowingRatingForm = true;

    private boolean nestedFragment = true;

    private Bundle mSavedState;

    private SharedPreferences mSharedPrefs;

    /**
     * Get instance
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
                NavigationAction.PRODUCT,
                R.layout.review_write_fragment,
                R.string.write_comment,
                ADJUST_CONTENT);
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
        // Validate the saved state
        if (savedInstanceState != null) {
            ratingForm = JumiaApplication.INSTANCE.ratingForm;
            reviewForm = JumiaApplication.INSTANCE.reviewForm;
            mSavedState = savedInstanceState;
            isShowingRatingForm = savedInstanceState.getBoolean(SHOWING_FORM);
        } else {
            mSavedState = new Bundle();
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
        init();
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
        // Save the form state
        Bundle bundle = new Bundle();
        if(mDynamicForm != null) {
            mDynamicForm.saveFormState(bundle);
            mSavedState = bundle;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "ON SAVE INSTANCE STATE");
        super.onSaveInstanceState(outState);
        // Retain the old state
        if(mSavedState != null) {
            outState.putAll(mSavedState);
        }
        // Save the form state
        if(mDynamicForm != null) {
            mDynamicForm.saveFormState(outState);
        }
        // Save form type
        outState.putBoolean(SHOWING_FORM, isShowingRatingForm);
        // Save for foreground
        mSavedState = outState;
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

    /**
     * initialize form data
     */
    private void init() {
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCompleteProductSku = arguments.getString(ConstantsIntentExtra.PRODUCT_SKU);
            Parcelable parcelableProduct = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
            if (parcelableProduct instanceof ProductComplete) {
                completeProduct = (ProductComplete) parcelableProduct;
            }
        }
        isExecutingSendReview = false;
        if (getArguments() != null && getArguments().containsKey(ReviewsFragment.CAME_FROM_POPULARITY)) {
            getView().findViewById(R.id.product_info_container).setVisibility(View.GONE);
            getView().findViewById(R.id.shadow).setVisibility(View.GONE);
        }
        if (getArguments() != null && getArguments().containsKey(RATING_SHOW)) {
            isShowingRatingForm = getArguments().getBoolean(RATING_SHOW);
            ratingForm = JumiaApplication.INSTANCE.ratingForm;
            reviewForm = JumiaApplication.INSTANCE.reviewForm;
        }
        setRatingReviewFlag();
        // load complete product URL
        if (mCompleteProductSku != null && mCompleteProductSku.equalsIgnoreCase("") && getArguments() != null && getArguments().containsKey(ConstantsIntentExtra.PRODUCT_SKU)) {
            String sku = getArguments().getString(ConstantsIntentExtra.PRODUCT_SKU);
            mCompleteProductSku = sku != null ? sku : "";
        }

        if (completeProduct == null) {
            triggerContentEvent(new GetProductHelper(), GetProductHelper.createBundle(mCompleteProductSku, null), this);
        } else if (ratingForm != null || reviewForm != null) {
            loadReviewAndRatingFormValues();
            if (isShowingRatingForm) {
                setRatingLayout(ratingForm);
            } else {
                setRatingLayout(reviewForm);
            }
        } else if (getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true)) {
            triggerRatingForm();
        } else if (!getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) && getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)) {
            triggerReviewForm();
        }
    }

    /*
     * Set review name by user name if is logged.
     */
    private void setReviewName(DynamicForm reviewForm) {
        if(reviewForm != null && reviewForm.getItemByKey(RestConstants.NAME) != null && reviewForm.getItemByKey(RestConstants.NAME).getValue().equals("")){
            Customer customer = JumiaApplication.CUSTOMER;
            if(customer != null && !TextUtils.isEmpty(customer.getFirstName())){
                reviewForm.getItemByKey(RestConstants.NAME).setValue(customer.getFirstName());
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
                triggerContentEvent(new GetProductHelper(), bundle, this);
            } else {
                showRetryLayout();
            }

        } else {
            mainContainer.setVisibility(View.VISIBLE);

            if (getBaseActivity() == null) return;

            mDynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), form);
            if(ratingContainer.getChildCount() > 0)
                ratingContainer.removeAllViews();

            ratingContainer.addView(mDynamicForm.getContainer());

            loadReviewAndRatingFormValues();
            setReviewName(mDynamicForm);

            //Validate if both reviews and ratings are enabled on country configuration
            if (getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) && getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)) {
                ((CheckBox) mDynamicForm.getContainer().findViewById(R.id.checkbox_form)).setChecked(!isShowingRatingForm);
                ((CheckBox) mDynamicForm.getContainer().findViewById(R.id.checkbox_form)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mDynamicForm.saveFormState(mSavedState);
                        isShowingRatingForm = !isChecked;
                        if (isChecked) {
                            if (reviewForm != null) {
                                setRatingLayout(reviewForm);
                            }
                        } else if (ratingForm != null) {
                            // Hide keyboard
                            KeyboardUtils.hide(getView());
                            setRatingLayout(ratingForm);
                        }
                    }
                });
            }

            setGenericLayout();

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
                triggerContentEvent(new GetProductHelper(), bundle, this);
            } else {
                showRetryLayout();
            }

        } else {
            mainContainer.setVisibility(View.VISIBLE);
            TextView productName = (TextView) getView().findViewById(R.id.product_detail_name);
            TextView productPriceSpecial = (TextView) getView().findViewById(R.id.product_price_special);
            TextView productPriceNormal = (TextView) getView().findViewById(R.id.pdv_text_price);
            ProductUtils.setPriceRules(completeProduct, productPriceNormal, productPriceSpecial);
            getView().findViewById(R.id.send_review).setOnClickListener(this);
            productName.setText(String.format(getString(R.string.first_and_second_placeholders), completeProduct.getBrand(), completeProduct.getName()));
        }
    }

    /**
     * Load rating and review form
     */
    private void loadReviewAndRatingFormValues() {
        if (mDynamicForm != null) {
            mDynamicForm.loadSaveFormState(mSavedState);
        }
    }

    /**
     * function to clean form
     */
    private void cleanForm() {
        if(mDynamicForm != null)
            mDynamicForm.clear();

        if(isShowingRatingForm){
            setRatingLayout(ratingForm);
        } else {
            setRatingLayout(reviewForm);
        }
        setReviewName(mDynamicForm);
    }

    /**
     * request the ratign form
     */
    private void triggerRatingForm() {
        triggerContentEvent(new GetRatingFormHelper(), null,this);
    }

    /**
     * request the review form
     */
    private void triggerReviewForm() {
        triggerContentEvent(new GetReviewFormHelper(), null,this);
    }

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
        if (!isExecutingSendReview) {
            // avoid sending more than one rating/review on double click
            isExecutingSendReview = true;

            if(mDynamicForm != null){
                if(!mDynamicForm.validate())
                    return;
            } else if (ratingForm != null) {
                if(isShowingRatingForm)
                    mDynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), ratingForm);
                else if(reviewForm != null)
                    mDynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.RATING_FORM, getBaseActivity(), reviewForm);

                if(!mDynamicForm.validate())
                    return;
            } else {
                triggerRatingForm();
            }
            if (isShowingRatingForm) {
                if (getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_REQUIRED_LOGIN, true) && JumiaApplication.CUSTOMER == null) {
                    showLoginFragment();
                } else {
                    executeSendReview(ratingForm.getAction(), mDynamicForm);
                }
            } else {
                if (getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_REQUIRED_LOGIN, true) && JumiaApplication.CUSTOMER == null) {
                    showLoginFragment();
                } else {
                    executeSendReview(reviewForm.getAction(), mDynamicForm);
                }
            }
        }
    }

    /**
     * store product SKU in bundle and switch to login fragment
     */
    private void showLoginFragment() {
        Bundle bundle = getArguments();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.WRITE_REVIEW);
        bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, mCompleteProductSku);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * function responsible for sending the rating/review to API
     */
    private void executeSendReview(String action, DynamicForm form) {
        form.getItemByKey(RestConstants.SKU).getEntry().setValue(completeProduct.getSku());
        ContentValues values = form.save();
        getRatingFormValues(values, form);
        triggerContentEventProgress(new RatingReviewProductHelper(), RatingReviewProductHelper.createBundle(action, values), this);
    }


    /**
     *
     * Function that retrieves info from rating bars form
     */
    private void getRatingFormValues(ContentValues values, DynamicForm form){

        String formName = form.getItemByKey(RestConstants.RATINGS).getName();

        Map<String, String> ratingMap = form.getItemByKey(RestConstants.RATINGS).getEntry().getDateSetRating();
        View  ratingFormContainer = form.getItemByKey(RestConstants.RATINGS).getEditControl();

        for (int i = 1; i < ratingMap.size()+1; i++) {
           int rate =  (int)((RatingBar)ratingFormContainer.findViewById(i).findViewById(R.id.option_stars)).getRating();
           String id =  ratingFormContainer.findViewById(i).findViewById(R.id.option_stars).getTag(R.id.rating_bar_id).toString();

           String key =formName+"["+id+"]";
           values.put(key, rate);
            // Remove entry that's used only for locally saving the form value
            values.remove(id);
        }
    }

    /**
     * function responsible for creating a map between the rating option name and it's value
     * needed for tracking only
     */
    private HashMap<String, Long> getRatingsMapValues(DynamicForm form){
        HashMap<String, Long> values = new HashMap<>();

            if(form != null){
                Map<String, String> ratingMap = form.getItemByKey(RestConstants.RATINGS).getEntry().getDateSetRating();
                View  ratingFormContainer = form.getItemByKey(RestConstants.RATINGS).getEditControl();

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

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

        Print.i(TAG, "ON SUCCESS EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "onSuccessEvent eventType : " + eventType);
        switch (eventType) {
            case REVIEW_RATING_PRODUCT_EVENT:

                Print.d(TAG, "review product completed: success");
                // Clean options after success
                Bundle params = new Bundle();
                params.putParcelable(TrackerDelegator.PRODUCT_KEY, completeProduct);

                //only needed for tracking purpose
                params.putSerializable(TrackerDelegator.RATINGS_KEY, getRatingsMapValues(mDynamicForm));

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
                break;

            case GET_FORM_RATING_EVENT:
                Print.i(TAG, "GET_FORM_RATING_EVENT");
                ratingForm = (Form) baseResponse.getContentData();
                setRatingLayout(ratingForm);
                if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)){
                    triggerReviewForm();
                } else {
                    showFragmentContentContainer();
                }

                break;

            case GET_FORM_REVIEW_EVENT:
                Print.i(TAG, "GET_FORM_REVIEW_EVENT");
                reviewForm = (Form) baseResponse.getContentData();
                if(ratingForm == null)
                    setRatingLayout(reviewForm);
                showFragmentContentContainer();
                break;

            case GET_PRODUCT_DETAIL:
                Print.d(TAG, "GOT GET_PRODUCT_EVENT");
                if (((ProductComplete) baseResponse.getMetadata().getData()).getName() == null) {
                    getActivity().onBackPressed();
                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.product_could_not_retrieved));
                } else {
                    completeProduct = (ProductComplete) baseResponse.getContentData();
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
                }break;

            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

        Print.d(TAG, "ON ERROR EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide progress
        hideActivityProgress();

        isExecutingSendReview = false;
        // Generic errors
        if(super.handleErrorEvent(baseResponse)) return;

        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "onErrorEvent: type = " + eventType + " code= "+errorCode);

        switch (eventType) {
            case GET_FORM_RATING_EVENT:
                if(getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)){
                    triggerReviewForm();
                } else {
                    showRetryLayout();
                }
                break;
            case GET_FORM_REVIEW_EVENT:
                showRetryLayout();
                break;
            case REVIEW_RATING_PRODUCT_EVENT:
                showFormValidateMessages(mDynamicForm, baseResponse, eventType);
                hideActivityProgress();
                isExecutingSendReview = false;
                break;
            case GET_PRODUCT_DETAIL:
                if (!ErrorCode.isNetworkError(errorCode)) {
                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.product_could_not_retrieved));
                    showFragmentContentContainer();
                    try {
                        getBaseActivity().onBackPressed();
                    } catch (IllegalStateException e) {
                        getBaseActivity().popBackStackUntilTag(FragmentType.HOME.toString());
                    }

                }break;
            default:
                break;
        }

    }
}