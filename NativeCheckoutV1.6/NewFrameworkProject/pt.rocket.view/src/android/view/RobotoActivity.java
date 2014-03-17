/**
 * @author Michael Kroez/Smart Mobile Factory
 * 
 * @version 1.00
 * 
 * 2013/02/26
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */

package android.view;

import com.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * Activity class responsible for creating a custom layout inflater
 * 
 * @author Michael Kroez
 * 
 */
public class RobotoActivity extends SlidingFragmentActivity {

	/**
	 * Creates the custom layout inflater
	 * 
	 * @return the inflater
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#getLayoutInflater()
	 */
	@Override
	public LayoutInflater getLayoutInflater() {
		return new MyLayoutInflater(this, getOrigInflater());
	}

	/**
	 * Creates the custom layout inflater if requested via system service
	 * 
	 * @return the inflater if requested by name, otherwise the system service
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#getSystemService(java.lang.String)
	 */
	@Override
	public Object getSystemService(String name) {
		if (name.equals(LAYOUT_INFLATER_SERVICE)) {
			return new MyLayoutInflater(this, getOrigInflater());
		}

		return super.getSystemService(name);
	}

	/**
	 * Creates the original layout inflater
	 * 
	 * @return the original layout inflater
	 */
	private LayoutInflater getOrigInflater() {
		return (LayoutInflater) super.getSystemService(LAYOUT_INFLATER_SERVICE);

	}

}
