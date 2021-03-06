package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

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

import com.bamilo.android.appmodule.bamiloapp.adapters.PaymentMethodAdapter;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.GetStepFinishHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.GetStepPaymentHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.SetStepFinishHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.SetStepPaymentHelper;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.modernbamilo.util.extension.StringExtKt;
import com.bamilo.android.framework.components.customfontviews.EditText;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.framework.service.forms.FormInputType;
import com.bamilo.android.framework.service.forms.PaymentInfo;
import com.bamilo.android.framework.service.forms.PaymentMethodForm;
import com.bamilo.android.framework.service.objects.checkout.CheckoutFinish;
import com.bamilo.android.framework.service.objects.checkout.MultiStepPayment;
import com.bamilo.android.framework.service.objects.checkout.PackagePurchaseEntity;
import com.bamilo.android.framework.service.objects.product.RichRelevance;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.R;

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
    private PackagePurchaseEntity mOrderFinish;

    private boolean isShowingNoPaymentNecessary;
    private boolean pageTracked = false;
    private PaymentMethodAdapter paymentMethodAdapter;

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
    }
    
    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
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
            if (paymentMethodAdapter.getSelectedId() == -1) {
                showWarningErrorMessage(getString(R.string.please_select_a_payment_method));
            } else {
                setMultistepConfirmation();
                getBaseActivity().hideKeyboard();
            }
        } else {
            // Case Unknown
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    // Process the click on retry button.
    @Override
    protected void onClickRetryButton(View view) {
        getBaseActivity().onBackPressed();
        /*super.onClickRetryButton(view);

        Bundle bundle = new Bundle();
        if (BamiloApplication.CUSTOMER != null) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }*/
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
                showFragmentContentContainer();
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
                setTotal(responseData.getOrderSummary().getTotal());
                hideActivityProgress();
            break;

            case SET_MULTI_STEP_PAYMENT:
                triggerContentEventProgress(new GetStepFinishHelper(), null, this);
            break;

            case GET_MULTI_STEP_FINISH:
                mOrderFinish = (PackagePurchaseEntity) baseResponse.getContentData();
                setTotal(mOrderFinish.getTotal());
                hideActivityProgress();
            break;

            case SET_MULTI_STEP_FINISH:
                mCheckoutFinish = (CheckoutFinish) baseResponse.getContentData();
//                TrackerDelegator.trackPurchase(mCheckoutFinish, BamiloApplication.INSTANCE.getCart());
                switchToSubmittedPayment();
                getBaseActivity().updateCartInfo();
            break;

            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        if (baseResponse.getEventType() == EventType.SET_MULTI_STEP_PAYMENT) {
            paymentMethodAdapter.clearSelection();
            paymentMethodAdapter.notifyDataSetChanged();
        }

    	// Generic error
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }

        // Get event type and error
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        // Validate event type
        switch (eventType) {
            case SET_MULTI_STEP_PAYMENT:
                paymentMethodAdapter.notifyDataSetChanged();
//                TrackerDelegator.trackFailedPayment(paymentName, BamiloApplication.INSTANCE.getCart());
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
        showGhostFragmentContentContainer();
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

            paymentMethodAdapter = new PaymentMethodAdapter(methodList, -1);
            paymentMethodAdapter.mFragmentBridge = selectedId -> {
                //int selectedId = ((PaymentMethodAdapter) mScrollView.getAdapter()).getSelectedId();
                ContentValues values = new ContentValues();
                values.put(PaymentFieldName, selectedId);
                setMultistepPayment(PaymentAction, values);
            };
            mScrollView.setAdapter(paymentMethodAdapter);
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
            bundle.putDouble(RestConstants.ORDER_GRAND_TOTAL, mOrderFinish.getTotalConverted());

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

    private void setTotal(double total) {
        // Get views
        TextView totalValue = (TextView) mTotalContainer.findViewById(R.id.total_value);
        TextView quantityValue = (TextView) mTotalContainer.findViewById(R.id.total_quantity);
        // Set views
        totalValue.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(total)));
        quantityValue.setText(R.string.cart_total_amount);

        mTotalContainer.setVisibility(View.VISIBLE);
    }
}
