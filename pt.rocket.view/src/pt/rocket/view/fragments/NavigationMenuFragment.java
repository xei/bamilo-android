///**
// * 
// */
//package pt.rocket.view.fragments;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//
//import pt.rocket.components.holo_font_views.TextView;
//
//import pt.rocket.app.JumiaApplication;
//import pt.rocket.constants.ConstantsIntentExtra;
//import pt.rocket.controllers.LogOut;
//import pt.rocket.controllers.fragments.FragmentController;
//import pt.rocket.controllers.fragments.FragmentType;
//import pt.rocket.framework.ErrorCode;
//import pt.rocket.components.NavigationListComponent;
//import pt.rocket.framework.utils.Constants;
//import pt.rocket.framework.utils.EventType;
//import pt.rocket.framework.utils.LoadingBarView;
//import pt.rocket.framework.utils.LogTagHelper;
//import pt.rocket.helpers.NavigationListHelper;
//import pt.rocket.interfaces.IResponseCallback;
//import pt.rocket.utils.NavigationAction;
//import pt.rocket.utils.dialogfragments.DialogGenericFragment;
//import pt.rocket.view.BaseActivity;
//import pt.rocket.view.R;
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.FragmentManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import de.akquinet.android.androlog.Log;
//
///**
// * Class used to show the navigation menus
// * @author sergiopereira
// * 
// */
//public class NavigationMenuFragment extends BaseFragment implements OnClickListener {
//
//    public static final String TAG = LogTagHelper.create(NavigationMenuFragment.class);
//
//    private static NavigationMenuFragment sNavigationMenuFragment;
//
//    private ViewGroup mNavigationContainer;
//
//    private LayoutInflater mInflater;
//
//    private LoadingBarView mLoadingBarView;
//
//    private View mLoadingBarContainer;
//
//    private int mRecoveryCount = 0;
//
//    private static DialogGenericFragment dialogLogout;
//
//    /**
//     * Get instance
//     * 
//     * @return
//     */
//    public static NavigationMenuFragment getInstance() {
//        sNavigationMenuFragment = new NavigationMenuFragment();
//        return sNavigationMenuFragment;
//    }
//
//    /**
//     * Empty constructor
//     */
//    public NavigationMenuFragment() {
//        super(IS_NESTED_FRAGMENT);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
//     */
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Log.i(TAG, "ON ATTACH");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i(TAG, "ON CREATE");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
//     * android.view.ViewGroup, android.os.Bundle)
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        Log.i(TAG, "ON CREATE VIEW");
//        mInflater = inflater;
//        return inflater.inflate(R.layout.navigation_container, container, false);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
//     * android.os.Bundle)
//     */
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // Get container
//        mNavigationContainer = (ViewGroup) view.findViewById(R.id.slide_menu_scrollable_container);
//        // Get loading
//        mLoadingBarContainer = mNavigationContainer.findViewById(R.id.loading_slide_menu_container);
//        mLoadingBarView = (LoadingBarView) mLoadingBarContainer.findViewById(R.id.loading_bar_view);
//        if (mLoadingBarView != null) {
//            mLoadingBarView.startRendering();
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onStart()
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.i(TAG, "ON START");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onResume()
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.i(TAG, "ON RESUME");
//        if (JumiaApplication.mIsBound) {
//            onResumeExecution();
//        } else {
//            JumiaApplication.INSTANCE.setResendMenuHander(serviceConnectedHandler);
//        }
//    }
//
//    private Handler serviceConnectedHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            onResumeExecution();
//        };
//    };
//
//    private void onResumeExecution() {
//        // Update
//        if (JumiaApplication.navigationListComponents != null) {
//            fillNavigationContainer(JumiaApplication.navigationListComponents);
//            //updateCart();
//        } else {
//            Log.i(TAG, "slidemenu trigger");
//            if (JumiaApplication.SHOP_ID != null)
//                trigger();
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.view.fragments.MyFragment#onPause()
//     */
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.i(TAG, "ON PAUSE");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.view.fragments.MyFragment#onStop()
//     */
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.i(TAG, "ON STOP");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onDestroyView()
//     */
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.i(TAG, "ON DESTROY VIEW");
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
//     */
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i(TAG, "ON DESTROY");
//    }
//
//    protected boolean onSuccessEvent(Bundle bundle) {
//
//        if (!isVisible())
//            return true;
//        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
//        Log.i(TAG, "ON SUCCESS EVENT");
//
//        switch (eventType) {
//        case GET_NAVIGATION_LIST_COMPONENTS_EVENT:
//            
//            if (mLoadingBarView != null) mLoadingBarView.stopRendering();
//            
//            if (mLoadingBarContainer != null) mLoadingBarContainer.setVisibility(View.GONE);
//
//            Log.d(TAG, "GET NAVIGATION LIST COMPONENTS EVENT");
//            JumiaApplication.navigationListComponents = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
//            fillNavigationContainer(JumiaApplication.navigationListComponents);
//            
//            break;
//        }
//
//        return true;
//    }
//
//    /**
//     * Update the sliding menu Called from BaseFragment
//     * 
//     * @author sergiopereira
//     */
//    public void onUpdate() {
//        if (JumiaApplication.navigationListComponents != null && !isOnStoppingProcess) {
//            Log.i(TAG, "ON UPDATE: NAV LIST IS NOT NULL");
//            // Update generic items or force reload for LogInOut
//            updateNavigationItems();
//        }
//    }
//
//    /**
//     * 
//     * @param view
//     */
//    private void setLogInOutText(View view) {
//        int text = JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() ? R.string.sign_out : R.string.sign_in;
//        ((TextView) view.findViewById(R.id.component_text)).setText(text);
//        view.setId(R.id.loginout_view);
//    }
//
//    /**
//     * Updated generic items
//     * 
//     * @author sergiopereira
//     */
//    private void updateNavigationItems() {
//        try {
//            // For each child validate the selected item
//            ViewGroup vGroup = ((ViewGroup) getView().findViewById(R.id.slide_menu_scrollable_container));
//            if (vGroup == null && mRecoveryCount < 3) {
//                mRecoveryCount++;
//                Message msg = new Message();
//                msg.arg1 = 0;
//                recoveryHandler.sendMessageDelayed(msg, 500);
//                return;
//            } else if (vGroup == null) {
//                return;
//            }
//            int count = vGroup.getChildCount();
//            for (int i = 0; i < count; i++)
//                updateItem(vGroup.getChildAt(i));
//            mRecoveryCount = 0;
//        } catch (NullPointerException e) {
//            Log.w(TAG, "ON UPDATE NAVIGATION: NULL POINTER EXCEPTION");
//            e.printStackTrace();
//        } catch (IndexOutOfBoundsException e) {
//            Log.w(TAG, "ON UPDATE NAVIGATION: INDEX OUT OF BOUNDS EXCEPTION");
//            e.printStackTrace();
//        }
//    }
//
//    Handler recoveryHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            if (msg.arg1 == 0) {
//                updateNavigationItems();
//            }
//        };
//    };
//
//    /**
//     * Update item
//     * 
//     * @param view
//     */
//    private void updateItem(View view) {
//        NavigationAction navAction = (NavigationAction) view.getTag(R.id.nav_action);
//        Log.d(TAG, "UPDATE NAV: " + navAction.toString());
//        switch (navAction) {
//        case LoginOut:
//            setLogInOutText(view);
//            break;
//        case Search:
//            // ...
//            break;
//        case Home:
//        case Categories:
//        case MyAccount:
//        case Country:
//        default:
//            break;
//        }
//        // Set selected
//        setActionSelected(view);
//    }
//
//    /**
//     * 
//     * @param components
//     */
//    private void fillNavigationContainer(ArrayList<NavigationListComponent> components) {
//        Log.d(TAG, "FILL NAVIGATION CONTAINER");
//        try {
//            mNavigationContainer.removeAllViews();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//
//        // Scrollable container
//        if (components != null) {
//            for (NavigationListComponent component : components) {
//
//                // Others
//                View actionElementLayout = getActionElementLayout(component, mNavigationContainer);
//                if (actionElementLayout != null)
//                    mNavigationContainer.addView(actionElementLayout);
//            }
//        }
//    }
//
//    public void refreshPosition() {
//        if (mNavigationContainer != null && (ViewGroup) getView() != null) {
//            if (((LinearLayout) getView().findViewById(R.id.slide_menu_scrollable_container)) != null) {
//                int count = ((LinearLayout) getView().findViewById(R.id.slide_menu_scrollable_container)).getChildCount();
//                LinearLayout vGroup = ((LinearLayout) getView().findViewById(R.id.slide_menu_scrollable_container));
//                for (int i = 0; i < count; i++) {
//                    View view = vGroup.getChildAt(i);
//                    setActionSelected(view);
//                }
//            }
//        }
//    }
//
//    /**
//     * Retrieves the layout element associated with a given action of the navigation list
//     * 
//     * @param action
//     *            The action we want to retrieve the layout for
//     * @param id
//     *            The id
//     * @return The layout of the navigation list element
//     */
//    public View getActionElementLayout(NavigationListComponent component, ViewGroup parent) {
//        View layout = null;
//        String elementUrl = component.getElementUrl();
//        if (elementUrl == null) {
//            elementUrl = "";
//        }
//        String[] nav = elementUrl.split("/");
//        NavigationAction action = NavigationAction.byAction(nav[nav.length - 1].trim());
//
//        switch (action) {
//        case Basket:
//            layout = createBasket(parent, component, this);
//            break;
//        case Home:
//            layout = createGenericComponent(parent, component, R.drawable.selector_navigation_home, R.string.home, this);
//            layout.findViewById(R.id.component_text).setTag(R.id.nav_action, action);
//            break;
//        case Search:
//            layout = createGenericComponent(parent, component, R.drawable.selector_navigation_search, R.string.search_label, this);
//            layout.findViewById(R.id.component_text).setTag(R.id.nav_action, action);
//            break;
//        case Categories:
//            layout = createGenericComponent(parent, component, R.drawable.selector_navigation_categories, R.string.categories, this);
//            layout.findViewById(R.id.component_text).setTag(R.id.nav_action, action);
//            break;
//        case MyAccount:
//            layout = createGenericComponent(parent, component, R.drawable.selector_navigation_settings, R.string.my_account, this);
//            layout.findViewById(R.id.component_text).setTag(R.id.nav_action, action);
//            break;
//        case LoginOut:
//            int text = JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() ? R.string.sign_out : R.string.sign_in;
//            layout = createGenericComponent(parent, component, R.drawable.selector_navigation_loginout, getString(text), this);
//            layout.findViewById(R.id.component_text).setTag(R.id.nav_action, action);
//            layout.setId(R.id.loginout_view);
//            break;
//        case Country:
//            layout = createGenericComponent(parent, component, R.drawable.selector_navigation_countrychange, R.string.nav_country, this);
//            layout.findViewById(R.id.component_text).setTag(R.id.nav_action, action);
//            break;
//        case TrackOrder:
//            layout = createGenericComponent(parent, component, R.drawable.selector_navigation_trackorder, R.string.nav_track_order, this);
//            layout.findViewById(R.id.component_text).setTag(R.id.nav_action, action);
//            break;
//        default:
//            layout = mInflater.inflate(R.layout.navigation_generic_component, parent, false);
//            TextView tVd = (TextView) layout.findViewById(R.id.component_text);
//            tVd.setText(component.getElementText());
//            break;
//        }
//        if (layout != null) {
//            layout.setTag(R.id.nav_action, action);
//            setActionSelected(layout);
//        }
//        return layout;
//    }
//
//    /**
//     * 
//     * @param view
//     */
//    private void setActionSelected(View view) {
//        if (view.getTag(R.id.nav_action) == getBaseActivity().getAction()) {
//            Log.i(TAG, "SELECTED ACTION: " + getBaseActivity().getAction());
//            if (!view.isSelected())
//                view.setSelected(true);
//        } else {
//            view.setSelected(false);
//        }
//    }
//
//    /**
//     * 
//     * @param parent
//     * @param component
//     * @param listener
//     * @return
//     */
//    private View createBasket(ViewGroup parent, NavigationListComponent component, OnClickListener listener) {
//        View layout = mInflater.inflate(R.layout.navigation_basket_component, parent, false);
//        View basket = layout.findViewById(R.id.nav_basket);
//        basket.setOnClickListener(listener);
//        return layout;
//    }
//
//    /**
//     * 
//     * @param parent
//     * @param component
//     * @param iconRes
//     * @param textRes
//     * @param listener
//     * @return
//     */
//    private View createGenericComponent(ViewGroup parent, NavigationListComponent component, int iconRes, int textRes, OnClickListener listener) {
//        return createGenericComponent(parent, component, iconRes, getString(textRes), listener);
//    }
//
//    /**
//     * 
//     * @param parent
//     * @param component
//     * @param iconRes
//     * @param text
//     * @param listener
//     * @return
//     */
//    private View createGenericComponent(ViewGroup parent, NavigationListComponent component, int iconRes, String text, OnClickListener listener) {
//        View navComponent = mInflater.inflate(R.layout.navigation_generic_component, parent, false);
//        TextView tVSearch = (TextView) navComponent.findViewById(R.id.component_text);
//        tVSearch.setText(text);
//        tVSearch.setContentDescription("calabash_" + text);
//        tVSearch.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
//        tVSearch.setOnClickListener(listener);
//        return navComponent;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.view.View.OnClickListener#onClick(android.view.View)
//     */
//    @Override
//    public void onClick(View v) {
//        NavigationAction navAction = (NavigationAction) v.getTag(R.id.nav_action);
//        Log.d(TAG, "Clicked on " + navAction + " while in " + ((BaseActivity) getActivity()).getAction());
//
//        if (navAction != null && ((BaseActivity) getActivity()).getAction() != navAction) {
//            switch (navAction) {
//            case Basket:
//                // ...
//                break;
//            case Home:
//                getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//                break;
//            case Search:
//                // ...
//                break;
//            case Categories:
//                Bundle bundle = new Bundle();
//                bundle.putString(ConstantsIntentExtra.CATEGORY_URL, null);
//                bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL, FragmentType.CATEGORIES_LEVEL_1);
//                getBaseActivity().onSwitchFragment(FragmentType.CATEGORIES_LEVEL_1, bundle, FragmentController.ADD_TO_BACK_STACK);
//                break;
//            case MyAccount:
//                getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//                break;
//            case LoginOut:
//                loginOut(getBaseActivity());
//                break;
//            case Country:
//                FragmentController.getInstance().removeEntriesUntilTag(FragmentType.HOME.toString());
//                getBaseActivity().onSwitchFragment(FragmentType.CHANGE_COUNTRY, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//                break;
//            case TrackOrder:
//                FragmentController.getInstance().removeEntriesUntilTag(FragmentType.HOME.toString());
//                getBaseActivity().onSwitchFragment(FragmentType.TRACK_ORDER, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//                break;
//            }
//
//            // Toggle
//            getBaseActivity().toggle();
//        } else {
//            Log.d(TAG, "Did not handle: " + navAction);
//        }
//    }
//
//    public static void loginOut(final BaseActivity activity) {
//        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
//            FragmentManager fm = activity.getSupportFragmentManager();
//            dialogLogout = DialogGenericFragment.newInstance(false, true, false,
//                    activity.getString(R.string.logout_title),
//                    activity.getString(R.string.logout_text_question),
//                    activity.getString(R.string.no_label), activity.getString(R.string.yes_label),
//                    new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            if (v.getId() == R.id.button2) {
//                                LogOut.performLogOut(new WeakReference<Activity>(activity));
//                            }
//                            dialogLogout.dismiss();
//
//                        }
//
//                    });
//            dialogLogout.show(fm, null);
//        } else {
//            activity.onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//        }
//    }
//
//    /**
//     * TRIGGERS
//     * 
//     * @author sergiopereira
//     */
//    private void trigger() {
//        triggerContentEvent(new NavigationListHelper(), null, mCallBack);
//    }
//
//    /**
//     * CALLBACK
//     * 
//     * @author sergiopereira
//     */
//    IResponseCallback mCallBack = new IResponseCallback() {
//
//        @Override
//        public void onRequestError(Bundle bundle) {
//            ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
//            Log.i(TAG, "failed to get navigation  " + errorCode);
//        }
//
//        @Override
//        public void onRequestComplete(Bundle bundle) {
//            onSuccessEvent(bundle);
//        }
//    };
//}
