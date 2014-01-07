package pt.rocket.utils;

import java.util.EnumSet;

import com.bugsense.trace.ExceptionCallback;

import pt.rocket.app.ApplicationComponent;
import pt.rocket.app.DarwinComponent;
import pt.rocket.app.ImageLoaderComponent;
import pt.rocket.app.UrbanAirshipComponent;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.IMetaData;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseErrorEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.events.InitializeEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.SingletonMap;
import android.app.Application;
import de.akquinet.android.androlog.Log;

public class JumiaApplication extends Application implements ResponseListener, ExceptionCallback {

    private static final String TAG = JumiaApplication.class.getSimpleName();

    public static JumiaApplication INSTANCE;

    public static final SingletonMap<ApplicationComponent> COMPONENTS =
            new SingletonMap<ApplicationComponent>(new UrbanAirshipComponent(),
                    new ImageLoaderComponent(), new DarwinComponent());
            
    private ResponseListener initListener;

    private ResponseEvent lastInitEvent;

    private boolean isInitializing = false;

    @Override
    public void onCreate() {
        Log.init(getApplicationContext());
        INSTANCE = this;
        
        EventManager.getSingleton().addResponseListener(EventType.INITIALIZE, this);
        init(false);
    }

    public synchronized void init(boolean isReInit) {
        isInitializing = true;
        Log.d(TAG, "Starting initialisation phase");
        AnalyticsGoogle.clearCheckoutStarted();
        for (ApplicationComponent component : COMPONENTS.values()) {
            ErrorCode result = component.init(this);
            if (result != ErrorCode.NO_ERROR) {
                handleEvent(new ResponseErrorEvent(new RequestEvent(EventType.INITIALIZE),
                        ErrorCode.REQUIRES_USER_INTERACTION));
                return;
            }
        }
        CheckVersion.clearDialogSeenInLaunch(getApplicationContext());
        InitializeEvent event = new InitializeEvent(EnumSet.noneOf(EventType.class));
        if ( isReInit ) {
            event.metaData.putBoolean( IMetaData.MD_IGNORE_CACHE, true);
        }
        EventManager.getSingleton().triggerRequestEvent( event );
        CheckVersion.init(getApplicationContext());
    }

    public synchronized void waitForInitResult(ResponseListener listener, boolean isReInit) {
        if (lastInitEvent != null) {
            Log.d(TAG, "Informing listener about last init event " + lastInitEvent);
            listener.handleEvent(lastInitEvent);
            lastInitEvent = null;
            return;
        } else {
            this.initListener = listener;
            if (!isInitializing) {
                init(isReInit);
            }
        }
    }
    
    public synchronized void clearInitListener() {
        initListener = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
     */
    @Override
    public synchronized void handleEvent(ResponseEvent event) {
        isInitializing = false;
        Log.d(TAG, "Handle initialization result: " + event);
        if (initListener != null) {
            Log.d(TAG, "Informing initialisation listener: " + initListener);
            initListener.handleEvent(event);
        } else {
            this.lastInitEvent = event;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#removeAfterHandlingEvent()
     */
    @Override
    public boolean removeAfterHandlingEvent() {
        return false;
    }

    @Override
    public String getMD5Hash() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void lastBreath(Exception arg0) {
              
    }

}
