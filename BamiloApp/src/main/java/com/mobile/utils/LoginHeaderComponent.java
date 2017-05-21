package com.mobile.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobile.service.objects.configs.AuthInfo;
import com.mobile.service.utils.TextUtils;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * This class will represent the Login Header for each step in Session Login workflow.
 *
 * @author msilva
 */
@SuppressWarnings("unused")
public class LoginHeaderComponent extends FrameLayout {

    // Login Types
    public static final int CHECK_EMAIL = 0;
    public static final int LOGIN = 1;
    public static final int CREATE_ACCOUNT = 2;
    @IntDef({CHECK_EMAIL, LOGIN, CREATE_ACCOUNT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginHeaderType {}

    /**
     * Views
     */
    protected TextView mTitleView;
    protected TextView mSubTitleView;
    // extra view below images
    protected TextView mSubTitleExtraView;
    protected LinearLayout mImagesContainerView;


    public LoginHeaderComponent(Context context) {
        super(context);
    }

    public LoginHeaderComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoginHeaderComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoginHeaderComponent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initialize layout
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoginHeaderComponentStyleable);
        @LoginHeaderType final int layoutType = a.getInt(R.styleable.LoginHeaderComponentStyleable_loginHeaderType, CHECK_EMAIL);
        a.recycle();

        inflate(context, R.layout._gen_login_header_component, this);

        mTitleView = (TextView) findViewById(R.id.login_header_title);
        mSubTitleView = (TextView) findViewById(R.id.login_header_subtitle);
        mSubTitleExtraView = (TextView) findViewById(R.id.login_header_subtitle_extra);
        mImagesContainerView = (LinearLayout) findViewById(R.id.login_header_images_container);

        switch (layoutType) {
            case CHECK_EMAIL:
                setTitle(getContext().getString(R.string.welcome_label));
                setSubTitle(getContext().getString(R.string.login_main_info));
                break;
            case LOGIN:
                setTitle(getContext().getString(R.string.welcome_back_label));
                setSubTitle(getContext().getString(R.string.login_email_info));
                break;
            case CREATE_ACCOUNT:
                setTitle(getContext().getString(R.string.welcome_label));
                setSubTitle(getContext().getString(R.string.register_info));
                break;
        }

    }

    public void setTitle(String title) {
        if (TextUtils.isNotEmpty(title)) {
            mTitleView.setText(title);
        }
    }

    public void setSubTitle(String subTitle) {
        if (TextUtils.isNotEmpty(subTitle)) {
            mSubTitleView.setText(subTitle);
        }
    }

    public void setSubTitleExtra(String subTitle, String toCompare) {
        if (TextUtils.isNotEmpty(subTitle) && !TextUtils.equals(mSubTitleView.getText(), toCompare)) {
            mSubTitleExtraView.setText(subTitle);
            mSubTitleExtraView.setVisibility(VISIBLE);
        } else {
            mSubTitleExtraView.setVisibility(GONE);
        }
    }

    public void setImages(final ArrayList<String> imagesList) {
        for (String imageUrl : imagesList) {
            final View view = inflate(getContext(), R.layout._gen_login_header_image_component, null);
            final ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            final ProgressBar mProgress = (ProgressBar) view.findViewById(R.id.image_loading_progress);
            mImagesContainerView.addView(view);
            /*
            RocketImageLoader.instance.loadImage(imageUrl, imageView, true, new RocketImageLoader.RocketImageLoaderListener() {
                @Override
                public void onLoadedSuccess(String imageUrl, Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    mProgress.setVisibility(GONE);
                }

                @Override
                public void onLoadedError() {
                    view.setVisibility(GONE);
                }

                @Override
                public void onLoadedCancel() {
                    view.setVisibility(GONE);
                }
            });*/
            ImageManager.getInstance().loadImage(imageUrl, imageView, mProgress, -1, false);
        }
    }

    public void showAuthInfo(@LoginHeaderType int loginType, @NonNull AuthInfo authInfo, @Nullable String text) {
        if (authInfo.hasAuthInfo()) {
            switch (loginType) {
                case CHECK_EMAIL:
                    setTitle(authInfo.getTitle(text));
                    setSubTitle(authInfo.getSubtitle(text));
                    setImages(authInfo.getImagesList());
                    break;
                case LOGIN:
                    setTitle(authInfo.getTitle(null));
                    setSubTitle(authInfo.getSubtitle(getContext().getString(R.string.login_email_info)));
                    setSubTitleExtra(getContext().getString(R.string.login_email_info), getContext().getString(R.string.login_email_info));
                    setImages(authInfo.getImagesList());
                    break;
                case CREATE_ACCOUNT:
                    setTitle(authInfo.getTitle(null));
                    setSubTitle(authInfo.getSubtitle(text));
                    setSubTitleExtra(getContext().getString(R.string.register_more_info), text);
                    setImages(authInfo.getImagesList());
                    break;
            }
        }
    }

}
