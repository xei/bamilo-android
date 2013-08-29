package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.ShoppingCartFragment;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 * @author sergiopereira
 *
 */
public class ShoppingCartFragmentActivity extends BaseActivity {
    
	protected final static String TAG = LogTagHelper.create(ShoppingCartFragmentActivity.class);
	
    public ShoppingCartFragmentActivity() {        
        super(NavigationAction.Basket,
                EnumSet.of(MyMenuItem.SEARCH),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                R.string.shoppingcart_title, R.layout.shopping_cart_fragments);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onDestroy() {
		super.onDestroy();
		unbindDrawables(findViewById(R.id.main_basket_container));
		System.gc();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see pt.rocket.utils.MyActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == maskRequestCodeId(R.id.request_login) && resultCode == Activity.RESULT_OK) {
            ((ShoppingCartFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_shopping_cart)).goToCheckout();
        }
    }
//Version
    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleedEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
    }

}
