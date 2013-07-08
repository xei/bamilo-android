package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.ProductsFragment;
import android.content.Intent;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class shows all products for a respective category.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. Written by sergiopereira, 19/06/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author sergiopereira
 * @modified Manuel Silva
 * 
 * @date 19/06/2012
 * 
 * @description
 * 
 */
public class ProductsActivityFragment extends BaseActivity  {
    
	private final static String TAG = LogTagHelper.create(ProductsActivityFragment.class);
	
	public ProductsActivityFragment() {
		super(NavigationAction.Products,
		        EnumSet.of(MyMenuItem.SEARCH),
		        EnumSet.noneOf(EventType.class),
		        EnumSet.noneOf(EventType.class),
		        0, R.layout.products_frame);
	}

	// boolean showSpinner = true;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "ON CREATE");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "ON RESUME");		
		AnalyticsGoogle.get().trackPage( R.string.gproductlist );
	}

	/**
	 * Clean all event listeners
	 */
	@Override
	public void onPause() {
		super.onPause();
		
		getIntent().getExtras().clear();
	}



    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        return super.onErrorEvent(event);
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
    }
}
