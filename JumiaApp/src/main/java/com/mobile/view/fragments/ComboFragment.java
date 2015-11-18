package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.interfaces.OnProductViewHolderClickListener;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.ComboGridView;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.utils.ui.ComboGridAdapter;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;


/**
 * This class represents the page for bundle products in a combo. It allows to add checked combo products to cart at once
 * @author alexandrapires
 */
public class ComboFragment extends BaseFragment implements IResponseCallback, OnProductViewHolderClickListener, DialogSimpleListFragment.OnDialogListListener {

    private BundleList bundleList;
    private String productSku;
    private DialogFragment mDialogAddedToCart;
    private TextView mTotalPrice;
    private ComboGridAdapter adapter;
    private ProductBundle mBundleWithMultiple;
    private ArrayList<ProductBundle> listBundlesOneSimple;
    private ArrayList<ProductBundle> listBundlesMultipleSimple;
    private int countMultipleProcessed = 0;

    /**
     * Empty constructor
     */
    public ComboFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.COMBOS,
                R.layout.pdv_combos_page,
                R.string.combos_label,
                NO_ADJUST_CONTENT);
    }

    /**
     * Create and return a new instance.
     *
     * @param bundle - arguments
     */
    public static ComboFragment getInstance(Bundle bundle) {
        ComboFragment comboFragment = new ComboFragment();
        comboFragment.setArguments(bundle);
        return comboFragment;
    }

     /*
     * ############ LIFE CYCLE ############
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get data from arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            Print.i(TAG, "ARGUMENTS: " + arguments);
            bundleList = arguments.getParcelable(RestConstants.JSON_BUNDLE_PRODUCTS);
            productSku = arguments.getString(ConstantsIntentExtra.PRODUCT_SKU);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        //update total price
        mTotalPrice = (com.mobile.components.customfontviews.TextView) view.findViewById(R.id.txTotalComboPrice);
        mTotalPrice.setText(CurrencyFormatter.formatCurrency(bundleList.getPrice()));
        ComboGridView gridView = (ComboGridView) view.findViewById(R.id.combo_grid_view);
        adapter = new ComboGridAdapter(getBaseActivity(), bundleList.getProducts(), productSku);
        adapter.setOnViewHolderClickListener(this);
        gridView.setAdapter(adapter);
        gridView.setGridLayoutManager(getResources().getInteger(R.integer.combos_num_columns));
        gridView.setHasFixedSize(true);
        gridView.setItemAnimator(new DefaultItemAnimator());
        view.findViewById(R.id.btBuyCombo).setOnClickListener(this);
    }

    /**
     * Click events
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        Print.i(TAG, "ON CLICK VIEW");
        // Get id
        int id = view.getId();
        // Case sort button
        if (id == R.id.btBuyCombo) {
            Print.i(TAG, "ADD CART CLICKED");
            addComboToCart();
        }
    }

    /**
     * separates bundle products in two lists: one with one simple variation e others with multiple variations to choose
     * this is needed to add in first the bundles with multiple variations to cart
     */
    private void separateProductsBySimpleType() {
        listBundlesOneSimple = new ArrayList<>();
        listBundlesMultipleSimple = new ArrayList<>();
        countMultipleProcessed = 0;

        ArrayList<ProductBundle> listBundles = bundleList.getProducts();
        for (ProductBundle productBundle : listBundles) {
            //distribute the checked bundles to both lists
            if (productBundle.isChecked()) {
                if (productBundle.hasOwnSimpleVariation()) {
                    listBundlesOneSimple.add(productBundle);
                } else if (productBundle.hasMultiSimpleVariations() && productBundle.getSimples().size() > 0) {
                    listBundlesMultipleSimple.add(productBundle);
                }
            }
        }
    }

    /**
     * Add selected combo products to chart
     */
    private void addComboToCart() {
        //separate teh products into lis with single and list with multiple
        separateProductsBySimpleType();
        //if there is a list of bundles with multiple, show dialog
        if (CollectionUtils.isNotEmpty(listBundlesMultipleSimple)) {
            ProductBundle productBundle = listBundlesMultipleSimple.get(0);
            addToCartWithSelectedSimple(productBundle);

        }
        //if there isn't bundles with multiplesimple , just simply add to cart
        else if (CollectionUtils.isNotEmpty(listBundlesOneSimple)) {
            for (ProductBundle productBundle : this.listBundlesOneSimple) {
                addToCartWithOnlySimple(productBundle);
            }
        }
    }

    /**
     * add to cart
     *
     * @param productBundle - arguments
     */
    private void proceedWithAddItemToCart(ProductBundle productBundle, ProductSimple simple) {
        // Validate simple sku
        String simpleSku = simple.getSku();
        // Add one unity to cart
        triggerAddItemToCart(productBundle.getSku(), simpleSku);
        // Tracking
        TrackerDelegator.trackProductAddedToCart(productBundle, simpleSku, mGroupType);
    }

    /**
     * add to cart a product with an only simples
     *
     * @param productBundle - arguments
     */
    private void addToCartWithOnlySimple(ProductBundle productBundle) {
        ProductSimple simples = productBundle.getSimples().get(0);
        proceedWithAddItemToCart(productBundle, simples);
    }

    /**
     * opens a dialog to choose the simples and add to cart
     *
     * @param productBundle - arguments
     */
    private void addToCartWithSelectedSimple(ProductBundle productBundle) {
        mBundleWithMultiple = productBundle;
        onClickSimpleVariationsButton(productBundle.getName());
    }

    /**
     * show dialog to choose the variation simples
     */
    private void onClickSimpleVariationsButton(String productName) {
        Print.i(TAG, "ON CLICK TO SHOW SIMPLE VARIATIONS");
        try {
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    productName,
                    mBundleWithMultiple,
                    this);
            dialog.show(getFragmentManager(), null);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
        }
    }

    /**
     * choose the simple and add to cart
     */
    @Override
    public void onDialogListItemSelect(int position) {
        try {
            //get selected simple
            ProductSimple selectedSimple = mBundleWithMultiple.getSimples().get(position);
            //update bundle with selected simple in adapter, to update variation label
            mBundleWithMultiple.setSelectedSimplePosition(position);
            adapter.setItemInArray(mBundleWithMultiple);
            adapter.notifyDataSetChanged();
            //add to cart with selected simple
            proceedWithAddItemToCart(mBundleWithMultiple, selectedSimple);
        } catch (NullPointerException e) {
            // ...
        }
    }

    @Override
    public void onDialogListClickView(View view) {

    }

    @Override
    public void onDialogListDismiss() {
    }

    private void triggerAddItemToCart(String sku, String simpleSKU) {
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), ShoppingCartAddItemHelper.createBundle(sku, simpleSKU), this);
    }

    @Override
    public void onHeaderClick(String targetType, String url, String title) {

    }

    /**
     * updates the combo total price in checking/unchecking bundle
     */
    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {
        //get Selected Item
        ProductBundle selectedBundle = ((ComboGridAdapter) adapter).getItem(position);
        //update total price and select a simple if is checked
        if (!selectedBundle.getSku().equals(productSku)) {
            bundleList.updateTotalPriceWhenChecking(position);
            mTotalPrice.setText(CurrencyFormatter.formatCurrency(bundleList.getPrice()));
        }
    }

    @Override
    public void onWishListClick(View view, RecyclerView.Adapter<?> adapter, int position) {

    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "ON SUCCESS EVENT: ");

        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide dialog progress
        hideActivityProgress();

        super.handleSuccessEvent(baseResponse);
        showAddToCartCompleteMessage(baseResponse);

        countMultipleProcessed++;   //count the added bundle with chosen simples

        //add simple if they exists after all added multiple
        if (countMultipleProcessed == listBundlesMultipleSimple.size() && listBundlesOneSimple.size() > 0) {
            for (ProductBundle productBundle : this.listBundlesOneSimple) {
                addToCartWithOnlySimple(productBundle);
            }
        } else if (countMultipleProcessed < listBundlesMultipleSimple.size()) //add next multiple
        {
            mBundleWithMultiple = listBundlesMultipleSimple.get(countMultipleProcessed);
            addToCartWithSelectedSimple(mBundleWithMultiple);
        }
    }


    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide dialog progress
        hideActivityProgress();

        // Specific errors
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();

        // Generic errors
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }

        Print.d(TAG, "onErrorEvent: type = " + eventType);

        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:

                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    Map<String, List<String>> errorMessages = baseResponse.getErrorMessages();

                    if (errorMessages != null) {
                        int msgRes = -1;

                        String message = null;
                        if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                            msgRes = R.string.product_outof_stock;
                        } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_PRODUCT_ADD_OVERQUANTITY)) {
                            msgRes = R.string.error_add_to_shopping_cart_quantity;
                        } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
                            List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                            if (validateMessages != null && validateMessages.size() > 0) {
                                message = validateMessages.get(0);
                            } else {
                                msgRes = R.string.error_add_to_cart_failed;
                            }
                        }

                        if (msgRes != -1) {
                            message = getString(msgRes);
                        } else if (message == null) {
                            return;
                        }

                        FragmentManager fm = getFragmentManager();
                        dialog = DialogGenericFragment.newInstance(true, false,
                                mBundleWithMultiple.getName(),
                                message,
                                getString(R.string.ok_label), "", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        int id = v.getId();
                                        if (id == R.id.button1) {
                                            dismissDialogFragment();
                                        }
                                    }
                                });
                        dialog.show(fm, null);
                        return;
                    }
                }
                if (!ErrorCode.isNetworkError(errorCode)) {
                    addToShoppingCartFailed();
                    return;
                }
        }
    }


    private void executeAddToShoppingCartCompleted() {
        getBaseActivity().warningFactory.showWarning(WarningFactory.ADDED_ITEM_TO_CART, getString(R.string.added_to_shop_cart_dialog_text));
    }


    private void addToShoppingCartFailed() {
        mDialogAddedToCart = DialogGenericFragment.newInstance(false, true, null,
                getResources().getString(R.string.error_add_to_shopping_cart),
                getResources().getString(R.string.ok_label),
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            mDialogAddedToCart.dismiss();
                        } else if (id == R.id.button2) {

                        }
                    }
                });

        mDialogAddedToCart.show(getFragmentManager(), null);
    }


}
