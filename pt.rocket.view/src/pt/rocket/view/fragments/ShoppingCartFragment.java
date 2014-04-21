/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
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
import pt.rocket.helpers.RemoveVoucherHelper;
import pt.rocket.helpers.SetVoucherHelper;
import pt.rocket.helpers.checkout.GetNativeCheckoutAvailableHelper;
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
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

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

    private View container;

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
//    private ShoppingBasketFragListAdapter mAdapter;

    /**
     * lView Basket grid view
     */
    private LinearLayout lView;

    /**
     * Button container so we can control positioning in the screen
     */

    private Button checkoutButton;

    /**
     * dialogList for DialogList
     */
    private DialogListFragment dialogList;

    // Voucher
    private Button couponButton;
    private View voucherDivider;
    private TextView voucherError;
    EditText voucherValue;
    private String mVoucher = null;
    private boolean noPaymentNeeded = false;

    private boolean removeVoucher = false;

    private String itemRemoved_sku;

    private String itemRemoved_price;

    public static class CartItemValues {
        public Boolean is_in_wishlist;
        public Boolean is_checked;
        public String product_name;
        public String price;
        public String price_disc;
        public Integer product_id;
        public long quantity;
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
                        EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT), 
                EnumSet.of(MyMenuItem.SEARCH, MyMenuItem.SEARCH_BAR),
                NavigationAction.Basket,
                R.string.shoppingcart_title, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
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

    private void triggerGetShoppingCart() {
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, responseCallback);
    }

    private void triggerRemoveItem(ShoppingCartItem item) {
        ContentValues values = new ContentValues();
        values.put("sku", item.getConfigSimpleSKU());
        itemRemoved_sku = item.getConfigSimpleSKU();
        itemRemoved_price = item.getSpecialPriceVal().toString();
        if(itemRemoved_price == null){
            itemRemoved_price = item.getPriceVal().toString();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartRemoveItemHelper.ITEM, values);
        triggerContentEvent(new GetShoppingCartRemoveItemHelper(), bundle, responseCallback);
    }

    private void triggerIsNativeCheckoutAvailable() {
        triggerContentEventWithNoLoading(new GetNativeCheckoutAvailableHelper(), null,
                responseCallback);
    }

    private void triggerSubmitVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetVoucherHelper.VOUCHER_PARAM, values);
        triggerContentEvent(new SetVoucherHelper(), bundle, responseCallback);
    }

    private void triggerRemoveVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RemoveVoucherHelper.VOUCHER_PARAM, values);
        triggerContentEvent(new RemoveVoucherHelper(), bundle, responseCallback);
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

        lView = null;

        checkoutButton = null;

        dialogList = null;
    }

    /**
     * Set the ShoppingCart layout using inflate
     */
    public void setAppContentLayout() {
        if (getView() == null) {
            return;
        }
        checkoutButton = (Button) getView().findViewById(R.id.checkout_button);
        noItems = (LinearLayout) getView().findViewById(R.id.no_items_container);
        container = getView().findViewById(R.id.shopping_cart_container);
        prepareCouponView();
    }

    public void setListeners() {

        // checkoutButton.setOnClickListener(checkoutClickListener);
        checkoutButton.setOnTouchListener(new OnTouchListener() {

            private DialogGenericFragment messageDialog;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (items != null && items.size() > 0) {
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
        if (!isVisible()) {
            return true;
        }
        if (getBaseActivity() == null) {
            getBaseActivity().handleSuccessEvent(bundle);
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        Log.d(TAG, "onSuccessEvent: eventType = " + eventType);
        switch (eventType) {
        case ADD_VOUCHER:
            couponButton.setText(getString(R.string.voucher_remove));
            voucherError.setVisibility(View.GONE);
//            voucherDivider.setBackgroundColor(R.color.grey_dividerlight);
            getBaseActivity().showContentContainer();
            noPaymentNeeded = false;
            removeVoucher = true;
            triggerGetShoppingCart();
            return true;
        case REMOVE_VOUCHER:
            noPaymentNeeded = false;
            couponButton.setText(getString(R.string.voucher_use));
            voucherError.setVisibility(View.GONE);
//            voucherDivider.setBackgroundColor(R.color.grey_dividerlight);
            getBaseActivity().showContentContainer();
            triggerGetShoppingCart();
            removeVoucher = false;
            return true;
        case NATIVE_CHECKOUT_AVAILABLE:
            boolean isAvailable = bundle.getBoolean(Constants.BUNDLE_RESPONSE_KEY);
            if (isAvailable) {
                Bundle mBundle = new Bundle();
                mBundle.putString(ConstantsIntentExtra.LOGIN_ORIGIN,
                        getString(R.string.mixprop_loginlocationcart));
                getBaseActivity().onSwitchFragment(FragmentType.ABOUT_YOU, mBundle,
                        FragmentController.ADD_TO_BACK_STACK);
            } else {
                goToWebCheckout();
            }
            return true;
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            Log.i(TAG, "code1removing and tracking"+itemRemoved_price);
            TrackerDelegator.trackProductRemoveFromCart(getActivity().getApplicationContext(), itemRemoved_sku, itemRemoved_price);
            getBaseActivity().showContentContainer();
            AnalyticsGoogle.get().trackLoadTiming(R.string.gshoppingcart, mBeginRequestMillis);
            displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
            getBaseActivity().updateSlidingMenu();
            return true;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            if (((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getCartItems() != null
                    && ((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY))
                            .getCartItems().values() != null) {
                TrackerDelegator.trackViewCart(getActivity().getApplicationContext(),
                        ((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY))
                                .getCartItems().values().size());
            }
        
        default:
            getBaseActivity().showContentContainer();
            AnalyticsGoogle.get().trackLoadTiming(R.string.gshoppingcart, mBeginRequestMillis);
            displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
            getBaseActivity().updateSlidingMenu();
        }
        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        if (!isVisible()) {
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case NATIVE_CHECKOUT_AVAILABLE:
            goToWebCheckout();
            break;
        case ADD_VOUCHER:
        case REMOVE_VOUCHER:
            voucherValue.setText("");
            voucherError.setVisibility(View.VISIBLE);
//            voucherDivider.setBackgroundColor(R.color.red_middle);
            getBaseActivity().showContentContainer();
            break;
        }
        if (getBaseActivity().handleErrorEvent(bundle)) {
            return true;
        }

        mBeginRequestMillis = System.currentTimeMillis();
        return true;
    }

    private void displayShoppingCart(ShoppingCart cart) {
        Log.d(TAG, "displayShoppingCart");
        TextView priceTotal = (TextView) getView().findViewById(R.id.price_total);
        TextView articlesCount = (TextView) getView().findViewById(R.id.articles_count);
        TextView extraCostsValue = (TextView) getView().findViewById(R.id.extra_costs_value);
        View extraCostsMain = getView().findViewById(R.id.extra_costs_container);
        TextView voucherValue = (TextView) getView().findViewById(R.id.text_voucher);
        View voucherContainer = getView().findViewById(R.id.voucher_info_container);
        // Get and set the cart value
        setTotal(cart);
        // Set voucher
        if(cart.getCouponDiscount() != null && !cart.getCouponDiscount().equalsIgnoreCase("") && Double.parseDouble(cart.getCouponDiscount()) > 0){
            voucherValue.setText("- "+CurrencyFormatter.formatCurrency(Double.parseDouble(cart.getCouponDiscount())));
            voucherContainer.setVisibility(View.VISIBLE);
        } else {
            voucherContainer.setVisibility(View.GONE);
        }

        items = new ArrayList<ShoppingCartItem>(cart.getCartItems().values());
        double cleanValue = 0;
        try {
            cleanValue = Double.parseDouble(cart.getCartCleanValue());
        } catch (Exception e) {
            e.printStackTrace();
            cleanValue = -1;
        }
        if(cleanValue > 0){
            priceTotal.setText(CurrencyFormatter.formatCurrency(cleanValue));
        } else {
            priceTotal.setText(cart.getCartCleanValue());
        }
        
        TextView shippingValue = (TextView) getView().findViewById(R.id.shipping_value);
        View shippingMain = getView().findViewById(R.id.shipping_container);
        if(!cart.isSumCosts()){
            extraCostsMain.setVisibility(View.VISIBLE);
            extraCostsValue.setText(CurrencyFormatter.formatCurrency(Double.parseDouble(cart.getExtraCosts())));
            if (cart.getShippingValue() != null
                    && !cart.getShippingValue().equalsIgnoreCase("null")
                    && !cart.getShippingValue().equalsIgnoreCase("")) {
                // Validate the shipping value
                if(!cart.getShippingValue().equals("0"))
                    shippingValue.setText(CurrencyFormatter.formatCurrency(Double.parseDouble(cart.getShippingValue())));
                else 
                    shippingValue.setText(getString(R.string.free_label));
                shippingMain.setVisibility(View.VISIBLE);
            }
        } else {
            extraCostsMain.setVisibility(View.GONE);
            if (cart.getSumCostsValue() != null
                    && !cart.getSumCostsValue().equalsIgnoreCase("null")
                    && !cart.getSumCostsValue().equalsIgnoreCase("")) {
                // Validate the shipping value
                if(!cart.getShippingValue().equals("0"))
                    shippingValue.setText(CurrencyFormatter.formatCurrency(Double.parseDouble(cart.getSumCostsValue())));
                else 
                    shippingValue.setText(getString(R.string.free_label));
                shippingMain.setVisibility(View.VISIBLE);
            }
        }
        
        
        String articleString = getResources().getQuantityString(
                R.plurals.shoppingcart_text_article, cart.getCartCount());
        articlesCount.setText(cart.getCartCount() + " " + articleString);
        if (items.size() == 0) {
            showNoItems();
        } else {
            lView = (LinearLayout) getView().findViewById(R.id.shoppingcart_list);
            lView.removeAllViewsInLayout();
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
                lView.addView(getView(i, lView, LayoutInflater.from(getBaseActivity()), values));
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

            TextView priceUnreduced = (TextView) getView().findViewById(R.id.price_unreduced);
            if (cartHasReducedItem) {
                priceUnreduced.setText(CurrencyFormatter.formatCurrency(unreduced_cart_price));
                priceUnreduced.setPaintFlags(priceUnreduced.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                priceUnreduced.setVisibility(View.VISIBLE);
            } else {
                priceUnreduced.setVisibility(View.INVISIBLE);
            }
            if (cart.getVatValue() != null && !cart.getVatValue().equalsIgnoreCase("null") && !cart.getShippingValue().equalsIgnoreCase("")) {
                TextView vatValue = (TextView) getView().findViewById(R.id.vat_value);
                View vatMain = getView().findViewById(R.id.vat_container);
                double cleanVatValue = 0;
                try {
                    cleanVatValue = Double.parseDouble(cart.getVatValue());
                } catch (Exception e) {
                    e.printStackTrace();
                    cleanVatValue = -1;
                }
                if(cleanVatValue > 0){
                    vatValue.setText(CurrencyFormatter.formatCurrency(cleanVatValue));
                } else {
                    vatValue.setText(cart.getVatValue());
                }
                vatMain.setVisibility(View.VISIBLE);
            }
           

            hideNoItems();
            AnalyticsGoogle.get().trackPage(R.string.gcartwithitems);

        }
    }
    
    /**
     * Set the total value
     * @param cart
     * @author sergiopereira
     */
    private void setTotal(ShoppingCart cart) {
        Log.d(TAG, "SET THE TOTAL VALUE");
        // Get views
        TextView totalValue = (TextView) getView().findViewById(R.id.total_value);
        View totalMain = getView().findViewById(R.id.total_container);
        // Set value
        if(!TextUtils.isEmpty(cart.getCartValue())) {
            totalValue.setText(CurrencyFormatter.formatCurrency(Double.parseDouble(cart.getCartValue())));
            totalMain.setVisibility(View.VISIBLE);
        } else 
            Log.w(TAG, "CART VALUES IS EMPTY");
    }

    /**
     * A representation of each item on the list
     */
    private static class Item {

        public TextView itemName;
        public TextView priceView;
        public Button quantityBtn;
        public ImageView productView;
        public View pBar;
        public TextView discountPercentage;
        public TextView priceDisc;
        public ImageView promoImg;
        public TextView variancesContainer;
//        public TextView stockInfo;
        public Button deleteBtn;
        public CartItemValues itemValues;
        public int position;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#finalize()
         */
        @Override
        protected void finalize() throws Throwable {
            itemValues = null;
            itemName = null;
            priceView = null;
            quantityBtn = null;
            productView = null;
            pBar = null;
            discountPercentage = null;
            priceDisc = null;
            promoImg = null;
            variancesContainer = null;
//            stockInfo = null;
            deleteBtn = null;

            super.finalize();
        }
    }

    public View getView(final int position, ViewGroup parent, LayoutInflater mInflater,
            CartItemValues item) {
        View view = mInflater.inflate(R.layout.shopping_basket_product_element_container, parent,
                false);

        final Item prodItem = new Item();
        prodItem.itemValues = item;
        // Log.d( TAG, "getView: productName = " + itemValues.product_name);

        prodItem.itemName = (TextView) view.findViewById(R.id.item_name);
        prodItem.priceView = (TextView) view.findViewById(R.id.item_regprice);
        prodItem.quantityBtn = (Button) view.findViewById(R.id.changequantity_button);

        prodItem.productView = (ImageView) view.findViewById(R.id.image_view);

        prodItem.pBar = view.findViewById(R.id.image_loading_progress);
        prodItem.discountPercentage = (TextView) view.findViewById(R.id.discount_percentage);
        prodItem.priceDisc = (TextView) view.findViewById(R.id.item_discount);
        prodItem.promoImg = (ImageView) view.findViewById(R.id.item_promotion);
        prodItem.variancesContainer = (TextView) view
                .findViewById(R.id.variances_container);
//        prodItem.stockInfo = (TextView) view.findViewById(R.id.item_stock);
        prodItem.deleteBtn = (Button) view.findViewById(R.id.delete_button);
        view.setTag(prodItem);

        prodItem.itemName.setText(prodItem.itemValues.product_name);
        prodItem.itemName.setSelected(true);

        String url = prodItem.itemValues.image;
        AQuery aq = new AQuery(getBaseActivity());
        aq.id(prodItem.productView).image(url, true, true, 0, 0, new BitmapAjaxCallback() {

            @Override
            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                prodItem.productView.setImageBitmap(bm);
                prodItem.productView.setVisibility(View.VISIBLE);
                prodItem.pBar.setVisibility(View.GONE);
            }
        });

        if (!prodItem.itemValues.price.equals(prodItem.itemValues.price_disc)) {
            prodItem.priceDisc.setText(prodItem.itemValues.price_disc);
            prodItem.priceDisc.setVisibility(View.VISIBLE);

            prodItem.priceView.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(View.VISIBLE);
            prodItem.priceView.setPaintFlags(prodItem.priceView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.priceView.setTextColor(getResources().getColor(R.color.grey_middlelight));

            prodItem.discountPercentage.setText("-" + prodItem.itemValues.discount_value.intValue()
                    + "%");
            prodItem.discountPercentage.setVisibility(View.VISIBLE);
            prodItem.promoImg.setVisibility(View.VISIBLE);
        } else {
            prodItem.priceDisc.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(View.INVISIBLE);
            prodItem.promoImg.setVisibility(View.GONE);
            prodItem.discountPercentage.setVisibility(View.GONE);
        }
        prodItem.variancesContainer.setVisibility(View.GONE);
        if (prodItem.itemValues.variation != null) {

            Map<String, String> simpleData = prodItem.itemValues.simpleData;

            if (prodItem.itemValues.variation != null && prodItem.itemValues.variation.length() > 0
                    && !prodItem.itemValues.variation.equalsIgnoreCase(",")
                    && !prodItem.itemValues.variation.equalsIgnoreCase("...")
                    && !prodItem.itemValues.variation.equalsIgnoreCase(".")) {
                prodItem.variancesContainer.setVisibility(View.VISIBLE);
                prodItem.variancesContainer.setText(prodItem.itemValues.variation);
            }
        }

//        if (prodItem.itemValues.stock > 0) {
//            prodItem.stockInfo.setText(getString(R.string.shoppingcart_instock));
//            prodItem.stockInfo.setTextColor(getResources().getColor(R.color.green_stock));
//        } else {
//            prodItem.stockInfo.setText(getString(R.string.shoppingcart_notinstock));
//            prodItem.stockInfo.setTextColor(getResources().getColor(R.color.red_basic));
//        }

        prodItem.deleteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prodItem.itemValues.is_checked = true;
                deleteSelectedElements();
            }
        });

        prodItem.quantityBtn.setText("  " + String.valueOf(prodItem.itemValues.quantity) + "  ");
        prodItem.quantityBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prodItem.itemValues.is_checked = true;
                changeQuantityOfItem(position);
            }
        });
        
        view.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                goToProducDetails(prodItem.position);
            }
        });

        return view;
    }

    /**
     * showNoItems update the layout when basket has no items
     */
    public void showNoItems() {
        setAppContentLayout();
        if (noItems == null) {
            return;
        }
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

    private void goToWebCheckout() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE,
                FragmentType.CHECKOUT_BASKET);
        bundle.putString(ConstantsIntentExtra.LOGIN_ORIGIN,
                getString(R.string.mixprop_loginlocationcart));
        ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.LOGIN, bundle,
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

        if (getBaseActivity().getResources().getStringArray(R.array.restbase_paths)[JumiaApplication.SHOP_ID]
                .contains("mobapi/v1")) {
            triggerIsNativeCheckoutAvailable();
        } else {
            goToWebCheckout();
        }

        // }
    }

    // public void goToCheckout() {
    // ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CHECKOUT_BASKET,
    // FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    // AnalyticsGoogle.get().trackCheckout(items);
    // TrackerDelegator.trackCheckout(getActivity().getApplicationContext(), items);
    // }

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
        if (noItems != null)
            noItems.setVisibility(View.GONE);
        if (container != null)
            container.setVisibility(View.VISIBLE);
        if (lView != null)
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

        long crrQuantity = items.get(position).getQuantity();

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

    private void changeItemQuantityInShoppingCart(List<ShoppingCartItem> items) {
        Bundle bundle = new Bundle();
        ContentValues values = new ContentValues();
        for (ShoppingCartItem item : items) {
            values.put("qty_" + item.getConfigSimpleSKU(), String.valueOf(item.getQuantity()));
        }
        bundle.putParcelable(GetShoppingCartChangeItemQuantityHelper.CART_ITEMS, values);
        triggerContentEventProgress(new GetShoppingCartChangeItemQuantityHelper(), bundle,
                responseCallback);
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

    private void prepareCouponView() {

        voucherValue = (EditText) getView().findViewById(R.id.voucher_name);
        if (mVoucher != null && mVoucher.length() > 0) {
            voucherValue.setText(mVoucher);
        }

        voucherDivider = getView().findViewById(R.id.voucher_divider);
        voucherError = (TextView) getView().findViewById(R.id.voucher_error_message);
        couponButton = (Button) getView().findViewById(R.id.voucher_btn);
        if (removeVoucher) {
            couponButton.setText(getString(R.string.voucher_remove));
        }
        couponButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mVoucher = voucherValue.getText().toString();
                getBaseActivity().hideKeyboard();
                if (mVoucher != null && mVoucher.length() > 0) {
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(SetVoucherHelper.VOUCHER_PARAM, mVoucher);
                    Log.i(TAG, "code1coupon : " + mVoucher);
                    if (couponButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.voucher_use))) {
                        triggerSubmitVoucher(mContentValues);
                    } else {
                        triggerRemoveVoucher(mContentValues);
                    }

                } else {
                    Toast.makeText(getBaseActivity(), getString(R.string.voucher_error_message),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
