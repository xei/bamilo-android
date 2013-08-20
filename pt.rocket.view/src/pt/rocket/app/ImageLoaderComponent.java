/**
 * 
 */
package pt.rocket.app;

import pt.rocket.framework.ErrorCode;
import pt.rocket.view.R;

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
                .discCacheExtraOptions(400, 200, CompressFormat.JPEG, 50)
                .discCacheSize(16 * 1024 * 1024)
                .memoryCacheSize(4 * 1024 * 1024)
                .defaultDisplayImageOptions(smallOption)
                .build();
        ImageLoader.getInstance().init(config);
        return ErrorCode.NO_ERROR;
    }

}
