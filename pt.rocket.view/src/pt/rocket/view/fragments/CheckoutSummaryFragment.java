/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import org.holoeverywhere.widget.TextView;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetShoppingCartItemsHelper;
import pt.rocket.helpers.GetShoppingCartRemoveItemHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.app.JumiaApplication;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutSummaryFragment extends BaseFragment implements OnClickListener,
        IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutSummaryFragment.class);

    private static CheckoutSummaryFragment mOrderSummaryFragment;

    private ViewGroup prodList;

    private TextView prodSubTotal;

    private ShoppingCart cart;

    private TextView prodShipFeeValue;

    private ViewGroup prodShipFeeView;

    private ViewGroup shipAddressView;

    private ViewGroup shipMethodView;

    private ViewGroup totalView;

    /**
     * 
     * @return
     */
    public static CheckoutSummaryFragment getInstance(Bundle bundle) {
        if (mOrderSummaryFragment == null)
            mOrderSummaryFragment = new CheckoutSummaryFragment();
        return mOrderSummaryFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutSummaryFragment() {
        super(EnumSet.of(
                EventType.GET_SHOPPING_CART_ITEMS_EVENT,
                EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT,
                EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Unknown,
                BaseActivity.CHECKOUT_NO_SET_HEADER);
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
        setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.checkout_summary_main, viewGroup, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get containers
        
        // Products
        prodList = (ViewGroup) view.findViewById(R.id.checkout_summary_products_list);
        view.findViewById(R.id.checkout_summary_products_btn_edit).setOnClickListener(this);
        prodShipFeeView = (ViewGroup) view.findViewById(R.id.checkout_summary_products_shippingfee_container);
        prodShipFeeValue = (TextView) view.findViewById(R.id.checkout_summary_products_text_shippingfee);
        prodSubTotal = (TextView) view.findViewById(R.id.checkout_summary_products_text_subtotal);
        // Shipping Address
        shipAddressView = (ViewGroup) view.findViewById(R.id.checkout_summary_include_shipping_address);
        view.findViewById(R.id.checkout_summary_shipping_address_btn_edit).setOnClickListener(this);
        // Shipping Method
        shipMethodView = (ViewGroup) view.findViewById(R.id.checkout_summary_include_shipping_method);
        view.findViewById(R.id.checkout_summary_shipping_method_btn_edit).setOnClickListener(this);
        // Total
        totalView = (ViewGroup) view.findViewById(R.id.checkout_summary_include_total);

        // Get cart
        cart = JumiaApplication.INSTANCE.getCart();
        if (cart == null) triggerGetShoppingCart();
        else showOrderSummary();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }

    private void showOrderSummary() {
        // Get and ser sub total
        prodSubTotal.setText(cart.getCartValue());
//        // Show each item
//        Map<String, ShoppingCartItem> items = cart.getCartItems();
//        ArrayList<ShoppingCartItem> cenas = (ArrayList<ShoppingCartItem>) items.values();
//        ShoppingCartItem item = cenas.get(0);
//        Log.d(TAG, "ORDER ITEM:" + item.getName());
    }

    /**
     * ############# CLICK LISTENER #############
     */

    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Prod Edit
        if (id == R.id.checkout_summary_products_btn_edit) onClickEditProdButton();
        // Ship Address Edit
        else if (id == R.id.checkout_summary_shipping_address_btn_edit) onClickEditAddessButton();
        // Ship Address Edit
        else if (id == R.id.checkout_summary_shipping_method_btn_edit) onClickEditMethodButton();
        // Remove
        else if (id == R.id.order_summary_item_btn_remove) onClickRemoveItemButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }

    private void onClickEditProdButton() {
        Log.i(TAG, "ON CLICK: EDIT PROD");
    }
    
    private void onClickEditAddessButton() {
        Log.i(TAG, "ON CLICK: EDIT ADDRESS");
    }
    
    private void onClickEditMethodButton() {
        Log.i(TAG, "ON CLICK: EDIT METHOD");
    }

    private void onClickRemoveItemButton() {
        Log.i(TAG, "ON CLICK: REMOVE ITEM");
    }

    /**
     * ############# RESPONSE #############
     */

    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
        case GET_SHOPPING_CART_ITEMS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHOPPING_CART_ITEMS_EVENT");
            cart = (ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            showOrderSummary();
            getBaseActivity().showContentContainer(false);
            break;
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED REMOVE_ITEM_FROM_SHOPPING_CART_EVENT");
            break;
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT");
            break;
        default:
            break;
        }

        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        if (!isVisible()) {
            return true;
        }
        if (getBaseActivity().handleErrorEvent(bundle)) {
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case GET_SHOPPING_CART_ITEMS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHOPPING_CART_ITEMS_EVENT");
            break;
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED REMOVE_ITEM_FROM_SHOPPING_CART_EVENT");
            break;
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT");
            break;
        default:
            break;
        }

        return false;
    }

    /**
     * ############# REQUESTS #############
     */

    private void triggerGetShoppingCart() {
        Log.i(TAG, "TRIGGER: GET SHOPPING CART");
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
    }

    private void triggerRemoveItemFromShoppingCart(ShoppingCartItem item) {
        Log.i(TAG, "TRIGGER: REMOVE ITEM SHOPPING CART");
        ContentValues values = new ContentValues();
        values.put("sku", item.getConfigSimpleSKU());
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartRemoveItemHelper.ITEM, values);
        triggerContentEvent(new GetShoppingCartRemoveItemHelper(), bundle, this);
    }

    private void triggerChangeQuantityInShoppingCart() {
        Log.i(TAG, "TRIGGER: CHANGE QUANTITY SHOPPING CART");
    }

    /**
     * ########### RESPONSE LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }

    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }

}
