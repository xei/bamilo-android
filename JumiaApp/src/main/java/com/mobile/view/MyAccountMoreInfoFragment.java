package com.mobile.view;

import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.fragments.BaseFragment;

import java.util.EnumSet;

/**
 * Created by rsoares on 10/13/15.
 */
public class MyAccountMoreInfoFragment extends BaseFragment{

    private static final String TAG = MyAccountMoreInfoFragment.class.getSimpleName();

    /**
     * Get instance
     * @return MyAddressesFragment
     */
    public static MyAccountMoreInfoFragment newInstance() {
        return new MyAccountMoreInfoFragment();
    }

    public MyAccountMoreInfoFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.layout.my_account_more_info,
                R.string.account_name,
                KeyboardState.ADJUST_CONTENT);
    }


}
