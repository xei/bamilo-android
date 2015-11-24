package com.mobile.utils.ui;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.view.View;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.1
 * @date 2015/04/13
 */
public class WarningFactory {

    /**
     * ################# ANIMATION DURATION #################
     */

//    public static final int _4_SECONDS = 4000;
    public static final int _3_SECONDS = 3000;
    public static final int _5_SECONDS = 5000;

    /**
     * ################# WARNING BAR #################
     */
    //Generic Error Messages
    public static final int ERROR_MESSAGE = 1;
    //Generic Success Messages
    public static final int SUCCESS_MESSAGE = 2;

    //Special cases
    public static final int CHOOSE_ONE_SIZE = 3;
    public static final int PROBLEM_FETCHING_DATA = 4;
    public static final int PROBLEM_FETCHING_DATA_ANIMATION = 5;


    @IntDef({
        ERROR_MESSAGE,
        SUCCESS_MESSAGE,
        CHOOSE_ONE_SIZE,
        PROBLEM_FETCHING_DATA,
        PROBLEM_FETCHING_DATA_ANIMATION,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WarningErrorType{}

    /**
     * The last warning that was built and might be re-used.
     */
    protected int actualWarning;

    protected View mWarningBar;

    private String mWarningMessage;
    private String actualWarningMessage;

    /**
     * Create a new instance of WarningFactory.
     *
     * @param warningBar
     * @throws java.lang.IllegalStateException In case of warningBar is null.
     */
    public WarningFactory(View warningBar){

        if(warningBar == null){
            throw new IllegalStateException("Warning bar not initialized");
        }

        actualWarning = -1;

        this.mWarningBar = warningBar;
        mWarningBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideWarning();
            }
        });
    }

    /**
     * constructor where the label to show can be dynamic
     * @param warning
     * @param warningMessage
     */
    public void showWarning(int warning, String warningMessage){
        mWarningMessage = warningMessage;
        showWarning(warning);
    }

    /**
     * Constructs warning bar.
     *
     * @param warning The warning desiredt.
     */
    public void showWarning(@WarningErrorType int warning){
        switch (warning){
            // Success cases
            case SUCCESS_MESSAGE:
                showWarningSuccess(warning);
                break;
            // Error cases
            case ERROR_MESSAGE:
                showWarningError(warning);
                break;
            // Specific cases
            case CHOOSE_ONE_SIZE:
                showWarningChooseOneSize();
                break;
            case PROBLEM_FETCHING_DATA:
                showWarningProblemFetchingData(false);
                break;
            case PROBLEM_FETCHING_DATA_ANIMATION:
                showWarningProblemFetchingData(true);
                break;
            default:
                break;
        }
    }
    /**
     * show dynamic success message
     * @param warning
     */
    private void showWarningSuccess(int warning){
        if(actualWarning != warning || !TextUtils.equals(actualWarningMessage, mWarningMessage)){
            new Builder().setText(mWarningMessage)
                    .setBackground(R.color.green_warning)
                    .setImageVisibility(false)
                    .setAnimationDuration(_5_SECONDS)
                    .startAnimation();
            actualWarning = warning;
            actualWarningMessage = mWarningMessage;
        } else {
            new Builder().startAnimation();
        }
    }

    /**
     * show dynamic error message
     * @param warning
     */
    private void showWarningError(int warning) {
        if(actualWarning != warning || !TextUtils.equals(actualWarningMessage, mWarningMessage)){
            new Builder().setText(mWarningMessage)
                    .setBackground(R.color.red_warning)
                    .setImageVisibility(true)
                    .setAnimationDuration(_5_SECONDS)
                    .startAnimation();
            actualWarning = warning;
            actualWarningMessage = mWarningMessage;
        } else {
            new Builder().startAnimation();
        }
    }

    private void showWarningChooseOneSize(){
        if(actualWarning != CHOOSE_ONE_SIZE) {
            new Builder().setText(R.string.product_variance_choose_error)
                    .setBackground(R.color.red_warning)
                    .setAnimationDuration(_5_SECONDS)
                    .setImageVisibility(false)
                    .show();

            actualWarning = CHOOSE_ONE_SIZE;
        } else {
            new Builder().show();
        }
    }

    private void showWarningProblemFetchingData(boolean withAnimation){
        if(!withAnimation){
            if(actualWarning != PROBLEM_FETCHING_DATA) {
                new Builder().setText(R.string.server_error)
                        .setBackground(R.color.red_warning)
                        .setAnimationDuration(_5_SECONDS)
                        .setImageVisibility(true)
                        .show();
                actualWarning = PROBLEM_FETCHING_DATA;
            } else {
                new Builder().show();
            }

        } else {

            if(actualWarning != PROBLEM_FETCHING_DATA_ANIMATION) {
                new Builder().setText(R.string.server_error)
                        .setBackground(R.color.red_warning)
                        .setImageVisibility(true)
                        .setAnimationDuration(_5_SECONDS)
                        .startAnimation();
                actualWarning = PROBLEM_FETCHING_DATA_ANIMATION;
            } else {
                new Builder().startAnimation();
            }

        }
    }


    public void hideWarning(){
        new Builder().hide();
    }

    /**
     * Class used for building the warning.
     */
    private class Builder {

        private int animationLength = _5_SECONDS;

        Builder(){
            mWarningBar.clearAnimation();
        }

        Builder setText(int message){
            ((TextView)mWarningBar.findViewById(R.id.warning_text)).setText(message);
            return this;
        }

        Builder setText(String message){
            ((TextView)mWarningBar.findViewById(R.id.warning_text)).setText(message);
            return this;
        }

        Builder setBackground(@DrawableRes int drawable){
            mWarningBar.setBackgroundResource(drawable);
            mWarningBar.setAlpha(0.95f);
            return this;
        }

        Builder setImageVisibility(boolean isToShowImage){
            mWarningBar.findViewById(R.id.warning_image).setVisibility(isToShowImage ? View.VISIBLE : View.GONE);
            return this;
        }

        Builder setImage(@DrawableRes int image){
            View imageView = mWarningBar.findViewById(R.id.warning_image);
            imageView.setVisibility(View.VISIBLE);
            ((ImageView)imageView).setImageResource(image);
            return this;
        }

        Builder setAnimationDuration(@IntRange(from=0) int animationLength){
            this.animationLength = animationLength;
            return this;
        }

        Builder startAnimation(){
            mWarningBar.setVisibility(View.VISIBLE);
            UIUtils.animateFadeInAndOut(mWarningBar.getContext(), mWarningBar, animationLength);
            return this;
        }

        Builder show(){
            mWarningBar.setVisibility(View.VISIBLE);
            return this;
        }

        Builder hide(){
            mWarningBar.setVisibility(View.GONE);
            return this;
        }


    }
}
