package com.mobile.utils.imageloader;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Narbeh M. on 4/23/17.
 */

public final class ImageManager {
    private final Context mContext;

    private static ImageManager instance = null;
    protected ImageManager(Context context) {
        mContext = context;
    }

    public static ImageManager getInstance() {
        assert instance != null: "Call Initialize() first";
        return instance;
    }

    public static void initialize(Context context) {
        assert instance == null: "Initialize already called. Use getInstance()";
        instance = new ImageManager(context);
    }

    public void loadImage(final String imageUrl, final ImageView imageView, final View progressView, final int placeHolderImageId, final boolean isThumbnail) {
        loadImage(imageUrl, imageView, progressView, placeHolderImageId, isThumbnail, new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                if(progressView != null) {
                    progressView.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
                if(progressView != null) {
                    progressView.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    public void loadImage(final String imageUrl, final ImageView imageView, final View progressView, final int placeHolderImageId,
                          final boolean isThumbnail, final RequestListener<String, GlideDrawable> listener) {
        if(progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }

        DrawableRequestBuilder<?> builder = Glide.with(mContext)
                .load(imageUrl)
                .placeholder(placeHolderImageId)
                .crossFade()
                .listener(listener)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        if(isThumbnail) {
            builder.thumbnail(0.1f);
        }

        builder.into(imageView);
    }
}
