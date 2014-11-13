/**
 * 
 */
package pt.rocket.utils;

import java.util.Arrays;
import java.util.List;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.view.ActionProvider;

import de.akquinet.android.androlog.Log;

/**
 * ActionProvider to present a Menu on ActionBar. used on main_menu.xml
 * 
 * @author Andre Lopes
 * 
 */
public class MyProfileActionProvider extends ActionProvider {

    public final static String TAG = LogTagHelper.create(MyProfileActionProvider.class);

    private List<NavigationAction> subMenuItems = Arrays.asList(
            NavigationAction.LoginOut,
            NavigationAction.Favorite,
            NavigationAction.RecentSearch,
            NavigationAction.RecentlyView,
            NavigationAction.MyAccount,
            NavigationAction.MyOrders
            );

    private Context mContext;
    private DismissibleSpinner mSpinner;
    private MyProfileAdapter mAdapter;
    private OnClickListener mAdapterOnClickListener;
    private View mIcon;
    
    private int mTotalFavourites = 0;

    public MyProfileActionProvider(Context context) {
        super(context);

        mContext = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.actionbarsherlock.view.ActionProvider#onCreateActionView()
     */
    @Override
    public View onCreateActionView() {
        View spinnerContainer = LayoutInflater.from(mContext).inflate(R.layout.action_bar_myprofile_layout, null);

        mSpinner = (DismissibleSpinner) spinnerContainer.findViewById(R.id.spinner_myprofile);
        mIcon = spinnerContainer.findViewById(R.id.image_myprofile);

        mIcon.setTag(R.id.nav_action, NavigationAction.MyProfile);

        if (mAdapterOnClickListener != null) {
            mIcon.setOnClickListener(mAdapterOnClickListener);
            mAdapter = new MyProfileAdapter(mContext, R.layout.action_bar_menu_item_layout, subMenuItems, mAdapterOnClickListener);
        } else {
            mAdapter = new MyProfileAdapter(mContext, R.layout.action_bar_menu_item_layout, subMenuItems);
        }

        mSpinner.setAdapter(mAdapter);

        return spinnerContainer;
    }

    /**
     * Update <code>OnClickListener</code> to be used on <code>MyProfileAdapter</code><br>
     * set such <code>OnClickListener</code> if <code>MyProfileAdapter</code> is already set
     * 
     * @param listener
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
     * update totalFavourites<br>
     * call after each click on MyProfile Button on ActionBar
     * 
     * @param totalFavourites
     */
    public void setTotalFavourites(int totalFavourites) {
        this.mTotalFavourites = totalFavourites; 
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
        Log.d(TAG, "dismissSpinner");
        mSpinner.dismiss();
    }

    /**
     * Adapter to be used on Spinner to manage the options on MyProfile menu
     */
    class MyProfileAdapter extends ArrayAdapter<NavigationAction> implements SpinnerAdapter {

        Context mContext;
        List<NavigationAction> mItemsNavigationActions;
        LayoutInflater mLayoutInflater;
        OnClickListener mOnClickListener;


        public MyProfileAdapter(Context context, int textViewResourceId,List<NavigationAction> itemsNavigationActions) {
            super(context, textViewResourceId, itemsNavigationActions);

            this.mContext = context;
            this.mItemsNavigationActions = itemsNavigationActions;

            mLayoutInflater = LayoutInflater.from(mContext);
        }

        public MyProfileAdapter(Context context, int textViewResourceId,List<NavigationAction> itemsNavigationActions, OnClickListener onClickListener) {
            this(context, textViewResourceId, itemsNavigationActions);
            
            this.mOnClickListener = onClickListener;
        }

        /**
         * Update <code>OnClickListener</code> to be applied on each item on
         * <code>getDropDownView()</code>
         * 
         * @param onClickListener
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
                view = mLayoutInflater.inflate(R.layout.action_bar_menu_item_layout, parent, false);
            }

            return view;
        }

        @Override
        public int getCount() {
            return mItemsNavigationActions.size();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.action_bar_menu_item_layout, parent, false);
            }

            ImageView icon = (ImageView) view.findViewById(R.id.menu_item_icon);
            TextView title = (TextView) view.findViewById(R.id.menu_item_title);
            // counter for favourites total 
            TextView counter = (TextView) view.findViewById(R.id.icon_counter);
            counter.setVisibility(View.INVISIBLE);

            NavigationAction navAction = mItemsNavigationActions.get(position);
            view.setTag(R.id.nav_action, navAction);

            if (mOnClickListener != null) {
                view.setOnClickListener(mOnClickListener);
            }

            switch (navAction) {
            case LoginOut:
                boolean hasCredentials = JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials();
                int resTitle = hasCredentials ? R.string.sign_out : R.string.sign_in;
                title.setText(resTitle);
                icon.setImageResource(R.drawable.ico_dropdown_signin);
                break;
            case Favorite:
                title.setText(R.string.favourites);
                icon.setImageResource(R.drawable.ico_dropdown_favourites);

                if (mTotalFavourites > 0) {
                    counter.setVisibility(View.VISIBLE);
                    counter.setText("" + mTotalFavourites);
                }
                break;
            case RecentSearch:
                title.setText(R.string.recent_searches);
                icon.setImageResource(R.drawable.ico_dropdown_recentsearch);
                break;
            case RecentlyView:
                title.setText(R.string.recently_viewed);
                icon.setImageResource(R.drawable.ico_dropdown_recentlyview);
                break;
            case MyAccount:
                title.setText(R.string.my_account);
                icon.setImageResource(R.drawable.ic_settings_highlighted);
                break;
            case MyOrders:
                title.setText(R.string.my_orders_label);
                icon.setImageResource(R.drawable.ic_orderstatuts_highlighted);
                break;
            default:
                Log.w(TAG, "WARNING GETDROPDOWNVIEW UNKNOWN VIEW");
                break;
            }

            return view;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public NavigationAction getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
