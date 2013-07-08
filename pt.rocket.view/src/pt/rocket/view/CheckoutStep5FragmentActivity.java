/**
 * 
 */
package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class CheckoutStep5FragmentActivity extends BaseActivity {
    
	private final static String TAG = LogTagHelper.create(CheckoutStep5FragmentActivity.class);

	/**
	 * 
	 */
	public CheckoutStep5FragmentActivity() {
		super(NavigationAction.Basket,
		        EnumSet.noneOf(MyMenuItem.class),
		        EnumSet.noneOf(EventType.class),
		        EnumSet.noneOf(EventType.class),
		        0, R.layout.checkout_step5_fragment);
	}

	/*
	 * (non-Javadoc)
	 * @see pt.rocket.utils.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
	}
	
	/*
	 * (non-Javadoc)
	 * @see pt.rocket.utils.BaseActivity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.utils.BaseActivity#onSwitchFragment(pt.rocket.view.fragments.FragmentType, java.lang.Boolean)
     */
    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {   
    }

}
