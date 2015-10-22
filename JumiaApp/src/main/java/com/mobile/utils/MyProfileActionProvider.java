package com.mobile.utils;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.widget.DismissibleSpinner;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * ActionProvider to present a Menu on ActionBar.<br>
 * Used on main_menu.xml.
 *
 * @author Andre Lopes
 * @modified spereira
 */
public class MyProfileActionProvider extends ActionProvider {

    public final static String TAG = MyProfileActionProvider.class.getSimpleName();
    private List<NavigationAction> mSubMenuItems;
    private DismissibleSpinner mSpinner;
    private MyProfileAdapter mAdapter;
    private OnClickListener mAdapterOnClickListener;
    private View mIcon;

    /**
     * Constructor
     */
    public MyProfileActionProvider(Context context) {
        super(context);
        // Create overflow list
        getDropdownList();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.view.ActionProvider#onCreateActionView(android.view.MenuItem)
     */
    @Override
    public View onCreateActionView(MenuItem forItem) {
        return onCreateActionView();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.view.ActionProvider#onCreateActionView()
     */
    @Override
    public View onCreateActionView() {
        Log.i(TAG, "ON CREATE ACTION VIEW");
        //
        View spinnerContainer = LayoutInflater.from(getContext()).inflate(R.layout.action_bar_myprofile_layout, null);
        mSpinner = (DismissibleSpinner) spinnerContainer.findViewById(R.id.spinner_myprofile);
        // Case in Bamilo and API 17 set spinner as gone
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1 && ShopSelector.isRtlShop())
            mSpinner.setVisibility(View.GONE);

        mIcon = spinnerContainer.findViewById(R.id.image_myprofile);
        mIcon.setTag(R.id.nav_action, NavigationAction.MyProfile);
        // Validate listener
        if (mAdapterOnClickListener != null) {
            mIcon.setOnClickListener(mAdapterOnClickListener);
            mAdapter = new MyProfileAdapter(getContext(), R.layout.action_bar_menu_item_layout, getDropdownList(), mAdapterOnClickListener);
        } else {
            mAdapter = new MyProfileAdapter(getContext(), R.layout.action_bar_menu_item_layout, getDropdownList());
        }
        mSpinner.setAdapter(mAdapter);
        // Return view
        return spinnerContainer;
    }

    /**
     * method that creates the overflow menu list, validating it the app is shop or bamilo
     */
    private List<NavigationAction> getDropdownList() {
        Log.i(TAG, "ON CREATE DROP DOWN LIST");
        // Validate state
        if (CollectionUtils.isEmpty(mSubMenuItems)) {
            mSubMenuItems = new ArrayList<>();
            mSubMenuItems.add(NavigationAction.Home);
            mSubMenuItems.add(NavigationAction.LoginOut);
            mSubMenuItems.add(NavigationAction.Saved);
            mSubMenuItems.add(NavigationAction.MyAccount);
            mSubMenuItems.add(NavigationAction.RecentSearches);
            mSubMenuItems.add(NavigationAction.RecentlyViewed);
            mSubMenuItems.add(NavigationAction.MyOrders);
        }
        return mSubMenuItems;
    }

    /**
     * Update <code>OnClickListener</code> to be used on <code>MyProfileAdapter</code><br>
     * set such <code>OnClickListener</code> if <code>MyProfileAdapter</code> is already set
     */
    public void setAdapterOnClickListener(OnClickListener listener) {
        mAdapterOnClickListener = listener;
        if (mIcon != null) {
            mIcon.setOnClickListener(mAdapterOnClickListener);
        }
        if (mAdapter != null) {
            mAdapter.setCustomOnClickListener(mAdapterOnClickListener);
        }
    }

    /**
     * Show spinner dropdown
     */
    public void showSpinner() {
        mSpinner.performClick();
    }

    /**
     * Change selection on Spinner to force a dismiss
     */
    public void dismissSpinner() {
        Print.d(TAG, "dismissSpinner");
        mSpinner.dismiss();
    }

    public void setFragmentNavigationAction(NavigationAction action) {
        // Get current list
        ArrayList<NavigationAction> list = (ArrayList<NavigationAction>) getDropdownList();
        // Case Home or Cart
        if (action == NavigationAction.Home || action == NavigationAction.Basket || action == NavigationAction.Saved) {
            // Remove home from array
            if(NavigationAction.Saved == list.get(2)) list.remove(2);
            if(NavigationAction.Home == list.get(0)) list.remove(0);
        }
        // Case others
        else if (NavigationAction.Home != list.get(0)) {
            list.add(0, NavigationAction.Home);
            list.add(2, NavigationAction.Saved);
        }

    }

    /**
     * Adapter to be used on Spinner to manage the options on MyProfile menu
     */
    class MyProfileAdapter extends ArrayAdapter<NavigationAction> implements SpinnerAdapter {

        OnClickListener mOnClickListener;

        public MyProfileAdapter(Context context, int textViewResourceId, List<NavigationAction> itemsNavigationActions) {
            super(context, textViewResourceId, itemsNavigationActions);
        }

        public MyProfileAdapter(Context context, int textViewResourceId, List<NavigationAction> itemsNavigationActions, OnClickListener onClickListener) {
            this(context, textViewResourceId, itemsNavigationActions);
            this.mOnClickListener = onClickListener;
        }

        /**
         * Update <code>OnClickListener</code> to be applied on each item on
         * <code>getDropDownView()</code>
         */
        public void setCustomOnClickListener(OnClickListener onClickListener) {
            this.mOnClickListener = onClickListener;
        }

        /**
         * Spinner is invisible
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.action_bar_menu_item_layout, parent, false);
            }
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.action_bar_menu_item_layout, parent, false);
            }
            // Get view
            ImageView icon = (ImageView) view.findViewById(R.id.menu_item_icon);
            TextView title = (TextView) view.findViewById(R.id.menu_item_title);
            // Get action
            NavigationAction navAction = getItem(position);
            view.setTag(R.id.nav_action, navAction);
            // Set listener
            view.setOnClickListener(mOnClickListener);
            // Set action
            switch (navAction) {
                case Home:
                    title.setText(R.string.home_label);
                    icon.setImageResource(R.drawable.ico_dropdown_home);
                    break;
                case LoginOut:
                    boolean hasCredentials = JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials();
                    int resTitle = hasCredentials ? R.string.sign_out : R.string.sign_in;
                    title.setText(resTitle);
                    icon.setImageResource(R.drawable.ico_dropdown_signin);
                    break;
                case Saved:
                    title.setText(R.string.saved);
                    icon.setImageResource(R.drawable.ico_dropdown_favourites);
                    break;
                case RecentSearches:
                    title.setText(R.string.recent_searches);
                    icon.setImageResource(R.drawable.ico_dropdown_recentsearch);
                    break;
                case RecentlyViewed:
                    title.setText(R.string.recently_viewed);
                    icon.setImageResource(R.drawable.ico_dropdown_recentlyview);
                    break;
                case MyAccount:
                    title.setText(R.string.my_account);
                    icon.setImageResource(R.drawable.ico_dropdown_myaccount);
                    break;
                case MyOrders:
                    title.setText(R.string.my_orders_label);
                    icon.setImageResource(R.drawable.ico_dropdown_order);
                    break;
                default:
                    Print.w(TAG, "WARNING GETDROPDOWNVIEW UNKNOWN VIEW");
                    break;
            }
            return view;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
