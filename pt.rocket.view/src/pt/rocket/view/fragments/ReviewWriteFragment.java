package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map.Entry;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.EditText;
import pt.rocket.components.customfontviews.TextView;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.account.GetCustomerHelper;
import pt.rocket.helpers.configs.GetRatingOptionsHelper;
import pt.rocket.helpers.products.GetProductHelper;
import pt.rocket.helpers.products.ReviewProductHelper;
import pt.rocket.helpers.session.GetLoginHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.Toast;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira rating-option--
 * 
 */
public class ReviewWriteFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ReviewWriteFragment.class);

    private static final String NAME = "name";

    private static final String TITLE = "title";

    private static final String COMMENT = "comment";

    private static ReviewWriteFragment writeReviewFragment;

    private CompleteProduct completeProduct;

    private TextView productName;

    private LinearLayout labelsContainer;

    private LinearLayout ratingBarContainer;

    private EditText titleText;

    private EditText nameText;

    private HashMap<String, Double> ratings;

    private EditText reviewText;

    private ProductReviewCommentCreated productReviewCreated;

    private DialogGenericFragment dialog_review_submitted;

    private Customer customerCred;

    private boolean isExecutingSendReview = false;

    private HashMap<String, HashMap<String, String>> ratingOptions;

    private boolean completedReview = false;

    private String mCompleteProductUrl= "";
    
    private LinearLayout mainContainer;
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

        completedReview = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        labelsContainer = (LinearLayout) view.findViewById(R.id.label_container);
        ratingBarContainer = (LinearLayout) view.findViewById(R.id.ratingbar_container);
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
        //Validate is service is available
        if (JumiaApplication.mIsBound) {
            // load complete product url
            if(mCompleteProductUrl.equalsIgnoreCase("") && getArguments() != null && getArguments().containsKey(ConstantsIntentExtra.CONTENT_URL)){
                String contentUrl = getArguments().getString(ConstantsIntentExtra.CONTENT_URL);
                mCompleteProductUrl = contentUrl != null ? contentUrl : "";
            }
            if(completeProduct == null) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
//                triggerAutoLogin();
                setReviewName();
                triggerCustomer();
                triggerRatingOptions();
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

        // only save the fragment if the user didn't finish the review
        if (!completedReview) {
            // Save review before rotation, going to background or leaving to Popularity fragment
            saveReview();
        }
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
    
    /*
     * Set review name by user name if is logged.
     * 
     */
    private void setReviewName(){
        Customer customer = JumiaApplication.CUSTOMER;
        if(customer != null && JumiaApplication.INSTANCE.isLoggedIn()){
            if (nameText == null) {
                nameText = (EditText) getView().findViewById(R.id.name_box);
            }
            if (nameText != null && customer != null && customer.getFirstName() != null) {
                // Set Customer Name only if name field is not yet filled in
                Editable name = nameText.getText();
                if (name == null || TextUtils.isEmpty(name.toString())) {
                    nameText.setText(customer.getFirstName());
                }
            }
        }
    }
    
    /**
     * Set the Products layout using inflate
     */
    private void setLayout() {
        if (completeProduct == null) {
            if(!mCompleteProductUrl.equalsIgnoreCase("")) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, mCallBack);
            } else {
                showFragmentRetry(this);
            }
            
        } else {
            mainContainer.setVisibility(View.VISIBLE);
            if (ratingBarContainer.getChildCount() > 0) return;
    
            if (getActivity() == null) return;
    
            LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int id = 77;
            // Only render ratings if available
            if (ratingOptions != null && !ratingOptions.isEmpty()) {
                int size = ratingOptions.size();
                for (Entry<String, HashMap<String, String>> option : ratingOptions.entrySet()) {
                    
                    View viewRating = mInflater.inflate(R.layout.rating_bar_component, null, false);
                    View viewLabel = mInflater.inflate(R.layout.label_rating_component, null, false);
                    viewRating.setTag(option.getKey());
                    viewRating.setId(id);
                    id++;
                    viewLabel.setTag(option.getKey());
                    
                    // Get the rating label
                    // FIXME : (TEMPORARY) Validate rating label if ins't a number from API
                    boolean toAdd = true;
                    String ratingLabel = option.getKey();
                    if (ratingLabel.equals("1")) ratingLabel = size == 1 ? getString(R.string.string_price) : getString(R.string.string_price);
                    else if (ratingLabel.equals("2")) ratingLabel = getString(R.string.string_appearance);
                    else if (ratingLabel.equals("3")) ratingLabel = getString(R.string.rating_quality);
                    else toAdd = false;
                    
                    if(toAdd){
                        ((TextView) viewLabel).setText(ratingLabel);
                        ratingBarContainer.addView(viewRating);
                        labelsContainer.addView(viewLabel);
                    }
                }
            }
                productName = (TextView) getView().findViewById(R.id.product_detail_name);
                TextView productPriceSpecial = (TextView) getView()
                        .findViewById(R.id.product_price_special);
                TextView productPriceNormal = (TextView) getView().findViewById(R.id.product_price_normal);
    
                titleText = (EditText) getView().findViewById(R.id.title_box);
                nameText = (EditText) getView().findViewById(R.id.name_box);
                reviewText = (EditText) getView().findViewById(R.id.review_box);
    
                ((Button) getView().findViewById(R.id.send_review))
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (checkReview() && !isExecutingSendReview) {
                                    isExecutingSendReview = true;
                                    executeSendReview();
                                }
                            }
                        });
    
                productName.setText(completeProduct.getBrand() + " " + completeProduct.getName());
                displayPriceInformation(productPriceNormal, productPriceSpecial);
    
                // Load the saved values
                ContentValues review = JumiaApplication.getReview();
                if (review != null) {
                    loadReview(review);
                }
        }
        
    }

    private void saveReview() {
        ContentValues values = new ContentValues();

        // Get rating bars
        if (ratingBarContainer != null) {
            int numberRatingBars = ratingBarContainer.getChildCount();
            if (numberRatingBars > 0) {
                for (int i = 0; i < numberRatingBars; i++) {
                    View ratingBar = ratingBarContainer.getChildAt(i);
                    if (ratingBar != null && ratingBar instanceof RatingBar) {
                        Object tag = ratingBar.getTag();
                        if (tag != null && tag instanceof String) {
                            values.put((String) tag, ((RatingBar) ratingBar).getRating());
                        }
                    }
                }
            }
        }

        // Get name
        values.put(NAME, (nameText != null) ? nameText.getText().toString() : "");
        // Get title
        values.put(TITLE, (titleText != null) ? titleText.getText().toString() : "");
        // Get comment
        values.put(COMMENT, (reviewText != null) ? reviewText.getText().toString() : "");

        JumiaApplication.setReview(values);
    }

    private void loadReview(ContentValues review) {
        if (review != null) {
            if (titleText == null) {
                titleText = (EditText) getView().findViewById(R.id.title_box);
            }
            if (nameText == null) {
                nameText = (EditText) getView().findViewById(R.id.name_box);
            }
            if (reviewText == null) {
                reviewText = (EditText) getView().findViewById(R.id.review_box);
            }
            if (ratingBarContainer != null) {
                // Load ratings
                int numberRatingBars = ratingBarContainer.getChildCount();
                if (numberRatingBars > 0) {
                    for (int i = 0; i < numberRatingBars; i++) {
                        View ratingBar = ratingBarContainer.getChildAt(i);
                        if (ratingBar != null && ratingBar instanceof RatingBar) {
                            String tag = ratingBar.getTag().toString();
                            if (review.containsKey(tag)) {
                                Float rating = review.getAsFloat(tag);
                                if (rating != null) {
                                    ((RatingBar) ratingBar).setRating(rating);
                                }
                            }
                        }
                    }
                }
            }

            // Load name
            String name = review.getAsString(NAME);
            if (!TextUtils.isEmpty(name)) {
                nameText.setText(name);
            }
            // Load title
            String title = review.getAsString(TITLE);
            if (!TextUtils.isEmpty(title)) {
                titleText.setText(title);
            }
            // Load comment
            String comment = review.getAsString(COMMENT);
            if (!TextUtils.isEmpty(comment)) {
                reviewText.setText(comment);
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

    private boolean checkReview() {
        ratings = new HashMap<String, Double>();
        boolean result = checkEmpty(getResources().getColor(R.color.red_basic), titleText, nameText, reviewText);
        int numberRatings = labelsContainer.getChildCount();
        if (numberRatings > 0) {
            for (int i = 0; i < numberRatings; i++) {
                if (((RatingBar) ratingBarContainer.getChildAt(i)).getRating() == 0) {
                    ((TextView) labelsContainer.getChildAt(i)).setTextColor(getResources().getColor(R.color.red_basic));
                    result = false;
                } else {
                    ratings.put((String) ratingBarContainer.getChildAt(i).getTag(), (double) ((RatingBar) ratingBarContainer.getChildAt(i)).getRating());
                    ((TextView) labelsContainer.getChildAt(i)).setTextColor(getResources().getColor(R.color.grey_middle));
                }
            }
        } else {
            // Warn user that there are no ratings available.
            /*-String title = getString(R.string.server_error_title);
            String message = "No Ratings available!";
            String buttonText = getString(R.string.ok_label);
            messageDialog = DialogGenericFragment.newInstance(false, true, false, title, message, buttonText, null, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageDialog.dismiss();
                }
            });
            messageDialog.show(getActivity().getSupportFragmentManager(), null);*/

            // Don't allow user to submit data without ratings
            return false;
        }

        return result;
    }

    private static boolean checkEmpty(int errorColorId, EditText... views) {
        boolean result = true;
        for (EditText view : views) {
            if (TextUtils.isEmpty(view.getText())) {
                view.setHintTextColor(errorColorId);
                result = false;
            }
        }
        return result;
    }

    private void executeSendReview() {
        productReviewCreated = new ProductReviewCommentCreated();
        productReviewCreated.setName(nameText.getText().toString());
        productReviewCreated.setTitle(titleText.getText().toString());
        productReviewCreated.setComments(reviewText.getText().toString());
        productReviewCreated.setRating(ratings);
        if (customerCred != null) {
            Log.i("SENDING CUSTOMER ID", " HERE " + customerCred.getId());
            triggerWriteReview(completeProduct.getSku(), customerCred.getId(), productReviewCreated);
        } else {
            Log.i("NOT SENDING CUSTOMER ID", " HERE ");
            triggerWriteReview(completeProduct.getSku(), productReviewCreated);
        }
    }

    private void cleanForm(){
        
        titleText.setText("");
        nameText.setText("");
        reviewText.setText(""); 
        
        int childs = ratingBarContainer.getChildCount();

        for(int i = 0; i<childs ; i++){
            
            View view = ratingBarContainer.getChildAt(i);
            if(view instanceof RatingBar){
                ((RatingBar)view).setRating(0f);
            }
            
        }
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
        case REVIEW_PRODUCT_EVENT:

            Log.d(TAG, "review product completed: success");
            // Clean options after success
            Bundle params = new Bundle();
            params.putParcelable(TrackerDelegator.PRODUCT_KEY, completeProduct);
            params.putParcelable(TrackerDelegator.REVIEW_KEY, productReviewCreated);
            params.putSerializable(TrackerDelegator.RATINGS_KEY, ratings);
            TrackerDelegator.trackItemReview(params);
            dialog_review_submitted = DialogGenericFragment.newInstance(false, true, false,
                    getString(R.string.submit_title),
                    getResources().getString(R.string.submit_text),
                    getResources().getString(R.string.dialog_to_reviews),
                    "",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_review_submitted.dismiss();
                            isExecutingSendReview = false;
                            if (getBaseActivity() != null) {
                                if(BaseActivity.isTabletInLandscape(getBaseActivity())){
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
            completedReview = true;
            ratings.clear();
            JumiaApplication.setReview(null);
            return false;

        case GET_RATING_OPTIONS_EVENT:
            Log.i(TAG, "GET_RATING_OPTIONS_EVENT");
            ratingOptions = (HashMap<String, HashMap<String, String>>) bundle
                    .getSerializable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.INSTANCE.setRatingOptions(ratingOptions);
            showFragmentContentContainer();
            setLayout();
            return true;

            // case GET_CUSTOMER:
        case LOGIN_EVENT:
            Log.i(TAG, "LOGIN_EVENT");
            JumiaApplication.INSTANCE.setLoggedIn(true);
            Customer customer = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            // TrackerDelegator.trackLoginSuccessful(getActivity(), customer, true,
            // getActivity().getString(R.string.mixprop_loginlocationreview), false);
            // Make sure name field is available
            setReviewName();
            return false;
        case GET_CUSTOMER:
            Log.i(TAG, "GET_CUSTOMER");
            customerCred = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.CUSTOMER = customerCred;
            return true;
        case GET_PRODUCT_EVENT:
            Log.d(TAG,"GOT GET_PRODUCT_EVENT");
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return true;
            } else {
                completeProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
//                triggerAutoLogin();
                triggerCustomer();
                triggerRatingOptions();
                // Waiting for the fragment comunication
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
        // Generic errors
        if(getBaseActivity().handleErrorEvent(bundle)) return true;
        
        isExecutingSendReview = false;
        switch (eventType) {
        // case GET_CUSTOMER:
        // List<String> errors = event.errorMessages.get( Errors.JSON_ERROR_TAG);
        // if ( errors.contains( Errors.CODE_CUSTOMER_NOT_LOGGED_ID )) {
        // return true;
        // }
        // return false;
        case LOGIN_EVENT:
            // don't care
            return true;

        case GET_CUSTOMER:
            // don't care
            // customerCred = null;
            // Log.i("DIDNT GET CUSTOMER"," HERE ");
            return true;
        case GET_RATING_OPTIONS_EVENT:
            dialog = DialogGenericFragment.createServerErrorDialog(getBaseActivity(),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            triggerRatingOptions();
                            dialog.dismiss();
                        }
                    }, false);
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
            return false;
        case REVIEW_PRODUCT_EVENT:
            dialog = DialogGenericFragment.createServerErrorDialog(getBaseActivity(),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checkReview() && !isExecutingSendReview) {
                                isExecutingSendReview = true;
                                executeSendReview();
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

    /**
     * TRIGGERS
     *  
     * @author sergiopereira
     * @deprecated
     */
    private void triggerAutoLogin() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        triggerContentEvent(new GetLoginHelper(), bundle, mCallBack);
        
    }

    private void triggerCustomer() {
        triggerContentEvent(new GetCustomerHelper(), null, mCallBack);
    }

    private void triggerRatingOptions() {
        triggerContentEvent(new GetRatingOptionsHelper(), null, mCallBack);
    }

    private void triggerWriteReview(String sku, int id,
            ProductReviewCommentCreated productReviewCreated2) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ReviewProductHelper.COMMENT_CREATED, productReviewCreated2);
        bundle.putString(ReviewProductHelper.PRODUCT_SKU, sku);
        bundle.putInt(ReviewProductHelper.CUSTOMER_ID, id);
        triggerContentEventProgress(new ReviewProductHelper(), bundle, mCallBack);
    }

    private void triggerWriteReview(String sku, ProductReviewCommentCreated productReviewCreated2) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ReviewProductHelper.COMMENT_CREATED, productReviewCreated2);
        bundle.putString(ReviewProductHelper.PRODUCT_SKU, sku);
        triggerContentEventProgress(new ReviewProductHelper(), bundle, mCallBack);
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

        }
    }
    

}
