package com.mobile.utils.imageloader;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mobile.view.R;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Narbeh M. on 4/23/17.
 */

public final class ImageManager {
    private ImageLoader mImageLoader;

    private static ImageManager instance = null;
    protected ImageManager() {
        // Exists only to defeat instantiation.
    }

    public static ImageManager getInstance() {
        if(instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    public void loadImage(Context context, final String imageUrl, final NetworkImageView imageView, View progressView, final int placeHolderImageId) {
        loadImage(context, imageUrl, false, imageView, progressView, placeHolderImageId, false, null, false);
    }

    public void loadImage(Context context, final String imageUrl, final NetworkImageView imageView, boolean hideView, RocketImageLoader.RocketImageLoaderListener listener) {
        loadImage(context, imageUrl, false, imageView, null, -1, hideView, listener, false);
    }

    public void loadImage(Context context, final String imageUrl, boolean isFilePathList, final NetworkImageView imageView, final View progressView, final int placeHolderImageId, final boolean hideImageView, final RocketImageLoader.RocketImageLoaderListener listener, boolean isDraftImage) {
        if (!TextUtils.isEmpty(imageUrl) && imageView !=  null) {
            if(isDraftImage) {
                if (progressView != null) {
                    progressView.setVisibility(View.GONE);
                }
            } else if (isFilePathList) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(imageUrl));

                if (progressView != null) {
                    progressView.setVisibility(View.GONE);
                }
            } else {
                ImageLoader.ImageContainer imgContainer = (ImageLoader.ImageContainer) imageView.getTag();
                if (null != imgContainer && !imgContainer.getRequestUrl().equals(imageUrl)) {
                    imgContainer.cancelRequest();
                    if (listener != null) {
                        listener.onLoadedCancel();
                    }
                }

                if (progressView != null) {
                    progressView.setVisibility(View.VISIBLE);
                }

                // clear any previous image
                if(placeHolderImageId != -1) {
                    imageView.setImageResource(placeHolderImageId);
                }

                mImageLoader = ImageFetcher.getInstance(context).getImageLoader();
                mImageLoader.get(imageUrl, this.getImageListener(context, imageUrl, imageView, placeHolderImageId, android.R.drawable.ic_dialog_alert, progressView, hideImageView, listener));
                imageView.setImageUrl(imageUrl, mImageLoader);
                imageView.setTag(imgContainer);
            }
        } else if (imageView != null) {
            if (progressView != null) {
                progressView.setVisibility(View.GONE);
            }

            // clear any previous image
            if(placeHolderImageId != -1) {
                imageView.setImageResource(placeHolderImageId);
            }

            if (listener != null) {
                listener.onLoadedError();
            }
        }
    }

    private ImageLoader.ImageListener getImageListener(final Context context, final String imageUrl, final NetworkImageView imageView, final int defaultImageResId, final int errorImageResId, final View progressView, final boolean hideImageView, final RocketImageLoader.RocketImageLoaderListener listener) {
        return new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorImageResId != 0) {
                    imageView.setImageResource(errorImageResId);
                }

                if (progressView != null) {
                    progressView.setVisibility(View.GONE);
                }

                if (listener != null) {
                    listener.onLoadedError();
                }

                if (hideImageView) {
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                /*if (response.getBitmap() != null) {
                    imageView.setImageBitmap(response.getBitmap());
                } else if (defaultImageResId != 0) {
                    imageView.setImageResource(defaultImageResId);
                }*/

                if (response.getBitmap() != null && response.getBitmap().getWidth() != -1) {
                    if (response.getRequestUrl().equals(imageUrl)) {
                        if (progressView != null) {
                            progressView.setVisibility(View.GONE);
                        }

                        // Animation
                        Boolean noAnimate = (Boolean) imageView.getTag(R.id.no_animate);
                        if (noAnimate == null || noAnimate) {
                            imageView.setImageBitmap(response.getBitmap());
                        } else {
                            Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_fade_in);
                            imageView.setImageBitmap(response.getBitmap());
                            imageView.startAnimation(animation);
                        }

                        if (listener != null) {
                            listener.onLoadedSuccess(imageUrl, response.getBitmap());
                        }

                        if (hideImageView) {
                            imageView.setVisibility(View.VISIBLE);
                        }
                    }
                } else if(defaultImageResId != -1) {
                    //imageView.setImageBitmap(null);
                    imageView.setImageResource(defaultImageResId);
                }
            }
        };
    }
}
