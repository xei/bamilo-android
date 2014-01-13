/**
 * 
 */
package pt.rocket.view.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.LogOut;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.components.NavigationListComponent;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.NavigationListHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.slidingmenu.lib.SlidingMenu;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SlideMenuFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(SlideMenuFragment.class);

    private static SlideMenuFragment slideMenuFragment;

    private ViewGroup navigationContainer;

    private LayoutInflater inflater;

    private static ShoppingCart shoppingCart;

    public static ArrayList<NavigationListComponent> navigationListComponents;

    private static DialogGenericFragment dialogLogout;

    /**
     * Get instance
     * 
     * @return
     */
    public static SlideMenuFragment getInstance() {
        if (slideMenuFragment == null)
            slideMenuFragment = new SlideMenuFragment();
        slideMenuFragment.setRetainInstance(true);
        return slideMenuFragment;
    }

    /**
     * Empty constructor
     */
    public SlideMenuFragment() {
        super(EnumSet.of(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Unknown,
                0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        inflater = LayoutInflater.from(getActivity());
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.navigation_container, container, false);
        navigationContainer = (ViewGroup) view.findViewById(R.id.slide_menu_container);
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        // Update
        if (navigationListComponents != null) {
            fillNavigationContainer(navigationListComponents);
            updateCart();
        } else {

            /**
             * TRIGGERS
             * 
             * @author sergiopereira
             */
            Log.i(TAG, "slidemenu trigger");
            if(JumiaApplication.INSTANCE.SHOP_ID >= 0)
                trigger();
            // triggerContentEvent(new
            // RequestEvent(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT));

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }

    protected boolean onSuccessEvent(Bundle bundle) {

        if (!isVisible())
            return true;
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT");

        switch (eventType) {
        case GET_NAVIGATION_LIST_COMPONENTS_EVENT:
            Log.d(TAG, "GET NAVIGATION LIST COMPONENTS EVENT");
            navigationListComponents = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            fillNavigationContainer(navigationListComponents);
            updateCart();
            break;
        }

        return true;
    }

    /**
     * Update the sliding menu Called from BaseFragment
     * 
     * @author sergiopereira
     */
    public void onUpdate() {
        if (navigationListComponents != null) {
            Log.i(TAG, "ON UPDATE: NAV LIST IS NOT NULL");
            // Update generic items or force reload for LogInOut
            updateNavigationItems();
            // Update cart
            updateCart();
        }
    }

    //
    // /**
    // * Set the wish list counter
    // * @param layout
    // * @author sergiopereira
    // */
    // private void setWishlistCount(View layout) {
    // if(wishlistCounter > 0)
    // ((TextView)
    // layout.findViewById(R.id.component_text)).setText(getString(R.string.nav_wishlist) + " (" +
    // wishlistCounter + ")");
    // else
    // ((TextView)
    // layout.findViewById(R.id.component_text)).setText(getString(R.string.nav_wishlist));
    // }
    //
    /**
     * 
     * @param view
     */
    private void setLogInOutText(View view) {
        int text = JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() ? R.string.sign_out
                : R.string.sign_in;
        ((TextView) view.findViewById(R.id.component_text)).setText(text);
        view.setId(R.id.loginout_view);
    }

    /**
     * Updated generic items
     * 
     * @author sergiopereira
     */
    private void updateNavigationItems() {
        try {
            // For each child validate the selected item
            ViewGroup vGroup = ((ViewGroup) getView().findViewById(
                    R.id.slide_menu_scrollable_container));
            int count = vGroup.getChildCount();
            for (int i = 0; i < count; i++)
                updateItem(vGroup.getChildAt(i));

        } catch (NullPointerException e) {
            Log.w(TAG, "ON UPDATE NAVIGATION: NULL POINTER EXCEPTION");
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "ON UPDATE NAVIGATION: INDEX OUT OF BOUNDS EXCEPTION");
            e.printStackTrace();
        }
    }

    /**
     * Update item
     * 
     * @param view
     */
    private void updateItem(View view) {
        NavigationAction navAction = (NavigationAction) view.getTag(R.id.nav_action);
        Log.d(TAG, "UPDATE NAV: " + navAction.toString());
        switch (navAction) {
        // Update logInOut
        case LoginOut:
            setLogInOutText(view);
            break;
        // // Update wish list counter
        // case Wishlist:
        // setWishlistCount(view);
        // break;
        case Home:
        case Search:
        case Categories:
        case MyAccount:
        case Country:
        default:
            break;
        }
        // Set selected
        setActionSelected(view);
    }

    /**
     * 
     * @param components
     */
    private void fillNavigationContainer(ArrayList<NavigationListComponent> components) {
        Log.d(TAG, "FILL NAVIGATION CONTAINER");
        navigationContainer.removeAllViews();

        // Header component
        inflater.inflate(R.layout.navigation_header_component, navigationContainer, true);

        // // Static container
        // inflater.inflate(R.layout.navigation_static_container, navigationContainer, true);
        // ViewGroup staticContainer = (ViewGroup) navigationContainer
        // .findViewById(R.id.slide_menu_static_container);

        // Scrollable container
        inflater.inflate(R.layout.navigation_scrollable_container, navigationContainer, true);
        LinearLayout scrollableContainer = (LinearLayout) navigationContainer
                .findViewById(R.id.slide_menu_scrollable_container);

        for (NavigationListComponent component : components) {
            Log.i(TAG, "code1 creating component : "+component.getElementText());
            // Basket
            ViewGroup viewGroup = scrollableContainer;
            // if (component.getElementId() == 7 && component.getElementText().equals("Basket"))
            // viewGroup = staticContainer;
            // Others
            View actionElementLayout = getActionElementLayout(component, viewGroup);
            if (actionElementLayout != null)
                viewGroup.addView(actionElementLayout);
        }
    }

    public void refreshPosition() {
        if (navigationContainer != null && (ViewGroup) getView() != null) {
            if (((LinearLayout) getView().findViewById(R.id.slide_menu_scrollable_container)) != null) {
                int count = ((LinearLayout) getView().findViewById(
                        R.id.slide_menu_scrollable_container)).getChildCount();
                LinearLayout vGroup = ((LinearLayout) getView().findViewById(
                        R.id.slide_menu_scrollable_container));
                Log.i(TAG, "code1 size is : " + count);
                for (int i = 0; i < count; i++) {
                    View view = vGroup.getChildAt(i);
                    Log.i(TAG, "code1 tag is  : " + count);
                    setActionSelected(view);
                }
            }

        }
    }

    /**
     * Retrieves the layout element associated with a given action of the navigation list
     * 
     * @param action
     *            The action we want to retrieve the layout for
     * @param id
     *            The id
     * @return The layout of the navigation list element
     */
    public View getActionElementLayout(NavigationListComponent component, ViewGroup parent) {
        View layout = null;
        String elementUrl = component.getElementUrl();
        if (elementUrl == null) {
            elementUrl = "";
        }
        String[] nav = elementUrl.split("/");
        NavigationAction action = NavigationAction.byAction(nav[nav.length - 1].trim());

        switch (action) {
        case Basket:
            layout = createBasket(parent, component, this);
            break;
        case Home:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_home, R.string.home,
                    this);
            break;
        case Search:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_search,
                    R.string.search_label, this);
            break;

        case Categories:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_categories,
                    R.string.categories, this);
            break;

        case MyAccount:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_account,
                    R.string.my_account, this);
            break;
        case LoginOut:
            int text = JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() ?
                    R.string.sign_out : R.string.sign_in;
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_loginout,
                    getString(text), this);
            layout.setId(R.id.loginout_view);
            break;
        case Country:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_countrychange,
                    R.string.nav_country, this);
            break;
        case TrackOrder:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_trackorder,
                    R.string.nav_track_order, this);
            break;
        default:
            layout = inflater.inflate(R.layout.navigation_generic_component, parent, false);
            TextView tVd = (TextView) layout.findViewById(R.id.component_text);
            // imageLoader.DisplayImage(component.getElementImageUrl(), imgd,
            // new OnLoadingCompleteListener() {
            //
            // @Override
            // public void onLoadingComplete() {
            // }
            //
            // });
            tVd.setText(component.getElementText());
            break;
        }
        if (layout != null) {
            layout.setTag(R.id.nav_action, action);
            setActionSelected(layout);
        }
        return layout;
    }

    /**
     * 
     * @param view
     */
    private void setActionSelected(View view) {
        if (view.getTag(R.id.nav_action) == ((BaseActivity) getActivity()).getAction()) {
            Log.i(TAG, "SELECTED ACTION: " + ((BaseActivity) getActivity()).getAction());
            if (!view.isSelected())
                view.setSelected(true);
        } else {
            view.setSelected(false);
        }
    }

    /**
     * 
     * @param parent
     * @param component
     * @param listener
     * @return
     */
    private View createBasket(ViewGroup parent, NavigationListComponent component,
            OnClickListener listener) {
        View layout = inflater.inflate(R.layout.navigation_basket_component, parent, false);
        View basket = layout.findViewById(R.id.nav_basket);
        basket.setOnClickListener(listener);
        return layout;
    }

    /**
     * 
     * @param parent
     * @param component
     * @param iconRes
     * @param textRes
     * @param listener
     * @return
     */
    private View createGenericComponent(ViewGroup parent, NavigationListComponent component,
            int iconRes, int textRes, OnClickListener listener) {
        return createGenericComponent(parent, component, iconRes, getString(textRes), listener);
    }

    /**
     * 
     * @param parent
     * @param component
     * @param iconRes
     * @param text
     * @param listener
     * @return
     */
    private View createGenericComponent(ViewGroup parent, NavigationListComponent component,
            int iconRes, String text, OnClickListener listener) {
        View navComponent = inflater.inflate(R.layout.navigation_generic_component, parent, false);
        TextView tVSearch = (TextView) navComponent.findViewById(R.id.component_text);
        tVSearch.setText(text);
        tVSearch.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        navComponent.setOnClickListener(listener);
        return navComponent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        NavigationAction navAction = (NavigationAction) v.getTag(R.id.nav_action);
        Log.d(TAG,
                "Clicked on " + navAction + " while in "
                        + ((BaseActivity) getActivity()).getAction());

        SlidingMenu slidingMenu = ((BaseActivity) getActivity()).getSlidingMenu();

        if (navAction != null && ((BaseActivity) getActivity()).getAction() != navAction) {
            switch (navAction) {
            case Basket:
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.SHOPPING_CART,
                        FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                if (slidingMenu.isSlidingEnabled())
                    slidingMenu.toggle(true);
                break;
            case Home:
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.HOME,
                        FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                if (slidingMenu.isSlidingEnabled())
                    slidingMenu.toggle(true);
                break;
            case Search:
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.SEARCH,
                        FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                if (slidingMenu.isSlidingEnabled())
                    slidingMenu.toggle(true);
                break;
            case Categories:
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CATEGORY_URL, null);
                bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL,
                        FragmentType.CATEGORIES_LEVEL_1);
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CATEGORIES_LEVEL_1,
                        bundle, FragmentController.ADD_TO_BACK_STACK);
                if (slidingMenu.isSlidingEnabled())
                    slidingMenu.toggle(true);
                break;
            case MyAccount:
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.MY_ACCOUNT,
                        FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                if (slidingMenu.isSlidingEnabled())
                    slidingMenu.toggle(true);
                break;
            case LoginOut:
                // ActivitiesWorkFlow.loginOut(getActivity());
                loginOut((BaseActivity) getActivity());
                if (slidingMenu.isSlidingEnabled())
                    slidingMenu.toggle(true);
                break;
            case Country:
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CHANGE_COUNTRY,
                        FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                if (slidingMenu.isSlidingEnabled())
                    slidingMenu.toggle(true);
                break;
            case TrackOrder:
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.TRACK_ORDER,
                        FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                if (slidingMenu.isSlidingEnabled())
                    slidingMenu.toggle(true);
                break;
            }

        } else {
            Log.d(TAG, "Did not handle: " + navAction);
        }
    }

    /**
     * Method used to update the information about the cart
     * 
     * @param cart
     */
    public void updateCartInfo(ShoppingCart cart) {
        shoppingCart = cart;
        updateCart();
    }

    private void updateCart() {

        if (shoppingCart == null || getView() == null)
            return;

        // Update ActionBar
        ((BaseActivity) getActivity()).updateCartInfoInActionBar(shoppingCart);

        View container = getView().findViewById(R.id.nav_basket);
        if (container == null) {
            Log.w(getTag(), "updateCartInfo: cant find basket container - doing nothing");
            return;
        }

        final TextView vCartCount = (TextView) container.findViewById(R.id.nav_cart_count);
        final TextView navIm = (TextView) container.findViewById(R.id.nav_basket_elements);
        final TextView navVat = (TextView) container.findViewById(R.id.nav_basket_vat);
        final View navCartEmptyText = container.findViewById(R.id.nav_basket_empty);

        final String value = shoppingCart != null ? shoppingCart.getCartValue() : "";
        final String quantity = shoppingCart == null ? "?" : shoppingCart.getCartCount() == 0 ? ""
                : String
                        .valueOf(shoppingCart.getCartCount());

        vCartCount.post(new Runnable() {
            @Override
            public void run() {
                if (quantity.length() > 0) {
                    vCartCount.setText(quantity);
                    Log.i(getTag(), "VALUE = " + value);
                    navIm.setText(value);
                    navIm.setVisibility(View.VISIBLE);
                    navVat.setVisibility(View.VISIBLE);
                    navCartEmptyText.setVisibility(View.INVISIBLE);
                    Log.d(getTag(), "updateCartInfo: setting for cart not empty");
                } else {
                    vCartCount.setText(quantity);
                    Log.d(getTag(), "updateCartInfo: setting for cart empty");
                    navIm.setVisibility(View.INVISIBLE);
                    navVat.setVisibility(View.INVISIBLE);
                    navCartEmptyText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static void loginOut(final BaseActivity activity) {
        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
            FragmentManager fm = activity.getSupportFragmentManager();
            dialogLogout = DialogGenericFragment.newInstance(false, true, false,
                    activity.getString(R.string.logout_title),
                    activity.getString(R.string.logout_text_question),
                    activity.getString(R.string.no_label), activity.getString(R.string.yes_label),
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.button2) {
                                LogOut.performLogOut(new WeakReference<Activity>(activity));
                            }
                            dialogLogout.dismiss();

                        }

                    });
            dialogLogout.show(fm, null);
        } else {
            activity.onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE,
                    FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     */
    private void trigger() {
        triggerContentEvent(new NavigationListHelper(), null, mCallBack);
        // JumiaApplication.INSTANCE.sendRequest(new GetCategoriesHelper(), bundle, mCallBack);
    }

    /**
     * CALLBACK
     * 
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            // TODO
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };
}
