/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.widget.Toast;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.AddableToCartListAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.framework.objects.AddableToCart;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.LastViewedAddableToCart;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetRecentlyViewedHelper;
import pt.rocket.helpers.GetShoppingCartAddItemHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show all last viewed items from database
 * 
 * @author Andre Lopes
 */
public class RecentlyViewedFragment extends BaseFragment implements IResponseCallback,
        OnClickListener {

    public final static String TAG = LogTagHelper.create(RecentlyViewedFragment.class);

    private static RecentlyViewedFragment mRecentlyViewedFragment;

    private AddableToCartListAdapter mRecentlyViewedAdapter;

    private ArrayList<AddableToCart> mRecentlyViewed;

    private View mRecentlyViewedEmptyView;

    private GridView mRecentlyViewedGridView;

    private Button mClearAllButton;

    private View mLoadingView;

    private View mRecentlyViewedView;

    private int mNumberOfItemsForCart = LastViewedAddableToCart.NO_SIMPLE_SELECTED;

    private final static int SINGLE_ITEM = 1;

    private int mAddedItemsCounter = 0;

    private ArrayList<Integer> mItemsNotAddedToCart = new ArrayList<Integer>();

    /**
     * Empty constructor
     */
    public RecentlyViewedFragment() {
        super(EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.RecentlyView,
                R.string.recently_viewed, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /**
     * Get a new instance
     * 
     * @return RecentlyViewedFragment
     */
    public static RecentlyViewedFragment getInstance() {
        mRecentlyViewedFragment = new RecentlyViewedFragment();
        return mRecentlyViewedFragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Retain the instance to receive callbacks from add all to cart
        setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.recentlyviewed, container, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get loading view
        mLoadingView = view.findViewById(R.id.loading_view_pager);
        // Get not found layout
        mRecentlyViewedEmptyView = view.findViewById(R.id.recentlyviewed_empty);
        // Get favourite container
        mRecentlyViewedView = view.findViewById(R.id.recentlyviewed_content);
        // Get grid view
        mRecentlyViewedGridView = (GridView) view.findViewById(R.id.recentlyviewed_grid);
        // Get clear all button
        mClearAllButton = (Button) view.findViewById(R.id.recentlyviewed_button_clear_all);
        mClearAllButton.setSelected(true);
        mClearAllButton.setOnClickListener((OnClickListener) this);

        // Get RecentlyViewed
        Log.i(TAG, "LOAD LAST VIEWED ITEMS");
        new GetRecentlyViewedHelper((IResponseCallback) this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVED INSTANCE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
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

    /**
     * ######### LAYOUTS #########
     */

    /**
     * Show content after get items
     * 
     * @author sergiopereira
     */
    private void showContent() {
        // Hide loading
        mLoadingView.setVisibility(View.GONE);
        // Validate favourites
        if (mRecentlyViewed != null && !mRecentlyViewed.isEmpty()) {
            Log.i(TAG, "ON SHOW CONTENT");
            mRecentlyViewedAdapter = new AddableToCartListAdapter(getBaseActivity(), mRecentlyViewed, (OnClickListener) this);
            mRecentlyViewedGridView.setAdapter(mRecentlyViewedAdapter);
            showContainer();
        } else {
            Log.i(TAG, "ON SHOW IS EMPTY");
            showEmpty();
        }
    }

    /**
     * Show container
     * 
     * @author sergiopereira
     */
    private void showContainer() {
        mRecentlyViewedView.setVisibility(View.VISIBLE);
        mRecentlyViewedEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * Show loading
     * 
     * @author sergiopereira
     */
    private void showLoading() {
        mRecentlyViewedView.setVisibility(View.GONE);
        mRecentlyViewedEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    /**
     * Show empty
     * 
     * @author sergiopereira
     */
    private void showEmpty() {
        mRecentlyViewedView.setVisibility(View.GONE);
        mRecentlyViewedEmptyView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * ######### LISTENERS #########
     */

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Case item
        if (id == R.id.favourite_item_container) onItemClick(view);
        // Case add to cart
        else if (id == R.id.favourite_button_shop) onClickAddToCart(view);
        // Case clear all
        else if (id == R.id.recentlyviewed_button_clear_all) onClickClearAll();
        // Case simple
        else if (id == R.id.favourite_button_variant) onClickVariation(view);
        // Case unknown
        else Log.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
    }

    /**
     * Process the click on variation button
     * 
     * @param view
     * @author sergiopereira
     */
    private void onClickVariation(View view) {
        try {
            // Hide warning
            getBaseActivity().showWarningVariation(false);
            // Show dialog
            int position = Integer.parseInt(view.getTag().toString());
            LastViewedAddableToCart recentlyViewed = (LastViewedAddableToCart) mRecentlyViewed.get(position);
            recentlyViewed.setFavoriteSelected(position);
            showVariantsDialog(recentlyViewed);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON CLICK VARIATION");
        }
    }

    /**
     * Process the click on item
     * 
     * @param view
     * @author sergiopereira
     */
    private void onItemClick(View view) {
        Log.i(TAG, "ON ITEM CLICK");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            LastViewedAddableToCart recentlyViewed = (LastViewedAddableToCart) mRecentlyViewed.get(position);
            recentlyViewed.setFavoriteSelected(position);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_URL, recentlyViewed.getUrl());
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON ITEM CLICK");
        }
    }

    /**
     * Process the click on clear all button
     * 
     * @author sergiopereira
     */
    private void onClickClearAll() {
        Log.i(TAG, "ON CLICK CLEAR ALL ITEMS");
        // Show progress
        showLoading();

        LastViewedTableHelper.deleteAllLastViewed();
        // needed to update mRecentSearchesAdapter
        for (int i = mRecentlyViewed.size() - 1; i >= 0; i--) {
            mRecentlyViewed.remove(i);
        }

        updateLayoutAfterAction();
    }

    /**
     * Process the click on add button
     * 
     * @param view
     * @author sergiopereira
     */
    private void onClickAddToCart(View view) {
        Log.i(TAG, "ON CLICK ADD ALL TO CART");
        int position = Integer.parseInt(view.getTag().toString());
        LastViewedAddableToCart recentlyViewed = (LastViewedAddableToCart) mRecentlyViewed.get(position);
        // Validate variation
        if (hasSelectedVariation(recentlyViewed)) {
            Log.i(TAG, "SELECTED VARIATION");
            onAddItemToCart(recentlyViewed, position);
        } else {
            Log.i(TAG, "NOT SELECTED VARIATION");
            mRecentlyViewedAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Validate if item has a selected varititon
     * 
     * @param item
     * @return boolean
     * @author sergiopereira
     */
    private boolean hasSelectedVariation(LastViewedAddableToCart item) {
        Log.d(TAG, "ON VALIDATE VARIATIONS: " + item.hasSimples() + " " + item.getSelectedSimple());
        // Validate if has simples > 1 and has a selected position
        if (item.hasSimples() && item.getSelectedSimple() == LastViewedAddableToCart.NO_SIMPLE_SELECTED) {
            // Set the item to show warning
            item.setChooseVariationWarning(true);
            // Return not selected
            return false;
        }
        // Return selected
        return true;
    }

    /**
     * Add an item to cart if completed
     * 
     * @param recentlyViewed
     * @param position
     * @author sergiopereira
     */
    private void onAddItemToCart(LastViewedAddableToCart recentlyViewed, int position) {
        Log.i(TAG, "ON EXECUTE ADD TO CART");
        // Show progress
        getBaseActivity().showProgress();
        // Initialize cart vars
        mAddedItemsCounter = 0;
        mNumberOfItemsForCart = SINGLE_ITEM;
        mItemsNotAddedToCart.clear();
        // Validate items
        if (recentlyViewed.isComplete())
            triggerAddProductToCart(recentlyViewed, position);
        else
            Log.w(TAG, "WARNING ITEM NOT COMPLETED: " + recentlyViewed.getName());
    }

    /**
     * ######### TRIGGERS #########
     */

    /**
     * Trigger to add an item to cart
     * 
     * @param recentlyViewed
     * @param position
     * @author sergiopereira
     */
    private synchronized void triggerAddProductToCart(LastViewedAddableToCart recentlyViewed, int position) {
        Log.i(TAG, "ON TRIGGER ADD TO CART: " + position);
        ProductSimple simple = getSelectedSimple(recentlyViewed);
        // Item data
        ContentValues values = new ContentValues();
        String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);
        values.put(GetShoppingCartAddItemHelper.PRODUCT_TAG, recentlyViewed.getSku());
        values.put(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
        values.put(GetShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        bundle.putInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, position);
        bundle.putString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG, recentlyViewed.getSku());
        bundle.putBoolean(GetShoppingCartAddItemHelper.REMOVE_RECENTLYVIEWED_TAG, true);
        // Trigger
        triggerContentEventWithNoLoading(new GetShoppingCartAddItemHelper(), bundle, (IResponseCallback) this);
    }

    /**
     * Get the seleted simple
     * 
     * @param recentlyViewed
     * @return ProductSimple
     * @author sergiopereira
     */
    public ProductSimple getSelectedSimple(LastViewedAddableToCart recentlyViewed) {
        Log.i(TAG, "ON GET SELECTED SIMPLE: " + recentlyViewed.getSimples().size());
        // Get item
        int selectedSimple = recentlyViewed.getSelectedSimple();
        // if (selectedSimple >= recentlyViewed.getSimples().size()) return null;
        // else if (selectedSimple == recentlyViewed.NO_SIMPLE_SELECTED) return null;
        return recentlyViewed.getSimples().get(selectedSimple);
    }

    /**
     * ######### RESPONSE #########
     */

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON RESPONSE COMPLETE " + getId());
        // Get event type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate the current state
        if (isOnStoppingProcess && eventType != EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT) {
            Log.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }
        // Validate the event type
        switch (eventType) {
        case GET_RECENLTLYVIEWED_LIST:
            Log.i(TAG, "ON RESPONSE COMPLETE: GET_RECENLTLYVIEWED_LIST");
            mRecentlyViewed = (ArrayList<AddableToCart>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_KEY);
            Log.d(TAG, "NUMBER : " + mRecentlyViewed.size());
            // Show content
            showContent();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            // Get data
            int pos = bundle.getInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, -1);
            String sku = bundle.getString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
            Log.i(TAG, "ON RESPONSE COMPLETE: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
            // Validate current counter
            validateResponseCounter(true, pos, -1);
            break;
        default:
            Log.d(TAG, "ON RESPONSE COMPLETE: UNKNOWN TYPE");
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG, "ON RESPONSE COMPLETE");
        // Get type
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate the current state
        if (isOnStoppingProcess && eventType != EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT) {
            Log.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }
        // Validate type
        switch (eventType) {
        case GET_RECENLTLYVIEWED_LIST:
            Log.d(TAG, "ON RESPONSE ERROR: GET_RECENLTLYVIEWED_LIST");
            mLoadingView.setVisibility(View.GONE);
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            Log.d(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT");
            // Inc counter
            mAddedItemsCounter++;
            // Get item set stock error
            int pos = bundle.getInt(GetShoppingCartAddItemHelper.PRODUCT_POS_TAG, LastViewedAddableToCart.NO_SIMPLE_SELECTED);
            String sku = bundle.getString(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG);
            Log.i(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT: " + pos + " " + sku + " " + mAddedItemsCounter + " " + mNumberOfItemsForCart);
            // Save the position
            if (mItemsNotAddedToCart != null) mItemsNotAddedToCart.add(pos);
            // Check type error is out of stock
            int error = checkTypeError(bundle, pos);
            // Validate current counter
            validateResponseCounter(false, pos, error);
        default:
            Log.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
            break;
        }
    }

    /**
     * Method used to validate the error response
     * 
     * @param bundle
     * @param pos
     * @return int - The string id
     */
    private int checkTypeError(Bundle bundle, int pos) {
        // Generic error
        int error = R.string.error_please_try_again;
        // Get error code
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        // Validate error
        if (errorCode == ErrorCode.CONNECT_ERROR) {
            Log.i(TAG, "ON RESPONSE ERROR: CONNECT_ERROR");
            error = R.string.error_no_connection;
        } else if (errorCode == ErrorCode.REQUEST_ERROR) {
            HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            // CASE OUT OF STOCK
            if (errorMessages != null && errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                Log.i(TAG, "ON RESPONSE ERROR: CODE_ORDER_PRODUCT_SOLD_OUT");
                if (mRecentlyViewed != null && !mRecentlyViewed.isEmpty()) mRecentlyViewed.get(pos).setVariationStockWarning(true);
                // Return string out of stock
                error = R.string.product_outof_stock;
                // CASE ERROR ADDING
            } else if (errorMessages != null && errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
                Log.i(TAG, "ON RESPONSE ERROR: CODE_ORDER_PRODUCT_ERROR_ADDING");
                // Return error add to cart failed
                // error = R.string.error_add_to_cart_failed;
                if (mRecentlyViewed != null && !mRecentlyViewed.isEmpty()) mRecentlyViewed.get(pos).setVariationStockWarning(true);
                // Return string out of stock
                error = R.string.product_outof_stock;
                // CASE UNKNOWN
            } else {
                Log.i(TAG, "ON RESPONSE ERROR: " + errorCode.toString());
            }
        }
        return error;
    }

    /**
     * Validates the response counter from success or error
     * 
     * @param success
     *            - from respective response
     * @param pos
     *            - current position
     * @param error
     *            - the string id for error
     * @author sergiopereira
     */
    private void validateResponseCounter(boolean success, int pos, int error) {
        // Set flag
        if (!isOnStoppingProcess) {
            validateItemWasAddedToCart(success, pos, error);
            // Update layout
            updateLayoutAfterAction();
        }
        // Update cart
        getBaseActivity().updateCartInfo();
    }

    /**
     * Validate the item was added to cart
     * 
     * @param success
     *            - from respective response
     * @param pos
     *            - current position
     * @param error
     *            - the string id for error
     * @author sergiopereira
     */
    private void validateItemWasAddedToCart(boolean success, int pos, int error) {
        // Assumed that was added to cart
        String message = getString(R.string.added_to_shop_cart_dialog_text);
        // Case added to cart
        if (success) mRecentlyViewed.remove(pos);
        // Case not added to cart
        else message = getString(error);
        // Show toast
        if (!isOnStoppingProcess) Toast.makeText(getBaseActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Update the layout after user action
     * 
     * @author sergiopereira
     */
    private synchronized void updateLayoutAfterAction() {
        // Update adapter
        mRecentlyViewedAdapter.notifyDataSetChanged();
        // Validate current state
        if (mRecentlyViewed.isEmpty()) {
            mRecentlyViewedEmptyView.setVisibility(View.VISIBLE);
            mClearAllButton.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
        }
        // Dismiss
        getBaseActivity().dismissProgress();
    }

    /**
     * ###### DIALOG ######
     */

    private final static String VARIATION_PICKER_ID = "variation_picker";

    private void showVariantsDialog(LastViewedAddableToCart recentlyViewed) {
        String title = getString(R.string.product_variance_choose);
        //
        ArrayList<String> mSimpleVariantsAvailable = new ArrayList<String>();
        ArrayList<String> mSimpleVariants = createSimpleVariants(recentlyViewed, mSimpleVariantsAvailable);
        //
        DialogListFragment dialogListFragment = DialogListFragment.newInstance(getBaseActivity(),
                                                                                new RecentlyViewedOnDialogListListener(recentlyViewed), 
                                                                                VARIATION_PICKER_ID, 
                                                                                title, 
                                                                                mSimpleVariants, 
                                                                                mSimpleVariantsAvailable, 
                                                                                recentlyViewed.getSelectedSimple());

        dialogListFragment.show(getFragmentManager(), null);
    }

    private class RecentlyViewedOnDialogListListener implements OnDialogListListener {

        LastViewedAddableToCart recentlyViewed;

        public RecentlyViewedOnDialogListListener(LastViewedAddableToCart recentlyViewed) {
            this.recentlyViewed = recentlyViewed;
        }

        @Override
        public void onDialogListItemSelect(String id, int position, String value) {
            Log.i(TAG, "size selected! onDialogListItemSelect : " + position);
            recentlyViewed.setChooseVariationWarning(false);
            recentlyViewed.setVariationStockWarning(false);
            recentlyViewed.setSelectedSimple(position);
            recentlyViewed.setSelectedSimpleValue(value);
            mRecentlyViewedAdapter.notifyDataSetChanged();
        }

    }

    private ArrayList<String> createSimpleVariants(LastViewedAddableToCart recentlyViewed, ArrayList<String> mSimpleVariantsAvailable) {
        Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants" + recentlyViewed.getName());
        ArrayList<ProductSimple> simples = (ArrayList<ProductSimple>) recentlyViewed.getSimples().clone();
        ArrayList<String> variations = recentlyViewed.getKnownVariations();
        if (variations == null || variations.size() == 0) {
            variations = new ArrayList<String>();
            variations.add("size");
            variations.add("color");
            variations.add("variation");
        }
        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(recentlyViewed.getSimples(), variations);

        ArrayList<String> variationValues = new ArrayList<String>();
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants in");
            String value = calcVariationStringForSimple(simple, foundKeys);

            /**
             * TODO: Uncommented to validate the stock
             * 
             * @author sergiopereira
             */
            // String quantity = simple.getAttributeByKey(ProductSimple.QUANTITY_TAG);
            // if (quantity != null && Long.parseLong(quantity) > 0) {
            variationValues.add(value);
            mSimpleVariantsAvailable.add(value);
            // } else {
            // variationValues.add(value);
            // }

        }

        return variationValues;
    }

    private Set<String> scanSimpleAttributesForKnownVariants(ArrayList<ProductSimple> simples, ArrayList<String> variations) {
        Set<String> foundVariations = new HashSet<String>();
        Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants");
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants in");
            scanSimpleForKnownVariants(simple, foundVariations, variations);
        }
        return foundVariations;
    }

    private void scanSimpleForKnownVariants(ProductSimple simple, Set<String> foundVariations, ArrayList<String> variations) {
        for (String variation : variations) {
            String attr = simple.getAttributeByKey(variation);
            Log.i(TAG, "scanSimpleForKnownVariations: variation = " + variation + " attr = " + attr);
            if (attr == null)
                continue;
            foundVariations.add(variation);
        }
    }

    private String calcVariationStringForSimple(ProductSimple simple, Set<String> keys) {
        String delim = ";";
        String loopDelim = "";
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = simple.getAttributeByKey(key);
            if (value != null) {
                sb.append(loopDelim);
                sb.append(value);
                loopDelim = delim;
            }
        }

        return sb.toString();
    }
}
