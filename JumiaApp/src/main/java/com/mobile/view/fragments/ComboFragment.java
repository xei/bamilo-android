package com.mobile.view.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.ComboGridView;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.ui.ComboGridAdapter;
import com.mobile.utils.ui.CustomBottomSheet;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;


/**
 * Created by alexandrapires on 9/11/15.
 * This class represents the page for bundle products in a combo. It allows to add checked combo products to cart at once
 */
public class ComboFragment extends BaseFragment implements IResponseCallback, OnViewHolderClickListener,DialogInterface.OnDismissListener{


    private BundleList bundleList;
    private double totalPrice=0.0;
    private boolean isAddingProductToCart=false;

    private DialogFragment mDialogAddedToCart;
    private TextView mTotalPrice;
    private ComboGridView gv;
    private ComboGridAdapter adapter;
    private Button btBuyCombo;
    private Context c;
    private  CustomBottomSheet bottomSheet;

    private Hashtable<ProductBundle,ProductSimple> selectedProducts;

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

    /**
     * Empty constructor
     */
    public ComboFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.pdp_combos_page,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

     /*
     * ############ LIFE CYCLE ############
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get data from arguments (Home/Categories/Deep link)
        Bundle arguments = getArguments();
        if (arguments != null) {
            Print.i(TAG, "ARGUMENTS: " + arguments.toString());
            totalPrice = arguments.getDouble("totalPrice");
            bundleList = arguments.getParcelable("bundleList");

        }

        c = getBaseActivity().getApplicationContext();
        selectedProducts = new Hashtable<>();


    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        //update total price
        mTotalPrice = (com.mobile.components.customfontviews.TextView) view.findViewById(R.id.txTotalComboPrice);
        mTotalPrice.setText(CurrencyFormatter.formatCurrency(totalPrice));

        //gridview
        gv = (ComboGridView) view.findViewById(R.id.combo_grid_view);
        adapter = new ComboGridAdapter(c,bundleList.getBundleProducts());
        adapter.setOnViewHolderClickListener(this);
        gv.setAdapter(adapter);
        gv.setGridLayoutManager(1);
        //      Drawable divider = getBaseActivity().getApplicationContext().getDrawable(R.drawable.divider);

        //     gv.addItemDecoration(divider);    //depending if is tablet or not: se this later
        gv.setHasFixedSize(true);
        gv.setItemAnimator(new DefaultItemAnimator());

        btBuyCombo = (Button) view.findViewById(R.id.btBuyCombo);
        btBuyCombo.setOnClickListener(this);

    }


    /**
     * Add selected combo products to chart
     *
     */

    private void addComboToChart()
    {
        //   findSelectedSimple(productBundle);
        ArrayList<ProductBundle> listBundles = bundleList.getBundleProducts();    //adapter.getItems(); //must be updated

        for(ProductBundle productBundle : listBundles)
        {
            if(productBundle.isChecked())
            {
                findSelectedSimple(productBundle);

            }
        }

        executeAddProductToCart();

    }

    /**
     * add a choosen Simples of a Product Bundle to cart
     *
     * @param productBundle - arguments
     */
    private void executeAddProductToCart() {
        // Get selected simple

        for( ProductBundle productBundle : selectedProducts.keySet())
        {
            ProductSimple simple = selectedProducts.get(productBundle);

            //   ProductSimple simple = getSelectedSimple(productBundle);//selectedProducts.get(productBundle);
            // Validate simple
            if (simple == null) {
                //showVariantsDialog();
                return;
            }
            // Validate quantity
            if (simple.getQuantity() == 0) {
                Toast.makeText(getBaseActivity(), R.string.product_outof_stock, Toast.LENGTH_SHORT).show();
                isAddingProductToCart = false;
                return;
            }
            // Validate simple sku
            String simpleSku = simple.getSku();
            // Add one unity to cart
            triggerAddItemToCart(productBundle.getSku(), simpleSku);
            // Tracking
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putString(TrackerDelegator.SKU_KEY, simpleSku);
            bundle.putDouble(TrackerDelegator.PRICE_KEY, productBundle.getPriceForTracking());
            bundle.putString(TrackerDelegator.NAME_KEY, productBundle.getName());
            bundle.putString(TrackerDelegator.BRAND_KEY, productBundle.getBrand());
            bundle.putDouble(TrackerDelegator.RATING_KEY, productBundle.getAvgRating());
            bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, productBundle.getMaxSavingPercentage());
            bundle.putString(TrackerDelegator.CATEGORY_KEY, productBundle.getCategories());
            bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.PRODUCTDETAILPAGE);
            bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
            TrackerDelegator.trackProductAddedToCart(bundle);
        }



    }




    /**
     * allows to get a SimpleProduct or choose one between several in case of multiple simples for a ProductBundle
     *
     * @param productBundle - arguments
     */
    private ProductSimple getSelectedSimple(ProductBundle productBundle) {
        // Case Own simple variation
        if(productBundle.hasOwnSimpleVariation()) {
            return productBundle.getSimples().get(0);
        }
        // Case Multi simple variations
        else if(productBundle.hasMultiSimpleVariations() &&  productBundle.getSimples().size() > 0) {
            return productBundle.getSimples().get(0);   //show bottomsheet here to choose the simple

        }
        // Case invalid
        else {
            return null;
        }
    }




    private void findSelectedSimple(ProductBundle productBundle) {
        // Case Own simple variation
        if(productBundle.hasOwnSimpleVariation()) {
            selectedProducts.put(productBundle, productBundle.getSimples().get(0));
        }
        // Case Multi simple variations
        else if(productBundle.hasMultiSimpleVariations() &&  productBundle.getSimples().size() > 0) {
            ShowBottomSheet(productBundle,productBundle.getVariationName());

        }

    }



    private void ShowBottomSheet(ProductBundle productBundle, String variationName)
    {
        bottomSheet = CustomBottomSheet.newInstance(c,productBundle,variationName);
        bottomSheet.setOnDismissListener(this);
        bottomSheet.show(getFragmentManager(), null);


    }


    private void triggerAddItemToCart(String sku, String simpleSKU) {
        ContentValues values = new ContentValues();
        values.put(ShoppingCartAddItemHelper.PRODUCT_TAG, sku);
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, simpleSKU);
        values.put(ShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), bundle, this);
    }



    @Override
    public void onHeaderClick(String targetType, String url, String title) {

    }

    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {

        //get Selected Item
        ProductBundle selectedBundle = ((ComboGridAdapter) adapter).getItem(position);
        //update total price and select a simple if is checked
        if(selectedBundle.isChecked())
        {
            if(selectedBundle.hasDiscount())
                totalPrice += selectedBundle.getSpecialPrice();
            else
                totalPrice += selectedBundle.getPrice();

        }else
        {
            if(selectedBundle.hasDiscount())
                totalPrice -= selectedBundle.getSpecialPrice();
            else
                totalPrice -= selectedBundle.getPrice();
        }

        mTotalPrice.setText(CurrencyFormatter.formatCurrency(totalPrice));

    }

    @Override
    public void onWishListClick(View view, RecyclerView.Adapter<?> adapter, int position) {

    }



    /**Click events */

    @Override
    public void onClick(View view) {
        Print.i(TAG, "ON CLICK VIEW");
        // Get id
        int id = view.getId();
        // Case sort button
        if (id == R.id.btBuyCombo) {
            Print.i(TAG, "ADD CART CLICKED");
            addComboToChart();
        }
    }





    @Override
    public void onRequestComplete(Bundle bundle) {
        Print.i(TAG, "ON SUCCESS EVENT: ");

        // Hide dialog progress
        hideActivityProgress();

        if (getBaseActivity() == null)
            return;

        super.handleSuccessEvent(bundle);

        //Finnish add product to cart
        isAddingProductToCart= false;
        executeAddToShoppingCartCompleted(false);
    }





    @Override
    public void onRequestError(Bundle bundle) {
        Print.i(TAG, "ON ERROR EVENT");

        // Hide dialog progress
        hideActivityProgress();

        // Specific errors
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        Print.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);

                    if (errorMessages != null) {
                        int titleRes = R.string.error_add_to_cart_failed;
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
                                getString(titleRes),
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
                if (!errorCode.isNetworkError()) {
                    addToShoppingCartFailed();
                    return;
                }
        }
    }


    private void executeAddToShoppingCartCompleted(boolean isBundle) {

        if (!isBundle) {
            getBaseActivity().warningFactory.showWarning(WarningFactory.ADDED_ITEM_TO_CART);
        } else {
            getBaseActivity().warningFactory.showWarning(WarningFactory.ADDED_ITEMS_TO_CART);
        }
    }


    private void addToShoppingCartFailed() {
        mDialogAddedToCart = DialogGenericFragment.newInstance(false, true, null,
                getResources().getString(R.string.error_add_to_shopping_cart), getResources()
                        .getString(R.string.ok_label), "", new View.OnClickListener() {

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




    @Override
    public void onDismiss(DialogInterface dialog) {

        //get bundle and it selected simple
        ProductBundle selectedBundle = bottomSheet.getProductBundle();
        ProductSimple simple = bottomSheet.getSelectedSimple();

        //put
        selectedProducts.put(selectedBundle,simple);

    }



}
