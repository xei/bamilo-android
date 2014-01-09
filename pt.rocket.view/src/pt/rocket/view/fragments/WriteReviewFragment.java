package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map.Entry;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetCategoriesHelper;
import pt.rocket.helpers.GetCustomerHelper;
import pt.rocket.helpers.GetLoginFormHelper;
import pt.rocket.helpers.GetLoginHelper;
import pt.rocket.helpers.GetRatingsHelper;
import pt.rocket.helpers.GetWriteReviewHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.MainFragmentActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import org.holoeverywhere.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import org.holoeverywhere.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira rating-option--
 * 
 */
public class WriteReviewFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(WriteReviewFragment.class);

    private static WriteReviewFragment writeReviewFragment;

    private CompleteProduct completeProduct;

    private TextView productName;

    private TextView productResultPrice;

    private TextView productNormalPrice;

    private LinearLayout labelsContainer;
    
    private LinearLayout ratingBarContainer;

    private EditText titleText;
    
    private EditText nameText;

    private HashMap<String, Double> ratings;
    
    private EditText reviewText;

    private ProductReviewCommentCreated productReviewCreated;

    private DialogGenericFragment dialog_review_submitted;

    private Customer customerCred;

    private String userName="user";
    
    private HashMap<String, HashMap<String, String>> ratingOptions;
    /**
     * Get instance
     * 
     * @return
     */
    public static WriteReviewFragment getInstance() {
        if (writeReviewFragment == null)
            writeReviewFragment = new WriteReviewFragment();
        return writeReviewFragment;
    }

    /**
     * Empty constructor
     */
    public WriteReviewFragment() {
        super(EnumSet.of(EventType.LOGIN_EVENT, EventType.GET_RATING_OPTIONS_EVENT,
                EventType.GET_CUSTOMER), EnumSet.of(EventType.REVIEW_PRODUCT_EVENT), EnumSet.noneOf(MyMenuItem.class), NavigationAction.Products,  R.string.writereview_page_title);
//        this.setRetainInstance(true);
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
        
        /**
         * TRIGGERS
         * @author sergiopereira
         */
        triggerAutoLogin();
        triggerCustomer();
        //EventManager.getSingleton().triggerRequestEvent(LogInEvent.TRY_AUTO_LOGIN);
        //triggerContentEvent(EventType.GET_CUSTOMER);
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
        View view = inflater.inflate(R.layout.writereview, container, false);
        
        
        labelsContainer = (LinearLayout) view.findViewById(R.id.label_container);
        
        ratingBarContainer = (LinearLayout) view.findViewById(R.id.ratingbar_container);
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
        
        /**
         * TRIGGERS
         * @author sergiopereira
         */
        triggerRatingOptions();
        //triggerContentEvent(EventType.GET_RATING_OPTIONS_EVENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        
        if(getArguments() != null && getArguments().containsKey(PopularityFragment.CAME_FROM_POPULARITY)){
            getView().findViewById(R.id.product_basicinfo_container).setVisibility(View.GONE);
        }
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
     * Set the Products layout using inflate
     */
    private void setLayout() {
        if(completeProduct == null){
            getActivity().finish();
            return;
        }
            
        if(ratingBarContainer.getChildCount()>0)
            return;
        LayoutInflater mInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        for (Entry<String, HashMap<String, String>> option : ratingOptions.entrySet()) {
            View viewRating = mInflater.inflate(R.layout.rating_bar_component, null, false);
            View viewLabel = mInflater.inflate(R.layout.label_rating_component, null, false);
            viewRating.setTag(option.getKey());
            viewLabel.setTag(option.getKey());
            ((TextView) viewLabel).setText(option.getKey());
            ratingBarContainer.addView(viewRating);
            labelsContainer.addView(viewLabel);
        }
        productName = (TextView) getView().findViewById(R.id.product_name);
        productResultPrice = (TextView) getView().findViewById(R.id.product_price_result);
        productNormalPrice = (TextView) getView().findViewById(R.id.product_price_normal);

        titleText = (EditText) getView().findViewById(R.id.title_box);
        nameText = (EditText) getView().findViewById(R.id.name_box);
        reviewText = (EditText) getView().findViewById(R.id.review_box);

        ((Button) getView().findViewById(R.id.send_review))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (checkReview())
                            executeSendReview();
                    }
                });

        productName.setText(completeProduct.getBrand()+" "+completeProduct.getName());
        displayPriceInformation();
    }

    private void displayPriceInformation() {
        String unitPrice = completeProduct.getPrice();
        String specialPrice = completeProduct.getSpecialPrice();
        if (specialPrice == null)
            specialPrice = completeProduct.getMaxSpecialPrice();
        displayPriceInfo(unitPrice, specialPrice);
    }

    private void displayPriceInfo(String unitPrice, String specialPrice) {
        if (specialPrice == null || (unitPrice.equals(specialPrice))) {
            // display only the normal price
            productResultPrice.setText(unitPrice);
            productResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            productNormalPrice.setVisibility(View.GONE);
        } else {
            // display reduced and special price
            productResultPrice.setText(specialPrice);
            productResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            productNormalPrice.setText(unitPrice);
            productNormalPrice.setVisibility(View.VISIBLE);
            productNormalPrice.setPaintFlags(productNormalPrice.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private boolean checkReview() {
        ratings = new HashMap<String, Double>();
        boolean result = checkEmpty(getResources().getColor(R.color.red_basic), titleText,nameText, reviewText);
        for (int i = 0; i < labelsContainer.getChildCount(); i++) {
            if( ((RatingBar) ratingBarContainer.getChildAt(i)).getRating() == 0){
                ((TextView) labelsContainer.getChildAt(i)).setTextColor(getResources().getColor(R.color.red_basic));
                result = false;
            } else {
                ratings.put((String) ratingBarContainer.getChildAt(i).getTag(), (double) ((RatingBar) ratingBarContainer.getChildAt(i)).getRating());
                ((TextView) labelsContainer.getChildAt(i)).setTextColor(getResources().getColor(R.color.grey_middle));
            }
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

            /**
             * TRIGGERS
             * @author sergiopereira
             */
            triggerWriteReview(completeProduct.getSku(), customerCred.getId(), productReviewCreated);
            //triggerContentEvent(new ReviewProductEvent(completeProduct.getSku(), customerCred.getId(), productReviewCreated));
        } else {
            Log.i("NOT SENDING CUSTOMER ID", " HERE ");
            
            /**
             * TRIGGERS
             * @author sergiopereira
             */
            triggerWriteReview(completeProduct.getSku(), productReviewCreated);
            //triggerContentEvent(new ReviewProductEvent(completeProduct.getSku(), productReviewCreated));
        }
        
        

    }

    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        switch (eventType) {
        case REVIEW_PRODUCT_EVENT:
            Log.d(TAG, "review product completed: success");
            TrackerDelegator.trackItemReview(getActivity().getApplicationContext(), completeProduct, productReviewCreated, ratings);
            dialog_review_submitted = DialogGenericFragment.newInstance(false, true, false,
                    getString(R.string.submit_title), getResources().getString(
                            R.string.submit_text), getResources().getString(
                            R.string.dialog_to_reviews), "", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog_review_submitted.dismiss();
                            
                            if(((BaseActivity) getActivity()) != null){
                                ((BaseActivity) getActivity()).onBackPressed();
                            }
                            
                        }
                    });

            // Fixed back bug
            dialog_review_submitted.setCancelable(false);

            dialog_review_submitted.show(getActivity().getSupportFragmentManager(), null);
            return false;
        case GET_RATING_OPTIONS_EVENT:
            ratingOptions = (HashMap<String, HashMap<String, String>>) event.result;
            setLayout();
            return true;
            // case GET_CUSTOMER:
        case LOGIN_EVENT:
            Customer customer = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
//            TrackerDelegator.trackLoginSuccessful(getActivity(), customer, true, getActivity().getString(R.string.mixprop_loginlocationreview), false);
            if(nameText != null && customer != null && customer.getFirstName() != null){
                nameText.setText(customer.getFirstName());
            }
            return false;
        case GET_CUSTOMER:
            Log.i("GOT CUSTOMER", "HERE ");
            customerCred = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);

            return true;

        default:
            return false;
        }
    }

    
    protected boolean onErrorEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
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
//            customerCred = null;
//            Log.i("DIDNT GET CUSTOMER"," HERE ");
            return true;

        default:
        }

        return false;
    }

    @Override
    public void notifyFragment(Bundle bundle) {
        // TODO Auto-generated method stub
        
    }

    
    /**
     * TRIGGERS
     * @author sergiopereira
     */
    private void triggerAutoLogin() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginFormHelper.LOGIN_CONTENT_VALUES, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        triggerContentEvent(new GetLoginHelper(), bundle, mCallBack);
    }
    
    private void triggerCustomer(){
        triggerContentEvent(new GetCustomerHelper(), null, mCallBack);
    }
    
    private void triggerRatingOptions(){
        triggerContentEvent(new GetRatingsHelper(), null, mCallBack);
    }
    
    private void triggerWriteReview(String string, int i, ProductReviewCommentCreated productReviewCreated2){
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetWriteReviewHelper(), bundle, mCallBack);
    }
    
    private void triggerWriteReview(String sku, ProductReviewCommentCreated productReviewCreated2) {
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetWriteReviewHelper(), bundle, mCallBack);
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
}
