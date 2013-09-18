/**
 * 
 */
package pt.rocket.app;

import pt.rocket.framework.ErrorCode;
import pt.rocket.view.R;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;

/**
 * @author nutzer2
 * 
 */
public class ImageLoaderComponent extends ApplicationComponent {

    public DisplayImageOptions largeLoaderOptions;

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.ApplicationComponent#initInternal(android.app.Application)
     */
    @Override
    protected ErrorCode initInternal(Application app) {
        largeLoaderOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.no_image_large)
                .showImageForEmptyUri(R.drawable.no_image_small)
                .cacheOnDisc()
                .cacheInMemory()
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Config.RGB_565)
                .build();

        DisplayImageOptions smallOption = new DisplayImageOptions.Builder()
                .cloneFrom(largeLoaderOptions)
                .showStubImage(R.drawable.no_image_large)
                .showImageForEmptyUri(R.drawable.no_image_small).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                app)
                .threadPoolSize(2)
                .discCacheExtraOptions(400, 200, CompressFormat.PNG, 50)
                .discCacheSize(16 * 1024 * 1024)
                .memoryCacheSize(4 * 1024 * 1024)
                .defaultDisplayImageOptions(smallOption)
                .build();
        
//        largeLoaderOptions = new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.no_image_large)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//                .bitmapConfig(Config.RGB_565)
//                .resetViewBeforeLoading()
//                .cacheOnDisc()
//                // .cacheInMemory()
//                .build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(app.getApplicationContext())
//                .threadPoolSize(2)
//                .memoryCache(new WeakMemoryCache())
//                .memoryCacheSize(4 * 1024 * 1024)
//                // 25% 240, 360
//                // 35% 208, 312
//                // 50% 160, 240
//                .memoryCacheExtraOptions(160, 240)
//                .discCacheSize(16 * 1024 * 1024)
//                .denyCacheImageMultipleSizesInMemory()
//                .defaultDisplayImageOptions(largeLoaderOptions)
//                // .enableLogging()
//                // .discCacheExtraOptions(400, 800, CompressFormat.JPEG, 100)
//                // .denyCacheImageMultipleSizesInMemory()
//                // .discCacheFileCount(20)
//                .build();
        
        ImageLoader.getInstance().init(config);
        return ErrorCode.NO_ERROR;
    }

}
