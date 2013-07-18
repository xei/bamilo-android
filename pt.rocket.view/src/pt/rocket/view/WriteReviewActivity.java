package pt.rocket.view;

import java.util.EnumSet;
import java.util.List;

import pt.rocket.framework.ErrorCode;
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
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

public class WriteReviewActivity extends MyActivity {
    private final static String TAG = LogTagHelper.create(WriteReviewActivity.class);

    private TextView productName;
    private TextView productResultPrice;
    private TextView productNormalPrice;
    private CompleteProduct completeProduct;
    private EditText titleText;
    private EditText reviewText;
    
    private Dialog dialog_review_submitted;

    private TextView userRatingText;
    private RatingBar userRating;

    private ProductReviewCommentCreated productReviewCreated;

    private TextView userNameText;

    private TextView userEmailText;

    public WriteReviewActivity() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SEARCH),
                EnumSet.of(EventType.LOGIN_EVENT,
                        EventType.GET_RATING_OPTIONS_EVENT),
                EnumSet.of(EventType.REVIEW_PRODUCT_EVENT),
                R.string.writereview_page_title, R.layout.writereview);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        completeProduct = ServiceManager.SERVICES.get(ProductService.class).getCurrentProduct();
        setAppContentLayout();
        EventManager.getSingleton().triggerRequestEvent(LogInEvent.TRY_AUTO_LOGIN);
        triggerContentEvent(EventType.GET_RATING_OPTIONS_EVENT);
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsGoogle.get().trackPage(R.string.gproductreviewscreate);
    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {
        productName = (TextView) findViewById(R.id.product_name);
        productResultPrice = (TextView) findViewById(R.id.product_price_result);
        productNormalPrice = (TextView) findViewById(R.id.product_price_normal);

        userNameText = (TextView) findViewById(R.id.name_box);
        userEmailText = (TextView) findViewById(R.id.email_box);
        userRatingText = (TextView) findViewById(R.id.quality_rating_text);
        userRating = (RatingBar) findViewById(R.id.user_rating);
        titleText = (EditText) findViewById(R.id.title_box);
        reviewText = (EditText) findViewById(R.id.review_box);

        ((Button) findViewById(R.id.send_review)).setOnClickListener(new OnClickListener() {

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
        boolean result = checkEmpty(getResources().getColor(R.color.red_basic), userNameText,
                userEmailText, titleText, reviewText);

        if (userRating.getRating() == 0) {
            userRatingText.setTextColor(getResources().getColor(R.color.red_basic));
            result = false;
        } else {
            userRatingText.setTextColor(getResources().getColor(R.color.grey_middle));
        }

        return result;
    }
    
    private static boolean checkEmpty(int errorColorId, TextView... views) {
        boolean result = true;
        for(TextView view: views) {
            if (TextUtils.isEmpty(view.getText())) {
                view.setHintTextColor(errorColorId);
                result = false;
            }
        }
        return result;
    }

    private void executeSendReview() {

        productReviewCreated = new ProductReviewCommentCreated();
        productReviewCreated.setName(userNameText.getText().toString());
        productReviewCreated.setEmail(userEmailText.getText().toString());
        productReviewCreated.setTitle(titleText.getText().toString());
        productReviewCreated.setComments(reviewText.getText().toString());
        productReviewCreated.setRating(userRating.getRating());
        triggerContentEvent(new ReviewProductEvent(completeProduct.getSku(), productReviewCreated));

    }
    
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        switch (event.getType()) {
//        case GET_CUSTOMER:
//            List<String> errors = event.errorMessages.get( Errors.JSON_ERROR_TAG);
//            if ( errors.contains( Errors.CODE_CUSTOMER_NOT_LOGGED_ID )) {
//                return true;
//            }
//            return false;
        case LOGIN_EVENT:
            // don't care
            return true;
        default:
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.getType()) {
        case REVIEW_PRODUCT_EVENT:
            Log.d(TAG, "review product completed: success = " + event.getSuccess());
            dialog_review_submitted = new DialogGeneric(this, false, true, false,
                    getString(R.string.submit_title), getResources().getString(
                            R.string.submit_text), getResources().getString(
                            R.string.dialog_to_reviews), "", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog_review_submitted.dismiss();
                            finish();
                            overridePendingTransition(R.anim.slide_in_left,
                                    R.anim.slide_out_right);
                        }
                    });

            dialog_review_submitted.show();
            return false;
        case GET_RATING_OPTIONS_EVENT:
            // TODO show rating options dependent on server answer
            return true;
//        case GET_CUSTOMER:
        case LOGIN_EVENT:
            Customer customer = (Customer) event.result;
            TrackerDelegator.trackLoginSuccessful(getApplicationContext(), customer, true);
            userNameText.setText(customer.getFirstName());
            userEmailText.setText(customer.getEmail());
            return false;
        default:
            return false;
        }
    }

}
