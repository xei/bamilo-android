/**
 * 
 */
package com.mobile.view.fragments;

import java.util.EnumSet;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CustomerUtils;
import com.mobile.framework.utils.DarwinRegex;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.cart.GetShoppingCartAddItemHelper;
import com.mobile.helpers.session.GetLoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * Class used to add items to cart from deep link
 * @deprecated due to a better way of adding items to cart. {@see ShoppingCartFragment#addItemsToCart(String)}
 * @author sergiopereira
 * 
 */
@Deprecated
public class HeadlessAddToCartFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = LogTagHelper.create(HeadlessAddToCartFragment.class);

    private final static int DELAY_FOR_EACH_ITEM = 10000;
    
    private static final String P_TAG = "p";
    
    private static final String SKU_TAG = "sku";
    
    private static final String QT_TAG = "quantity";
    
    private static final String DEF_QT_VALUE = "1";
    
    private String[] mItemsToCart;

    private Thread mAddToCartThread;

    private Handler mTimer;
    
    private int mAddedItemsCounter = 1;

    private boolean hasErrorInAdd = false;

    private boolean isFinishing = false;


    /**
     * Get instance
     * @return HeadlessAddToCartFragment
     */
    public static HeadlessAddToCartFragment getInstance() {
        return new HeadlessAddToCartFragment();
    }

    /**
     * Empty constructor
     */
    public HeadlessAddToCartFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Unknown,
                R.layout.fragment_headless,
                0,
                KeyboardState.NO_ADJUST_CONTENT);
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
        // Retain on configuration change
        setRetainInstance(true);
        // Get items from arguments
        getItemsToCart();
        // Validate session to add items
        validateSession();
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
        // Show loading
        showFragmentLoading();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onStart()
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
        // Validate if fragment comes from background
        if(isFinishing) gotoCartFragment();
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
        isFinishing = true;
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
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        // Clean
        cleanAddToCartThread();
        cleanTimer();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        cleanAddToCartThread();
        cleanTimer();
        return super.allowBackPressed();
    }

    /**
     * Get items from string
     * @author sergiopereira
     */
    private void getItemsToCart(){
        Bundle bundle = getArguments();
        String items = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        mItemsToCart = items.split(DarwinRegex.SKU_DELIMITER);
        Log.d(TAG, "RECEIVED : " + items + " " + mItemsToCart.length);
    }
    
    /**
     * Validate the current session to add all items to cart
     * @author sergiopereira
     */
    private void validateSession(){
        Log.d(TAG, "VALIDATE SESSION");
        if(JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
            // Try auto login
            triggerAutoLogin();
        } else {
            // Start the thread used to add all items
            startAddToCartThread();
            // Start the timer
            //startTimer();
        }
    }    
    
    /**
     * Update the counter for each response
     * @author sergiopereira
     */
    private synchronized void updateCounter(){
        Log.d(TAG, "UPDATE COUNTER");
        if((mItemsToCart == null || mAddedItemsCounter == mItemsToCart.length) && !isFinishing) gotoCartFragment();
        else mAddedItemsCounter++;
    }
    
    /**
     * Goto cart to show items
     * @author sergiopereira
     */
    private void gotoCartFragment(){
        Log.d(TAG, "FINISHED");
        isFinishing  = true;
        cleanAddToCartThread();
        cleanTimer();
        if(hasErrorInAdd) Toast.makeText(getBaseActivity(), getString(R.string.some_products_not_added), Toast.LENGTH_LONG).show();
        FragmentController.getInstance().popLastEntry(FragmentType.HEADLESS_CART.toString());
        getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    
    /**
     * ########### TIMEOUT RUNNABLE ########### 
     */
    
    /**
     * Start the count down
     * @author sergiopereira 
     */
    @SuppressWarnings("unused")
    private void startTimer(){
        mTimer = new Handler();
        mTimer.postDelayed(mTimerRunnable, mItemsToCart.length * DELAY_FOR_EACH_ITEM);
    }
    
    /**
     * Clean the timer for garbage
     * @author sergiopereira 
     */
    private void cleanTimer(){
        if(mTimer != null) { mTimer.removeCallbacks(mTimerRunnable); mTimer = null; }
    }
    
    /**
     * Runnable for timer
     * @author sergiopereira
     */
    Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "RUN TIMER RUNNABLE");
            gotoCartFragment();
        }
    };
    
    /**
     * ########### ADD RUNNABLE ########### 
     */
    
    /**
     * Start the thread to add items to cart
     * @author sergiopereira
     */
    private void startAddToCartThread(){
        mAddToCartThread = new Thread(mAddToCartRunnable);
        mAddToCartThread.start();
    }
    
    /**
     * Clean the add thread for garbage
     * @author sergiopereira
     */
    private void cleanAddToCartThread() {
        if (mAddToCartThread != null) {
            mAddToCartThread.interrupt();
            mAddToCartThread = null;
        }
    }
    
    /**
     * Runnable to add items to cart
     * @author sergiopereira
     */
    Runnable mAddToCartRunnable = new Runnable() {
        @Override
        public void run() {
            if(mItemsToCart != null){
                Log.d(TAG, "NUMBER OF ITEMS: " + mItemsToCart.length);
                for (String sku : mItemsToCart) {
                    ContentValues values = new ContentValues();
                    values.put(P_TAG, sku.split("-")[0]);
                    values.put(SKU_TAG, sku);
                    values.put(QT_TAG, DEF_QT_VALUE);
                    triggerAddItemToCart(values);
                }
            }
            else {
                Log.d(TAG, "ITEMS TO CART IS NULL");
                gotoCartFragment();
            }
        }
    };
        
    /**
     * ############# REQUESTS #############
     */

    /**
     * Trigger the event to add the item to cart
     * @param values
     * @author sergiopereira
     */
    private void triggerAddItemToCart(ContentValues values) {
        Log.i(TAG, "TRIGGER: ADD TO CART: " + values.toString());
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        triggerContentEventWithNoLoading(new GetShoppingCartAddItemHelper(), bundle, this);
    }
    
    /**
     * Trigger to perform the auto login
     * @author sergiopereira
     */
    private void triggerAutoLogin(){
        Log.i(TAG, "TRIGGER: AUTO LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        triggerContentEventWithNoLoading(new GetLoginHelper(), bundle, this);
    }
    
    /**
     * ############# RESPONSE #############
     */

    /**
     * Process the success response
     * @param bundle
     * @return true/false
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
        case LOGIN_EVENT:
            Log.d(TAG, "RECEIVED LOGIN_EVENT");
            startAddToCartThread();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED ADD_ITEM_TO_SHOPPING_CART_EVENT");
            updateCounter();
            break;
        default:
            break;
        }

        return true;
    }

    /**
     * Process the error response
     * @param bundle
     * @return true/false
     */
    protected boolean onErrorEvent(Bundle bundle) {
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case LOGIN_EVENT:
            Log.d(TAG, "RECEIVED LOGIN_EVENT");
            clearCredentials();
            startAddToCartThread();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED ADD_ITEM_TO_SHOPPING_CART_EVENT");
            hasErrorInAdd = true;
            updateCounter();
            break;
        default:
            break;
        }

        return true;
    }

    /**
     * ########### RESPONSE LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
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
