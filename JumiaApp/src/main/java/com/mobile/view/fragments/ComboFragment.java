package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.GetShoppingCartAddBundleHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.interfaces.OnProductViewHolderClickListener;
import com.mobile.newFramework.objects.product.BundleList;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.ComboGridView;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.utils.ui.ComboGridAdapter;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;


/**
 * This class represents the page for bundle products in a combo. It allows to add checked combo products to cart at once
 * @author alexandrapires
 */
public class ComboFragment extends BaseFragment implements IResponseCallback, OnProductViewHolderClickListener, DialogSimpleListFragment.OnDialogListListener {

    private BundleList mBundleList;
    private String productSku;
    private TextView mTotalPrice;
    private ComboGridView mGridView;
    private ComboGridAdapter adapter;
    private ProductBundle mBundleWithMultiple;
    private boolean mIsToAddBundleToCart = false;

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
            mBundleList = arguments.getParcelable(RestConstants.BUNDLE_PRODUCTS);
            productSku = arguments.getString(ConstantsIntentExtra.CONTENT_ID);
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
        mTotalPrice.setText(CurrencyFormatter.formatCurrency(mBundleList.getPrice()));
        mGridView = (ComboGridView) view.findViewById(R.id.combo_grid_view);
        adapter = new ComboGridAdapter(getBaseActivity(), mBundleList.getProducts(), productSku);
        adapter.setOnViewHolderClickListener(this);
        mGridView.setAdapter(adapter);
        mGridView.setGridLayoutManager(getResources().getInteger(R.integer.combos_num_columns));
        mGridView.setHasFixedSize(true);
        mGridView.setItemAnimator(new DefaultItemAnimator());
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
     * Add selected combo products to chart
     */
    private void addComboToCart() {
        mIsToAddBundleToCart = true;
        ArrayList<ProductBundle> bundleListProducts = mBundleList.getProducts();
        boolean allSimplesSelected = true;
        for (int i = 0; i < bundleListProducts.size(); i++) {
            if (!bundleListProducts.get(i).hasSelectedSimpleVariation() && bundleListProducts.get(i).isChecked()) {
                allSimplesSelected = false;
                addToCartWithSelectedSimple(bundleListProducts.get(i));
                return;
            }
        }

        if(allSimplesSelected){
            Print.i(TAG,"ADD BUNDLE TO CART");
            triggerContentEventProgress(new GetShoppingCartAddBundleHelper(), GetShoppingCartAddBundleHelper.createBundle(mBundleList), this);
            mIsToAddBundleToCart = false;
        }
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
    private void onClickSimpleVariationsButton(String title) {
        Print.i(TAG, "ON CLICK TO SHOW SIMPLE VARIATIONS");
        try {
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    title,
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
        Print.i(TAG, "ON CLICK VARIATION LIST ITEM");
        // Update the Combo adapter
        updateComboContainer();
        if(mIsToAddBundleToCart)
            addComboToCart();
    }

    @Override
    public void onDialogListClickView(View view) {

    }

    @Override
    public void onDialogListDismiss() {
    }

    @Override
    public void onHeaderClick(String target, String title) {

    }

    /**
     * go to de PDV of the specific bundle when clicking it's view
     */
    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {
        //get Selected Item
        String selectedSku = ((ComboGridAdapter) adapter).getItem(position).getSku();
        //go to PDV
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID,selectedSku);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);

    }


    /**
     * updates the combo total price in checking/unchecking bundle
     */
    @Override
    public void onViewHolderItemClick(View view, RecyclerView.Adapter<?> adapter, int position) {
        // User pressed the check button
        if(view.getId() == R.id.item_check ) {
            //get Selected Item
            ProductBundle selectedBundle = ((ComboGridAdapter) adapter).getItem(position);

            if (!selectedBundle.getSku().equals(productSku)) {
                //update checkbox status
                CheckBox cb = (CheckBox) view;
                cb.setChecked(!cb.isChecked());
                //update total price
                mBundleList.updateTotalPriceWhenChecking(position);
                mTotalPrice.setText(CurrencyFormatter.formatCurrency(mBundleList.getPrice()));
            }

            adapter.notifyDataSetChanged();
        }
        // User pressed the size button
        else if (view.getId() == R.id.choosen_variation){
            mIsToAddBundleToCart = false;
            try {
                int positions = (int) view.getTag(R.id.position);
                Print.i(TAG,"POSITION:"+position);
                Print.i(TAG,"POSITIONS:"+positions);
                ProductBundle product = ((ComboGridAdapter) adapter).getItem(positions);
                if(product != null){
                    mBundleWithMultiple = product;
                    onClickSimpleVariationsButton(getString(R.string.product_variance_choose));
                }
            } catch (NullPointerException e) {
                Print.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
            }
        }

    }


    /**
     * Update the combo list container
     */
    protected void updateComboContainer() {
        // Update content
        ComboGridAdapter adapter = (ComboGridAdapter) mGridView.getAdapter();
        adapter.notifyDataSetChanged();
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
        // Generic errors
        super.handleErrorEvent(baseResponse);
    }

}
