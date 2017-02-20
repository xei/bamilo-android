package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.forms.ShippingMethodForm;
import com.mobile.forms.ShippingMethodFormBuilder;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.cart.ShoppingCartAddMultipleItemsHelper;
import com.mobile.helpers.cart.ShoppingCartChangeItemQuantityHelper;
import com.mobile.helpers.cart.ShoppingCartRemoveItemHelper;
import com.mobile.helpers.checkout.GetStepFinishHelper;
import com.mobile.helpers.checkout.GetStepPaymentHelper;
import com.mobile.helpers.checkout.GetStepShippingHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.CheckoutFinish;
import com.mobile.newFramework.objects.checkout.Fulfillment;
import com.mobile.newFramework.objects.checkout.ShippingMethodOption;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.FulfillmentUiBuilder;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;
import com.mobile.view.newfragments.NewBaseFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 *
 */
public class CheckoutConfirmationFragment extends NewBaseFragment implements View.OnClickListener ,IResponseCallback{
    TextView next , address , telephone , user , order_count_title , order_price ,ship_price,voucher_price,all_price,ship_time,all_voucher ,voucher_error;
    Switch voucher_switch;
    LinearLayout voucher_lay;
    private ShippingMethodFormBuilder mFormResponse;
    LinearLayout voucher_layer;
    private EditText mVoucherView;
    private Button couponButton;
    ScrollView scrollView;
    private boolean isShowingNoPaymentNecessary;
    private boolean removeVoucher = false;
    private String mVoucherCode;
    private static final String TAG = CheckoutConfirmationFragment.class.getSimpleName();
    private PurchaseEntity mOrderFinish;
    private List<CardChoutItem> cardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardCheckOutAdapter mAdapter;


    /**
     * Empty constructor
     */
    public CheckoutConfirmationFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.checkout_confirmation,
                R.string.checkout_label,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_CONFIRMATION);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Tracking checkout step
       // TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_ADDRESSES);
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
        super.setCheckoutStep(view, 2);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getBaseActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        triggerGetMultiStepFinish();
        voucher_lay = (LinearLayout) view.findViewById(R.id.voucher_value_title);
        next = (TextView) view.findViewById(R.id.checkout_confirmation_btn);
        address = (TextView) view.findViewById(R.id.checkout_address);
        telephone = (TextView) view.findViewById(R.id.checkout_telephone);
        user = (TextView) view.findViewById(R.id.checkout_user_reciver);
        order_count_title = (TextView) view.findViewById(R.id.checkout_order_count_title);
        order_price = (TextView) view.findViewById(R.id.checkout_order_price);
        ship_price = (TextView) view.findViewById(R.id.checkout_order_ship_price);
        voucher_price = (TextView) view.findViewById(R.id.checkout_order_voucher_price);
        all_price = (TextView) view.findViewById(R.id.checkout_order_all_price);
        ship_time = (TextView) view.findViewById(R.id.checkout_order_ship_time);
        all_voucher = (TextView) view.findViewById(R.id.checkout_order_all_discount);
        voucher_error = (TextView) view.findViewById(R.id.error_message_mandatory);
        voucher_switch = (Switch) view.findViewById(R.id.voucher_switch);
        voucher_layer = (LinearLayout) view.findViewById(R.id.voucher_layout);
        mVoucherView = (EditText) view.findViewById(R.id.voucher_codename);
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
        recyclerView.setAdapter(mAdapter);
        //prepareCardData();
        voucher_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    voucher_layer.setVisibility(View.VISIBLE);

                }else{
                    voucher_layer.setVisibility(View.GONE);
                    triggerRemoveVoucher();
                }

            }
        });

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
        if (viewId== R.id.checkout_confirmation_btn)
        {
           getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_PAYMENT, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
        else if (viewId == R.id.checkout_button_enter) {
            if (!TextUtils.isEmpty(mVoucherView.getText()) && !TextUtils.equals(couponButton.getText(), getString(R.string.remove_label))) {
                hideError();
                validateCoupon();
            } else {
              if (TextUtils.isEmpty(mVoucherView.getText()))
              {
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
    private void hideError(){
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
        all_price.setText(mOrderFinish.getTotal()+" ریال ");
        order_count_title.setText(mOrderFinish.getCartCount()+" "+getContext().getString(R.string.checkout_count_title));
        order_price.setText(mOrderFinish.getSubTotalUnDiscounted()+" ریال ");
        all_price.setTextColor(Color.GREEN);
        all_voucher.setText(mOrderFinish.getSubTotalUnDiscounted()-mOrderFinish.getTotal()+" ریال ");
        if (mOrderFinish.hasCouponDiscount()){
            voucher_lay.setVisibility(View.VISIBLE);
        voucher_price.setText(mOrderFinish.getCouponDiscount()+" ریال ");
        }
        else{
            voucher_lay.setVisibility(View.GONE);
        }
    }

    private void showProducts() {
        cardList = new ArrayList<CardChoutItem>();
        for (PurchaseCartItem item : mOrderFinish.getCartItems()) {
            CardChoutItem card = new CardChoutItem(item.getBrandName().toString(), item.getName().toString(), item.getPrice()+"",item.getQuantity()+"",item.getImageUrl());
            cardList.add(card);
        }
        mAdapter = new CardCheckOutAdapter(cardList);

        recyclerView.setAdapter(mAdapter);
    }

    private void showOrderAddresses() {

        if (mOrderFinish.hasShippingAddress()) {
            address.setText(mOrderFinish.getShippingAddress().getAddress().toString());
            telephone.setText("تلفن : "+mOrderFinish.getShippingAddress().getPhone().toString());
            user.setText(mOrderFinish.getShippingAddress().getFirstName().toString()+" "+mOrderFinish.getShippingAddress().getLastName().toString());
        }
        else {

        }
    }


    private void showShippingMethod() {

        if (mOrderFinish.hasFreeShipping()||mOrderFinish.getShippingValue()==0){
            ship_price.setText("رایگان");
            ship_price.setTextColor(Color.GREEN);
        }
        else {
            ship_price.setText(mOrderFinish.getShippingValue()+" ریال ");//hazineh haml kol
            ship_price.setTextColor(Color.BLACK);
        }

    }
    private void triggerGetShippingMethods(){
        Print.i(TAG, "TRIGGER: GET SHIPPING METHODS");
        triggerContentEventProgress(new GetStepShippingHelper(), null, this);
    }


    private void setOrderInfo(PurchaseEntity purchaseEntity) {
        // Update voucher
        updateVoucher(purchaseEntity);
        // Show checkout summary
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_PAYMENT, purchaseEntity);
    }

    private void prepareCouponView() {

      /*  if (!TextUtils.isEmpty(mVoucherCode)) {
            mVoucherView.setText(mVoucherCode);
        } triggerSubmitVoucher(mVoucherCode);
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
        });*/
    }


    /**
     * Fill Coupon field if orderSummary has discountCouponCode
     */
    private void updateVoucher(PurchaseEntity orderSummary) {
        if (orderSummary != null) {
            if (orderSummary.hasCouponDiscount()) {
                mVoucherCode = orderSummary.getCouponCode();
                if (!TextUtils.isEmpty(mVoucherCode)) {
                    removeVoucher = true;
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
                couponButton.setText(getString(eventType == EventType.ADD_VOUCHER ? R.string.remove_label : R.string.checkout_submit_voucher));
                removeVoucher = eventType == EventType.ADD_VOUCHER;
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
                couponButton.setText(getString(eventType == EventType.REMOVE_VOUCHER ? R.string.checkout_submit_voucher : R.string.checkout_submit_voucher));
                setOrderInfo((PurchaseEntity) baseResponse.getContentData());
                triggerGetMultiStepFinish();
                break;
            case GET_MULTI_STEP_SHIPPING:
                onSuccessGetShippingMethods(baseResponse);
                hideActivityProgress();
                break;
             case GET_MULTI_STEP_FINISH:
                mOrderFinish = (PurchaseEntity) baseResponse.getContentData();

                if(mOrderFinish == null) {
                    showFragmentErrorRetry();
                } else {
                    showMyOrder();

                    triggerGetShippingMethods();
                    hideActivityProgress();
                }

                 if (mOrderFinish.hasCouponDiscount())
                 {
                     couponButton.setText(getContext().getString(R.string.remove_label));
                     mVoucherView.setText(mOrderFinish.getCouponCode());
                     mVoucherView.setFocusable(false);
                     voucher_switch.setChecked(true);
                 }else {
                     couponButton.setText(getContext().getString(R.string.checkout_submit_voucher));
                     mVoucherView.setText("");
                     mVoucherView.setFocusable(true);
                     mVoucherView.setFocusableInTouchMode(true);
                 }
                break;

            default:
                break;
        }
    }


    public void onSuccessGetShippingMethods(BaseResponse baseResponse){
        Print.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
        //
        GetStepShippingHelper.ShippingMethodFormStruct shippingMethodsForm = (GetStepShippingHelper.ShippingMethodFormStruct) baseResponse.getContentData();
        // Get order summary
        PurchaseEntity orderSummary = shippingMethodsForm.getOrderSummary();
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_SHIPPING, orderSummary);
        showDeliveryTime(loadForm(shippingMethodsForm.getFormBuilder()));
    }

    private void showDeliveryTime(String time) {
        ship_time.setText(time);//
    }

    private String loadForm(ShippingMethodFormBuilder form) {
        ShippingMethodForm field = new ShippingMethodForm(form.shippingMethodFormBuilderHolder.fields.get(0));
       return field.optionsShippingMethod.get("UniversalShippingMatrix").deliveryTime;
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
            case ADD_VOUCHER:
            case REMOVE_VOUCHER:
                hideActivityProgress();
                break;
        }
    }


}
