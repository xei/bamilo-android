package com.mobile.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class used to represent some single layouts.
 *
 * @author msilva
 */
@SuppressWarnings("unused")
public class SingleLineComponent extends FrameLayout {

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
        init(context, attrs);
    }

    public SingleLineComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SingleLineComponent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
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
        final int startImage = a.getResourceId(R.styleable.SingleLineWithIconStyleable_imageStart, -1);
        final int endImage = a.getResourceId(R.styleable.SingleLineWithIconStyleable_imageEnd, -1);
        a.recycle();

        inflate(context, layouts_supported[layoutType], this);

        if (useSelector) {
            background(ContextCompat.getDrawable(getContext(), backgroundSelector));
        }

        mImageStartView = (ImageView) findViewById(R.id.icon_start);
        mImageEndView = (ImageView) findViewById(R.id.icon_end);
        mTextView = (TextView) findViewById(R.id.tx_single_line_text);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);

        if (showDivider) {
            visibility(findViewById(R.id.divider), VISIBLE);
        }
    }

    @Nullable
    public ImageView getStartImageView() {
        return mImageStartView;
    }

    @Nullable
    public CheckBox getCheckBox() {
        return mCheckBox;
    }

    public void showImageStartViewVisible() {
        visibility(mImageStartView, VISIBLE);
    }

    public void hideImageStartViewVisible() {
        visibility(mImageStartView, INVISIBLE);
    }

    public void removeImageStartViewVisible() {
        visibility(mImageStartView, GONE);
    }

    /**
     * Returns the End imageview regarding layout direction
     */
    @Nullable
    public ImageView getEndImageView() {
        return mImageEndView;
    }

    public void showImageEndViewVisible() {
        visibility(mImageEndView, VISIBLE);
    }

    public void hideImageEndViewVisible() {
        visibility(mImageEndView, INVISIBLE);
    }

    public void removeImageEndViewVisible() {
        visibility(mImageEndView, GONE);
    }

    @NonNull
    public TextView getTextView() {
        return mTextView;
    }

    @SuppressLint("NewApi")
    public void removeSelector() {
        background(null);
    }

    private void visibility(View view, int flag) {
        if (view != null) {
            view.setVisibility(flag);
        }
    }

    @SuppressWarnings("deprecation")
    private void background(@Nullable Drawable background) {
        if (DeviceInfoHelper.isPosJellyBean()) {
            super.setBackground(background);
        } else {
            super.setBackgroundDrawable(background);
        }
    }
}
