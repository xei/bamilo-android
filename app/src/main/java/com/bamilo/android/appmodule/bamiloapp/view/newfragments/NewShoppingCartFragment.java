package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bamilo.android.appmodule.bamiloapp.adapters.CartItemAdapter;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.GetShoppingCartItemsHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartAddMultipleItemsHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartChangeItemQuantityHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartRemoveItemHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.wishlist.AddToWishListHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.wishlist.RemoveFromWishListHelper;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModelFactory;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.tracking.AdjustTracker;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.DarwinRegex;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.Toast;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.cart.UICartUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.CustomToastView;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogGenericFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogListFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.product.UIProductUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.WishListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 *
 */
public class NewShoppingCartFragment extends NewBaseFragment implements IResponseCallback {

    private static final String TAG = NewShoppingCartFragment.class.getSimpleName();

    private static final String cartValue = "";
    private long mBeginRequestMillis;
    // DROID-10
    private long mGABeginRequestMillis;
    private PurchaseCartItem mQuantityChangedItem;
    private List<PurchaseCartItem> items;
    private ViewGroup mCartItemsContainer;
    private View mTotalContainer;
    private ViewGroup mDiscountContainer;
    private ViewGroup mDiscountContainerShadow;
    private View mCheckoutButton;
    private View mCallToOrderButton;
    private View mFreeShippingView;
    private DialogListFragment dialogList;
    private RecyclerView mCartRecycler;
    private int mCartItemsCount;
    private DialogGenericFragment dialogLogout;
/* DROID-63
    private TextView mCouponButton;
    private EditText mVoucherView;
    private String mVoucherCode;
*/
    private String mItemRemovedSku;
    private String mPhone2Call = "";
    private double mItemRemovedPriceTracking = 0d;
    private long mItemRemovedQuantity;
    private double mItemRemovedRating;
    private String mItemRemovedCartValue;
    private String mItemsToCartDeepLink;
    private int selectedPosition;
    private int crrQuantity;

    View mClickedFavourite;
    private AppBarLayout.LayoutParams  startParams;
    private boolean pageTracked = false;
    //RecommendManager recommendManager;

    private SimpleEventModel removeFromWishListEventModel;
    private MainEventModel removeFromCartEventModel;
    private MainEventModel addToWishListEventModel;

    /**
     * Empty constructor
     */
    public NewShoppingCartFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.BASKET_INDICATOR),
                NavigationAction.BASKET,
                R.layout.new_shopping_basket,
                R.string.basket_fragment_title,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mItemsToCartDeepLink = arguments.getString(ConstantsIntentExtra.DATA);
            arguments.remove(ConstantsIntentExtra.DATA);
        }

/* DROID-63
        // Get saved state
        if(savedInstanceState != null) {
            mVoucherCode = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
        }
*/

        selectedPosition = 0;
        //recommendManager = new RecommendManager();

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CART.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get and set views
        mCartRecycler = (RecyclerView) view.findViewById(R.id.shoppingcart_list);
        mCartRecycler.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mCartRecycler.setLayoutManager(llm);
        mCartRecycler.removeOnScrollListener(scrollChanged);
        setAppContentLayout(view);

        mCheckoutButton = view.findViewById(R.id.checkout_button);
        mCheckoutButton.setOnClickListener(this);

    }

    RecyclerView.OnScrollListener scrollChanged = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            mTotalContainer.setVisibility(View.VISIBLE);
            SetAnimation(mTotalContainer, View.VISIBLE);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                mDiscountContainerShadow.setVisibility(View.VISIBLE);
                mDiscountContainer.setVisibility(View.VISIBLE);
                SetAnimation(mDiscountContainer, View.VISIBLE);
                SetAnimation(mDiscountContainerShadow, View.VISIBLE);
                LinearLayoutManager layoutManager = ((LinearLayoutManager)mCartRecycler.getLayoutManager());
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition >= mCartItemsCount)
                {
                    //mTotalContainer.setVisibility(View.GONE);
                    //mDiscountContainer.setVisibility(View.GONE);
                    //mDiscountContainerShadow.setVisibility(View.GONE);
                    SetAnimation(mTotalContainer, View.GONE);
                    SetAnimation(mDiscountContainer, View.GONE);
                    SetAnimation(mDiscountContainerShadow, View.GONE);
                }
            }
            else
            {
                //mDiscountContainer.setVisibility(View.GONE);
                //mDiscountContainerShadow.setVisibility(View.GONE);

                SetAnimation(mDiscountContainer, View.GONE);
                SetAnimation(mDiscountContainerShadow, View.GONE);
            }
        }


    };

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        final Bundle args = getArguments();
        if(args != null) {
            if(args.containsKey(AddToWishListHelper.ADD_TO_WISHLIST)){
                String mClickedSku = args.getString(AddToWishListHelper.ADD_TO_WISHLIST);
                if(BamiloApplication.isCustomerLoggedIn() && mClickedSku != null && mClickedSku.trim()!=""){
                    triggerAddToWishList(mClickedSku);
                    //TrackerDelegator.trackAddToFavorites(mClicked);
                }
                args.remove(AddToWishListHelper.ADD_TO_WISHLIST);
            }
            else if(args.containsKey(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST)){
                String mClickedSku = args.getString(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST);
                if(BamiloApplication.isCustomerLoggedIn() && mClickedSku != null && mClickedSku.trim()!=""){
                    triggerRemoveFromWishList(mClickedSku);
                    //TrackerDelegator.trackRemoveFromFavorites(mClicked);
                }
                args.remove(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST);
            }

        }
        mBeginRequestMillis = System.currentTimeMillis();
        mGABeginRequestMillis = System.currentTimeMillis();
        // Case deep link
        if (!TextUtils.isEmpty(mItemsToCartDeepLink)) addItemsToCart(mItemsToCartDeepLink);
        // Case normal
        else triggerGetShoppingCart();
        /*Toolbar toolbar = (Toolbar) getBaseActivity().findViewById(R.id.toolbar);  // or however you need to do it for your code
        startParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        AppBarLayout.LayoutParams params = startParams;
        params.setScrollFlags(0);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
/* DROID-63
        if(mVoucherView != null) {
            mVoucherCode = mVoucherView.getText().toString();
        }
*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE");
/* DROID-63
        // Save the voucher code
        if(mVoucherView != null) {
            mVoucherCode = mVoucherView.getText().toString();
            outState.putString(ConstantsIntentExtra.ARG_1, mVoucherCode);
        }
*/
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
        releaseVars();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
        releaseVars();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    /*
     * ####### LAYOUT #######
     */

    /**
     * Set the ShoppingCart layout using inflate
     */
    public void setAppContentLayout(View view) {
        mCheckoutButton = view.findViewById(R.id.checkout_button);
        mTotalContainer = view.findViewById(R.id.total_container);
        mDiscountContainer = (ViewGroup) view.findViewById(R.id.discount_container);
        mDiscountContainerShadow = (ViewGroup) view.findViewById(R.id.discount_container_shadow);


        // Get free shipping
        mFreeShippingView = view.findViewById(R.id.cart_total_text_shipping);
    }

    /**
     * Set the total value
     */
    private void setTotal(@NonNull PurchaseEntity cart) {
        Print.d(TAG, "SET THE TOTAL VALUE");
        // Get views
        TextView totalValue = (TextView) mTotalContainer.findViewById(R.id.total_value);
        TextView quantityValue = (TextView) mTotalContainer.findViewById(R.id.total_quantity);
        // Set views
        totalValue.setText(CurrencyFormatter.formatCurrency(cart.getTotal()));
        quantityValue.setText(TextUtils.getResourceString(getBaseActivity(), R.string.cart_total_quantity, new Integer[] {cart.getCartCount()}));


        mTotalContainer.setVisibility(View.VISIBLE);

    }

    /**
     * showNoItems update the layout when basket has no items
     */
    public void showNoItems() {
        showErrorFragment(ErrorLayoutFactory.CART_EMPTY_LAYOUT, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        });
        getBaseActivity().hideKeyboard();
    }


    /*
     * ####### LISTENER #######
     */


    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case next button
        if(id == R.id.checkout_button) {
            onClickCheckoutButton();
        }
        // Case super
        else super.onClick(view);
    }


    /**
     * Process the click on checkout button.
     */
    private void onClickCheckoutButton() {

        //recommendManager.sendPurchaseRecommend();
        //sendRecommend();

        if (items != null && items.size() > 0) {

            SimpleEventModel sem = SimpleEventModelFactory.createModelForCheckoutStart(items);
            TrackerManager.trackEvent(getContext(), EventConstants.CheckoutStart, sem);

            Bundle bundle = new Bundle();
            bundle.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, true);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case invalid cart
        else {
            String title = getString(R.string.basket_label);
            String message = getString(R.string.shoppingcart_alert_message_no_items);
            String buttonText = getString(R.string.ok_label);
            DialogGenericFragment.createInfoDialog(title, message, buttonText).show(getActivity().getSupportFragmentManager(), null);
        }
    }



    /*
     * ####### TRIGGERS #######
     */

    /**
     * Trigger to get cart items validating FavouritesFragment state is completed
     */
    private void triggerGetShoppingCart() {
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
    }

    /**
     * Trigger to remove item from cart
     */
    private void triggerRemoveItem(PurchaseCartItem item) {

        removeFromCartEventModel = new MainEventModel(getString(TrackingPage.CART.getName()), EventActionKeys.REMOVE_FROM_CART,
                item.getSku(), (long) item.getPrice(),
                MainEventModel.createRemoveFromCartEventModelAttributes(item.getSku()));

        mItemRemovedSku = item.getConfigSimpleSKU();
        mItemRemovedPriceTracking = item.getPriceForTracking();
        mItemRemovedQuantity = item.getQuantity();
        mItemRemovedRating = -1d;
        if (TextUtils.isEmpty(cartValue)) {
            TextView totalValue = (TextView) mTotalContainer.findViewById(R.id.total_value);
            mItemRemovedCartValue = totalValue.toString();
        } else {
            mItemRemovedCartValue = cartValue;
        }
        triggerContentEventProgress(new ShoppingCartRemoveItemHelper(), ShoppingCartRemoveItemHelper.createBundle(item.getConfigSimpleSKU()), this);
    }


    /**
     * Trigger to add all items to cart (Deep link).
     */
    private void triggerAddAllItems(ArrayList<String> values) {
        triggerContentEventProgress(new ShoppingCartAddMultipleItemsHelper(), ShoppingCartAddMultipleItemsHelper.createBundle(values), this);
    }

    /**
     * Trigger used to change quantity
     */
    public void triggerChangeItemQuantityInShoppingCart(int position, int quantity) {
        PurchaseCartItem item = items.get(position);
        item.setQuantity(quantity);
        mBeginRequestMillis = System.currentTimeMillis();
        mGABeginRequestMillis = System.currentTimeMillis();
        mQuantityChangedItem = item;
        //
        triggerContentEventProgress(new ShoppingCartChangeItemQuantityHelper(), ShoppingCartChangeItemQuantityHelper.createBundle(item.getConfigSimpleSKU(), quantity), this);
    }

    /**
     *
     */
    private void releaseVars() {
        mCartItemsContainer = null;
        mCheckoutButton = null;
        dialogList = null;
    }


    /**
     * Get items from string
     *
     * @author sergiopereira
     */
    private void addItemsToCart(String items) {
        String[] itemsToCart = items.split(DarwinRegex.SKU_DELIMITER);
        Print.i(TAG, "RECEIVED : " + items + " " + itemsToCart.length);
        // Create arguments to add all items to cart
        ArrayList<String> productBySku = new ArrayList<>();
        Collections.addAll(productBySku, itemsToCart);
        // Case valid deep link
        if (!productBySku.isEmpty()) {
            triggerAddAllItems(productBySku);
        }
        // Case invalid deep link
        else {
            triggerGetShoppingCart();
        }
    }

    /*
     * ####### RESPONSES #######
     */

    /**
     *
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
/* DROID-63*/
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Update cart info
        super.handleSuccessEvent(baseResponse);



        Bundle params;
        EventType eventType = baseResponse.getEventType();

        Print.d(TAG, "onSuccessEvent: eventType = " + eventType);
        switch (eventType) {
            case REMOVE_PRODUCT_FROM_WISH_LIST:
                hideActivityProgress();
                // Force wish list reload for next time
                WishListFragment.sForceReloadWishListFromNetwork = true;
                // Update value
                updateWishListValue(false);
                if (removeFromWishListEventModel != null) {
                    TrackerManager.trackEvent(getContext(), EventConstants.RemoveFromWishList, removeFromWishListEventModel);
                }
                break;

            case ADD_PRODUCT_TO_WISH_LIST:
                hideActivityProgress();
                // Force wish list reload for next time
                WishListFragment.sForceReloadWishListFromNetwork = true;
                // Update value
                updateWishListValue(true);
                if (addToWishListEventModel != null) {
                    TrackerManager.trackEvent(getContext(), EventConstants.AddToWishList, addToWishListEventModel);
                }
                break;

            case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
                if (removeFromCartEventModel != null) {
                    TrackerManager.trackEvent(getContext(), EventConstants.RemoveFromCart, removeFromCartEventModel);
                }
                //Print.i(TAG, "code1removing and tracking" + itemRemoved_price);
                params = new Bundle();
                params.putString(TrackerDelegator.SKU_KEY, mItemRemovedSku);
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                params.putDouble(TrackerDelegator.PRICE_KEY, mItemRemovedPriceTracking);
                params.putLong(TrackerDelegator.QUANTITY_KEY, mItemRemovedQuantity);
                params.putDouble(TrackerDelegator.RATING_KEY, mItemRemovedRating);
                params.putString(TrackerDelegator.CARTVALUE_KEY, mItemRemovedCartValue);
                TrackerDelegator.trackProductRemoveFromCart(params);
                // DROID-10 TrackerDelegator.trackLoadTiming(params);
//                TrackerDelegator.trackScreenLoadTiming(R.string.gaRemoveItemFromShoppingCart, mGABeginRequestMillis, mItemRemovedSku);
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
                hideActivityProgress();
                break;
            case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
                hideActivityProgress();
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
                break;
            case GET_SHOPPING_CART_ITEMS_EVENT:
                hideActivityProgress();
                PurchaseEntity purchaseEntity = (PurchaseEntity) baseResponse.getContentData();
                params = new Bundle();
                params.putParcelable(AdjustTracker.CART, purchaseEntity);
                TrackerDelegator.trackPageForAdjust(TrackingPage.CART_LOADED, params);
                displayShoppingCart(purchaseEntity);
                if (!pageTracked) {
                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CART.getName()), getString(R.string.gaScreen),
                            "",
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }
                break;
            case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
                onAddItemsToShoppingCartRequestSuccess(baseResponse);
                break;
            default:
                PurchaseEntity defPurchaseEntity = (PurchaseEntity) baseResponse.getMetadata().getData();
                displayShoppingCart(defPurchaseEntity);
                break;
        }
    }


    /**
     *
     */
    private void onAddItemsToShoppingCartRequestSuccess(BaseResponse baseResponse){
        hideActivityProgress();
        ShoppingCartAddMultipleItemsHelper.AddMultipleStruct addMultipleStruct = (ShoppingCartAddMultipleItemsHelper.AddMultipleStruct) baseResponse.getContentData();

        if (addMultipleStruct.getErrorMessages() != null) {
            ArrayList<String> notAdded = addMultipleStruct.getErrorMessages();
            if (!notAdded.isEmpty()) {
                getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.some_products_not_added));
            }
        }

        getBaseActivity().updateCartInfo();
        if (BamiloApplication.INSTANCE.getCart() != null) {
            displayShoppingCart(BamiloApplication.INSTANCE.getCart());
        }
    }


    /**
     *
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {

        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        hideActivityProgress();
        EventType eventType = baseResponse.getEventType();

        // Validate generic errors
        if (super.handleErrorEvent(baseResponse)) {
            if(eventType == EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT ){
                items.get(selectedPosition).setQuantity(crrQuantity); //restarts the previous position for load selected quantity before the error
            }
            return;
        }

        switch (eventType) {
            case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
                showNoItems();
                break;
            case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
                if (items.size() == 0) {
                    showNoItems();
                } else {
                    hideNoItems();
                }
                break;
            case REMOVE_PRODUCT_FROM_WISH_LIST:
            case ADD_PRODUCT_TO_WISH_LIST:
            default:
                break;
        }
        mBeginRequestMillis = System.currentTimeMillis();
        mGABeginRequestMillis = System.currentTimeMillis();
    }

    /*private void sendRecommend() {
        *//*recommendedAdapter.clear();
        recommendedAdapter.notifyDataSetChanged();
        recyclerView.invalidate();*//*

        recommendManager.sendCartRecommend(new RecommendListCompletionHandler() {
            @Override
            public void onRecommendedRequestComplete(String category, List<RecommendedItem> data) {

            }
        });

    }*/

    /**
     * Display shopping cart info
     */
    private void displayShoppingCart(PurchaseEntity cart) {
        Print.d(TAG, "displayShoppingCart");
        // Case invalid cart
        if (cart == null || CollectionUtils.isEmpty(cart.getCartItems())) {
            showNoItems();
            return;
        }
        // Case invalid view
        if (getView() == null) {
            showErrorFragment(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, this);
            return;
        }

        // Case valid state
        items = cart.getCartItems();
        mCartItemsCount = items.size();
        CartItemAdapter mItemsAdapter = new CartItemAdapter(getBaseActivity(), items, onQuantityChangeClickListener, onRemoveItemClickListener, cart, onClickWishListButton, onProdcutClickListener);
        mItemsAdapter.baseFragment = this;
        mCartRecycler.setAdapter(mItemsAdapter);
        setTotal(cart);
        setDiscount(cart);

        LinearLayoutManager layoutManager = ((LinearLayoutManager)mCartRecycler.getLayoutManager());
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        if (lastVisiblePosition >= mCartItemsCount)
        {
            //mTotalContainer.setVisibility(View.GONE);
            //mDiscountContainer.setVisibility(View.GONE);
            //mDiscountContainerShadow.setVisibility(View.GONE);
            SetAnimation(mTotalContainer, View.GONE);
            SetAnimation(mDiscountContainer, View.GONE);
            SetAnimation(mDiscountContainerShadow, View.GONE);
        }

        showFragmentContentContainer();

    }

    private void setDiscount(PurchaseEntity cart) {
        mDiscountContainer.setVisibility(View.GONE);
        mDiscountContainerShadow.setVisibility(View.GONE);
        mCartRecycler.removeOnScrollListener(scrollChanged);
        mDiscountContainer.removeAllViews();


        View totalView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.new_shopping_basket_discount_element, (ViewGroup) mDiscountContainer, false);
        TextView labelT = (TextView) totalView.findViewById(R.id.discount_label);
        TextView valueT = (TextView) totalView.findViewById(R.id.discount_amount);
        labelT.setText(R.string.cart_total_amount);
        valueT.setText(CurrencyFormatter.formatCurrency(cart.getSubTotalUnDiscounted()));
        mDiscountContainer.addView(totalView);

        View discountView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.new_shopping_basket_discount_element, (ViewGroup) mDiscountContainer, false);
        TextView label = (TextView) discountView.findViewById(R.id.discount_label);
        TextView value = (TextView) discountView.findViewById(R.id.discount_amount);
        label.setText(R.string.cart_total_discount);
        value.setText(CurrencyFormatter.formatCurrency(cart.getSubTotalUnDiscounted()+cart.getShippingValue()-cart.getTotal()));
        mDiscountContainer.addView(discountView);

        LinearLayoutManager layoutManager = ((LinearLayoutManager)mCartRecycler.getLayoutManager());
        int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1 && cart.getCartItems().size() > 1) {
            mCartRecycler.addOnScrollListener(scrollChanged);
        }

        if (cart.getCartItems().size() == 1)
        {
            mTotalContainer.setVisibility(View.GONE);
        }

    }


    /**
     * Fill view item with PurchaseCartItem data
     */

    public View createCartItemView(final int position, ViewGroup parent, LayoutInflater mInflater, final PurchaseCartItem item) {
        View view = mInflater.inflate(R.layout.shopping_cart_product_container, parent, false);
        Log.d( TAG, "getView: productName = " + item.getName());
        // Get item
        ImageView productView = (ImageView) view.findViewById(R.id.image_view);
        View pBar = view.findViewById(R.id.image_loading_progress);
        TextView itemName = (TextView) view.findViewById(R.id.cart_item_text_name);
        TextView priceView = (TextView) view.findViewById(R.id.cart_item_text_price);
        TextView quantityBtn = (TextView) view.findViewById(R.id.cart_item_button_quantity);
        ImageView shopFirstImage = (ImageView) view.findViewById(R.id.cart_item_image_shop_first);
        TextView deleteBtn = (TextView) view.findViewById(R.id.cart_item_button_delete);
        TextView variationName = (TextView) view.findViewById(R.id.cart_item_text_variation);
        TextView variationValue = (TextView) view.findViewById(R.id.cart_item_text_variation_value);
        // Set item
        itemName.setText(item.getName());
        itemName.setSelected(true);
        String imageUrl = item.getImageUrl();
        // Variation
        UICartUtils.setVariation(item, variationName, variationValue);
        // Hide shop view image if is_shop is false
        UIProductUtils.setShopFirst(item, shopFirstImage);
        // Show shop first overlay message
        UIProductUtils.showShopFirstOverlayMessage(this, item, shopFirstImage);
        // Image
        //RocketImageLoader.instance.loadImage(imageUrl, productView, pBar, R.drawable.no_image_small);
        ImageManager.getInstance().loadImage(imageUrl, productView, pBar, R.drawable.no_image_large, false);
        // Price
        UIProductUtils.setPriceRules(item, priceView);
        // Delete
        deleteBtn.setTag(R.id.position, position);
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedElements(view);
            }
        });
        // Quantity
        quantityBtn.setText(String.valueOf(item.getQuantity()));
        if(item.getMaxQuantity() > 1) {
            quantityBtn.setEnabled(true);
            quantityBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQuantityDialog(position);
                }
            });
        } else {
            quantityBtn.setEnabled(false);
            if (DeviceInfoHelper.isPosJellyBean()) {
                quantityBtn.setBackground(null);
            } else {
                //noinspection deprecation
                quantityBtn.setBackgroundDrawable(null);
            }
        }
        // Save the position to process the click on item
        view.setTag(R.id.target_sku, item.getSku());
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    goToProductDetails((String) v.getTag(R.id.target_sku));
                } catch (NullPointerException e) {
                    Print.w(TAG, "WARNING: NPE ON GET CLICKED TAG");
                }
            }
        });

        return view;
    }

    /**
     * Function to redirect to the selected product details.
     */
    private void goToProductDetails(String sku) {
        if (!TextUtils.isEmpty(sku)) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcart_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * This method manages the deletion of selected elements
     */
    public void deleteSelectedElements(View view) {
        // Get position
        int position = (int) view.getTag(R.id.position);
        // Validate position
        if (position < items.size()) {
            mBeginRequestMillis = System.currentTimeMillis();
            mGABeginRequestMillis = System.currentTimeMillis();
            triggerRemoveItem(items.get(position));
        }
    }

    public void hideNoItems() {
        showFragmentContentContainer();
    }

    public void showQuantityDialog(final int position) {
        ArrayList<String> quantities = new ArrayList<>();
        selectedPosition = position;
        for (int i = 1; i <= items.get(position).getMaxQuantity(); i++) {
            quantities.add(String.valueOf(i));
        }
        crrQuantity = items.get(position).getQuantity();
        OnDialogListListener listener = new OnDialogListListener() {
            @Override
            public void onDialogListItemSelect(int quantity, String value) {
                if(quantity != crrQuantity -1){
                    triggerChangeItemQuantityInShoppingCart(position, quantity+1);
                }
                if(dialogList != null) {
                    dialogList.dismissAllowingStateLoss();
                    dialogList = null;
                }
            }
            @Override
            public void onDismiss() {
            }
        };
        dialogList = DialogListFragment.newInstance(this, listener, getString(R.string.choose_quantity), quantities, crrQuantity - 1);
        dialogList.show(getActivity().getSupportFragmentManager(), null);
    }


    OnClickListener onQuantityChangeClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            //if (view.getTag() == null) return;
            int max = (int)view.getTag(R.id.item_max);
            int position = (int)view.getTag(R.id.item_position);
            int quantity = (int)view.getTag(R.id.item_quantity);
            int addValue = (int)view.getTag(R.id.item_change);
            quantity += addValue;
            if (quantity>max)
            {
                CustomToastView.makeText(getBaseActivity(), String.format(getResources().getString( R.string.reached_max_quantity), max), Toast.LENGTH_LONG).show();
                return;
            }
            if (quantity == 0)
            {
                //Toast.makeText(getBaseActivity(), R.string.reached_min_quantity, Toast.LENGTH_LONG).show();
                return;
            }


            triggerChangeItemQuantityInShoppingCart(position, quantity);
        }
    };

    OnClickListener onRemoveItemClickListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {
            //if (view.getTag() == null) return;
            dialogLogout = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.remove_item_from_cart_title),
                    getString(R.string.remove_item_from_cart_question),
                    getString(R.string.no_label),
                    getString(R.string.yes_label),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.button2) {
                                int position = (int)view.getTag(R.id.item_position);
                                PurchaseCartItem item = items.get(position);
                                triggerRemoveItem(item);
                            }
                            dialogLogout.dismiss();
                        }
                    });
            dialogLogout.show(getBaseActivity().getSupportFragmentManager(), null);


        }
    };

    private void triggerAddToWishList(String sku) {
        addToWishListEventModel = new MainEventModel(getString(TrackingPage.CART.getName()), EventActionKeys.ADD_TO_WISHLIST,
                sku, SimpleEventModel.NO_VALUE,
                MainEventModel.createAddToWishListEventModelAttributes(null, null, false));
        if (items != null) {
            for (PurchaseCartItem item : items) {
                if (item.getSku().equals(sku)) {
                    addToWishListEventModel.value = (long) item.getPrice();
                    addToWishListEventModel.customAttributes = MainEventModel.createAddToWishListEventModelAttributes(item.getSku(), item.getCategoryKey(), true);
                    break;
                }
            }
        }
        triggerContentEventProgress(new AddToWishListHelper(), AddToWishListHelper.createBundle(sku), this);
    }

    private void triggerRemoveFromWishList(String sku) {
        removeFromWishListEventModel = new SimpleEventModel();
        removeFromWishListEventModel.category = getString(TrackingPage.CART.getName());
        removeFromWishListEventModel.action = EventActionKeys.REMOVE_FROM_WISHLIST;
        removeFromWishListEventModel.label = sku;
        removeFromWishListEventModel.value = SimpleEventModel.NO_VALUE;
        if (items != null) {
            for (PurchaseCartItem item : items) {
                if (item.getSku().equals(sku)) {
                    removeFromWishListEventModel.value = (long) item.getPrice();
                    break;
                }
            }
        }
        triggerContentEventProgress(new RemoveFromWishListHelper(), RemoveFromWishListHelper.createBundle(sku), this);
    }

    OnClickListener onClickWishListButton = new OnClickListener() {
        @Override
        public void onClick(View view) {
        // Validate customer is logged in
            mClickedFavourite = (View)view.getTag(R.id.cart_fav_icon);
        String mProductSku = (String)view.getTag(R.id.sku);
        if (BamiloApplication.isCustomerLoggedIn()) {
            try {
                // Get item
                if (mClickedFavourite.isSelected()) {
                    triggerRemoveFromWishList(mProductSku);
                    //TrackerDelegator.trackRemoveFromFavorites(mProduct);
                } else {
                    triggerAddToWishList(mProductSku);
                    //TrackerDelegator.trackAddToFavorites(mProduct);
                }
            } catch (NullPointerException e) {
            }
        } else {
            // Save values to end action after login
            final Bundle args = getArguments();
            if(args != null) {
                if (mClickedFavourite.isSelected()) {
                    args.putString(RemoveFromWishListHelper.REMOVE_FROM_WISHLIST, mProductSku);
                } else {
                    args.putString(AddToWishListHelper.ADD_TO_WISHLIST, mProductSku);
                }
            }

            // Goto login
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }};

    /**
     * Method used to update the wish list value.
     */
    private void updateWishListValue(boolean value) {
        try {
            //boolean value = mProduct.isWishList();
            if (mClickedFavourite != null)
                mClickedFavourite.setSelected(value);

           // mWishListButton.setSelected(value);
        } catch (NullPointerException e) {
        }
    }




    OnClickListener onProdcutClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String sku = (String)view.getTag(R.id.target_sku);
            if (!TextUtils.isEmpty(sku)) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
                bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcart_prefix);
                bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    };


    private void SetAnimation(View view, int visibility)
    {
        if (visibility == View.GONE)
        {
            view.animate().alpha(0.0f).setDuration(400);;
        }
        else
        {
            view.animate().alpha(1.0f).setDuration(400);;
        }
    }

}
