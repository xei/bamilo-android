/*
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.session.EmailCheckHelper;
import com.mobile.helpers.session.LoginAutoHelper;
import com.mobile.helpers.session.LoginFacebookHelper;
import com.mobile.helpers.session.LoginGuestHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.configs.AuthInfo;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerEmailCheck;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.LoginHeaderComponent;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.view.R;
import com.mobile.view.newfragments.NewBaseFragment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.mobile.view.fragments.BaseFragment.ADJUST_CONTENT;

*/
/**
 * Class used to perform login via Facebook,
 * @author sergiopereira
 *//*

public class SessionLoginNewMainFragment extends NewBaseFragment implements IResponseCallback {

    private static final String TAG = SessionLoginNewMainFragment.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;

    */
/**
     * Empty constructor
     *//*

    public SessionLoginNewMainFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.new_session_fragment,
                R.string.register_title,
                ADJUST_CONTENT);
    }

    */
/*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     *//*

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    */
/*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     *//*


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        Bundle arguments = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            // Force load form if comes from deep link
            mParentFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE);
            mNextStepFromParent = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
            isInCheckoutProcess = arguments.getBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API);
        }
        // Show checkout tab layout
        if (isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT) {
            checkoutStep = ConstantsCheckout.CHECKOUT_ABOUT_YOU;
        }

    }
    private boolean setGuestButton(boolean checkout) {
        if(checkout) {
            return true;
        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        Bundle t = new Bundle();
        t.putBoolean("istrue",setGuestButton( isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT));
        ViewPagerAdapter adapter = new ViewPagerAdapter(getBaseActivity().getSupportFragmentManager());
        LoginNewMainFragment n = new LoginNewMainFragment();
        n.setArguments(t);
        adapter.addFragment(n, getBaseActivity().getResources().getString(R.string.login_label));
        adapter.addFragment(new SessionRegisterFragment(), getBaseActivity().getResources().getString(R.string.create_label));
        viewPager.setAdapter(adapter);
    }



    */
/*
         * (non-Javadoc)
         *
         * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
         * android.os.Bundle)
         *//*

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        Print.i(TAG, "ON VIEW CREATED");
    }

    */
/*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     *//*

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    */
/*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     *//*

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");

    }

    */
/*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     *//*


    */
/*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     *//*

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    */
/*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     *//*

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    getBaseActivity().hideKeyboard();
    }

    */
/*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     *//*

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    */
/*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     *//*

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");

    }

    */
/*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     *//*

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

    }
}
*/
