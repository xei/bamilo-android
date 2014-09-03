///**
// * 
// */
//package pt.rocket.app;
//
//import pt.rocket.framework.ErrorCode;
//import pt.rocket.framework.utils.ImageResolutionHelper;
//import pt.rocket.framework.utils.WindowHelper;
//import pt.rocket.view.R;
//import android.app.Application;
//import android.content.Context;
//import android.graphics.Bitmap.Config;
//import de.akquinet.android.androlog.Log;
//
//import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.utils.L;
//
///**
// * @author nutzer2
// * @modified sergiopereira
// * 
// * @documentation http://www.intexsoft.com/blog/item/68-universal-image-loader-part-1.html
// */
//public class ImageLoaderComponent extends ApplicationComponent {
//
//    private static final String TAG = ImageLoaderComponent.class.getSimpleName();
//
//    public DisplayImageOptions largeLoaderOptions;
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.ApplicationComponent#initInternal(android.app.Application)
//     */
//    @Override
//    protected ErrorCode initInternal(Application app) {
//        Log.i(TAG, "INIT INTERNAL");
//        //
//        int classification = ImageResolutionHelper.init(app);
//        //
//        switch (classification) {
//        case ImageResolutionHelper.DEVICE_WEAK_RESOURCES:
//            activateWeakDeviceMode(app);
//            break;
//        case ImageResolutionHelper.DEVICE_NORMAL_RESOURCES:
//            activateNormalDeviceMode(app);
//            break;
//        case ImageResolutionHelper.DEVICE_HIGH_RESOURCES:
//            activateHighDeviceMode(app);
//            break;
//        default:
//            activateNormalDeviceMode(app);
//            break;
//        }
//
//        return ErrorCode.NO_ERROR;
//    }
//
//    /**
//     * 
//     * FIXME - OutOfMemoryError (BOSTON DEVICE)
//     * 
//     * @param context
//     */
//    private void activateWeakDeviceMode(Context context) {
//        Log.d(TAG, "ACTIVATE WEAK DEVICE MODE");
//
//        largeLoaderOptions = new DisplayImageOptions.Builder()
//                .showImageOnFail(R.drawable.no_image_large)
//                .showImageOnLoading(R.drawable.no_image_large)
//                .showImageForEmptyUri(R.drawable.no_image_small)
//                .cacheOnDisc(true)
//                .cacheInMemory(false)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//                .bitmapConfig(Config.RGB_565)
//                .resetViewBeforeLoading(true)
//                .build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                .threadPoolSize(2)
//                .memoryCache(new WeakMemoryCache())
//                .memoryCacheSize(4 * 1024 * 1024)
//                .denyCacheImageMultipleSizesInMemory()
//                // 25% 240, 360
//                // 35% 208, 312
//                // 50% 160, 240
//                .memoryCacheExtraOptions((int) (WindowHelper.getWidth(context) * 0.60),
//                        (int) (WindowHelper.getHeight(context) * 0.60))
//                .discCacheSize(16 * 1024 * 1024)
//                .denyCacheImageMultipleSizesInMemory()
//                .defaultDisplayImageOptions(largeLoaderOptions)
//                .build();
//        ImageLoader.getInstance().init(config);
////        L.disableLogging();
//    }
//
//    /**
//     * 
//     * @param context
//     */
//    private void activateNormalDeviceMode(Context context) {
//        Log.d(TAG, "ACTIVATE NORMAL DEVICE MODE");
//
//        largeLoaderOptions = new DisplayImageOptions.Builder()
//                .showImageOnFail(R.drawable.no_image_large)
//                .showImageOnLoading(R.drawable.no_image_large)
//                .showImageForEmptyUri(R.drawable.no_image_small)
//                .cacheOnDisc(true)
//                .cacheInMemory(true)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//                .bitmapConfig(Config.ARGB_8888)
//                .build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                .discCacheSize(16 * 1024 * 1024)
//                .memoryCacheSize(4 * 1024 * 1024)
//                .memoryCacheExtraOptions((int) (WindowHelper.getWidth(context) * 0.45),
//                        (int) (WindowHelper.getHeight(context) * 0.45))
//                .denyCacheImageMultipleSizesInMemory()
//                .defaultDisplayImageOptions(largeLoaderOptions)
//                .build();
//
//        ImageLoader.getInstance().init(config);
//    }
//
//    /**
//     * 
//     * @param context
//     */
//    private void activateHighDeviceMode(Context context) {
//        Log.d(TAG, "ACTIVATE HIGH DEVICE MODE");
//
//        largeLoaderOptions = new DisplayImageOptions.Builder()
//                .showImageOnFail(R.drawable.no_image_large)
//                .showImageOnLoading(R.drawable.no_image_large)
//                .showImageForEmptyUri(R.drawable.no_image_small)
//                .cacheOnDisc(true)
//                .cacheInMemory(true)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .bitmapConfig(Config.ARGB_8888)
//                .build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                .discCacheSize(16 * 1024 * 1024)
//                .memoryCacheSize(8 * 1024 * 1024)
//                .memoryCacheExtraOptions((int) (WindowHelper.getWidth(context) * 0.90),
//                        (int) (WindowHelper.getHeight(context) * 0.90))
//                .denyCacheImageMultipleSizesInMemory()
//                .defaultDisplayImageOptions(largeLoaderOptions)
//                .build();
//
//        ImageLoader.getInstance().init(config);
//    }
//
//}
