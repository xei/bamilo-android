/**
 * 
 */
package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class defines the activity for the the fifth and final step of the
 * checkout process.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. Written by nunocastro, 02/08/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author nunocastro
 * 
 * 
 * @date 02/08/2012
 * 
 * @description
 * 
 */
public class CheckoutStep5Activity extends BaseActivity {
	private final static String TAG = LogTagHelper.create(CheckoutStep5Activity.class);

	/**
	 * 
	 */
	public CheckoutStep5Activity() {
		super(NavigationAction.Basket,
		        EnumSet.noneOf(MyMenuItem.class),
		        EnumSet.noneOf(EventType.class),
		        EnumSet.noneOf(EventType.class),
		        0, R.layout.checkout_step5);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		findViewById(R.id.btn_checkout_continue).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						ActivitiesWorkFlow
								.homePageActivity(CheckoutStep5Activity.this);
					}
				});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		AnalyticsGoogle.get().trackPage(R.string.gcheckoutfinal);
	}

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

}
