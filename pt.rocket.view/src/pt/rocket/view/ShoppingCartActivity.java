package pt.rocket.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.ShoppingBasketListAdapter;
import pt.rocket.framework.components.ExpandableGridViewComponent;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.ChangeItemQuantityInShoppingCartEvent;
import pt.rocket.framework.event.events.GetShoppingCartItemsEvent;
import pt.rocket.framework.event.events.RemoveItemFromShoppingCartEvent;
import pt.rocket.framework.objects.MinOrderAmount;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.DialogList;
import pt.rocket.utils.DialogList.OnDialogListListener;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class shows and manages the shopping cart items.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential. Written by josedourado, 19/06/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author josedourado
 * 
 * @modified josedourado
 * 
 * @date 19/06/2012
 * 
 * @description
 * 
 */

public class ShoppingCartActivity extends MyActivity implements OnItemClickListener {
    private final static String TAG = LogTagHelper.create(ShoppingCartActivity.class);

    private final static String ID_CHANGE_QUANTITY = "id_change_quantity";

    /**
     * Boolean to the define the activities type: false - ShoppingBasket | true . Checkout
     */
    public boolean isShoppingBasket = true;

    /**
     * mAdapter Basket adapter(ShoppingBasketListAdapter)
     */
    private ShoppingBasketListAdapter mAdapter;

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
    private DialogList dialogList;

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
        public Integer stock;
        public Integer min_delivery_time;
        public Integer max_delivery_time;
        public Map<String, String> simpleData;
    }

    private List<ShoppingCartItem> items;
    private List<CartItemValues> itemsValues;

    private double unreduced_cart_price;

    private double reduced_cart_price;

    private long beginRequestMillis;

    private LinearLayout noItems;

    private LinearLayout container;

    private MinOrderAmount minAmount;

    public ShoppingCartActivity() {
        super(NavigationAction.Basket,
                EnumSet.of(MyMenuItem.SEARCH),
                EnumSet.of(EventType.GET_SHOPPING_CART_ITEMS_EVENT),
                EnumSet.of(EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT,
                        EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT),
                R.string.shoppingcart_title, R.layout.shopping_basket);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppContentLayout();
        // EventManager.getSingleton().triggerRequestEvent(new RequestEvent(
        // EventType.GET_MIN_ORDER_AMOUNT));
    }

    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.main_basket_container));
        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
        beginRequestMillis = System.currentTimeMillis();
        triggerContentEvent(GetShoppingCartItemsEvent.FORCE_API_CALL);
        setListeners();
        AnalyticsGoogle.get().trackPage(R.string.gshoppingcart);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == maskRequestCodeId(R.id.request_login)
                && resultCode == Activity.RESULT_OK) {
            goToCheckout();
        }
    }

    /**
     * Set the ShoppingCart layout using inflate
     */
    public void setAppContentLayout() {
        checkoutButton = (Button) findViewById(R.id.checkout_button);
        noItems = (LinearLayout) findViewById(R.id.no_items_container);
        container = (LinearLayout) findViewById(R.id.container1);
    }

    public void setListeners() {

        // checkoutButton.setOnClickListener(checkoutClickListener);
        checkoutButton.setOnTouchListener(new OnTouchListener() {

            private DialogGeneric messageDialog;

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

                        messageDialog = new DialogGeneric(ShoppingCartActivity.this, false, true,
                                false, title, message, buttonText, null, null);
                        messageDialog.show();
                    }

                    break;
                }

                return false;
            }
        });

    }

    private void checkMinOrderAmount() {
        // if (minAmount == null) {
        // Toast.makeText(getApplicationContext(),
        // getString(R.string.shoppingcart_minamount_waiting), Toast.LENGTH_LONG).show();
        // } else
        if (reduced_cart_price < minAmount.getValue()) {
            String formattedMinAmount = CurrencyFormatter.formatCurrency(minAmount.getValue());
            String message = String.format(getString(R.string.shoppingcart_minamount,
                    formattedMinAmount));
            dialog = new DialogGeneric(this, true, true, false,
                    getString(R.string.shoppingcart_dialog_title),
                    message, getString(R.string.continue_shopping), null, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        } else {
            ActivitiesWorkFlow.loginActivity(ShoppingCartActivity.this, true);
        }
    }

    private void goToCheckout() {
        ActivitiesWorkFlow.checkoutActivity(ShoppingCartActivity.this,
                ConstantsCheckout.CHECKOUT_BASKET);
        AnalyticsGoogle.get().trackCheckout(items);
    }

    /**
     * Function to redirect to the selected product details.
     * 
     * @param position
     */
    private void goToProducDetails(int position) {
        if (items.get(position).getProductUrl().equals(""))
            return;

        ActivitiesWorkFlow.productsDetailsActivity(this, items.get(position).getProductUrl(),
                R.string.gcart_prefix, "");
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
                    ActivitiesWorkFlow.homePageActivity(ShoppingCartActivity.this);
                    finish();
                }

            }
        });

        AnalyticsGoogle.get().trackPage(R.string.gcartempty);
    }

    public void hideNoItems() {
        noItems.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
        lView.setVisibility(View.VISIBLE);
    }

    /**
     * This method manages the deletion of selected elements
     */
    public void deleteSelectedElements() {
        for (int position = items.size() - 1; position >= 0; position--) {
            if (itemsValues.get(position).is_checked) {
                itemsValues.remove(position);
                beginRequestMillis = System.currentTimeMillis();
                EventManager.getSingleton().triggerRequestEvent(
                        new RemoveItemFromShoppingCartEvent(items.get(position)));
                items.remove(position);

            }
        }

        if (items.size() == 0) {
            showNoItems();
        } else {
            hideNoItems();
        }
    }

    public void changeQuantityOfItem(final int position) {
        ArrayList<String> quantities = new ArrayList<String>();

        int stock = items.get(position).getStock();
        int maxQuantity = items.get(position).getMaxQuantity();

        int actualMaxQuantity = stock < maxQuantity ? stock : maxQuantity;

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

        dialogList = new DialogList(this, listener, ID_CHANGE_QUANTITY,
                getString(R.string.shoppingcart_choose_quantity), quantities, crrQuantity);
        dialogList.show();
    }

    public void changeQuantityOfItem(int position, int quantity) {
        items.get(position).setQuantity(quantity);
        beginRequestMillis = System.currentTimeMillis();
        triggerContentEventProgress(new ChangeItemQuantityInShoppingCartEvent(items));
    }

    private void displayShoppingCart(ShoppingCart cart) {
        Log.d(TAG, "displayShoppingCart");
        TextView priceTotal = (TextView) findViewById(R.id.price_total);
        TextView articlesCount = (TextView) findViewById(R.id.articles_count);

        items = new ArrayList<ShoppingCartItem>(cart.getCartItems().values());
        priceTotal.setText(cart.getCartValue());

        String articleString = getResources().getQuantityString(
                R.plurals.shoppingcart_text_article, cart.getCartCount());
        articlesCount.setText(cart.getCartCount() + " " + articleString);
        if (items.size() == 0) {
            showNoItems();
        } else {
            lView = (ExpandableGridViewComponent) findViewById(R.id.shoppingcart_list);
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
                values.discount_value = item.getSavingPercentage();
                values.stock = item.getStock();
                values.min_delivery_time = 0;
                values.max_delivery_time = 99;
                values.simpleData = item.getSimpleData();
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

            mAdapter = new ShoppingBasketListAdapter(this, itemsValues);

            /**
             * Setting Adapter
             */
            lView.setAdapter(mAdapter);
            lView.setFastScrollEnabled(true);

            TextView priceUnreduced = (TextView) findViewById(R.id.price_unreduced);
            if (cartHasReducedItem) {
                priceUnreduced.setText(CurrencyFormatter.formatCurrency(unreduced_cart_price));
                priceUnreduced.setPaintFlags(priceUnreduced.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                priceUnreduced.setVisibility(View.VISIBLE);
            } else {
                priceUnreduced.setVisibility(View.INVISIBLE);
            }

            hideNoItems();
            AnalyticsGoogle.get().trackPage(R.string.gcartwithitems);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        goToProducDetails(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleedEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        Log.d(TAG, "onSuccessEvent: eventType = " + event.getType());
        switch (event.type) {
//        case GET_MIN_ORDER_AMOUNT:
//            minAmount = ((MinOrderAmount) event.result);
//            Log.d(TAG, "onSuccessEvent: minOrderAmount = " + minAmount.getValue());
//            return false;
            // case GET_SESSION_STATE:
            // if ((Boolean) event.result) {
            // goToCheckout();
            // } else {
            // ActivitiesWorkFlow
            // .loginActivity(ShoppingCartActivity.this, true);
            // }
            // return false;
        default:
            AnalyticsGoogle.get().trackLoadTiming(R.string.gshoppingcart, beginRequestMillis);
            displayShoppingCart((ShoppingCart) event.result);
            return true;
        }
    }
}
