package pt.rocket.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.WishlistAdapterPlus;
import pt.rocket.framework.components.ScrollViewEx;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.ModifyWishListEvent;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;

import de.akquinet.android.androlog.Log;


/**
 * <p>This class defines the activity for the WishList.</p>
 * <p/>
 * <p>Copyright (C) 2012 Rocket Internet - All Rights Reserved</p>
 * <p/>
 * <p>Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. Written by sergiopereira, 07/08/2012.</p>
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author nunocastro
 * 
 * @modified sergiopereira
 * 
 * @date 07/08/2012
 * 
 * @description 
 * 
 */
public class WishListActivity extends MyActivity {

    protected final String TAG = LogTagHelper.create( WishListActivity.class );
    public Context context;
    private List<Product> products;

    private boolean multiSelectionActive = false;
    private ActionMode multiSelectionMode;
    boolean isFirstBoot = true;
    
    /**
	 * 
	 */
    public WishListActivity() {
        super(NavigationAction.Unknown,
                EnumSet.of(MyMenuItem.SEARCH),
                EnumSet.of(EventType.GET_WISHLIST_EVENT),
                EnumSet.of(EventType.REMOVE_ITEM_FROM_WISHLIST_EVENT),
                R.layout.wishlist, R.string.wishlist_header);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        // Get Aplication Context
        context = getApplicationContext();

        this.products = new ArrayList<Product>();

        // Gets the persistantData singleton
        
        
        // Inflate Wishlist Layout
        setAppContentLayout();
    }
    
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        onOrientationChanged(newConfig.orientation);

    }    

    /**
     * Get Products Set header Layout Add all the event listeners
     */
    @Override
    public void onResume() {
        super.onResume();
        
        onOrientationChanged(getResources().getConfiguration().orientation);

        // if the user has logged in
//        if (ServiceManager.getService(CustomerAccountService.class).isLoggedIn()) {
//
//            // Get Products
//            addProductEvent();
//
//        } else {
//            finish();
//        }
       
        /**
         * Redraw Menu
         */
        invalidateOptionsMenu();
    }
    
    // ########### Screen Orientation ###########
    /**
     * Changes the orientation of the view
     * 
     * @param orientation
     *            the constant indicating which is the current device orientation
     */
    private void onOrientationChanged(int orientation) {

        ListView wishGrid = (ListView) findViewById(R.id.middle_wishlist_list);

        switch (orientation) {
        case Configuration.ORIENTATION_PORTRAIT:
            break;

        case Configuration.ORIENTATION_LANDSCAPE:
            break;
        }

        //((WishlistAdapterPlus)productsGrid.getAdapter()).notifyDataSetChanged();
    }
    
    /**
     * unselects an item from the wishlist multiselect operation 
     * 
     * @param the number of item
     */
    public void unselectItem ( Integer count ) {
        ListView wishList = (ListView) findViewById(R.id.middle_wishlist_list);
        //WishlistAdapterPlus adapter = ( WishlistAdapterPlus ) wishList.getAdapter();

        multiSelectionActive = count > 0;      
        if ( count > 0 ) {
            multiSelectionMode.setTitle(count + " selected item(s)");
        } else {
            multiSelectionMode.finish();
        }
         
//        adapter.setMultiselection( multiSelectionActive ); 
        toggleMultiSelectionActions();
       
    }
    
    /**
     * indicates if the user is in multiselection mode or not
     * 
     * @return true if the user is in multiselection mode, otherwise false
     */
    public boolean isMultiselectionActive ( ) {
        
        return multiSelectionActive;
    }
    
    /**
     * Get Products using event Manager
     */
    private void addProductEvent() {

        // Get ListView
        final ListView wishList = (ListView) findViewById(R.id.middle_wishlist_list);
        // Create a ListView Adapter
        //WishlistAdapter wishlistAdpater = (WishlistAdapter) wishList.getAdapter();
        WishlistAdapterPlus wishlistAdpater = (WishlistAdapterPlus) wishList.getAdapter();

        if (wishlistAdpater == null || wishlistAdpater.getCount() == 0) {
            // Create a ListView Adapter
            //wishlistAdpater = new WishlistAdapter(this);
            wishlistAdpater = new WishlistAdapterPlus(this);
            // Set Adapter
            wishList.setAdapter(wishlistAdpater);

            // Get wishlist items From Event Manager
            EventManager.getSingleton().triggerRequestEvent(
                    new RequestEvent(EventType.GET_WISHLIST_EVENT));

        } else {
            //wishlistAdpater.updateWishList(this.products);
            wishlistAdpater.updateProducts(this.products);
            wishList.invalidateViews();
        }
    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {
        
        ListView wishGrid = (ListView) findViewById( R.id.middle_wishlist_list );
        setListeners();
        
        toggleMultiSelectionActions();        
    }

    /**
     * Sets the flag that indicates that the users does not want to see the
     * confirmation dialog for delete items
     * 
     * @param value
     *            indicates the state of the flag (true/ false)
     */
    public void setDontAskOnDelete(boolean value) {
        SharedPreferences prefs = getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Editor edit = prefs.edit();
        // edit.putBoolean(ConstantsSharedPrefs.SHARED_PREFS_OPTION_WISH_ALERT, value);
        edit.commit();
    }

    /**
     * Gets the flag that indicates that the users does not want to see the
     * confirmation dialog for delete items
     * 
     * @return true, if the user has marked the checkbox not to display any more
     *         messages; false otherwise
     */
    public boolean getDontAskOnDelete() {
        SharedPreferences prefs = getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // TODO: kill wishlist alltogether
        return true; // prefs.getBoolean(ConstantsSharedPrefs.SHARED_PREFS_OPTION_WISH_ALERT, false);
    }
    
    /**
     * Toggle the buttons between the ones for multiselection and the regular ones
     */
    private void toggleMultiSelectionActions ( ) {
        Button btnAddAll = ( Button ) findViewById( R.id.wishlist_button_addall );
        Button btnLogin = ( Button ) findViewById( R.id.wishlist_button_login );
        
        if (multiSelectionActive) {
            btnAddAll.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
        } else {
//            btnAddAll
//                    .setVisibility(ServiceManager.getService(CustomerAccountService.class)
//                            .isLoggedIn() ? View.VISIBLE
//                            : View.GONE);
//            btnLogin.setVisibility(!ServiceManager.getService(CustomerAccountService.class)
//                    .isLoggedIn() ? View.VISIBLE
//                    : View.GONE);
        }
    }
    
    private void setListeners() {
        final ListView wishList = (ListView) findViewById( R.id.middle_wishlist_list );
        Button btnAddAll = ( Button ) findViewById( R.id.wishlist_button_addall );
        Button btnLogin = ( Button ) findViewById( R.id.wishlist_button_login );
        

                 
        btnAddAll.setOnClickListener( new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // Add all items in the wishlist to the shopping cart                 
                addAllItemsToCart();                
            }
        });
        
        btnLogin.setOnClickListener( new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // Displays the login screen
                ActivitiesWorkFlow.loginActivity(WishListActivity.this, true);                
            }
        });
        
        // Set List View Listener
        wishList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( !multiSelectionActive ) {
                    ActivitiesWorkFlow.productsDetailsActivity(WishListActivity.this, null, -1, null);                      
                } else {
                    WishlistAdapterPlus adapter = ( WishlistAdapterPlus ) wishList.getAdapter();                        
                    adapter.addSelectedItem( position );   
                    multiSelectionMode.setTitle(adapter.getSelectedItems().size()+" selected item(s)"); 
                }                        
            }
        });
        
        if ( Boolean.valueOf( getString( R.string.enable_multi_selection ) ) ) {
            wishList.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    WishlistAdapterPlus adapter = ( WishlistAdapterPlus ) wishList.getAdapter();
                    if ( !multiSelectionActive ) {
                        multiSelectionActive = true;                         
                        toggleMultiSelectionActions();
                        // TODO: enable the action mode
                        // multiSelectionMode = startActionMode(new AnActionModeOfEpicProportions(activity, ConstantsActivities.WISHLIST_ACTIVITY));
                        
                        adapter.addSelectedItem( position ); 
                        multiSelectionMode.setTitle(adapter.getSelectedItems().size()+" selected item(s)");                    
                    } else {
                        
                        adapter.clearSelectedItems();                        
                    }
                    
                    return false;
                }
            });        
        }
        
    }
    
    public void contextMenuAddToBasket() {
        addSelectedItemsToCart();
    }
    
    public void contextMenuBuyAll() {
        
    }
    
    public void dismissContextMenu() {
        ListView wishList = (ListView) findViewById(R.id.middle_wishlist_list);
        WishlistAdapterPlus adapter = ( WishlistAdapterPlus ) wishList.getAdapter();

        if ( adapter.getSelectedItems().size() > 0 ) {
            adapter.clearSelectedItems();
        }        
    }
    
    public void contextMenuDelete() {
        
//        if ( !getDontAskOnDelete() ) {
//            //Display an confirmation message to the user 
//            //currentItem = wishlistItemView;
//            AlertMessagesComponent.confitmationCheckAlertMessage(activity, activity.getString( R.string.wishlist_remove_title ),  
//                    activity.getString( R.string.wishlist_remove_message ), activity.getString( R.string.wishlist_btn_yes ), activity.getString( R.string.wishlist_btn_no ), 0, handleComponent);
//        } else {
            //removes the item from the wishlist
            //removeItem( wishlistItemView, position );
            deleteSelectedItems();
//        }      
        
//        Log.i("WISHLIST", "  --> contextMenuDelete <-- ");
    }
    
    /**
     * Adds the selected wishlist items to the shopping cart
     */
    private void addSelectedItemsToCart() {

        // GridView wishList = (GridView) findViewById(R.id.middle_wishlist_list);
        ListView wishList = (ListView) findViewById(R.id.middle_wishlist_list);
        WishlistAdapterPlus adapter = (WishlistAdapterPlus) wishList.getAdapter();

        ListIterator<Integer> posIter = adapter.getSelectedItems().listIterator();
        addItemsToCart(posIter);
    }
    
    /**
     * Adds all the products on the wishlist to the shopping cart
     */
    private void addAllItemsToCart() {
        ArrayList<Integer> prodIds = new ArrayList<Integer>();
        
        for (int x = 0; x < products.size(); x++ ) {
            prodIds.add( x );
        }
        
        ListIterator<Integer> posIter = prodIds.listIterator();
        addItemsToCart ( posIter );             
    }
    
    /**
     * Adds the products in a given list to the shopping cart
     * @param posIter The iterator to the list containing the positions of the products in the products array to send to the cart
     */
    private void addItemsToCart(  ListIterator<Integer> posIter ) {
        
        Product prod;
        //TextView tV;
        //ImageView iM;
        ShoppingCart cart;
        Integer pos;
        
        while ( posIter.hasNext() ) {
            pos = posIter.next();
            
            //get product information
            prod = products.get( pos );
            //create the shopping cart item to add
//            cart = new ShoppingCart(prod.getName(), 1, prod.getPrice(), prod.getImages().get(0).getUrl(), 0,"", prod.getSKU(), prod);            
//            cart.setInWishList(true);
            //send the product to the cart

            //Toast.makeText(activity, prod.getName() + " added to your shopping cart ", Toast.LENGTH_SHORT).show();            
        }
        
        Toast.makeText(this, "The selected products were added to your shopping cart ",
                Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Deletes the select items from the wishlist.
     */
    private void deleteSelectedItems() {
        //GridView wishList = (GridView) findViewById(R.id.middle_wishlist_list);
        ListView wishList = (ListView) findViewById( R.id.middle_wishlist_list );
        WishlistAdapterPlus adapter = ( WishlistAdapterPlus ) wishList.getAdapter();
        ArrayList <String> selectedProductsIds = new ArrayList <String>(); 
        
        
        ListIterator<Integer> posIter = adapter.getSelectedItems().listIterator();
        Integer pos;       
        
        while ( posIter.hasNext() ) {
            pos = posIter.next();
            selectedProductsIds.add(products.get( pos ).getSKU());
        }

        EventManager.getSingleton().triggerRequestEvent(
                new ModifyWishListEvent(EventType.REMOVE_ITEM_FROM_WISHLIST_EVENT,
                        selectedProductsIds));
        
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {

        // Get ListView
        // GridView wishList = (GridView) findViewById(R.id.middle_wishlist_list);
        ListView wishList = 
                (ListView) findViewById(R.id.middle_wishlist_list);
        ScrollViewEx wishListScroll = (ScrollViewEx) findViewById(R.id.middle_wishlist_gridlayout);
        // get the listview adapter
        // WishlistAdapter wishAdapter = (WishlistAdapter) wishList.getAdapter();
        WishlistAdapterPlus wishAdapter = (WishlistAdapterPlus) wishList.getAdapter();
        // get the no item view
        LinearLayout noitem = (LinearLayout) findViewById(R.id.middle_noitems);

        switch (event.getType()) {
        case GET_WISHLIST_EVENT:

            // Get Products
            this.products = (List<Product>) event.result;

            // update the items list of the listview
            // wishAdapter.updateWishList(this.products);
            wishAdapter.updateProducts(this.products);

            // tell the listview to refresh herself
            wishList.invalidateViews();

            noitem.setVisibility(this.products.size() > 0 ? View.GONE : View.VISIBLE);
            wishListScroll.setVisibility(this.products.size() == 0 ? View.GONE : View.VISIBLE);


            break;

        case REMOVE_ITEM_FROM_WISHLIST_EVENT:

            // wishAdapter.updateWishList(this.products);
            wishAdapter.updateProducts(this.products);
            wishList.invalidateViews();

            noitem.setVisibility(this.products.size() > 0 ? View.GONE : View.VISIBLE);
            wishListScroll.setVisibility(this.products.size() == 0 ? View.GONE : View.VISIBLE);

            Toast.makeText(this, "The selected products were removed from the wishlist ",
                    Toast.LENGTH_SHORT).show();
            break;
        }
        return true;
    }

}
