package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.LogInEvent;
import pt.rocket.framework.event.events.ReviewProductEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
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

    private TextView userRatingText;

    private TextView appearenceRatingText;

    private TextView priceText;

    private EditText titleText;
    
    private EditText nameText;

    private RatingBar qualityRating;

    private RatingBar appearenceRating;

    private RatingBar priceRating;

    private EditText reviewText;

    private ProductReviewCommentCreated productReviewCreated;

    private DialogGenericFragment dialog_review_submitted;

    private Customer customerCred;

    private String userName="user";
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
                EventType.GET_CUSTOMER), EnumSet.of(EventType.REVIEW_PRODUCT_EVENT));
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
        completeProduct = ServiceManager.SERVICES.get(ProductService.class).getCurrentProduct();
        EventManager.getSingleton().triggerRequestEvent(LogInEvent.TRY_AUTO_LOGIN);
        triggerContentEvent(EventType.GET_RATING_OPTIONS_EVENT);
        triggerContentEvent(EventType.GET_CUSTOMER);
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
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {
        productName = (TextView) getView().findViewById(R.id.product_name);
        productResultPrice = (TextView) getView().findViewById(R.id.product_price_result);
        productNormalPrice = (TextView) getView().findViewById(R.id.product_price_normal);

        userRatingText = (TextView) getView().findViewById(R.id.quality_rating_text);
        qualityRating = (RatingBar) getView().findViewById(R.id.quality_rating);

        appearenceRatingText = (TextView) getView().findViewById(R.id.appearence_rating_text);
        appearenceRating = (RatingBar) getView().findViewById(R.id.appearence_rating);

        priceText = (TextView) getView().findViewById(R.id.price_rating_text);
        priceRating = (RatingBar) getView().findViewById(R.id.price_rating);

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

        productName.setText(completeProduct.getName());
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
        boolean result = checkEmpty(getResources().getColor(R.color.red_basic), titleText,nameText, reviewText);
        if (qualityRating.getRating() == 0) {
            userRatingText.setTextColor(getResources().getColor(R.color.red_basic));
            result = false;
        } else {
            userRatingText.setTextColor(getResources().getColor(R.color.grey_middle));
        }
        if (appearenceRating.getRating() == 0) {
            appearenceRatingText.setTextColor(getResources().getColor(R.color.red_basic));
            result = false;
        } else {
            appearenceRatingText.setTextColor(getResources().getColor(R.color.grey_middle));
        }
        if (priceRating.getRating() == 0) {
            priceText.setTextColor(getResources().getColor(R.color.red_basic));
            result = false;
        } else {
            priceText.setTextColor(getResources().getColor(R.color.grey_middle));
        }
        return result;
    }

    private static boolean checkEmpty(int errorColorId, TextView... views) {
        boolean result = true;
        for (TextView view : views) {
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
        productReviewCreated.setRating(qualityRating.getRating());
        productReviewCreated.setAppearenceRating(appearenceRating.getRating());
        Log.i("RATING TO SEND"," = Q "+qualityRating.getRating()+" A "+appearenceRating.getRating()+" P "+priceRating.getRating());
        productReviewCreated.setPriceRating(priceRating.getRating());
        if (customerCred != null) {
            Log.i("SENDING CUSTOMER ID", " HERE " + customerCred.getId());
            triggerContentEvent(new ReviewProductEvent(completeProduct.getSku(),
                    customerCred.getId(), productReviewCreated));
        } else {
            Log.i("NOT SENDING CUSTOMER ID", " HERE ");
            triggerContentEvent(new ReviewProductEvent(completeProduct.getSku(),
                    productReviewCreated));
        }
        
        TrackerDelegator.trackItemReview(getActivity().getApplicationContext(), completeProduct, productReviewCreated, priceRating.getRating(), appearenceRating.getRating(), qualityRating.getRating());

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.view.fragments.MyFragment#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent
     * )
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.getType()) {
        case REVIEW_PRODUCT_EVENT:
            Log.d(TAG, "review product completed: success = " + event.getSuccess());
            dialog_review_submitted = DialogGenericFragment.newInstance(false, true, false,
                    getString(R.string.submit_title), getResources().getString(
                            R.string.submit_text), getResources().getString(
                            R.string.dialog_to_reviews), "", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog_review_submitted.dismiss();
                            getActivity().finish();
                            getActivity().overridePendingTransition(R.anim.slide_in_left,
                                    R.anim.slide_out_right);
                        }
                    });

            // Fixed back bug
            dialog_review_submitted.setCancelable(false);

            dialog_review_submitted.show(getActivity().getSupportFragmentManager(), null);
            return false;
        case GET_RATING_OPTIONS_EVENT:
            // TODO show rating options dependent on server answer
            return true;
            // case GET_CUSTOMER:
        case LOGIN_EVENT:
            Customer customer = (Customer) event.result;
            TrackerDelegator.trackLoginSuccessful(getActivity(), customer, true, getActivity().getString(R.string.mixprop_loginlocationreview));
            nameText.setText(customer.getFirstName());
            return false;
        case GET_CUSTOMER:
            Log.i("GOT CUSTOMER", "HERE ");
            customerCred = (Customer) event.result;

            return true;

        default:
            return false;
        }
    }

    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        switch (event.getType()) {
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
            customerCred = null;
            Log.i("DIDNT GET CUSTOMER"," HERE ");
            return false;

        default:
        }

        return false;
    }

}
