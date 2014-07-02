package pt.rocket.utils;

import java.io.File;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class RocketImageLoader {

    public static RocketImageLoader instance;

    private com.android.volley.toolbox.ImageLoader volleyImageLoader;

    // Indicates no value. Will be "translated" to default library-dependent
    // values, if necessary
    public static int NO_VALUE_INTEGER = -1;

    private Application application;

    private static int FILE_DISC_CACHE_SIZE = 50 * 1024 * 1024; // TODO depend
                                                                // of
                                                                // capabilities
                                                                // of device,
                                                                // also use in
                                                                // all libraries
                                                                // - currently
                                                                // only volley
                                                                // and universal
    private RequestQueue volleyRequestQueue;

    public static interface RocketImageLoaderListener {
        void onLoadedSuccess(Bitmap bitmap);

        void onLoadedError();

        void onLoadedCancel(String imageUrl);
    }

    public static void init(Application application) {
        instance = new RocketImageLoader();
        instance.application = application;

        instance.initLibrary(application);
    }

    public static RocketImageLoader getInstance() {
        if (instance == null) {
            throw new IllegalStateException("RocketImageLoader has to be initialized in initialization of application object and before calling getInstance()");
        }
        return instance;
    }

    private void initLibrary(Application application) {

        initVolley(application);

    }

    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     */
    public void loadImage(String imageUrl, ImageView imageView) {
        loadImage(imageUrl, imageView, null);
    }
    

    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     * @param listener
     */
    public void loadImage(String imageUrl, ImageView imageView, RocketImageLoaderListener listener) {
        loadImage(imageUrl, imageView, null, NO_VALUE_INTEGER, NO_VALUE_INTEGER, true, listener);
    }

    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     * @param progressView
     * @param placeHolderImageId
     */
    public void loadImage(String imageUrl, ImageView imageView, View progressView, int placeHolderImageId) {
        loadImage(imageUrl, imageView, progressView, NO_VALUE_INTEGER, placeHolderImageId, true, null);
    }

    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     * @param progressView
     * @param placeHolderImageId
     * @param listener
     */
    public void loadImage(String imageUrl, ImageView imageView, View progressView, int placeHolderImageId, RocketImageLoaderListener listener) {
        loadImage(imageUrl, imageView, progressView, NO_VALUE_INTEGER, placeHolderImageId, true, listener);
    }

    /**
     * 
     * Loads an image in an imageview using currently active image library
     * 
     * Some parameters make sense only for specific image libraries
     * 
     * @param imageUrl
     * @param imageView
     * @param progressView
     * @param targetWidth
     *            aquery specific:
     *            "Target width for down sampling when reading large images. 0 = no downsampling."
     * @param placeHolderImageId
     * @param fadeIn
     * @param listener
     */
    public void loadImage(final String imageUrl, final ImageView imageView, final View progressView, int targetWidth, int placeHolderImageId, boolean fadeIn,
            final RocketImageLoaderListener listener) {
        
        

        if (!imageUrl.equals("") && null != imageView) {
//            Log.i("TAG"," LOADING IMAGE");
            ImageContainer imgContainer = (ImageContainer) imageView.getTag();
            if (null != imgContainer && !imgContainer.getRequestUrl().equals(imageUrl)) {
                imgContainer.cancelRequest();
                if (listener != null) {
                    listener.onLoadedCancel(imgContainer.getRequestUrl());
                }
            }

            if (progressView != null) {
                progressView.setVisibility(View.VISIBLE);
            }

            // clear any previous image
            imageView.setImageResource(placeHolderImageId);

            imgContainer = volleyImageLoader.get(imageUrl, new ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressView != null) {
                        progressView.setVisibility(View.GONE);
                    }
                    if (listener != null) {
                        listener.onLoadedError();
                    }
                    
//                    Log.i("TAG"," ERROR GETTING IMAGEVIEW");
                }

                @Override
                public void onResponse(ImageContainer response, boolean isImmediate) {
                    // only when the bitmap is fully downloaded do we update the
                    // imageview and fire the events
                    
                    if (null != response.getBitmap() && response.getBitmap().getWidth() != -1) {
                        if (progressView != null) {
                            progressView.setVisibility(View.GONE);
                        }

                        imageView.setImageBitmap(response.getBitmap());

                        if (listener != null) {
                            listener.onLoadedSuccess(response.getBitmap());
                        }
                        
//                        Log.i("TAG"," SETTING IMAGEVIEW "+imageView+" "+imageView.getVisibility());
                        
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }, 0, 0);
            imageView.setTag(imgContainer);

        }else{
//            Log.i("TAG"," NOT LOADING");
        }
    }

    public void cancelImageRequest(final String imageUrl, final ImageView imageView) {
        if (null != imageView) {
            ImageContainer imgContainer = (ImageContainer) imageView.getTag();
            if (null != imgContainer && imgContainer.getRequestUrl().equals(imageUrl)) {
                imgContainer.cancelRequest();
                imageView.setTag(null);
            }
        }

    }

    public void cancelImageRequests(final List<String> imageUrls) {

        // it should be more efficient to cancel using cancelAll(tag), but
        // didn't find how to set tag in the requests
        volleyRequestQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return imageUrls.contains(request.getUrl());
            }
        });

    }

    private void initVolley(Application application) {
        volleyRequestQueue = newRequestQueue(application.getApplicationContext(), null);
        volleyImageLoader = new com.android.volley.toolbox.ImageLoader(volleyRequestQueue, new BitmapLruCache());
    }

    public void preload(final String imageUrl, final RocketImageLoaderListener listener) {
        volleyImageLoader.get(imageUrl, new ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onLoadedError();
            }

            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
                listener.onLoadedSuccess(response.getBitmap());
            }
        });
    }

    /**
     * Create request queue for Volley Method copied from
     * com.android.volley.toolbox.Volley reimplemented in order to change the
     * file cache size
     * 
     * @param context
     * @param stack
     * @return
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), "volley");

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();

            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See:
                // http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir, FILE_DISC_CACHE_SIZE), network);
        queue.start();

        return queue;
    }
}