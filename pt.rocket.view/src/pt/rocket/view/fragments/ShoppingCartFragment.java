/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.ShoppingBasketFragListAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.components.ExpandableGridViewComponent;
import pt.rocket.framework.objects.MinOrderAmount;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetShoppingCartChangeItemQuantityHelper;
import pt.rocket.helpers.GetShoppingCartItemsHelper;
import pt.rocket.helpers.GetShoppingCartRemoveItemHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.holoeverywhere.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ShoppingCartFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = LogTagHelper.create(ShoppingCartFragment.class);

    private final static String ID_CHANGE_QUANTITY = "id_change_quantity";

    private static ShoppingCartFragment reviewFragment;

    private long mBeginRequestMillis;

    private LinearLayout noItems;

    private LinearLayout container;

    private MinOrderAmount minAmount;

    private List<ShoppingCartItem> items;

    private ArrayList<CartItemValues> itemsValues;

    private double unreduced_cart_price;

    private double reduced_cart_price;

    /**
     * Boolean to the define the activities type: false - ShoppingBasket | true . Checkout
     */
    public boolean isShoppingBasket = true;

    /**
     * mAdapter Basket adapter(ShoppingBasketListAdapter)
     */
    private ShoppingBasketFragListAdapter mAdapter;

    /**
     * lView Basket grid view
     */
    private ExpandableGridViewComponent lView;

    /**
     * Button container so we can control positioning in the screen
     */

    private Button checkoutButton;

    /**
     * dialogList for DialogList
     */
    private DialogListFragment dialogList;

    public static class CartItemValues {
        public Boolean is_in_wishlist;
        public Boolean is_checked;
        public String product_name;
        public String price;
        public String price_disc;
        public Integer product_id;
        public Integer quantity;
        public String image;
        public String simple_product_sku;
        public String product_sku;
        public Double discount_value;
        public long stock;
        public Integer min_delivery_time;
        public Integer max_delivery_time;
        public Map<String, String> simpleData;
        public String variation;
    }

    /**
     * Get instance
     * 
     * @return
     */
    public static ShoppingCartFragment getInstance() {
        if (reviewFragment == null)
            reviewFragment = new ShoppingCartFragment();
        return reviewFragment;
    }

    /**
     * Empty constructor
     */
    public ShoppingCartFragment() {
        super(EnumSet.of(EventType.GET_SHOPPING_CART_ITEMS_EVENT),
                EnumSet.of(EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT,
                        EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT), EnumSet
                        .of(MyMenuItem.SEARCH),
                NavigationAction.Basket,
                R.string.shoppingcart_title);
        this.setRetainInstance(true);
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
        View view = inflater.inflate(R.layout.shopping_basket, container, false);
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
        setAppContentLayout();


        // EventManager.getSingleton().triggerRequestEvent(new RequestEvent(
        // EventType.GET_MIN_ORDER_AMOUNT));

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
        mBeginRequestMillis = System.currentTimeMillis();
        triggerGetShoppingCart();
        
        setListeners();
        AnalyticsGoogle.get().trackPage(R.string.gshoppingcart);
    }
    
    private void triggerGetShoppingCart(){
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, responseCallback);
    }

    private void triggerRemoveItem(ShoppingCartItem item){
        ContentValues values = new ContentValues();
        values.put("sku", item.getConfigSimpleSKU());
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartRemoveItemHelper.ITEM, values);
        triggerContentEvent(new GetShoppingCartRemoveItemHelper(), bundle, responseCallback);
//        EventManager.getSingleton().triggerRequestEvent(
//                new RemoveItemFromShoppingCartEvent(items));
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
        releaseVars();
//        EventManager.getSingleton().removeResponseListener(this,
//                EnumSet.of(EventType.GET_SHOPPING_CART_ITEMS_EVENT,
//                        EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT,
//                        EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT));
        System.gc();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        releaseVars();
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");
    }

    private void releaseVars() {
        reviewFragment = null;

        noItems = null;

        container = null;

        minAmount = null;

        itemsValues = null;

        mAdapter = null;

        lView = null;

        checkoutButton = null;

        dialogList = null;
    }

    /**
     * Set the ShoppingCart layout using inflate
     */
    public void setAppContentLayout() {
        checkoutButton = (Button) getView().findViewById(R.id.checkout_button);
        noItems = (LinearLayout) getView().findViewById(R.id.no_items_container);
        container = (LinearLayout) getView().findViewById(R.id.container1);
    }

    public void setListeners() {

        // checkoutButton.setOnClickListener(checkoutClickListener);
        checkoutButton.setOnTouchListener(new OnTouchListener() {

            private DialogGenericFragment messageDialog;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (items.size() > 0) {
                        checkMinOrderAmount();
                    } else {
                        String title = getString(R.string.shoppingcart_alert_header);
                        String message = getString(R.string.shoppingcart_alert_message_no_items);
                        String buttonText = getString(R.string.ok_label);
                        messageDialog = DialogGenericFragment.newInstance(false, true,
                                false, title, message, buttonText, null, null);
                        messageDialog.show(getActivity().getSupportFragmentManager(), null);
                    }
                    break;
                }
                return false;
            }
        });

    }

    protected boolean onSuccessEvent(Bundle bundle) {
        if(!isVisible()){
            return true;
        }
        getBaseActivity().handleSuccessEvent(bundle);
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        List<String> errors = (List<String>) bundle
                .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
        Log.d(TAG, "onSuccessEvent: eventType = " + eventType);
        switch (eventType) {

        // case GET_SESSION_STATE:
        // if ((Boolean) event.result) {
        // goToCheckout();
        // } else {
        // ActivitiesWorkFlow
        // .loginActivity(ShoppingCartActivity.this, true);
        // }
        // return false;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            if(((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getCartItems() != null && ((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getCartItems().values() != null){
                TrackerDelegator.trackViewCart(getActivity().getApplicationContext(), ((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getCartItems().values().size());
            }
        default:
            getBaseActivity().showContentContainer(false);
            AnalyticsGoogle.get().trackLoadTiming(R.string.gshoppingcart, mBeginRequestMillis);
            displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
        }
        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        if(isVisible()){
            return true;
        }
        
        if(getBaseActivity().handleErrorEvent(bundle)){
            return true;
        }
        mBeginRequestMillis = System.currentTimeMillis();
        getBaseActivity().handleErrorEvent(bundle);
        return true;
    }

    private void displayShoppingCart(ShoppingCart cart) {
        Log.d(TAG, "displayShoppingCart");
        TextView priceTotal = (TextView) getView().findViewById(R.id.price_total);
        TextView articlesCount = (TextView) getView().findViewById(R.id.articles_count);

        items = new ArrayList<ShoppingCartItem>(cart.getCartItems().values());
        priceTotal.setText(cart.getCartValue());

        String articleString = getResources().getQuantityString(
                R.plurals.shoppingcart_text_article, cart.getCartCount());
        articlesCount.setText(cart.getCartCount() + " " + articleString);
        if (items.size() == 0) {
            showNoItems();
        } else {
            lView = (ExpandableGridViewComponent) getView().findViewById(R.id.shoppingcart_list);
            lView.setExpanded(true);
            lView.setOnItemClickListener(this);

            itemsValues = new ArrayList<CartItemValues>();
            unreduced_cart_price = 0;
            reduced_cart_price = 0;
            boolean cartHasReducedItem = false;
            for (int i = 0; i < items.size(); i++) {
                ShoppingCartItem item = items.get(i);
                CartItemValues values = new CartItemValues();
                values.is_in_wishlist = false;
                values.is_checked = false;
                values.product_name = item.getName();
                values.price = item.getPrice();
                values.product_id = 0;
                values.quantity = item.getQuantity();
                values.image = item.getImageUrl();
                values.price_disc = item.getSpecialPrice();
                values.discount_value = (double) Math.round(item.getSavingPercentage());
                values.stock = item.getStock();
                values.min_delivery_time = 0;
                values.max_delivery_time = 99;
                values.simpleData = item.getSimpleData();
                values.variation = item.getVariation();
                itemsValues.add(values);

                if (!item.getPrice().equals(item.getSpecialPrice())) {
                    cartHasReducedItem = true;
                }

                Double actItemPrice;
                if (item.getPrice().equals(item.getSpecialPrice())) {
                    actItemPrice = item.getPriceVal();
                } else {
                    actItemPrice = item.getSpecialPriceVal();
                }

                reduced_cart_price += actItemPrice * item.getQuantity();
                unreduced_cart_price += item.getPriceVal() * item.getQuantity();
            }

            mAdapter = new ShoppingBasketFragListAdapter(ShoppingCartFragment.this, itemsValues);

            /**
             * Setting Adapter
             */
            lView.setAdapter(mAdapter);
            lView.setFastScrollEnabled(true);

            TextView priceUnreduced = (TextView) getView().findViewById(R.id.price_unreduced);
            if (cartHasReducedItem) {
                priceUnreduced.setText(CurrencyFormatter.formatCurrency(unreduced_cart_price));
                priceUnreduced.setPaintFlags(priceUnreduced.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                priceUnreduced.setVisibility(View.VISIBLE);
            } else {
                priceUnreduced.setVisibility(View.INVISIBLE);
            }
            if (cart.getVatValue() != null && !cart.getVatValue().equalsIgnoreCase("null")
                    && !cart.getShippingValue().equalsIgnoreCase("")) {
                TextView vatValue = (TextView) getView().findViewById(R.id.vat_value);
                vatValue.setText(getString(R.string.vat_string) + ": " + cart.getVatValue());
                vatValue.setVisibility(View.VISIBLE);
            }
            if (cart.getShippingValue() != null
                    && !cart.getShippingValue().equalsIgnoreCase("null")
                    && !cart.getShippingValue().equalsIgnoreCase("")) {
                TextView shippingValue = (TextView) getView().findViewById(R.id.shipping_value);
                shippingValue
                        .setText(getString(R.string.shipping) + ": " + cart.getShippingValue());
                shippingValue.setVisibility(View.VISIBLE);
            }

            hideNoItems();
            AnalyticsGoogle.get().trackPage(R.string.gcartwithitems);

        }
    }

    /**
     * showNoItems update the layout when basket has no items
     */
    public void showNoItems() {
        noItems.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
        Button continueShopping = (Button) noItems.findViewById(R.id.continue_shopping_button);
        continueShopping.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.continue_shopping_button) {
                    ActivitiesWorkFlow.homePageActivity(getActivity());
                    getActivity().finish();
                }

            }
        });

        AnalyticsGoogle.get().trackPage(R.string.gcartempty);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        goToProducDetails(position);
    }

    /**
     * Function to redirect to the selected product details.
     * 
     * @param position
     */
    private void goToProducDetails(int position) {
        if (items.get(position).getProductUrl().equals(""))
            return;

        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, items.get(position).getProductUrl());
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcart_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    private void checkMinOrderAmount() {
        // if (minAmount == null) {
        // Toast.makeText(getActivity(),
        // getString(R.string.shoppingcart_minamount_waiting), Toast.LENGTH_LONG).show();
        // } else if (reduced_cart_price < minAmount.getValue()) {
        // String formattedMinAmount = CurrencyFormatter.formatCurrency(minAmount.getValue());
        // String message = String.format(getString(R.string.shoppingcart_minamount,
        // formattedMinAmount));
        // dialog = DialogGenericFragment.newInstance(true, true, false,
        // getString(R.string.shoppingcart_dialog_title),
        // message, getString(R.string.continue_shopping), null, new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // dialog.dismiss();
        // }
        // });
        // dialog.show( getActivity().getSupportFragmentManager(), null);
        // } else {
        TrackerDelegator.trackCheckout(getActivity().getApplicationContext(), items);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE,
                FragmentType.CHECKOUT_BASKET);
        bundle.putString(ConstantsIntentExtra.LOGIN_ORIGIN,
                getString(R.string.mixprop_loginlocationcart));
        ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.LOGIN, bundle,
                FragmentController.ADD_TO_BACK_STACK);
        // }
    }

//    public void goToCheckout() {
//        ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CHECKOUT_BASKET,
//                FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//        AnalyticsGoogle.get().trackCheckout(items);
//        TrackerDelegator.trackCheckout(getActivity().getApplicationContext(), items);
//    }
    
    

    /**
     * This method manages the deletion of selected elements
     */
    public void deleteSelectedElements() {
        for (int position = items.size() - 1; position >= 0; position--) {
            if (itemsValues.get(position).is_checked) {
                itemsValues.remove(position);
                mBeginRequestMillis = System.currentTimeMillis();
                
                triggerRemoveItem(items.get(position));
                
                items.remove(position);

            }
        }

        if (items.size() == 0) {
            showNoItems();
        } else {
            hideNoItems();
        }
    }

    public void hideNoItems() {
        noItems.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
        lView.setVisibility(View.VISIBLE);
    }

    public void changeQuantityOfItem(final int position) {
        ArrayList<String> quantities = new ArrayList<String>();

        long stock = items.get(position).getStock();
        int maxQuantity = items.get(position).getMaxQuantity();

        long actualMaxQuantity = stock < maxQuantity ? stock : maxQuantity;

        for (int i = 0; i <= actualMaxQuantity; i++) {
            quantities.add(String.valueOf(i));
        }

        int crrQuantity = items.get(position).getQuantity();

        OnDialogListListener listener = new OnDialogListListener() {

            @Override
            public void onDialogListItemSelect(String id, int quantity, String value) {
                changeQuantityOfItem(position, quantity);
            }
        };

        dialogList = DialogListFragment.newInstance(getActivity(), listener, ID_CHANGE_QUANTITY,
                getString(R.string.shoppingcart_choose_quantity), quantities, crrQuantity);
        dialogList.show(getActivity().getSupportFragmentManager(), null);
    }

    public void changeQuantityOfItem(int position, int quantity) {
        items.get(position).setQuantity(quantity);
        mBeginRequestMillis = System.currentTimeMillis();
        changeItemQuantityInShoppingCart(items);
    }
    
    
    private void changeItemQuantityInShoppingCart(List<ShoppingCartItem> items){
        Bundle bundle = new Bundle();
        ContentValues values = new ContentValues();
        for (ShoppingCartItem item : items) {
            values.put("qty_" + item.getConfigSimpleSKU(), String.valueOf(item.getQuantity()));
        }
        bundle.putParcelable(GetShoppingCartChangeItemQuantityHelper.CART_ITEMS, values);
        triggerContentEventProgress(new GetShoppingCartChangeItemQuantityHelper(), bundle, responseCallback);
//        triggerContentEventProgress(new ChangeItemQuantityInShoppingCartEvent(items));
    }
    
    IResponseCallback responseCallback = new IResponseCallback() {
        
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
            
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
            
        }
    };

}
