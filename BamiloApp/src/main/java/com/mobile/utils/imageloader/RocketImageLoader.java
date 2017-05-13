package com.mobile.utils.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.mobile.service.utils.output.Print;
import com.mobile.view.R;

import java.io.File;

public class RocketImageLoader {

    public static RocketImageLoader instance;

    private static final int NETWORK_THREAD_POOL_SIZE = 3;
    
    private ImageLoader volleyImageLoader;

    // Indicates no value. Will be "translated" to default library-dependent
    // values, if necessary
    public static int NO_VALUE_INTEGER = -1;

    private static final int FILE_DISC_CACHE_SIZE = 50 * 1024 * 1024; // TODO depend
                                                                // of
                                                                // capabilities
                                                                // of device,
                                                                // also use in
                                                                // all libraries
                                                                // - currently
                                                                // only volley
                                                                // and universal
    private RequestQueue volleyRequestQueue;
    private boolean isVolleyRequestQueueRunning = true;

    private Context context;

    public interface RocketImageLoaderListener {
        void onLoadedSuccess(String imageUrl, Bitmap bitmap);

        void onLoadedError();

        void onLoadedCancel();
    }

    public static void init(Context context) {
        instance = new RocketImageLoader();
        instance.initVolley(context);
    }

    public static RocketImageLoader getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RocketImageLoader has to be initialized in initialization of application object and before calling getInstance()");
        }
        return instance;
    }

    /**
     * Convenience method
     */
    public void loadImage(String imageUrl, ImageView imageView) {
        loadImage(imageUrl, imageView, false, null);
    }

    /**
     * Convenience method
     */
    public void loadImage(String imageUrl, ImageView imageView, boolean hideView) {
        loadImage(imageUrl, imageView, hideView, null);
    }

    /**
     * Convenience method
     */
    public void loadImage(String imageUrl, ImageView imageView, boolean hideView, RocketImageLoaderListener listener) {
        loadImage(imageUrl, false, imageView, null, NO_VALUE_INTEGER, hideView, listener, false);
    }

    /**
     * Convenience method
     */
    public void loadImage(String imageUrl, ImageView imageView, View progressView, int placeHolderImageId, String tag) {
        loadImage(imageUrl, false, imageView, progressView, placeHolderImageId, false, null, false);
    }

    /**
     * Convenience method
     */
    public void loadImage(String imageUrl, ImageView imageView, View progressView, int placeHolderImageId) {
        loadImage(imageUrl, false, imageView, progressView, placeHolderImageId, false, null, false);
    }
    
    /**
     * Convenience method
     */
    public void loadImage(String imageUrl, boolean isFilePathList, ImageView imageView, View progressView, int placeHolderImageId) {
        loadImage(imageUrl, isFilePathList, imageView, progressView, placeHolderImageId, false, null, false);
    }
    
    /**
     * Convenience method
     */
    public void loadImage(String imageUrl, boolean isFilePathList, ImageView imageView, View progressView, int placeHolderImageId, boolean isDraftImage) {
        loadImage(imageUrl, isFilePathList, imageView, progressView, placeHolderImageId, false, null,isDraftImage);
    }

    /**
     * Convenience method
     */
    public void loadImage(String imageUrl, ImageView imageView, View progressView, int placeHolderImageId, RocketImageLoaderListener listener) {
        loadImage(imageUrl, false, imageView, progressView, placeHolderImageId, false, listener, false);
    }

    /**
     * 
     * Loads an image in an imageview using currently active image library
     * 
     * Some parameters make sense only for specific image libraries
     */
    public void loadImage(final String imageUrl, boolean isFilePathList, final ImageView imageView, final View progressView, final int placeHolderImageId, final boolean hideImageView, final RocketImageLoaderListener listener, boolean isDraftImage) {
        if (!TextUtils.isEmpty(imageUrl) && null != imageView) {
            if(isDraftImage) {
            	imageView.setImageBitmap(decodeSampledBitmapFromResource(imageUrl));

                if (progressView != null) {
                    progressView.setVisibility(View.GONE);
                }
            } else if (isFilePathList) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(imageUrl));

                if (progressView != null) {
                    progressView.setVisibility(View.GONE);
                }

            } else {

                ImageContainer imgContainer = (ImageContainer) imageView.getTag();
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
                if(placeHolderImageId != NO_VALUE_INTEGER) {
                    imageView.setImageResource(placeHolderImageId);
                }

                imgContainer = volleyImageLoader.get(imageUrl, new ImageListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        
                        //Log.i("IMAGE LOADER", "ON ERROR RESPONSE: " + imageUrl);
 
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
                    public void onResponse(final ImageContainer response, boolean isImmediate) {
                        // only when the bitmap is fully downloaded do we update
                        // the
                        // imageview and fire the events

//                        if (isImmediate ) {
//                            new Handler().post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    onResponse(response, false);
//                                }
//                            });
//                            return;
//                        }
                        
                        if (null != response.getBitmap() && response.getBitmap().getWidth() != -1) {
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
                        } else {
                            //imageView.setImageBitmap(null);
                            if(placeHolderImageId != NO_VALUE_INTEGER)
                                imageView.setImageResource(placeHolderImageId);
                        }
                    }
                }, 0, 0/*-, tag*/);
                imageView.setTag(imgContainer);
            }
        } else if (imageView != null) {

            if (progressView != null) {
                progressView.setVisibility(View.GONE);
            } 
            
            // clear any previous image
            if(placeHolderImageId != NO_VALUE_INTEGER) {
                imageView.setImageResource(placeHolderImageId);
            }

            if (listener != null) {
                listener.onLoadedError();
            }            
        }
    }

	public static Bitmap decodeSampledBitmapFromResource(String path) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// Calculate inSampleSize
		options.inSampleSize = 10; // calculateInSampleSize(options, reqWidth,
									// reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

//	public static int calculateInSampleSize(BitmapFactory.Options options,
//			int reqWidth, int reqHeight) {
//		// Raw height and width of image
//		final int height = options.outHeight;
//		final int width = options.outWidth;
//		int inSampleSize = 1;
//		// Log.i(TAG, "code1images : width : "+width+"height : "+height);
//		if (height > reqHeight || width > reqWidth) {
//			final int halfHeight = height / 2;
//			final int halfWidth = width / 2;
//			// Log.i(TAG,
//			// "code1images : halfWidth : "+halfWidth+"halfHeight : "+halfHeight);
//			// Calculate the largest inSampleSize value that is a power of 2 and
//			// keeps both
//			// height and width larger than the requested height and width.
//			while ((halfHeight / inSampleSize) > reqHeight
//					&& (halfWidth / inSampleSize) > reqWidth) {
//				inSampleSize *= 2;
//			}
//		}
//		// Log.i(TAG, "code1images : inSampleSize : "+inSampleSize);
//		return inSampleSize;
//	}
    
//    public void cancelImageRequest(final String imageUrl, final ImageView imageView) {
//        if (null != imageView) {
//            ImageContainer imgContainer = (ImageContainer) imageView.getTag();
//            if (null != imgContainer && imgContainer.getRequestUrl().equals(imageUrl)) {
//                imgContainer.cancelRequest();
//                imageView.setTag(null);
//            }
//        }
//
//    }

//    public void cancelImageRequests(final List<String> imageUrls) {
//
//        // it should be more efficient to cancel using cancelAll(tag), but
//        // didn't find how to set tag in the requests
//        volleyRequestQueue.cancelAll(new RequestFilter() {
//            @Override
//            public boolean apply(Request<?> request) {
//                return imageUrls.contains(request.getUrl());
//            }
//        });
//
//    }
    
    public void stopProcessingQueue() {
        stopProcessingQueue(null);
    }

    public void stopProcessingQueue(String tag) {        
        if (isVolleyRequestQueueRunning) {
            Print.i("RocketImageLoader", " --- > STOP ProcessingQueue");
            isVolleyRequestQueueRunning = false;
            volleyRequestQueue.stop();
            if (null != tag)
                volleyRequestQueue.cancelAll(tag);
        }
    }
    
    
    public void startProcessingQueue() {
        if (!isVolleyRequestQueueRunning) {
            isVolleyRequestQueueRunning = true;
            Print.i("RocketImageLoader", " --- > START ProcessingQueue");
            volleyRequestQueue.start();
        }
    }

    private void initVolley(Context context) {
        this.context = context;
        initVolley();
    }
    
    private void initVolley() {
        volleyRequestQueue = newRequestQueue(context, null);
        volleyImageLoader = new ImageLoader(volleyRequestQueue, new BitmapLruCache());
    }

//    public void preload(final String imageUrl, final RocketImageLoaderListener listener) {
//        volleyImageLoader.get(imageUrl, new ImageListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                listener.onLoadedError();
//            }
//
//            @Override
//            public void onResponse(ImageContainer response, boolean isImmediate) {
//                listener.onLoadedSuccess(imageUrl, response.getBitmap());
//            }
//        });
//    }

    /**
     * Create request queue for Volley Method copied from
     * com.android.volley.toolbox.Volley reimplemented in order to change the
     * file cache size
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), "volley");

//        String userAgent = "volley/0";
//        try {
//            String packageName = context.getPackageName();
//
//            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
//            userAgent = packageName + "/" + info.versionCode;
//        } catch (NameNotFoundException ignored) {
//        }

        if (stack == null) {
            stack = new HurlStack();
        }

        Network network = new BasicNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir, FILE_DISC_CACHE_SIZE), network, NETWORK_THREAD_POOL_SIZE);
        queue.start();

        return queue;
    }
    
//    public interface RocketImageLoaderLoadImagesListener{
//        void onCompleteLoadingImages(ArrayList<ImageHolder> successUrls);
//    }
    
//    public class ImageHolder{
//        public String url;
//        public Bitmap bitmap;
//
//        //added apires
//        public Bitmap getBitmap()
//        {
//            return bitmap;
//        }
//    }
    
//    public void loadImages(final ArrayList<String> urls, final RocketImageLoaderLoadImagesListener rocketImageLoaderLoadImagesListener){
//        RocketImageLoaderListener rocketImageLoaderListener = new RocketImageLoaderListener() {
//            private int images = 0;
//            private ArrayList<ImageHolder> successUrls = new ArrayList<>();
//
//            @Override
//            public void onLoadedSuccess(String url, Bitmap bitmap) {
//                if(bitmap != null){
//                    ImageHolder imgHolder = new ImageHolder();
//                    imgHolder.url = url;
//                    imgHolder.bitmap = bitmap;
//                    successUrls.add(imgHolder);
//                    checkIfRequestComplete(++images);
//                }
//            }
//
//            @Override
//            public void onLoadedError() {
//                checkIfRequestComplete(++images);
//            }
//
//            @Override
//            public void onLoadedCancel() {
//                checkIfRequestComplete(++images);
//            }
//
//            private void checkIfRequestComplete(int images){
//                int totalImages = urls.size();
//                if(images == totalImages && rocketImageLoaderLoadImagesListener != null){
//                    rocketImageLoaderLoadImagesListener.onCompleteLoadingImages(successUrls);
//                }
//            }
//        };
//
//        for(String str : urls){
//            RocketImageLoader.getInstance().preload(str, rocketImageLoaderListener);
//        }
//    }


}