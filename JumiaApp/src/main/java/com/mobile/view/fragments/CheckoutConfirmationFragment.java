package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.cart.ShoppingCartAddMultipleItemsHelper;
import com.mobile.helpers.cart.ShoppingCartChangeItemQuantityHelper;
import com.mobile.helpers.cart.ShoppingCartRemoveItemHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DarwinRegex;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.cart.UICartUtils;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;
import com.mobile.view.newfragments.NewBaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 *
 */
public class CheckoutConfirmationFragment extends NewBaseFragment implements View.OnClickListener ,IResponseCallback{
 TextView next;
    Switch voucher_switch;
    LinearLayout voucher_layer;
    private EditText mVoucherView;
    private Button couponButton;
    private boolean removeVoucher = false;
    private String mVoucherCode;
    private static final String TAG = CheckoutConfirmationFragment.class.getSimpleName();

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Print.i(TAG, "ON VIEW CREATED");
        super.setCheckoutStep(view, 2);
        next = (TextView) view.findViewById(R.id.checkout_confirmation_btn);
        voucher_switch = (Switch) view.findViewById(R.id.voucher_switch);
        voucher_layer = (LinearLayout) view.findViewById(R.id.voucher_layout);
        mVoucherView = (EditText) view.findViewById(R.id.voucher_name);
      /*  couponButton = (Button) view.findViewById(R.id.checkout_button_enter);*/
        next.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.cheackout_recycler_view);

        mAdapter = new CardCheckOutAdapter(cardList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareCardData();
        voucher_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    voucher_layer.setVisibility(View.VISIBLE);
                }else{
                    voucher_layer.setVisibility(View.GONE);
                }

            }
        });

    }

    private void prepareCardData() {
        CardChoutItem card = new CardChoutItem("پارس خزر", "جاروبرقی شارٰی 500 وات", "2000000 ریال","2");
        cardList.add(card);

        card = new CardChoutItem("Samsung ", "هدفون سامسونگ مدل In-Ear Fit", "49,000 تومان","4");
        cardList.add(card);
        mAdapter.notifyDataSetChanged();
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
    }

    private void triggerRemoveVoucher() {
        triggerContentEventProgress(new RemoveVoucherHelper(), null, this);
    }

    private void triggerSubmitVoucher(String code) {
        triggerContentEventProgress(new AddVoucherHelper(), AddVoucherHelper.createBundle(code), this);
    }

    private void onClickSubmitPaymentButton() {

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId== R.id.checkout_confirmation_btn)
        {
            Toast.makeText(getActivity(),"asdasdasdas",Toast.LENGTH_LONG).show();
        }
        else if (viewId == R.id.checkout_button_enter) {
            if (!TextUtils.isEmpty(mVoucherView.getText()) && !TextUtils.equals(couponButton.getText(), getString(R.string.remove_label))) {
                validateCoupon();
            } else {
                onClickSubmitPaymentButton();
            }
            getBaseActivity().hideKeyboard();
        }
    }

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
            case REMOVE_VOUCHER:
                mVoucherCode = null;
            case ADD_VOUCHER:
                couponButton.setText(getString(eventType == EventType.ADD_VOUCHER ? R.string.remove_label : R.string.use_label));
                removeVoucher = eventType == EventType.ADD_VOUCHER;
                hideActivityProgress();
                // setOrderInfo((PurchaseEntity) baseResponse.getContentData());
                // Case voucher removed and is showing no payment necessary
       /* if(isShowingNoPaymentNecessary) {
            isShowingNoPaymentNecessary = false;
            triggerGetPaymentMethods();
        }*/
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

    }


}
