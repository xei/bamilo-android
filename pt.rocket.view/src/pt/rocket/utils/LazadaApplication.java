package pt.rocket.utils;

import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import pt.rocket.view.R;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.RemoteService;
import pt.rocket.framework.utils.Constants;
import pt.rocket.interfaces.IResponseCallback;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author sergiopereira
 * 
 */
public class LazadaApplication extends Application {
    private IRemoteService mService;
    private static String TAG = LazadaApplication.class.getSimpleName();
    public HashMap<String, IResponseCallback> responseCallbacks;
    ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onServiceDisconnected");
            Toast.makeText(getApplicationContext(), "DISCONECTED", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            Log.i(TAG, "onServiceConnected");
            Toast.makeText(getApplicationContext(), "CONECTED", Toast.LENGTH_LONG).show();
            responseCallbacks = new HashMap<String, IResponseCallback>();
            mService = IRemoteService.Stub.asInterface(service);
            ServiceSingleton.getInstance().setResponseCallbacks(responseCallbacks);
            ServiceSingleton.getInstance().setService(mService);

        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        RestClientSingleton.init(getApplicationContext());
        responseCallbacks = new HashMap<String, IResponseCallback>();
        
        bindService(new Intent(this, RemoteService.class), mConnection, Context.BIND_AUTO_CREATE);

        DisplayImageOptions largeLoaderOptions = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.no_image_large)
        .showImageForEmptyUri(R.drawable.no_image_small)
        .cacheOnDisc()
        .cacheInMemory()
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .bitmapConfig(Config.RGB_565)
        .build();

        DisplayImageOptions smallOption = new DisplayImageOptions.Builder()
        .cloneFrom(largeLoaderOptions)
        .showStubImage(R.drawable.no_image_small)
        .showImageForEmptyUri(R.drawable.no_image_small).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .discCacheSize(16 * 1024 * 1024)
                .memoryCacheSize(4 * 1024 * 1024)
                .defaultDisplayImageOptions(smallOption)
                .enableLogging()
                .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * Handles correct responses
     * 
     * @param bundle
     */
    private void handleResponse(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (responseCallbacks.containsKey(id)) {
            responseCallbacks.get(id).onRequestComplete(bundle);
        }
        responseCallbacks.remove(id);
    }

    /**
     * Handles error responses
     * 
     * @param bundle
     */
    private void handleError(Bundle bundle) {
        String id = bundle.getString(Constants.BUNDLE_MD5_KEY);
        if (responseCallbacks.containsKey(id)) {
            responseCallbacks.get(id).onRequestError(bundle);
        }
        responseCallbacks.remove(id);
    }

    /**
     * Check if is a dev version
     * 
     * @return
     * @author sergiopereira
     */
    public static boolean isDevVersion() {

        return false;
    }

}
