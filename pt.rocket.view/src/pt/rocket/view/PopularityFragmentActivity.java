//package pt.rocket.view;
//
//import java.util.EnumSet;
//
//import pt.rocket.controllers.ActivitiesWorkFlow;
//import pt.rocket.framework.event.EventType;
//import pt.rocket.framework.event.ResponseResultEvent;
//import pt.rocket.framework.utils.AnalyticsGoogle;
//import pt.rocket.framework.utils.LogTagHelper;
//import pt.rocket.utils.MyMenuItem;
//import pt.rocket.utils.NavigationAction;
//import pt.rocket.view.fragments.FragmentType;
//import android.content.Intent;
//import android.os.Bundle;
//import de.akquinet.android.androlog.Log;
//
///**
// * 
// * @author sergiopereira
// * 
// */
//public class PopularityFragmentActivity extends BaseActivity {
//
//    protected final static String TAG = LogTagHelper.create(PopularityFragmentActivity.class);
//
//    /**
//	 * 
//	 */
//    public PopularityFragmentActivity() {
//        super(NavigationAction.Products,
//                EnumSet.of(MyMenuItem.SHARE),
//                EnumSet.noneOf(EventType.class),
//                EnumSet.noneOf(EventType.class),
//                R.string.reviews, R.layout.popularity_fragments);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#onStart()
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#onResume()
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        AnalyticsGoogle.get().trackPage(R.string.gproductreviews);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#onStop()
//     */
//    @Override
//    public void onStop() {
//        super.onStop();
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#onSwitchFragment(pt.rocket.view.fragments.FragmentType,
//     * java.lang.Boolean)
//     */
//    @Override
//    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent)
//     */
//    @Override
//    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
//        Log.d(TAG, "ON SUCCESS EVENT");
//        return false;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == maskRequestCodeId(R.id.request_login) && resultCode == RESULT_OK) {
//            writeReview();
//        }
//    }
//
//    /**
//     * This method is invoked when the user wants to create a review.
//     */
//    private void writeReview() {
//        ActivitiesWorkFlow.writeReviewActivity(this);
//    }
//
//}
