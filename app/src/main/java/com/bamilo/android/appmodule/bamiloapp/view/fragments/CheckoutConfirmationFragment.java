package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.NestedScrollView.OnScrollChangeListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.adapters.CheckoutPackagesListAdapter;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.GetStepFinishHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.voucher.AddVoucherHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.voucher.RemoveVoucherHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.view.newfragments.NewBaseFragment;
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton;
import com.bamilo.android.appmodule.modernbamilo.util.extension.StringExtKt;
import com.bamilo.android.framework.components.customfontviews.Button;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.checkout.PackagePurchaseEntity;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import java.util.EnumSet;
import java.util.Locale;

/**
 * @author sergiopereira
 */
public class CheckoutConfirmationFragment extends NewBaseFragment implements View.OnClickListener,
        IResponseCallback {

    BamiloActionButton next;

    TextView address, telephone, user, order_count_title, order_price,
            ship_price, voucher_price, all_price, all_voucher, voucher_error, all_price_title;
    NestedScrollView svCheckoutConfirmation;
    TextView tvDeliveryNotice;
    SwitchCompat voucher_switch;
    TextView tvVoucherValueTitle;
    LinearLayout voucher_layer;
    private EditText mVoucherView;
    private Button couponButton;
    private String mVoucherCode;
    private static final String TAG = CheckoutConfirmationFragment.class.getSimpleName();
    private PackagePurchaseEntity mOrderFinish;
    private RecyclerView recyclerView;
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

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(
                getString(TrackingPage.CHECKOUT_CONFIRMATION.getName()),
                getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getBaseActivity().setActionBarTitle(R.string.checkout_confirmation_step);
    }

    private void triggerGetMultiStepFinish() {
        showGhostFragmentContentContainer();
        triggerContentEventProgress(new GetStepFinishHelper(), null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.setCheckoutStep(view, 2);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getBaseActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        triggerGetMultiStepFinish();
        tvVoucherValueTitle = view.findViewById(R.id.checkout_order_voucher_price_title);
        next = view.findViewById(R.id.checkout_confirmation_btn);
        svCheckoutConfirmation = view.findViewById(R.id.checkout_scrollview);
        svCheckoutConfirmation
                .setOnScrollChangeListener(
                        (OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> getBaseActivity().hideKeyboard());
        address = view.findViewById(R.id.checkout_address);
        telephone = view.findViewById(R.id.checkout_telephone);
        user = view.findViewById(R.id.checkout_user_reciver);
        order_count_title = view.findViewById(R.id.checkout_order_count_title);
        order_price = view.findViewById(R.id.checkout_order_price);
        ship_price = view.findViewById(R.id.checkout_order_ship_price);
        voucher_price = view.findViewById(R.id.checkout_order_voucher_price);
        all_price = view.findViewById(R.id.checkout_order_all_price);
        all_price_title = view.findViewById(R.id.all_price_total_title);
        all_voucher = view.findViewById(R.id.checkout_order_all_discount);
        voucher_error = view.findViewById(R.id.error_message_mandatory);
        voucher_switch = view.findViewById(R.id.voucher_switch);
        voucher_layer = view.findViewById(R.id.voucher_layout);
        mVoucherView = view.findViewById(R.id.voucher_codename);
        tvDeliveryNotice = view.findViewById(R.id.tvDeliveryNotice);
        couponButton = view.findViewById(R.id.checkout_button_enter);
        next.setOnClickListener(this);
        couponButton.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.cheackout_recycler_view);
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        voucher_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                voucher_layer.setVisibility(View.VISIBLE);
            } else {
                voucher_layer.setVisibility(View.GONE);
                triggerRemoveVoucher();
            }
        });

        TextView step1 = view.findViewById(R.id.step1);
        step1.setOnClickListener(view1 -> onCheckoutCircleClick(2, 1));
    }

    @Override
    protected void onClickRetryButton(View view) {
        getBaseActivity().onBackPressed();
    }

    private void validateCoupon() {
        mVoucherCode = mVoucherView.getText().toString();
        getBaseActivity().hideKeyboard();
        if (!TextUtils.isEmpty(mVoucherCode)) {
            if (TextUtils
                    .equals(getString(R.string.checkout_submit_voucher), couponButton.getText())) {
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
        triggerContentEventProgress(new AddVoucherHelper(), AddVoucherHelper.createBundle(code),
                this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.checkout_confirmation_btn) {
            getBaseActivity()
                    .onSwitchFragment(FragmentType.CHECKOUT_PAYMENT, FragmentController.NO_BUNDLE,
                            FragmentController.ADD_TO_BACK_STACK);
        } else if (viewId == R.id.checkout_button_enter) {
            if (!TextUtils.isEmpty(mVoucherView.getText()) && !TextUtils
                    .equals(couponButton.getText(), getString(R.string.remove_label))) {
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
        showFragmentContentContainer();

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

        if (TextUtils.isNotEmpty(mOrderFinish.getDeliveryNotice())) {
            showDeliveryNotice(mOrderFinish.getDeliveryNotice());
        }
    }

    private void showOrderdetail() {
        all_price.setText(StringExtKt.persianizeDigitsInString(
                CurrencyFormatter.formatCurrency(mOrderFinish.getTotal())));
        all_price_title.setText(StringExtKt.persianizeDigitsInString(String.format(new Locale("fa"),
                getString(R.string.checkout_confirmation_total_price),
                mOrderFinish.getCartCount())));
        order_price.setText(StringExtKt.persianizeDigitsInString(
                CurrencyFormatter.formatCurrency(mOrderFinish.getSubTotalUnDiscounted())));
        all_price_title.setTextColor(getResources().getColor(R.color.checkout_order_green));
        all_price.setTextColor(getResources().getColor(R.color.checkout_order_green));
        all_voucher.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(
                mOrderFinish.getSubTotalUnDiscounted() + mOrderFinish.getShippingValue()
                        - mOrderFinish.getTotal())));
        if (mOrderFinish.hasCouponDiscount()) {
            tvVoucherValueTitle.setVisibility(View.VISIBLE);
            voucher_price.setVisibility(View.VISIBLE);
            voucher_price.setText(StringExtKt.persianizeDigitsInString(
                    CurrencyFormatter.formatCurrency(mOrderFinish.getCouponDiscount())));
        } else {
            tvVoucherValueTitle.setVisibility(View.GONE);
            voucher_price.setVisibility(View.GONE);
        }
    }

    private void showProducts() {
        CheckoutPackagesListAdapter adapter = new CheckoutPackagesListAdapter(
                mOrderFinish.getPackages());
        recyclerView.setAdapter(adapter);
    }

    private void showOrderAddresses() {

        if (mOrderFinish.hasShippingAddress()) {
            address.setText(mOrderFinish.getShippingAddress().getAddress());
            telephone.setText(
                    String.format(new Locale("fa"), getString(R.string.checkout_confirmation_phone),
                            mOrderFinish.getShippingAddress().getPhone()));
            user.setText(String.format(new Locale("fa"), "%s %s",
                    mOrderFinish.getShippingAddress().getFirstName(),
                    mOrderFinish.getShippingAddress().getLastName()));
        }
    }

    private void showShippingMethod() {

        if (mOrderFinish.hasFreeShipping() || mOrderFinish.getShippingValue() == 0) {
            ship_price.setText(R.string.price_free);
            ship_price.setTextColor(getResources().getColor(R.color.checkout_order_green));
        } else {
            ship_price.setText(StringExtKt.persianizeDigitsInString(
                    CurrencyFormatter.formatCurrency(mOrderFinish.getShippingValue())));
            ship_price.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void setOrderInfo(PurchaseEntity purchaseEntity) {
        // Update voucher
        updateVoucher(purchaseEntity);
    }

    /**
     * Fill Coupon field if orderSummary has discountCouponCode
     */
    private void updateVoucher(PurchaseEntity orderSummary) {
        if (orderSummary != null) {
            if (orderSummary.hasCouponDiscount()) {
                mVoucherCode = orderSummary.getCouponCode();
                if (!TextUtils.isEmpty(mVoucherCode)) {
                    mVoucherView.setText(StringExtKt.persianizeDigitsInString(mVoucherCode));
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
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate the event
        switch (eventType) {
            case ADD_VOUCHER:
                couponButton.setText(getString(R.string.remove_label));
                hideActivityProgress();
                setOrderInfo((PurchaseEntity) baseResponse.getContentData());
                triggerGetMultiStepFinish();

                break;
            case REMOVE_VOUCHER:
                mVoucherCode = null;
                couponButton.setText(getString(R.string.checkout_submit_voucher));
                setOrderInfo((PurchaseEntity) baseResponse.getContentData());
                triggerGetMultiStepFinish();
                break;
            case GET_MULTI_STEP_FINISH:
                mOrderFinish = (PackagePurchaseEntity) baseResponse.getContentData();
                if (!pageTracked) {

                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(
                            getString(TrackingPage.CHECKOUT_CONFIRMATION.getName()),
                            getString(R.string.gaScreen),
                            "",
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }

                if (mOrderFinish == null) {
                    showFragmentErrorRetry();
                    break;
                } else {
                    showMyOrder();

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

    private void showDeliveryNotice(String deliveryNotice) {
        tvDeliveryNotice.setVisibility(View.VISIBLE);
        tvDeliveryNotice.setText(deliveryNotice);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            getBaseActivity().onBackPressed();
            return;
        }
        // Get event type and error
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        // Validate event type
        switch (eventType) {
            case ADD_VOUCHER:
            case REMOVE_VOUCHER:
                hideActivityProgress();
                break;
        }
    }
}
