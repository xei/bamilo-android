package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.FlurryTracker;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.view.fragments.CheckoutWebFragment;
import pt.rocket.view.fragments.FragmentType;
import android.os.Bundle;
import android.view.View.OnClickListener;

/**
 * 
 * @author Ralph Holland-Moritz
 * @modified Manuel Silva
 * 
 */
public class CheckoutWebActivityFragment extends BaseActivity {

    private static final String TAG = LogTagHelper.create(CheckoutWebActivityFragment.class);
    
    private OnActivityFragmentInteraction mCallbackCheckoutWebActivityFragment;
    
    private CheckoutWebFragment checkoutWebFragment; 
    
    public CheckoutWebActivityFragment() {
        super(NavigationAction.Basket,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class),
                0,
                R.layout.checkoutweb_fragment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoading();
        
        checkoutWebFragment = CheckoutWebFragment.getInstance();
        startFragmentCallbacks();
        fragmentManagerTransition(R.id.rocket_app_checkoutweb_container, checkoutWebFragment, false, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryTracker.get().begin(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsGoogle.get().trackPage(R.string.gcheckoutbegin);
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryTracker.get().end(this);
    }

    private void startFragmentCallbacks() {
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackCheckoutWebActivityFragment = (OnActivityFragmentInteraction) checkoutWebFragment;

        } catch (ClassCastException e) {
            throw new ClassCastException(checkoutWebFragment.toString()
                    + " must implement OnActivityFragmentInteraction");
        }

    }
    
    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

    public void onBackPressed() {
        if (!mCallbackCheckoutWebActivityFragment.allowBackPressed()) {
            super.onBackPressed();
        }
    }
    
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void sendClickListenerToActivity(OnClickListener clickListener) {
        // TODO Auto-generated method stub
        showError(clickListener);
        
    }
}
