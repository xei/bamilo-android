package com.mobile.view.newfragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.adapters.PaymentMethodAdapter;
import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.checkout.GetStepFinishHelper;
import com.mobile.helpers.checkout.GetStepPaymentHelper;
import com.mobile.helpers.checkout.SetStepFinishHelper;
import com.mobile.helpers.checkout.SetStepPaymentHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.forms.FormInputType;
import com.mobile.service.forms.PaymentInfo;
import com.mobile.service.forms.PaymentMethodForm;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.objects.checkout.CheckoutFinish;
import com.mobile.service.objects.checkout.MultiStepPayment;
import com.mobile.service.objects.product.RichRelevance;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.tracking.TrackingEvent;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private boolean pageTracked = false;

    /**
     * Empty constructor
     */
    public NewCheckoutPaymentMethodsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.new_checkout_payment,
                R.string.checkout_payment_method_step,
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

        // Validate the saved values 
        if(savedInstanceState != null){
            mSavedState = savedInstanceState;
            mVoucherCode = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
        }
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_PAYMENT);

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CHECKOUT_PAYMENT_METHOD.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        // Get and show addresses
        getMultistepPayment();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
        getBaseActivity().setActionBarTitle(R.string.checkout_payment_method_step);
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
            setMultistepConfirmation();
            getBaseActivity().hideKeyboard();
        } else {
            // Case Unknown
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
        if (BamiloApplication.CUSTOMER != null) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

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

    /**
     * ############# RESPONSE #############
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        
        EventType eventType = baseResponse.getEventType();

        switch (eventType) {
            case GET_MULTI_STEP_PAYMENT:
                if (!pageTracked) {
//                    TrackerDelegator.trackPage(TrackingPage.CHECKOUT_PAYMENT_METHOD, getLoadTime(), false);

                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CHECKOUT_PAYMENT_METHOD.getName()), getString(R.string.gaScreen),
                            "" ,
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }
                MultiStepPayment responseData = (MultiStepPayment) baseResponse.getContentData();
                bindPaymentMethods(responseData);
                setTotal(responseData.getOrderSummary());
                hideActivityProgress();
            break;

            case SET_MULTI_STEP_PAYMENT:
                triggerContentEventProgress(new GetStepFinishHelper(), null, this);
            break;

            case GET_MULTI_STEP_FINISH:
                mOrderFinish = (PurchaseEntity) baseResponse.getContentData();
                setTotal(mOrderFinish);
                hideActivityProgress();
            break;

            case SET_MULTI_STEP_FINISH:
                mCheckoutFinish = (CheckoutFinish) baseResponse.getContentData();
                TrackerDelegator.trackPurchase(mCheckoutFinish, BamiloApplication.INSTANCE.getCart());
                switchToSubmittedPayment();
                getBaseActivity().updateCartInfo();
            break;

            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }

    	// Generic error
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }

        // Get event type and error
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        // Validate event type
        switch (eventType) {
            case GET_MULTI_STEP_PAYMENT:
                mOrderFinish = (PurchaseEntity) baseResponse.getContentData();
            break;

            case SET_MULTI_STEP_PAYMENT:
                TrackerDelegator.trackFailedPayment(paymentName, BamiloApplication.INSTANCE.getCart());
                showWarningErrorMessage(baseResponse.getValidateMessage());
                showFragmentContentContainer();
            break;

            default:
                break;
        }
    }
    
    /**
     * ############# REQUESTS #############
     */
    private void getMultistepPayment(){
        triggerContentEventProgress(new GetStepPaymentHelper(), null, this);
    }

    private void setMultistepPayment(String endpoint, ContentValues values) {
        triggerContentEventProgress(new SetStepPaymentHelper(), SetStepPaymentHelper.createBundle(endpoint, values), this);
    }

    private void setMultistepConfirmation() {
        triggerContentEvent(new SetStepFinishHelper(), SetStepFinishHelper.createBundle(getUserAgentAsExtraData()), this);
    }

    private void bindPaymentMethods(MultiStepPayment payment) {
        LinkedHashMap<String, String> paymentMethods = new LinkedHashMap<>();
        ArrayList<PaymentMethod> methodList= new ArrayList<>();
        HashMap<String, PaymentInfo> paymentInfoMap = null;
        PaymentAction = payment.getForm().getAction();
        PaymentFieldName= payment.getForm().getFields().get(0).getName();
        try {
            paymentInfoMap = payment.getForm().getFieldKeyMap().get(RestConstants.PAYMENT_METHOD).getPaymentInfoList();
            paymentMethods = payment.getForm().getFields().get(0).getDataSet();
            HashMap<String, PaymentInfo> infoList = payment.getForm().getFields().get(0).getPaymentInfoList();
            for (Map.Entry<String, String> entry : paymentMethods.entrySet()) {
                PaymentMethod method = new PaymentMethod();
                method.setMethod(entry, paymentInfoMap);
                methodList.add(method);
            }

            PaymentMethodAdapter adapter = new PaymentMethodAdapter(methodList, -1);
            adapter.mFragmentBridge = new PaymentMethodAdapter.IPaymentMethodAdapter() {
                @Override
                public void paymentMethodSelected(int selectedId) {
                    //int selectedId = ((PaymentMethodAdapter) mScrollView.getAdapter()).getSelectedId();
                    ContentValues values = new ContentValues();
                    values.put(PaymentFieldName, selectedId);
                    setMultistepPayment(PaymentAction, values);
                }
            };
            mScrollView.setAdapter(adapter);
        } catch (Exception ex) {

        }
    }

    private String getUserAgentAsExtraData() {
        return getResources().getBoolean(R.bool.isTablet) ? "tablet" : "mobile";
    }

    private void switchToSubmittedPayment() {
        PaymentMethodForm mPaymentSubmitted = mCheckoutFinish.getPaymentMethodForm();
        // Case external payment
        if (mPaymentSubmitted.isExternalPayment()) {
            /*Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantsIntentExtra.DATA, mCheckoutFinish.getPaymentMethodForm());
            if(mCheckoutFinish.getRichRelevance() != null) {
                bundle.putParcelable(RestConstants.RECOMMENDED_PRODUCTS, mCheckoutFinish.getRichRelevance());
            }
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EXTERNAL_PAYMENT, bundle, FragmentController.ADD_TO_BACK_STACK);*/
            switchToPaymentPage();
            getBaseActivity().onSwitchFragment(FragmentType.HOME, null, false);
        } else {
            // Case other
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

    private void switchToPaymentPage() {
        String resNum = mCheckoutFinish.getOrderNumber();

        String PARAM_RESOURCE_NUMBER = "ResNum";
        String PARAM_SET_DEVICE = "setDevice";
        String DEVICE_MOBILE = "mobile";
        String uri = Uri.parse(mCheckoutFinish.getPaymentLandingPage())
                .buildUpon()
                .appendQueryParameter(PARAM_RESOURCE_NUMBER, resNum)
                .appendQueryParameter(PARAM_SET_DEVICE, DEVICE_MOBILE)
                .build().toString();


        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(uri)));
        startActivity(browserIntent);
    }

    private void setTotal(@NonNull PurchaseEntity cart) {
        // Get views
        TextView totalValue = (TextView) mTotalContainer.findViewById(R.id.total_value);
        TextView quantityValue = (TextView) mTotalContainer.findViewById(R.id.total_quantity);
        // Set views
        totalValue.setText(CurrencyFormatter.formatCurrency(cart.getTotal()));
        quantityValue.setText(R.string.cart_total_amount);

        mTotalContainer.setVisibility(View.VISIBLE);
    }
}
