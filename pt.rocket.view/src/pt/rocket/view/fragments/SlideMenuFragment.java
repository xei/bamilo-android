/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.components.NavigationListComponent;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SlideMenuFragment extends BaseFragment implements OnClickListener{ 

    private static final String TAG = LogTagHelper.create(SlideMenuFragment.class);

    private static SlideMenuFragment slideMenuFragment;

    private ViewGroup navigationContainer;

    private LayoutInflater inflater;

    private static ShoppingCart shoppingCart;

    public static ArrayList<NavigationListComponent> navigationListComponents;
    
    /**
     * Get instance
     * 
     * @return
     */
    public static SlideMenuFragment getInstance() {
        if (slideMenuFragment == null)
            slideMenuFragment = new SlideMenuFragment();
        return slideMenuFragment;
    }

    /**
     * Empty constructor
     */
    public SlideMenuFragment() {
        super(EnumSet.of(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT),  EnumSet.noneOf(EventType.class));
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
        inflater = LayoutInflater.from(getActivity());
        
        //triggerContentEvent(new RequestEvent(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT));
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
        if(navigationListComponents != null) {
            fillNavigationContainer(navigationListComponents);
            updateCart();
        } else {
            triggerContentEvent(new RequestEvent(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT));
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
        EventManager.getSingleton().removeResponseListener(this, EnumSet.of(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT));
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");

        unbindDrawables(navigationContainer);
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
        ((ViewGroup) view).removeAllViews();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        Log.i(TAG, "ON SUCCESS EVENT");
        if (event.getSuccess()) {
            switch (event.type) {
            case GET_NAVIGATION_LIST_COMPONENTS_EVENT:
                Log.d(TAG, "GET NAVIGATION LIST COMPONENTS EVENT");
                ResponseResultEvent<Collection<NavigationListComponent>> getNavEvent = (ResponseResultEvent<Collection<NavigationListComponent>>) event;
                navigationListComponents = (ArrayList<NavigationListComponent>) getNavEvent.result;
                fillNavigationContainer(navigationListComponents);
                updateCart();
                break;                
            }
        }
        return true;
    }
    
    /**
     * 
     * @param components
     */
    private void fillNavigationContainer(ArrayList<NavigationListComponent> components) {
        Log.d(TAG, "FILL NAVIGATION CONTAINER");
        navigationContainer.removeAllViews();
        inflater.inflate(R.layout.navigation_header_component, navigationContainer, true);
        for (NavigationListComponent component : components) {
            View actionElementLayout = getActionElementLayout(component, navigationContainer);
            if (actionElementLayout != null) {
                navigationContainer.addView(actionElementLayout);
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
            int text = ServiceManager.SERVICES.get(CustomerAccountService.class).hasCredentials() ?
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
            Log.i(TAG, "SELECTED ACTION: " + ((BaseActivity) getActivity()).getAction() );
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
    private View createBasket(ViewGroup parent, NavigationListComponent component, OnClickListener listener) {
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
    private View createGenericComponent(ViewGroup parent, NavigationListComponent component, int iconRes, int textRes, OnClickListener listener) {
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
    private View createGenericComponent(ViewGroup parent, NavigationListComponent component, int iconRes, String text, OnClickListener listener) {
        View navComponent = inflater.inflate(R.layout.navigation_generic_component, parent, false);
        TextView tVSearch = (TextView) navComponent.findViewById(R.id.component_text);
        ImageView imgView = (ImageView) navComponent.findViewById(R.id.component_img);
        tVSearch.setText(text);
        imgView.setImageResource(iconRes);
        navComponent.setOnClickListener(listener);
        return navComponent;
    }
    

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        NavigationAction navAction = (NavigationAction) v.getTag(R.id.nav_action);
        Log.d(TAG, "Clicked on " + navAction + " while in " + ((BaseActivity) getActivity()).getAction());
        if (navAction != null && ((BaseActivity) getActivity()).getAction() != navAction) {
            switch (navAction) {
            case Basket:
                ActivitiesWorkFlow.shoppingCartActivity(getActivity());
                break;
            case Home:
                ActivitiesWorkFlow.homePageActivity(getActivity());
                break;
            case Search:
                ActivitiesWorkFlow.searchActivity(getActivity());
                break;
            case Categories:
                ActivitiesWorkFlow.categoriesActivityNew(getActivity(), null);
                break;
            case MyAccount:
                ActivitiesWorkFlow.myAccountActivity(getActivity());
                break;
            case LoginOut:
                ActivitiesWorkFlow.loginOut(getActivity());
                break;
            case Country:
                ActivitiesWorkFlow.changeCountryActivity(getActivity());
                break;
            }
        } else {
            Log.d(TAG, "Did not handle: " + navAction);
        }
    }
    
    /**
     * Method used to update the information about the cart
     * @param cart
     */
    public void updateCartInfo(ShoppingCart cart){
        shoppingCart = cart;
        updateCart();
    }
    
    private void updateCart() {
        
        if(shoppingCart == null)
            return;
        
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
        final String quantity = shoppingCart == null ? "?" : shoppingCart.getCartCount() == 0 ? "" : String
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

}
