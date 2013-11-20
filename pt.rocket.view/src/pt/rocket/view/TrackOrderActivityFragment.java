//package pt.rocket.view;
//
//import java.util.EnumSet;
//
//import pt.rocket.framework.event.EventType;
//import pt.rocket.framework.event.ResponseResultEvent;
//import pt.rocket.utils.MyMenuItem;
//import pt.rocket.utils.NavigationAction;
//import pt.rocket.utils.OnActivityFragmentInteraction;
//import pt.rocket.view.fragments.FragmentType;
//import pt.rocket.view.fragments.TermsFragment;
//import pt.rocket.view.fragments.TrackOrderFragment;
//import android.os.Bundle;
//
///**
// * @project WhiteLabelRocket
// * 
// * @version 1.01
// * 
// * @author manuelsilva
// *  
// * @description Allows the user to track their orders.
// * 
// */
//
//public class TrackOrderActivityFragment extends BaseActivity {
//
//    private TrackOrderFragment mTrackOrderFragment;
//    private OnActivityFragmentInteraction mCallbackTrackOrderActivityFragment;
//    
//	public TrackOrderActivityFragment() {
//		super(NavigationAction.TrackOrder,
//		        EnumSet.of(MyMenuItem.SEARCH),
//		        EnumSet.noneOf(EventType.class),
//		        EnumSet.noneOf(EventType.class),
//		        0, R.layout.track_order_frame);
//	}
//    
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//    
//    @Override
//    public void onResume() {
//        super.onResume();
//        setTitle(R.string.order_status);
//        setAppContentLayout();
//    }
//    
//    /**
//     * Inflate the current activity layout into the main layout template
//     */
//    private void setAppContentLayout() {
//        if (mTrackOrderFragment == null) {
//            mTrackOrderFragment = TrackOrderFragment.getInstance();
//            startFragmentCallbacks();
//            fragmentManagerTransition(R.id.track_order_container, mTrackOrderFragment, false, true);
//        }
//        
//    }
//
//    private void startFragmentCallbacks() {
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception
//        try {
//            mCallbackTrackOrderActivityFragment = (OnActivityFragmentInteraction) mTrackOrderFragment;
//
//        } catch (ClassCastException e) {
//            throw new ClassCastException(mTrackOrderFragment.toString()
//                    + " must implement OnActivityFragmentInteraction");
//        }
//
//    }
//    
//    /* (non-Javadoc)
//     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
//     */
//    @Override
//    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
//        return true;
//    }
//
//    @Override
//    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
//        // TODO Auto-generated method stub
//        
//    }
//}