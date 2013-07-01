package pt.rocket.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.components.NavigationListComponent;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * A generator to display the Navigation List
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Sergio Pereira
 * @author Michael Kroez (refactoring, cleanups)
 * 
 */
public class RocketNavigationListGenerator implements ResponseListener {
    protected static final String TAG = LogTagHelper.create(RocketNavigationListGenerator.class);

    private Context context;
    private LayoutInflater inflater;

    private ViewGroup navigationContainer;

    private MyActivity activity;

    private OnClickListener navActionListener = new NavigationActionListener();

    public RocketNavigationListGenerator(Context ctx) {
        context = ctx.getApplicationContext();
        inflater = LayoutInflater.from(context);
        navigationContainer = (ViewGroup) inflater.inflate(R.layout.navigation_container,
                null, false);
        EventManager.getSingleton().addResponseListener(
                this,
                EnumSet.of(EventType.LOGIN_EVENT, EventType.LOGOUT_EVENT, 
                        EventType.REGISTER_ACCOUNT_EVENT,
                        EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT));
        EventManager.getSingleton().triggerRequestEvent(
                new RequestEvent(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT));
    }

    private String getString(int res) {
        return context.getString(res);
    }

    /**
     * Genreates the navigation list
     * 
     * @return The layout with all the graphic elements for the navigation list
     */
    public ViewGroup getNavigation(MyActivity activity) {
        this.activity = activity;
        if(navigationContainer.getParent() != null) {
            ((ViewGroup)navigationContainer.getParent()).removeView(navigationContainer);
        }
        for (int i = 0; i < navigationContainer.getChildCount(); i++) {
            View child = navigationContainer.getChildAt(i);
            if (child.getTag(R.id.nav_action) == activity.getAction()) {
                child.setSelected(true);
            } else {
                child.setSelected(false);
            }
        }
        return navigationContainer;
    }
    
    private void fillNavigationContainer(ArrayList<NavigationListComponent> components) {
        navigationContainer.removeAllViews();
        inflater.inflate(R.layout.navigation_header_component, navigationContainer, true);
        for (NavigationListComponent component : components) {
            View actionElementLayout = getActionElementLayout(component,
                    navigationContainer);
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
            layout = createBasket(parent, component, navActionListener);
            break;
        case Home:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_home, R.string.home,
                    navActionListener);
            break;
        case Search:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_search,
                    R.string.search_label, navActionListener);
            break;

        case Categories:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_categories,
                    R.string.categories, navActionListener);
            break;

        case MyAccount:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_account,
                    R.string.my_account, navActionListener);
            break;
        case LoginOut:
            int text = ServiceManager.SERVICES.get(CustomerAccountService.class).hasCredentials() ?
                    R.string.sign_out : R.string.sign_in;
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_loginout,
                    getString(text), navActionListener);
            layout.setId(R.id.loginout_view);
            break;
        case Country:
            layout = createGenericComponent(parent, component,
                    R.drawable.selector_navigation_countrychange,
                    R.string.nav_country, navActionListener);
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
        }
        return layout;
    }

    private View createBasket(ViewGroup parent, NavigationListComponent component,
            OnClickListener listener) {
        View layout = inflater.inflate(R.layout.navigation_basket_component, parent, false);
        View basket = layout.findViewById(R.id.nav_basket);
        basket.setOnClickListener(listener);
        return layout;
    }

    private View createGenericComponent(ViewGroup parent, NavigationListComponent component,
            int iconRes, int textRes, OnClickListener listener) {
        return createGenericComponent(parent, component, iconRes, getString(textRes),
                listener);
    }

    private View createGenericComponent(ViewGroup parent, NavigationListComponent component,
            int iconRes, String text, OnClickListener listener) {
        View navComponent;
        navComponent = inflater.inflate(R.layout.navigation_generic_component, parent, false);
        TextView tVSearch = (TextView) navComponent.findViewById(R.id.component_text);
        tVSearch.setText(text);
        tVSearch.setCompoundDrawablesWithIntrinsicBounds(iconRes,
                0, 0, 0);
        navComponent.setOnClickListener(listener);
        return navComponent;
    }

    private class NavigationActionListener implements OnClickListener {

        /*
         * (non-Javadoc)
         * 
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
            NavigationAction navAction = (NavigationAction) v.getTag(R.id.nav_action);
            Log.d(TAG, "Clicked on " + navAction + " while in " + activity.getAction());
            if (navAction != null && activity.getAction() != navAction) {
                switch (navAction) {
                case Basket:
                    ActivitiesWorkFlow.shoppingCartActivity(activity);
                    break;
                case Home:
                    ActivitiesWorkFlow.homePageActivity(activity);
                    break;
                case Search:
                    ActivitiesWorkFlow.searchActivity(activity);
                    break;
                case Categories:
                    ActivitiesWorkFlow.categoriesActivityNew(activity, null);
                    break;
                case MyAccount:
                    ActivitiesWorkFlow.myAccountActivity(activity);
                    break;
                case LoginOut:
                    ActivitiesWorkFlow.loginOut(activity);
                    break;
                case Country:
                    ActivitiesWorkFlow.changeCountryActivity(activity);
                    break;
                }
            } else {
                Log.d(TAG, "Did not handle: " + navAction);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework.event.IEvent)
     */
    @Override
    public void handleEvent(ResponseEvent event) {
        if (event.getSuccess()) {
            switch (event.type) {
            case GET_NAVIGATION_LIST_COMPONENTS_EVENT:
                Log.d(TAG, "handleEvent: GET_NAVIGATION_LIST_COMPONENTS_EVENT");
                ResponseResultEvent<Collection<NavigationListComponent>> getNavEvent =
                        (ResponseResultEvent<Collection<NavigationListComponent>>) event;
                fillNavigationContainer((ArrayList<NavigationListComponent>) getNavEvent.result);
                break;                
            case LOGIN_EVENT:
            case REGISTER_ACCOUNT_EVENT:
                updateLoginState(true);
                break;
            case LOGOUT_EVENT:
                updateLoginState(false);
                break;
//            case GET_SESSION_STATE:
//                updateLoginState(((ResponseResultEvent<Boolean>)event).result);
//                break;
            }
        } else if (event.type == EventType.LOGIN_EVENT
                && event.errorCode == ErrorCode.REQUEST_ERROR) {
            updateLoginState(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.event.EventListener#removeAfterHandlingEvent()
     */
    @Override
    public boolean removeAfterHandlingEvent() {
        return false;
    }

    private void updateLoginState(boolean loggedIn) {
        if(navigationContainer == null) {
            Log.w(TAG, "Could not update loggin state in side bar because missing navigation");
            return;
        }
        View loginOutView = navigationContainer.findViewById(R.id.loginout_view);
        if(loginOutView == null) {
            Log.w(TAG, "Could not update loggin state in side bar because missing login/out view");
            return;
        }
        TextView loginout = (TextView)
                loginOutView.findViewById(
                        R.id.component_text);
        if (loginout != null) {
            loginout.setText(loggedIn ? R.string.sign_out : R.string.sign_in);
        }
    }

}
