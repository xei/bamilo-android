package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mobile.classes.models.BaseScreenModel;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.checkout.GetStepFinishHelper;
import com.mobile.helpers.checkout.GetStepShippingHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;
import com.mobile.view.newfragments.NewBaseFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * @author sergiopereira
 */
public class CheckoutConfirmationFragment extends NewBaseFragment implements View.OnClickListener, IResponseCallback {
    TextView next, address, telephone, user, order_count_title, order_price,
            ship_price, voucher_price, all_price, ship_time, all_voucher, voucher_error, all_price_title;
    NestedScrollView svCheckoutConfirmation;
    TextView tvDeliveryTimeHeader;
    TextView tvDeliveryNotice;
    SwitchCompat voucher_switch;
    TextView tvVoucherValueTitle;
    LinearLayout voucher_layer;
    private EditText mVoucherView;
    private Button couponButton;
    private String mVoucherCode;
    private static final String TAG = CheckoutConfirmationFragment.class.getSimpleName();
    private PurchaseEntity mOrderFinish;
    private List<CardChoutItem> cardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardCheckOutAdapter mAdapter;
    private boolean pageTracked = false;


    /**
     * Empty constructor
     */
    public CheckoutConfirmationFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.checkout_confirmation_fragment,
                R.string.checkout_confirmation_step,
                NO_ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_CONFIRMATION);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Tracking checkout step
        // TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_ADDRESSES);

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CHECKOUT_CONFIRMATION.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getBaseActivity().setActionBarTitle(R.string.checkout_confirmation_step);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    private void triggerGetMultiStepFinish() {
        triggerContentEventProgress(new GetStepFinishHelper(), null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Print.i(TAG, "ON VIEW CREATED");
        super.onViewCreated(view, savedInstanceState);
        super.setCheckoutStep(view, 2);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getBaseActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        triggerGetMultiStepFinish();
        tvVoucherValueTitle = (TextView) view.findViewById(R.id.checkout_order_voucher_price_title);
        next = (TextView) view.findViewById(R.id.checkout_confirmation_btn);
        svCheckoutConfirmation = (NestedScrollView) view.findViewById(R.id.checkout_scrollview);
        svCheckoutConfirmation.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                getBaseActivity().hideKeyboard();
            }
        });
        address = (TextView) view.findViewById(R.id.checkout_address);
        telephone = (TextView) view.findViewById(R.id.checkout_telephone);
        user = (TextView) view.findViewById(R.id.checkout_user_reciver);
        order_count_title = (TextView) view.findViewById(R.id.checkout_order_count_title);
        order_price = (TextView) view.findViewById(R.id.checkout_order_price);
        ship_price = (TextView) view.findViewById(R.id.checkout_order_ship_price);
        voucher_price = (TextView) view.findViewById(R.id.checkout_order_voucher_price);
        all_price = (TextView) view.findViewById(R.id.checkout_order_all_price);
        ship_time = (TextView) view.findViewById(R.id.checkout_order_ship_time);
        all_price_title = (TextView) view.findViewById(R.id.all_price_total_title);
        all_voucher = (TextView) view.findViewById(R.id.checkout_order_all_discount);
        voucher_error = (TextView) view.findViewById(R.id.error_message_mandatory);
        voucher_switch = (SwitchCompat) view.findViewById(R.id.voucher_switch);
        voucher_layer = (LinearLayout) view.findViewById(R.id.voucher_layout);
        mVoucherView = (EditText) view.findViewById(R.id.voucher_codename);
        tvDeliveryNotice = (TextView) view.findViewById(R.id.tvDeliveryNotice);
        tvDeliveryTimeHeader = (TextView) view.findViewById(R.id.textView12);
        couponButton = (Button) view.findViewById(R.id.checkout_button_enter);
        next.setOnClickListener(this);
        couponButton.setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.cheackout_recycler_view);
        mVoucherView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAdapter = new CardCheckOutAdapter(cardList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);
        //prepareCardData();
        voucher_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    voucher_layer.setVisibility(View.VISIBLE);

                } else {
                    voucher_layer.setVisibility(View.GONE);
                    triggerRemoveVoucher();
                }

            }
        });

        TextView step1 = (TextView) view.findViewById(R.id.step1);
        step1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckoutCircleClick(2, 1);
            }
        });

    }

    @Override
    protected void onClickRetryButton(View view) {
        getBaseActivity().onBackPressed();
    }

    private void validateCoupon() {
        mVoucherCode = mVoucherView.getText().toString();
        getBaseActivity().hideKeyboard();
        if (!TextUtils.isEmpty(mVoucherCode)) {
            if (TextUtils.equals(getString(R.string.checkout_submit_voucher), couponButton.getText())) {
                triggerSubmitVoucher(mVoucherCode);
            } else {
                triggerRemoveVoucher();
            }
        } else {
            showWarningErrorMessage(getString(R.string.voucher_error_message));
        }
    }

    private void triggerRemoveVoucher() {
        triggerContentEventProgress(new RemoveVoucherHelper(), null, this);
    }

    private void triggerSubmitVoucher(String code) {
        triggerContentEventProgress(new AddVoucherHelper(), AddVoucherHelper.createBundle(code), this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.checkout_confirmation_btn) {
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_PAYMENT, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        } else if (viewId == R.id.checkout_button_enter) {
            if (!TextUtils.isEmpty(mVoucherView.getText()) && !TextUtils.equals(couponButton.getText(), getString(R.string.remove_label))) {
                hideError();
                validateCoupon();
            } else {
                if (TextUtils.isEmpty(mVoucherView.getText())) {
                    showError();
                }
                validateCoupon();
            }
            getBaseActivity().hideKeyboard();
        }
    }

    private void showError() {
        voucher_error.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        voucher_error.setVisibility(View.GONE);
    }

    private void showMyOrder() {
        // Show Product data
        showOrderdetail();
        // Get cart
        showProducts();
        // Get addresses
        showOrderAddresses();
        // Get shipping method
        showShippingMethod();

        // Show container
        showFragmentContentContainer();
    }

    private void showOrderdetail() {
        all_price.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getTotal()));
        // order_count_title.setText(mOrderFinish.getCartCount()+" "+getContext().getString(R.string.checkout_count_title));
        all_price_title.setText(String.format(new Locale("fa"), getString(R.string.checkout_confirmation_total_price), mOrderFinish.getCartCount()));
        order_price.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getSubTotalUnDiscounted()));
        all_price_title.setTextColor(getResources().getColor(R.color.checkout_order_green));
        all_price.setTextColor(getResources().getColor(R.color.checkout_order_green));
        all_voucher.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getSubTotalUnDiscounted() + mOrderFinish.getShippingValue() - mOrderFinish.getTotal()));
        if (mOrderFinish.hasCouponDiscount()) {
            tvVoucherValueTitle.setVisibility(View.VISIBLE);
            voucher_price.setVisibility(View.VISIBLE);
            voucher_price.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getCouponDiscount()));
        } else {
            tvVoucherValueTitle.setVisibility(View.GONE);
            voucher_price.setVisibility(View.GONE);
        }
    }

    private void showProducts() {
        cardList = new ArrayList<CardChoutItem>();
        for (PurchaseCartItem item : mOrderFinish.getCartItems()) {
            CardChoutItem card;
            if (item.hasDiscount()) {
                card = new CardChoutItem(item.getBrandName(), item.getName(), String.valueOf(item.getSpecialPrice()), String.valueOf(item.getQuantity()), item.getImageUrl());

            } else {
                card = new CardChoutItem(item.getBrandName(), item.getName(), String.valueOf(item.getPrice()), String.valueOf(item.getQuantity()), item.getImageUrl());

            }
            cardList.add(card);
        }
        mAdapter = new CardCheckOutAdapter(cardList);

        recyclerView.setAdapter(mAdapter);
    }

    private void showOrderAddresses() {

        if (mOrderFinish.hasShippingAddress()) {
            address.setText(mOrderFinish.getShippingAddress().getAddress());
            telephone.setText(String.format(new Locale("fa"), getString(R.string.checkout_confirmation_phone), mOrderFinish.getShippingAddress().getPhone()));
            user.setText(String.format(new Locale("fa"), "%s %s", mOrderFinish.getShippingAddress().getFirstName(), mOrderFinish.getShippingAddress().getLastName()));
        } else {

        }
    }


    private void showShippingMethod() {

        if (mOrderFinish.hasFreeShipping() || mOrderFinish.getShippingValue() == 0) {
            ship_price.setText(R.string.price_free);
            ship_price.setTextColor(getResources().getColor(R.color.checkout_order_green));
        } else {
            ship_price.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getShippingValue()));
            ship_price.setTextColor(getResources().getColor(R.color.black));
        }

    }

    private void triggerGetShippingMethods() {
        Print.i(TAG, "TRIGGER: GET SHIPPING METHODS");
        triggerContentEventProgress(new GetStepShippingHelper(), null, this);
    }

    private void setOrderInfo(PurchaseEntity purchaseEntity) {
        // Update voucher
        updateVoucher(purchaseEntity);
        // Show checkout summary
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_PAYMENT, purchaseEntity);
    }

    /**
     * Fill Coupon field if orderSummary has discountCouponCode
     */
    private void updateVoucher(PurchaseEntity orderSummary) {
        if (orderSummary != null) {
            if (orderSummary.hasCouponDiscount()) {
                mVoucherCode = orderSummary.getCouponCode();
                if (!TextUtils.isEmpty(mVoucherCode)) {
                    mVoucherView.setText(mVoucherCode);
                    mVoucherView.setFocusable(false);
                } else {
                    mVoucherCode = null;
                }
            } else {
                mVoucherView.setText(TextUtils.isNotEmpty(mVoucherCode) ? mVoucherCode : "");
                mVoucherView.setFocusable(true);
                mVoucherView.setFocusableInTouchMode(true);
            }
        }
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate the event
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
            case ADD_VOUCHER:
                couponButton.setText(getString(R.string.remove_label));
                hideActivityProgress();
                setOrderInfo((PurchaseEntity) baseResponse.getContentData());
               /* // Case voucher removed and is showing no payment necessary
                if(isShowingNoPaymentNecessary) {
                    isShowingNoPaymentNecessary = false;
                    triggerGetPaymentMethods();
                }*/
                triggerGetMultiStepFinish();

                break;
            case REMOVE_VOUCHER:
                mVoucherCode = null;
                couponButton.setText(getString(R.string.checkout_submit_voucher));
                setOrderInfo((PurchaseEntity) baseResponse.getContentData());
                triggerGetMultiStepFinish();
                break;
            case GET_MULTI_STEP_SHIPPING:
                onSuccessGetShippingMethods(baseResponse);
                hideActivityProgress();
                break;
            case GET_MULTI_STEP_FINISH:
                mOrderFinish = (PurchaseEntity) baseResponse.getContentData();
                if (!pageTracked) {
//                    TrackerDelegator.trackPage(TrackingPage.CHECKOUT_CONFIRMATION, getLoadTime(), false);

                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CHECKOUT_CONFIRMATION.getName()), getString(R.string.gaScreen),
                            "" ,
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }

                if (mOrderFinish == null) {
                    showFragmentErrorRetry();
                } else {
                    showMyOrder();

                    triggerGetShippingMethods();
                    hideActivityProgress();
                }

                if (mOrderFinish.hasCouponDiscount()) {
                    couponButton.setText(getContext().getString(R.string.remove_label));
                    mVoucherView.setText(mOrderFinish.getCouponCode());
                    mVoucherView.setFocusable(false);
                    voucher_switch.setChecked(true);
                } else {
                    couponButton.setText(getContext().getString(R.string.checkout_submit_voucher));
                    mVoucherView.setText("");
                    mVoucherView.setFocusable(true);
                    mVoucherView.setFocusableInTouchMode(true);
                    voucher_switch.setChecked(false);
                }
                break;

            default:
                break;
        }
    }


    public void onSuccessGetShippingMethods(BaseResponse baseResponse) {
        Print.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
        //
        GetStepShippingHelper.ShippingMethodFormStruct shippingMethodsForm = (GetStepShippingHelper.ShippingMethodFormStruct) baseResponse.getContentData();
        // Get order summary
        PurchaseEntity orderSummary = shippingMethodsForm.getOrderSummary();
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_SHIPPING, orderSummary);
//        showDeliveryTime(loadForm(shippingMethodsForm.getFormBuilder()));
        showDeliveryTime(shippingMethodsForm.getEstimatedDeliveryTime());
        if (shippingMethodsForm.getDeliveryNotice() != null) {
            showDeliveryNotice(shippingMethodsForm.getDeliveryNotice());
        }
    }

    private void showDeliveryNotice(String deliveryNotice) {
        tvDeliveryNotice.setVisibility(View.VISIBLE);
        tvDeliveryNotice.setText(deliveryNotice);
    }

    private void showDeliveryTime(String time) {
        ship_time.setText(time);//
    }

    // TODO: 8/14/2017 REMOVE ASAP
    /*private String loadForm(ShippingMethodFormBuilder form) {
        ShippingMethodForm field = new ShippingMethodForm(form.shippingMethodFormBuilderHolder.fields.get(0));
       return field.optionsShippingMethod.get("UniversalShippingMatrix").deliveryTime;
    }*/

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Check if the request is a partial request
        if (baseResponse.getEventType() == EventType.GET_MULTI_STEP_SHIPPING) {
            tvDeliveryTimeHeader.setVisibility(View.GONE);
            ship_time.setVisibility(View.GONE);
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            getBaseActivity().onBackPressed();
            return;
        }
        // Get event type and error
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        // Validate event type
        switch (eventType) {
            case ADD_VOUCHER:
            case REMOVE_VOUCHER:
                hideActivityProgress();
                break;
        }
    }


}
