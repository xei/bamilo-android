package com.bamilo.android.appmodule.bamiloapp.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.framework.components.widget.DismissibleSpinner;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.List;


/**
 * ActionProvider to present a Menu on ActionBar.<br>
 * Used on main_menu.xml.
 *
 * @author Andre Lopes
 * @modified spereira
 */
public class MyProfileActionProvider extends ActionProvider {

    public final static String TAG = MyProfileActionProvider.class.getSimpleName();
    @NavigationAction.Type
    private List<Integer> mSubMenuItems;
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

    @Override
    public View onCreateActionView(MenuItem forItem) {
        return onCreateActionView();
    }

    @Override
    public View onCreateActionView() {
        //
        View spinnerContainer = LayoutInflater.from(getContext()).inflate(R.layout.action_bar_myprofile_layout, null);
        mSpinner = spinnerContainer.findViewById(R.id.spinner_myprofile);
        // Case in Bamilo and API 17 set spinner as gone
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1 && ShopSelector.isRtlShop())
            mSpinner.setVisibility(View.GONE);

        mIcon = spinnerContainer.findViewById(R.id.image_myprofile);
        mIcon.setTag(R.id.nav_action, NavigationAction.MY_PROFILE);
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
    @NavigationAction.Type
    private List<Integer> getDropdownList() {
        // Validate state
        if (CollectionUtils.isEmpty(mSubMenuItems)) {
            mSubMenuItems = new ArrayList<>();
            mSubMenuItems.add(NavigationAction.HOME);
            boolean hasCredentials = BamiloApplication.INSTANCE.getCustomerUtils().hasCredentials();
            if (!hasCredentials) {
                mSubMenuItems.add(NavigationAction.LOGIN_OUT);
            }
            mSubMenuItems.add(NavigationAction.CATEGORIES);
            mSubMenuItems.add(NavigationAction.SAVED);
            mSubMenuItems.add(NavigationAction.MY_ACCOUNT);
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
        mSpinner.dismiss();
    }

    public void setFragmentNavigationAction(@NavigationAction.Type int action) {
        // Get current list
        ArrayList<Integer> list = (ArrayList<Integer>) getDropdownList();
        // Case Home or Cart
        if (list.indexOf(action) != -1) {
            list.remove(list.indexOf(action));
        }
    }

    /**
     * Adapter to be used on Spinner to manage the options on MyProfile menu
     */
    class MyProfileAdapter extends ArrayAdapter<Integer> implements SpinnerAdapter {

        OnClickListener mOnClickListener;

        public MyProfileAdapter(Context context, int textViewResourceId, @NavigationAction.Type List<Integer> itemsNavigationActions) {
            super(context, textViewResourceId, itemsNavigationActions);
        }

        public MyProfileAdapter(Context context, int textViewResourceId, @NavigationAction.Type List<Integer> itemsNavigationActions, OnClickListener onClickListener) {
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
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.gen_single_line_with_icon, parent, false);
            }
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.gen_single_line_with_icon, parent, false);
            }
            // Get view
            ImageView icon = ((SingleLineComponent) view).getStartImageView();
            ((SingleLineComponent) view).showImageStartViewVisible();
            TextView title = ((SingleLineComponent) view).getTextView();
            // Get action
            int navAction = getItem(position);
            view.setTag(R.id.nav_action, navAction);
            // Set listener
            view.setOnClickListener(mOnClickListener);

            // Set action
            switch (navAction) {
                case NavigationAction.HOME:
                    title.setText(R.string.home_label);
                    icon.setImageResource(R.drawable.ico_dropdown_home);
                    break;
                case NavigationAction.LOGIN_OUT:
                    title.setText(R.string.sign_in);
                    icon.setImageResource(R.drawable.ico_dropdown_login);
                    break;
                case NavigationAction.CATEGORIES:
                    title.setText(R.string.drawer_categories);
                    icon.setImageResource(R.drawable.ico_dropdown_categories);
                    break;
                case NavigationAction.SAVED:
                    title.setText(R.string.saved);
                    icon.setImageResource(R.drawable.ico_dropdown_favourites);
                    break;
                case NavigationAction.RECENT_SEARCHES:
                    title.setText(R.string.recent_searches);
                    icon.setImageResource(R.drawable.ico_dropdown_recentsearch);
                    break;
                case NavigationAction.RECENTLY_VIEWED:
                    title.setText(R.string.recently_viewed);
                    icon.setImageResource(R.drawable.ico_dropdown_recentlyview);
                    break;
                case NavigationAction.MY_ACCOUNT:
                    title.setText(R.string.my_account);
                    icon.setImageResource(R.drawable.ico_dropdown_myaccount);
                    break;
                case NavigationAction.MY_ORDERS:
                    title.setText(R.string.my_orders_label);
                    icon.setImageResource(R.drawable.ico_dropdown_order);
                    break;
                case NavigationAction.FAQ:
                    title.setText(R.string.faq);
                    icon.setImageResource(R.drawable.ico_dropdown_help);
                    break;
                case NavigationAction.ABOUT:
                    title.setText(R.string.about_us_label);
                    icon.setImageResource(R.drawable.ico_dropdown_contact_us);
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
