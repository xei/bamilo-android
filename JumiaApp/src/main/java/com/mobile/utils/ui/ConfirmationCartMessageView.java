package com.mobile.utils.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;


/**
 * Created by alexandrapires on 10/19/15.
 *
 * This class allows to show a configurable confirmation message when an item is added to cart if the flag hasCartPopup = true
 * (depending of country configurations)
 */


public class ConfirmationCartMessageView {

    protected View mConfigurablecartViewBar;

    private TextView mTxCartTotalPrice;

    private Button mCTAButtonViewCart;

    private Button mCTAContinueShopping;

    private  Context mContext;

    private boolean isShowing;




/**
 * Contructor
 * @param mConfigurablecartViewBar - view corresponding to the message
 * @param context - Base Activity context
 *
 * */
    public ConfirmationCartMessageView(View mConfigurablecartViewBar, Context context){

        if(mConfigurablecartViewBar == null){
            throw new IllegalStateException("configurablecartViewBar bar not initialized");
        }

        this.mContext = context;
        this.mConfigurablecartViewBar = mConfigurablecartViewBar;

        mTxCartTotalPrice = (TextView) mConfigurablecartViewBar.findViewById(R.id.tx_cart_total_price);
        mCTAButtonViewCart = (Button) mConfigurablecartViewBar.findViewById(R.id.cta_button_view_cart);
        mCTAButtonViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessage();
                ((BaseActivity) ConfirmationCartMessageView.this.mContext).onSwitchFragment(FragmentType.SHOPPING_CART, new Bundle(), FragmentController.ADD_TO_BACK_STACK);
            }
        });

        mCTAContinueShopping = (Button) mConfigurablecartViewBar.findViewById(R.id.cta_link_continue_shopping);
        mCTAContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessage();
            }
        });

        isShowing = false;

    }


    /**
     * Hide view with animation
     * */
    public void hideMessage(){
        if(isShowing){
            UIUtils.animateSlideUp(mContext, mConfigurablecartViewBar);
            isShowing = false;
        }

    }


    /**
     * Show view with animation
     * @param totalCartPrice - total price in cart
     * */
    public void showMessage(double totalCartPrice){
        String message = this.mContext.getResources().getString(R.string.cart_total_price,CurrencyFormatter.formatCurrency(totalCartPrice));
        mTxCartTotalPrice.setText(message);
        isShowing = true;
        UIUtils.animateSlideDown(mContext, mConfigurablecartViewBar);
    }




}
