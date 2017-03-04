package com.mobile.view.newfragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.mobile.adapters.AddressAdapter;
import com.mobile.adapters.PaymentMethodAdapter;
import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.checkout.GetStepFinishHelper;
import com.mobile.helpers.checkout.GetStepPaymentHelper;
import com.mobile.helpers.checkout.SetStepFinishHelper;
import com.mobile.helpers.checkout.SetStepPaymentHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.forms.PaymentInfo;
import com.mobile.newFramework.forms.PaymentMethodForm;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.CheckoutFinish;
import com.mobile.newFramework.objects.checkout.MultiStepPayment;
import com.mobile.newFramework.objects.product.RichRelevance;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;
import com.mobile.view.fragments.BaseFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static android.R.attr.data;
import static android.R.attr.label;
import static android.R.attr.value;

/**
 * 
 * @author sergiopereira
 * 
 */
public class NewCheckoutPaymentMethodsFragment extends NewBaseFragment implements IResponseCallback {

    private static final String TAG = NewCheckoutPaymentMethodsFragment.class.getSimpleName();

    private EditText mVoucherView;

    private ViewGroup mPaymentContainer;

    private DynamicForm mDynamicForm;

    private TextView couponButton;

    private String mVoucherCode;

    private boolean removeVoucher = false;
    private View mTotalContainer;

    private Bundle mSavedState;

    private String paymentName = "";

    private View mCheckoutTotalBar;

    private View mCheckoutButtonNext;

    private RecyclerView mScrollView;

    private View mVoucherContainer;
    private String PaymentAction;
    private String PaymentFieldName;
    private CheckoutFinish mCheckoutFinish;
    private PurchaseEntity mOrderFinish;

    private boolean isShowingNoPaymentNecessary;

    /**
     * Empty constructor
     */
    public NewCheckoutPaymentMethodsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.new_checkout_payment,
                R.string.checkout_label,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_PAYMENT);
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
        // Validate the saved values 
        if(savedInstanceState != null){
            mSavedState = savedInstanceState;
            mVoucherCode = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
        }
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_PAYMENT);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get containers
        mPaymentContainer = (ViewGroup) view.findViewById(R.id.checkout_payment_methods_container);
        mScrollView = (RecyclerView) view.findViewById(R.id.payment_scroll);
        LinearLayoutManager llmanager = new LinearLayoutManager(getActivity());
        mScrollView.setLayoutManager(llmanager);
        mTotalContainer = view.findViewById(R.id.total_container);
        // Buttons
        view.findViewById(R.id.payment_continue).setOnClickListener(this);
        super.setCheckoutStep(view, 3);

        TextView step1 = (TextView)view.findViewById(R.id.step1);
        step1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckoutCircleClick(3,1);
            }
        });

        TextView step2 = (TextView)view.findViewById(R.id.step2);
        step2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckoutCircleClick(3, 2);
            }
        });

/*  DROID-65
        // Voucher
        mVoucherContainer = view.findViewById(R.id.voucher_container);
        // Checkout total view
        mCheckoutTotalBar = view.findViewById(R.id.checkout_total_bar);
        mCheckoutButtonNext = view.findViewById(R.id.checkout_button_enter);*/
        // Get and show addresses
        triggerGetPaymentMethods();
    }

    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
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
        TrackerDelegator.trackPage(TrackingPage.PAYMENT_SCREEN, getLoadTime(), true);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
/* DROID-65
        // Save the form state
        if(mDynamicForm != null) {
            mDynamicForm.saveFormState(outState);
        }
        // Save voucher
        if(mVoucherView != null) {
            outState.putString(ConstantsIntentExtra.ARG_1, mVoucherView.getText().toString());
        }
*/
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /**
     * ############# CLICK LISTENER #############
     */

    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Submit
        if (id == R.id.payment_continue) {
/* DROID-65
            if (!TextUtils.isEmpty(mVoucherView.getText()) && !TextUtils.equals(couponButton.getText(), getString(R.string.remove_label))) {
                validateCoupon();
            } else {
                onClickSubmitPaymentButton();
            }
*/
            onClickSubmitPaymentButton();
            getBaseActivity().hideKeyboard();
        }
        // Case Unknown
        else {
            Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    // Process the click on retry button.
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        if (JumiaApplication.CUSTOMER != null) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Load the dynamic form
     */
/* DROID-65
    private void loadForm(Form form) {
        Print.i(TAG, "LOAD FORM");
        mDynamicForm = FormFactory.create(FormConstants.PAYMENT_DETAILS_FORM, getActivity(), form);
        mPaymentContainer.removeAllViews();
        mPaymentContainer.addView(mDynamicForm.getContainer());
        mDynamicForm.loadSaveFormState(mSavedState);
        mPaymentContainer.refreshDrawableState();
        prepareCouponView();
        validatePaymentIsAvailableOrIsNecessary();
        showFragmentContentContainer();
    }
*/


    /**
     * Set the all views associated to order.
     */
/* DROID-65
    private void setOrderInfo(PurchaseEntity purchaseEntity) {
        // Set the checkout total bar
        CheckoutStepManager.setTotalBar(mCheckoutTotalBar, purchaseEntity);
        // Update voucher
        updateVoucher(purchaseEntity);
        // Show checkout summary
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_PAYMENT, purchaseEntity);
    }
*/

    /*
     * Disable the next button case No payment options available
     */
    @SuppressWarnings("ConstantConditions")
    private void validatePaymentIsAvailableOrIsNecessary() {
        try {
            // Get input type to validate the form
            FormInputType type = (FormInputType) getView().findViewById(R.id.text_information).getTag();
            // Case no payment necessary
            if(type == FormInputType.infoMessage) {
                isShowingNoPaymentNecessary = true;
            }
            // Case no payment available
            else if(type == FormInputType.errorMessage) {
                mCheckoutButtonNext.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            //...
        }
    }

/* DROID-65   private void prepareCouponView() {
        mVoucherView = (EditText) mVoucherContainer.findViewById(R.id.voucher_name);
        if (!TextUtils.isEmpty(mVoucherCode)) {
            mVoucherView.setText(mVoucherCode);
        }
        UIUtils.scrollToViewByClick(mScrollView, mVoucherView);
        couponButton = (TextView) mVoucherContainer.findViewById(R.id.voucher_btn);
        if (removeVoucher) {
            couponButton.setText(getString(R.string.remove_label));
        }
        couponButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCoupon();
            }
        });
    }

    private void validateCoupon() {
        mVoucherCode = mVoucherView.getText().toString();
        getBaseActivity().hideKeyboard();
        if (!TextUtils.isEmpty(mVoucherCode)) {
            if (TextUtils.equals(getString(R.string.use_label), couponButton.getText())) {
                triggerSubmitVoucher(mVoucherCode);
            } else {
                triggerRemoveVoucher();
            }
        } else {
            showWarningErrorMessage(getString(R.string.voucher_error_message));
        }
    }*/
    
    private void onClickSubmitPaymentButton() {
        Print.i(TAG, "ON CLICK: Submit Payment Method");
        //payment_method[payment_method]
        //PaymentFieldName
        int selectedId = ((PaymentMethodAdapter) mScrollView.getAdapter()).getSelectedId();
        ContentValues values = new ContentValues();
        values.put(PaymentFieldName, selectedId);
        triggerSubmitPaymentMethod(PaymentAction, values);
        /*if(mDynamicForm != null){
            if(mDynamicForm.validate()){
                ContentValues values = mDynamicForm.save();
                paymentName = values.getAsString(RestConstants.NAME);
                triggerSubmitPaymentMethod(mDynamicForm.getForm().getAction(), values);
            } else {
                showWarningErrorMessage(getString(R.string.please_fill_all_data));
            }
        }*/
    }

    /**
     * Fill Coupon field if orderSummary has discountCouponCode
     */
/* DROID-65
    private void updateVoucher(PurchaseEntity orderSummary) {
        if (orderSummary != null) {
            if (orderSummary.hasCouponDiscount()) {
                mVoucherCode = orderSummary.getCouponCode();
                if (!TextUtils.isEmpty(mVoucherCode)) {
                    removeVoucher = true;
                    prepareCouponView();
                    mVoucherView.setText(mVoucherCode);
                    mVoucherView.setFocusable(false);
                } else {
                    mVoucherCode = null;
                }
            } else {
                mVoucherView.setText(TextUtils.isNotEmpty(mVoucherCode) ? mVoucherCode : "" );
                mVoucherView.setFocusable(true);
                mVoucherView.setFocusableInTouchMode(true);
            }
        }
    }
*/

    /**
     * ############# RESPONSE #############
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_MULTI_STEP_PAYMENT:
            Print.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            // Get order summary
            MultiStepPayment responseData = (MultiStepPayment) baseResponse.getContentData();

            LoadMethods(responseData);
            setTotal(responseData.getOrderSummary());
            // Form
            //DROID-65 loadForm(responseData.getForm());
            // Set the checkout total bar
            //DROID-65 setOrderInfo(responseData.getOrderSummary());
            hideActivityProgress();
            break;
        case SET_MULTI_STEP_PAYMENT:
            Print.d(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            triggerContentEvent(new GetStepFinishHelper(), null, this);

            /*NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
            FragmentType nextFragment = nextStepStruct.getFragmentType();
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.CHECKOUT_FINISH;
            // Tracking
            String userId = JumiaApplication.CUSTOMER != null ? JumiaApplication.CUSTOMER.getIdAsString() : "";
            String email = JumiaApplication.INSTANCE.getCustomerUtils().getEmail();
            TrackerDelegator.trackPaymentMethod(userId, email, paymentName);
            // Switch to FINISH
            getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);*/
            break;
            case GET_MULTI_STEP_FINISH:
                mOrderFinish = (PurchaseEntity) baseResponse.getContentData();
                triggerContentEvent(new SetStepFinishHelper(), SetStepFinishHelper.createBundle(getUserAgentAsExtraData()), this);
                break;
            case SET_MULTI_STEP_FINISH:
                mCheckoutFinish = (CheckoutFinish) baseResponse.getContentData();
                // Tracking purchase
                TrackerDelegator.trackPurchase(mCheckoutFinish, JumiaApplication.INSTANCE.getCart());
                // Next step
                switchToSubmittedPayment();
                // Update cart info
                getBaseActivity().updateCartInfo();
                break;



/* DROID-65
        case REMOVE_VOUCHER:
            mVoucherCode = null;
        case ADD_VOUCHER:
            couponButton.setText(getString(eventType == EventType.ADD_VOUCHER ? R.string.remove_label : R.string.use_label));
            removeVoucher = eventType == EventType.ADD_VOUCHER;
            hideActivityProgress();
            setOrderInfo((PurchaseEntity) baseResponse.getContentData());
            // Case voucher removed and is showing no payment necessary
            if(isShowingNoPaymentNecessary) {
                isShowingNoPaymentNecessary = false;
                triggerGetPaymentMethods();
            }
            break;
*/
        default:
            break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
    	// Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return;
        }
        // Get event type and error
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        // Validate event type
        switch (eventType) {
        case GET_MULTI_STEP_PAYMENT:
            Print.i(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            mOrderFinish = (PurchaseEntity) baseResponse.getContentData();
            break;
        case SET_MULTI_STEP_PAYMENT:
            Print.i(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            TrackerDelegator.trackFailedPayment(paymentName, JumiaApplication.INSTANCE.getCart());
            showWarningErrorMessage(baseResponse.getValidateMessage());
            showFragmentContentContainer();
            break;
/* DROID-65
        case ADD_VOUCHER:
        case REMOVE_VOUCHER:
            hideActivityProgress();
            break;
*/
        default:
            break;
        }
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerSubmitPaymentMethod(String endpoint, ContentValues values) {
        triggerContentEvent(new SetStepPaymentHelper(), SetStepPaymentHelper.createBundle(endpoint, values), this);
    }
    
/* DROID-65
    private void triggerSubmitVoucher(String code) {
        triggerContentEventProgress(new AddVoucherHelper(), AddVoucherHelper.createBundle(code), this);
    }
    
    private void triggerRemoveVoucher() {
        triggerContentEventProgress(new RemoveVoucherHelper(), null, this);
    }
*/

    private void triggerGetPaymentMethods(){
        triggerContentEventProgress(new GetStepPaymentHelper(), null, this);
    }

    private void LoadMethods(MultiStepPayment payment)
    {
        LinkedHashMap<String, String> paymentMethods = new LinkedHashMap<>();
        ArrayList<PaymentMethod> methodList= new ArrayList<>();
        HashMap<String, PaymentInfo> paymentInfoMap = null;
        PaymentAction = payment.getForm().getAction();
        PaymentFieldName= payment.getForm().getFields().get(0).getName();
        try
        {
            paymentInfoMap = payment.getForm().getFieldKeyMap().get(RestConstants.PAYMENT_METHOD).getPaymentInfoList();
            paymentMethods = payment.getForm().getFields().get(0).getDataSet();
            HashMap<String, PaymentInfo> infoList = payment.getForm().getFields().get(0).getPaymentInfoList();
            for (Map.Entry<String, String> entry : paymentMethods.entrySet())
            {
                PaymentMethod method = new PaymentMethod();
                method.setMethod(entry, paymentInfoMap);
                methodList.add(method);
            }

            PaymentMethodAdapter adapter = new PaymentMethodAdapter(methodList, -1);
            mScrollView.setAdapter(adapter);
        }
        catch (Exception ex)
        {

        }



    }
    private String getUserAgentAsExtraData() {
        return getResources().getBoolean(R.bool.isTablet) ? "tablet" : "mobile";
    }

    private void triggerCheckoutFinish() {
        Print.i(TAG, "TRIGGER: CHECKOUT FINISH");
        triggerContentEvent(new SetStepFinishHelper(), SetStepFinishHelper.createBundle(getUserAgentAsExtraData()), this);
    }

    private void switchToSubmittedPayment() {
        PaymentMethodForm mPaymentSubmitted = mCheckoutFinish.getPaymentMethodForm();
        // Case external payment
        if (mPaymentSubmitted.isExternalPayment()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantsIntentExtra.DATA, mCheckoutFinish.getPaymentMethodForm());
            if(mCheckoutFinish.getRichRelevance() != null) {
                bundle.putParcelable(RestConstants.RECOMMENDED_PRODUCTS, mCheckoutFinish.getRichRelevance());
            }
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EXTERNAL_PAYMENT, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case other
        else {
            Bundle bundle = new Bundle();
            bundle.putString(RestConstants.ORDER_NUMBER, mCheckoutFinish.getOrderNumber());
            bundle.putString(RestConstants.TRANSACTION_SHIPPING, String.valueOf(mOrderFinish.getShippingValue()));
            bundle.putString(RestConstants.TRANSACTION_TAX, String.valueOf(mOrderFinish.getVatValue()));
            bundle.putString(RestConstants.PAYMENT_METHOD, mOrderFinish.getPaymentMethod());
            bundle.putDouble(RestConstants.ORDER_GRAND_TOTAL, mOrderFinish.getPriceForTracking());

            if(mCheckoutFinish.getRichRelevance() == null && CollectionUtils.isNotEmpty(mCheckoutFinish.getRelatedProducts())) {
                final RichRelevance richRelevance = new RichRelevance();
                richRelevance.setRichRelevanceProducts(mCheckoutFinish.getRelatedProducts());
                mCheckoutFinish.setRichRelevance(richRelevance);
            }

            if(mCheckoutFinish.getRichRelevance() != null){
                bundle.putParcelable(RestConstants.RECOMMENDED_PRODUCTS, mCheckoutFinish.getRichRelevance());
            }

            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }
    private void setTotal(@NonNull PurchaseEntity cart) {
        Print.d(TAG, "SET THE TOTAL VALUE");
        // Get views
        TextView totalValue = (TextView) mTotalContainer.findViewById(R.id.total_value);
        TextView quantityValue = (TextView) mTotalContainer.findViewById(R.id.total_quantity);
        // Set views
        totalValue.setText(CurrencyFormatter.formatCurrency(cart.getTotal()));
        quantityValue.setText(R.string.cart_total_amount);


        mTotalContainer.setVisibility(View.VISIBLE);

    }
}
