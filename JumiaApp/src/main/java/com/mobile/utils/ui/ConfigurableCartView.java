package com.mobile.utils.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;


/**
 * Created by alexandrapires on 10/19/15.
 *
 * This class allows to show a configurable confirmation message when an item is added to cart if the flag hasCartPopup = true
 * (depending of country configurations)
 */


public class ConfigurableCartView {

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
    public ConfigurableCartView(View mConfigurablecartViewBar,Context context){

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
                ((BaseActivity) ConfigurableCartView.this.mContext).onSwitchFragment(FragmentType.SHOPPING_CART, new Bundle(), FragmentController.ADD_TO_BACK_STACK);
            }
        });

        mCTAContinueShopping = (Button) mConfigurablecartViewBar.findViewById(R.id.cta_link_continue_shopping);
        mCTAContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMessage();
            }
        });

        //change image direction if is rtl
        if(ShopSelector.isRtl() && android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            ImageView checkImage = (ImageView) mConfigurablecartViewBar.findViewById(R.id.im_check);
            checkImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_success_message_rtl));
        }

        isShowing = false;

    }


    public void hideMessage(){
        if(isShowing){
            new Builder().hideWithAnimation();
            isShowing = false;
        }

    }


    /**
     * Show view with animation
     * @param message - message including cart's total price
     * */
    public void showMessage(String message){
        mTxCartTotalPrice.setText(message);
        isShowing = true;
        new Builder().showWithAnimation();
    }



    /**
     * Inner class for creating a dialog throught the view
     *
     * */
    private class Builder {

        Builder(){
            mConfigurablecartViewBar.clearAnimation();
        }


        Builder showWithAnimation(){
            mConfigurablecartViewBar.setVisibility(View.VISIBLE);
            applyAnimation(R.anim.slide_down, mContext.getResources().getInteger(R.integer.config_add_cart_anim_duration));

            return this;
        }


        Builder hideWithAnimation(){
            mConfigurablecartViewBar.setVisibility(View.GONE);
            applyAnimation(R.anim.slide_up, mContext.getResources().getInteger(R.integer.config_add_cart_anim_duration));
            return this;
        }



        /**
         * allows to apply an animation with a certain duration to the main view
         * @param animationResourceId - animation resource id
         * @param duration - animation duration in miliseconds
         * */
        private void applyAnimation(int animationResourceId,long duration)
        {
            Animation animation = AnimationUtils.loadAnimation(mConfigurablecartViewBar.getContext(), animationResourceId);
          //  animation.setDuration(duration);
            mConfigurablecartViewBar.clearAnimation();
            mConfigurablecartViewBar.startAnimation(animation);
        }


    }
}
