package pt.rocket.utils.imageloader;

import java.io.File;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import de.akquinet.android.androlog.Log;

public class RocketImageLoader {

    public static RocketImageLoader instance;

    private ImageLoader volleyImageLoader;

    // Indicates no value. Will be "translated" to default library-dependent
    // values, if necessary
    public static int NO_VALUE_INTEGER = -1;

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
    private boolean isVolleyRequestQueueRunning = true;
    private Context context;

    public static interface RocketImageLoaderListener {
        void onLoadedSuccess(Bitmap bitmap);

        void onLoadedError();

        void onLoadedCancel(String imageUrl);
    }

    public static void init(Application application) {
        instance = new RocketImageLoader();

        instance.initLibrary(application);
    }

    public ImageLoader getImageLoader(){
    	return volleyImageLoader;
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
        loadImage(imageUrl, imageView, false, null);
    }

    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     * @param hideView
     */
    public void loadImage(String imageUrl, ImageView imageView, boolean hideView) {
        loadImage(imageUrl, imageView, hideView, null);
    }

    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     * @param listener
     */
    public void loadImage(String imageUrl, ImageView imageView, boolean hideView, RocketImageLoaderListener listener) {
        loadImage(imageUrl, false, imageView, null, NO_VALUE_INTEGER, NO_VALUE_INTEGER, hideView, listener, false, null);
    }

    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     * @param progressView
     * @param placeHolderImageId
     * @param tag
     */
    public void loadImage(String imageUrl, ImageView imageView, View progressView, int placeHolderImageId, String tag) {
        loadImage(imageUrl, false, imageView, progressView, NO_VALUE_INTEGER, placeHolderImageId, false, null, false, tag);
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
        loadImage(imageUrl, false, imageView, progressView, NO_VALUE_INTEGER, placeHolderImageId, false, null, false, null);
    }
    
    
    
    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     * @param progressView
     * @param placeHolderImageId
     */
    public void loadImage(String imageUrl, boolean isFilePathList, ImageView imageView, View progressView, int placeHolderImageId) {
        loadImage(imageUrl, isFilePathList, imageView, progressView, NO_VALUE_INTEGER, placeHolderImageId, false, null, false, null);
    }
    
    /**
     * Convenience method
     * 
     * @param imageUrl
     * @param imageView
     * @param progressView
     * @param placeHolderImageId
     */
    public void loadImage(String imageUrl, boolean isFilePathList, ImageView imageView, View progressView, int placeHolderImageId, boolean isDraftImage) {
        loadImage(imageUrl, isFilePathList, imageView, progressView, NO_VALUE_INTEGER, placeHolderImageId, false, null,isDraftImage, null);
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
        loadImage(imageUrl, false, imageView, progressView, NO_VALUE_INTEGER, placeHolderImageId, false, listener, false, null);
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
     * @param hideImageView
     * @param listener
     */
    public void loadImage(final String imageUrl, boolean isFilePathList, final ImageView imageView, final View progressView, int targetWidth,
            final int placeHolderImageId, final boolean hideImageView, final RocketImageLoaderListener listener, boolean isDraftImage, String tag) {

        if (null != imageUrl && !imageUrl.equals("") && null != imageView) {
            if(isDraftImage) {
            	imageView.setImageBitmap(decodeSampledBitmapFromResource(imageUrl, imageView.getWidth(), imageView.getHeight()));

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

                        if (hideImageView)
                            imageView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(final ImageContainer response, boolean isImmediate) {
                        // only when the bitmap is fully downloaded do we update
                        // the
                        // imageview and fire the events

                        if (null != response.getBitmap() && response.getBitmap().getWidth() != -1) {
                            if (progressView != null) {
                                progressView.setVisibility(View.GONE);
                            }
                            
                            imageView.setImageBitmap(response.getBitmap());

                            if (listener != null) {
                                listener.onLoadedSuccess(response.getBitmap());
                            }

                            if (hideImageView)
                                imageView.setVisibility(View.VISIBLE);
                        } else {
                        	//imageView.setImageBitmap(null);
                        	imageView.setImageResource(placeHolderImageId);
                        }
                    }
                }, 0, 0, tag);
                imageView.setTag(imgContainer);
            }
        } else {
            // clear any previous image
            imageView.setImageResource(placeHolderImageId);
        }
    }

	public static Bitmap decodeSampledBitmapFromResource(String path,
			int reqWidth, int reqHeight) {

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

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		// Log.i(TAG, "code1images : width : "+width+"height : "+height);
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			// Log.i(TAG,
			// "code1images : halfWidth : "+halfWidth+"halfHeight : "+halfHeight);
			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		// Log.i(TAG, "code1images : inSampleSize : "+inSampleSize);
		return inSampleSize;
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
    
    public void stopProcessingQueue() {
        stopProcessingQueue(null);
    }

    public void stopProcessingQueue(String tag) {        
        if (isVolleyRequestQueueRunning) {
            Log.i("RocketImageLoader", " --- > STOP ProcessingQueue");
            isVolleyRequestQueueRunning = false;
            if (null != tag)
                volleyRequestQueue.cancelAll(tag);
            volleyRequestQueue.stop();
        }
    }
    
    
    public void startProcessingQueue() {
        if (!isVolleyRequestQueueRunning) {
            isVolleyRequestQueueRunning = true;
            Log.i("RocketImageLoader", " --- > START ProcessingQueue");        
            volleyRequestQueue.start();
        }
    }

    private void initVolley(Application application) {
        context = application.getApplicationContext();
        initVolley(context);
    }
    
    private void initVolley(Context appContext) {
        volleyRequestQueue = newRequestQueue(context, null);
        volleyImageLoader = new ImageLoader(volleyRequestQueue, new BitmapLruCache());
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