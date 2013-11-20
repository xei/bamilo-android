//package pt.rocket.view;
//
//import java.util.EnumSet;
//
//import pt.rocket.framework.event.EventType;
//import pt.rocket.framework.event.ResponseResultEvent;
//import pt.rocket.framework.utils.LogTagHelper;
//import pt.rocket.utils.MyMenuItem;
//import pt.rocket.utils.NavigationAction;
//import pt.rocket.utils.OnActivityFragmentInteraction;
//import pt.rocket.view.fragments.FragmentType;
//import pt.rocket.view.fragments.ProductDetailsDescriptionFragment;
//import android.content.Intent;
//import android.os.Bundle;
///**
// * 
// * @modified manuelsilva
// *
// */
//public class ProductDetailsDescriptionActivityFragment extends BaseActivity {
//	private final static String TAG = LogTagHelper.create( ProductDetailsDescriptionActivityFragment.class );
//
//	private ProductDetailsDescriptionFragment productDetailsDescriptionFragment;
//	
//	private OnActivityFragmentInteraction mCallbackProductDetailsDescriptionFragment;
//		
//	public ProductDetailsDescriptionActivityFragment() {
//		super(NavigationAction.Products,
//		        EnumSet.of(MyMenuItem.SHARE),
//		        EnumSet.noneOf(EventType.class),
//		        EnumSet.noneOf(EventType.class),
//		        R.string.product_details_title, R.layout.productdetailsdescription_fragment);
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setAppContentLayout();
//	}
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//    }	
//	
//	/**
//	 * Set the Products layout using inflate
//	 */
//	private void setAppContentLayout() {
//	    if(productDetailsDescriptionFragment == null){
//	        productDetailsDescriptionFragment = ProductDetailsDescriptionFragment.getInstance();
//	        startFragmentCallbacks();
//	        fragmentManagerTransition(R.id.product_details_description_container, productDetailsDescriptionFragment, false, true);
//	    }
//	}
//	
//	private void startFragmentCallbacks() {
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception
//        try {
//            mCallbackProductDetailsDescriptionFragment = (OnActivityFragmentInteraction) productDetailsDescriptionFragment;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(productDetailsDescriptionFragment.toString()
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
