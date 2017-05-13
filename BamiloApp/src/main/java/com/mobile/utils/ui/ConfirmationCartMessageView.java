package com.mobile.utils.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;


/**
 * Created by alexandrapires on 10/19/15.
 *
 * This class allows to show a configurable confirmation message when an item is added to cart if the flag hasCartPopup = true
 * (depending of country configurations)
 */


public class ConfirmationCartMessageView implements View.OnClickListener {

    private final static String TAG = ConfirmationCartMessageView.class.getSimpleName();

    protected View mCartViewBar;

    private final TextView mTxCartTotalPrice;

    private final Context mContext;

    private boolean isShowing;


    /**
     * Constructor
     *
     * @param cartViewBar - view corresponding to the message
     * @param context     - Base Activity context
     */
    public ConfirmationCartMessageView(@NonNull View cartViewBar, @NonNull Context context) {
        mContext = context;
        mCartViewBar = cartViewBar;
        mTxCartTotalPrice = (TextView) cartViewBar.findViewById(R.id.tx_cart_total_price);
        cartViewBar.findViewById(R.id.cta_button_view_cart).setOnClickListener(this);
        cartViewBar.findViewById(R.id.cta_link_continue_shopping).setOnClickListener(this);
        isShowing = false;
    }


    /**
     * Hide view with animation
     */
    public void hideMessageWithAnimation() {
        if (isShowing) {
            UIUtils.animateSlideUp(mCartViewBar);
            isShowing = false;
        }
    }

    /**
     * Hide view without animation
     */
    public void hideMessage() {
        if (isShowing) {
            mCartViewBar.setVisibility(View.GONE);
            isShowing = false;
        }
    }

    /**
     * Show view with animation
     *
     * @param totalCartPrice - total price in cart
     */
    public void showMessage(double totalCartPrice) {
        try {
            String message = this.mContext.getResources().getString(R.string.cart_total_price, CurrencyFormatter.formatCurrency(totalCartPrice));
            mTxCartTotalPrice.setText(message);
            isShowing = true;
            UIUtils.animateSlideDown(mCartViewBar);
            mCartViewBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isShowing){
                        UIUtils.animateSlideUp(mCartViewBar);
                    }

                }
            }, WarningFactory._3_SECONDS);
        } catch (Exception e) {
            Print.e(TAG, "ERROR IN SHOW MESSAGE: " + e.getMessage());
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cta_button_view_cart: //view cart
                hideMessageWithAnimation();
                ((BaseActivity) ConfirmationCartMessageView.this.mContext).onSwitchFragment(FragmentType.SHOPPING_CART, new Bundle(), FragmentController.ADD_TO_BACK_STACK);
                break;
            default:
                hideMessageWithAnimation();
                break;
        }
    }
}
