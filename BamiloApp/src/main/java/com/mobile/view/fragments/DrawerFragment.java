package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobile.app.BamiloApplication;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.objects.home.type.TeaserGroupType;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Class used to show the cart info and a navigation container, menu or categories
 * @author sergiopereira
 */
public class DrawerFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = DrawerFragment.class.getSimpleName();

    private FragmentType mSavedStateType;
    private RecyclerView mDrawerRecycler;

    /**
     * Constructor via bundle
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static DrawerFragment newInstance() {
        return new DrawerFragment();
    }

    /**
     * Empty constructor
     */
    public DrawerFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.drawer_fragment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        mSavedStateType = savedInstanceState != null ? (FragmentType) savedInstanceState.getSerializable(TAG) : null;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

        mDrawerRecycler = (RecyclerView) view.findViewById(R.id.drawer_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseActivity());
        mDrawerRecycler.setLayoutManager(layoutManager);

       CreateDrawer();

        if (mSavedStateType == null) {
            Print.d(TAG, "SAVED IS NULL");
            //addListItems();
        }

    }

    public void CreateDrawer() {
        if (mDrawerRecycler == null) return;
        ArrayList<DrawerItem> items = new ArrayList<>();
        if (BamiloApplication.isCustomerLoggedIn()) {
            String gender = BamiloApplication.CUSTOMER.getGender();
            items.add(new DrawerItem("سلام " + BamiloApplication.CUSTOMER.getFirstName(), BamiloApplication.CUSTOMER.getEmail(), gender, null));

        } else {
            items.add(new DrawerItem("خوش آمدید", "ورود/ثبت نام", "male", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!BamiloApplication.isCustomerLoggedIn()) {
                        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, null, true);
                        getBaseActivity().closeNavigationDrawer();
                    }
                }
            }));


        }

        int cartItems = 0;
        PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
        if (cart != null && cart.getCartCount() != 0) {
            cartItems = cart.getCartCount();
        }

        items.add(new DrawerItem(R.drawable.drawer_home, R.string.drawer_home, false, 0, R.color.drawer_orange, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();
                getBaseActivity().onSwitchFragment(FragmentType.HOME, null, true);
            }
        }));
        items.add(new DrawerItem(R.drawable.drawer_categories, R.string.drawer_categories, false, 0, R.color.drawer_orange, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                getBaseActivity().onSwitchFragment(FragmentType.CATEGORIES, null, true);
            }
        }));
        items.add(new DrawerItem(true));
        items.add(new DrawerItem(R.drawable.drawer_order_tracking, R.string.drawer_order_tracking, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                getBaseActivity().onSwitchFragment(FragmentType.MY_ORDERS, null, true);
            }
        }));
        items.add(new DrawerItem(true));
        items.add(new DrawerItem(R.drawable.drawer_wishlist, R.string.drawer_wishlist, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                getBaseActivity().onSwitchFragment(FragmentType.WISH_LIST, null, true);
            }
        }));
        items.add(new DrawerItem(R.drawable.drawer_recently_viewed, R.string.drawer_recently_viewed, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                getBaseActivity().onSwitchFragment(FragmentType.RECENTLY_VIEWED_LIST, null, true);
            }
        }));
        items.add(new DrawerItem(R.drawable.drawer_cart, R.string.drawer_cart, true, cartItems, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, null, true);
            }
        }));
        items.add(new DrawerItem(R.drawable.drawer_profile, R.string.drawer_myprofile, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT, null, true);
            }
        }));
        items.add(new DrawerItem(true));
        items.add(new DrawerItem(0, R.string.drawer_contactus, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                Intent callintent = new Intent(Intent.ACTION_DIAL);
                callintent.setData(Uri.parse(getResources().getString(R.string.call_center_number)));
                startActivity(callintent);
            }
        }));
        items.add(new DrawerItem(0, R.string.drawer_emailus, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                UIUtils.emailToCS(getBaseActivity());
            }
        }));
        items.add(new DrawerItem(0, R.string.drawer_bug_report, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                UIUtils.emailBugs(getBaseActivity());
            }
        }));
        items.add(new DrawerItem(0, R.string.drawer_faq, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                @TargetLink.Type String link = TargetLink.SHOP_IN_SHOP.concat("::help-android");
                new TargetLink(getWeakBaseActivity(), link)
                        .addTitle(R.string.faq)
                        .setOrigin(TeaserGroupType.MAIN_TEASERS)
                        //.addAppendListener(this)
                        //.addCampaignListener(this)
                        .retainBackStackEntries()
                        .enableWarningErrorMessage()
                        .run();
            }
        }));
        items.add(new DrawerItem(true));
        items.add(new DrawerItem(0, R.string.drawer_share, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                UIUtils.shareApp(getBaseActivity());
            }
        }));
        items.add(new DrawerItem(0, R.string.drawer_rateus, false, 0, R.color.drawer_defaultcolor, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().closeNavigationDrawer();

                UIUtils.rateApp(getBaseActivity());
            }
        }));




        DrawerFragmentAdapter adapter = new DrawerFragmentAdapter(getBaseActivity(), items);
        mDrawerRecycler.setAdapter(adapter);
    }

    private void addListItems() {
        Print.i(TAG,"ADD LIST ITEMS");
        onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL, new Bundle());
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }
    
    /**
     * ########### LAYOUT ###########  
     */

    /**
     * Method used to switch between the filter fragments
     * @author sergiopereira
     */
    public void onSwitchChildFragment(FragmentType filterType, Bundle bundle) {
        Print.i(TAG, "ON SWITCH CHILD FRAG: " + filterType);
        switch (filterType) {
        case NAVIGATION_CATEGORIES_ROOT_LEVEL:
            DrawerFragment navigationCategoryFragment = new DrawerFragment();//.getInstance(bundle);
            fragmentChildManagerTransition(R.id.navigation_container_list, filterType, navigationCategoryFragment, false, true);
            break;
        default:
            Print.w(TAG, "ON SWITCH FILTER: UNKNOWN TYPE");
            break;
        }
    }
    
    /**
     * Method used to associate the container and fragment.
     */
    public void fragmentChildManagerTransition(int container, FragmentType filterType, Fragment fragment, final boolean animated, boolean addToBackStack) {
        final FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Fragment tag
        String tag = filterType != null ? filterType.toString() : null;

        /**
         * FIXME: Excluded piece of code due to crash on API = 18.
         * Temporary fix - https://code.google.com/p/android/issues/detail?id=185457
         */
        DeviceInfoHelper.executeCodeExcludingJellyBeanMr2Version(new Runnable() {
            @Override
            public void run() {
                // Animations
                if (animated)
                    fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        // Replace
        fragmentTransaction.replace(container, fragment, tag);
        // Back stack
        if (addToBackStack)
            fragmentTransaction.addToBackStack(tag);
        // Commit
        // fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }


}
