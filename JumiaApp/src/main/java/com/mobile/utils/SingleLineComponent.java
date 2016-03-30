package com.mobile.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by msilva on 3/8/16.
 */
public class SingleLineComponent extends RelativeLayout {

    // Types
    private static final int ONE_ICON = 0;
    private static final int TWO_ICONS = 1;
    private static final int CHECKBOX = 2;
    @IntDef({ONE_ICON, TWO_ICONS, CHECKBOX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SingleLineType {}

    private final int[] layouts_supported = {R.layout.single_line_with_icon, R.layout.single_line_with_two_icons, R.layout.single_line_with_check};

    /**
     * Views
     */
    protected TextView mTextView;
    protected ImageView mImageStartView;
    protected ImageView mImageEndView;
    protected CheckBox mCheckBox;


    public SingleLineComponent(Context context) {
        super(context);
    }

    public SingleLineComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public SingleLineComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleLineComponent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    /**
     * Initialize layout
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleLineWithIconStyleable);
        @SingleLineType final int layoutType = a.getInt(R.styleable.SingleLineWithIconStyleable_layoutType, ONE_ICON);
        final boolean showDivider = a.getBoolean(R.styleable.SingleLineWithIconStyleable_showDivider, false);
        final boolean useSelector = a.getBoolean(R.styleable.SingleLineWithIconStyleable_useSelector, false);
        final int backgroundSelector = a.getResourceId(R.styleable.SingleLineWithIconStyleable_backgroundSelector, R.drawable.selector_myprofile_highlight);
        //final int startImage = a.getResourceId(R.styleable.SingleLineWithIconStyleable_imageStart, -1);
        //final int endImage = a.getResourceId(R.styleable.SingleLineWithIconStyleable_imageEnd, -1);
        a.recycle();

        inflate(context, layouts_supported[layoutType], this);

        if (useSelector) {
            if (DeviceInfoHelper.isPreJellyBeanMR2()) {
                setBackgroundDrawable(ContextCompat.getDrawable(getContext(), backgroundSelector));
            } else {
                setBackground(ContextCompat.getDrawable(getContext(), backgroundSelector));
            }

        }

        mImageStartView = (ImageView) findViewById(R.id.icon_start);
        mImageEndView = (ImageView) findViewById(R.id.icon_end);
        mTextView = (TextView) findViewById(R.id.tx_single_line_text);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);

        View divider = findViewById(R.id.divider);
        if (showDivider && divider != null) {
            divider.setVisibility(VISIBLE);
        }
    }

    /**
     * Returns the Start imageview regarding layout direction
     */
    public ImageView getStartImageView() {
        return mImageStartView;
    }

    public CheckBox getCheckBox() {
        return mCheckBox;
    }

    public void showImageStartViewVisible(){
        getStartImageView().setVisibility(VISIBLE);
    }

    public void showImageEndViewVisible(){
        getEndImageView().setVisibility(VISIBLE);
    }

    public void hideImageStartViewVisible(){
        getStartImageView().setVisibility(INVISIBLE);
    }

    public void hideImageEndViewVisible(){
        getEndImageView().setVisibility(INVISIBLE);
    }

    public void removeImageEndViewVisible(){
        getEndImageView().setVisibility(GONE);
    }

    public void removeImageStartViewVisible(){
        getStartImageView().setVisibility(GONE);
    }

    /**
     * Returns the End imageview regarding layout direction
     */
    public ImageView getEndImageView() {
        return mImageEndView;
    }

    /**
     * Returns the textviewc
     */
    public TextView getTextView() {
        return mTextView;
    }


    public void setEndImageDrawable(Drawable drawable){
        getEndImageView().setImageDrawable(drawable);
    }

    public void removeSelector(){
        if(DeviceInfoHelper.isPreJellyBeanMR2()){
            setBackgroundDrawable(null);
        } else {
            setBackground(null);
        }
    }

}
