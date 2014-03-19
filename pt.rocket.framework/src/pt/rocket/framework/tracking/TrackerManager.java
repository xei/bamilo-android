//package pt.rocket.framework.tracking;
//
//import pt.rocket.framework.R;
//import android.content.Context;
//
//import com.google.analytics.tracking.android.Fields;
//import com.google.analytics.tracking.android.GAServiceManager;
//import com.google.analytics.tracking.android.GoogleAnalytics;
//import com.google.analytics.tracking.android.Logger.LogLevel;
//import com.google.analytics.tracking.android.MapBuilder;
//import com.google.analytics.tracking.android.StandardExceptionParser;
//import com.google.analytics.tracking.android.Tracker;
//
//import de.akquinet.android.androlog.Log;
//
///**
// * Takes care of the initlizaing of the GA tracking
// * 
// * @author josedourado
// * 
// */
//public abstract class TrackerManager {
//
//    private static String TAG=TrackerManager.class.getSimpleName();
//    private static GoogleAnalytics mga;
//    private static Tracker tracker;
//    private static Context mContext;
//
//    @SuppressWarnings("deprecation")
//    public static void googleTrackingInit(Context context) {
//        Log.i(TAG,"Initializing GA");
//        if (mga == null) {
//            mga = GoogleAnalytics.getInstance(context);
//        }
//        if (tracker == null) {
//            tracker = mga.getTracker(context.getResources().getString(R.string.ga_trackingId));
//        }
//        GAServiceManager.getInstance().setLocalDispatchPeriod(context.getResources().getInteger(R.integer.ga_dispatchperiod));
//        if (context.getResources().getBoolean(R.bool.ga_logenabled)) {
//            mga.getLogger().setLogLevel(LogLevel.VERBOSE);
//        }
//
//        mContext = context;
//
//    }
//
//    /**
//     * Tracks all types of event and send them to the queue
//     * 
//     * @param eventCategory
//     * @param eventAction
//     * @param eventLabel
//     * @param eventValue
//     */
//    public void sendTrackingEvent(String eventCategory, String eventAction, String eventLabel, Long eventValue) {
//        if (tracker != null) {
//            tracker.send(MapBuilder.createEvent(eventCategory, // Event category
//                    eventAction, // Event action (required)
//                    eventLabel, // Event label
//                    eventValue) // Event value
//                    .build());
//        }
//
//    }
//
//    /**
//     * Receives a throwable and builds and exception to be tracked in ga
//     * 
//     * @param error
//     */
//    public static void sendTrackingError(Exception error) {
//        if (tracker != null) {
//            Log.i(TAG,"Error tracking activated");
//            tracker.send(MapBuilder.createException(new StandardExceptionParser(mContext, null).getDescription(Thread.currentThread().getName(), error), false)
//                    .build());
//        }
//    }
//
//    public static void sendTrackingActivity(String activityName) {
//        if (tracker != null) {
//            tracker.set(Fields.SCREEN_NAME, activityName);
//            tracker.send(MapBuilder.createAppView().build());
//        }
//    }
//
//    /**
//     * Returns the ga tracker
//     * 
//     * @return
//     */
//    public static Tracker getGoogleAnalyticsTracker() {
//        return tracker;
//    }
//
//    public static GoogleAnalytics getGAInstance() {
//        return mga;
//    }
//
//}
