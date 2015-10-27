package com.mobile.utils.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.view.R;


/**
 * Created by alexandrapires on 10/19/15.
 *
 * This class allows to show a configurable confirmation message when an item is added to cart if the flag hasCartPopup = true
 * (depending of country configurations)
 */


public class ConfirmationCartMessageView implements View.OnClickListener {

    private final static String TAG = ConfirmationCartMessageView.class.getSimpleName();

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
    public ConfirmationCartMessageView(@NonNull View mConfigurablecartViewBar,@NonNull Context context){

            this.mContext = context;
            this.mConfigurablecartViewBar = mConfigurablecartViewBar;

            mTxCartTotalPrice = (TextView) mConfigurablecartViewBar.findViewById(R.id.tx_cart_total_price);
            mCTAButtonViewCart = (Button) mConfigurablecartViewBar.findViewById(R.id.cta_button_view_cart);
            mCTAContinueShopping = (Button) mConfigurablecartViewBar.findViewById(R.id.cta_link_continue_shopping);

            mCTAButtonViewCart.setOnClickListener(this);
            mCTAContinueShopping.setOnClickListener(this);

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

        try {
            String message = this.mContext.getResources().getString(R.string.cart_total_price, CurrencyFormatter.formatCurrency(totalCartPrice));
            mTxCartTotalPrice.setText(message);
            isShowing = true;
            UIUtils.animateSlideDown(mContext, mConfigurablecartViewBar);

        }catch(Exception e){
            Print.e(TAG, "ERROR IN SHOW MESSAGE: "+ e.getMessage());
        }
    }


    @Override
    public void onClick(View view) {

            hideMessage();

    }
}
