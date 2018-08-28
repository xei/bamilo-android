package com.bamilo.android.appmodule.bamiloapp.utils.imageloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Narbeh M. on 4/23/17.
 */

public final class ImageManager {

    private final Context mContext;

    @SuppressLint("StaticFieldLeak")
    private static ImageManager instance = null;

    protected ImageManager(Context context) {
        mContext = context;
    }

    public static ImageManager getInstance() {
        assert instance != null : "Call Initialize() first";
        return instance;
    }

    public static void initialize(Context context) {
        assert instance == null : "Initialize already called. Use getInstance()";
        instance = new ImageManager(context);
    }

    public void loadImage(final String imageUrl, final ImageView imageView, final View progressView,
            final int placeHolderImageId, final boolean isThumbnail) {
        loadImage(imageUrl, imageView, progressView, placeHolderImageId, isThumbnail,
                new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                            Target<Drawable> target,
                            boolean isFirstResource) {
                        if (progressView != null) {
                            progressView.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                            Target<Drawable> target,
                            DataSource dataSource, boolean isFirstResource) {
                        if (progressView != null) {
                            progressView.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
    }

    public void loadImage(final String imageUrl, final ImageView imageView, final View progressView,
            final int placeHolderImageId,
            final boolean isThumbnail, final RequestListener<Drawable> listener) {
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }

        GlideRequest glideRequest = GlideApp.with(mContext)
                .load(imageUrl)
                .placeholder(placeHolderImageId)
                .listener(listener)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        if (isThumbnail) {
            glideRequest = glideRequest.thumbnail(0.1f);
        }
        glideRequest.into(imageView);
    }
}
