package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bamilo.android.framework.components.customfontviews.CheckBox;
import android.widget.TextView;
import com.bamilo.android.framework.components.recycler.DividerItemDecoration;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.GetShoppingCartAddBundleHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.interfaces.OnProductViewHolderClickListener;
import com.bamilo.android.framework.service.objects.campaign.CampaignItem;
import com.bamilo.android.framework.service.objects.product.BundleList;
import com.bamilo.android.framework.service.objects.product.pojo.ProductBundle;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.ComboGridView;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogSimpleListFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ComboGridAdapter;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.EnumSet;


/**
 * This class represents the page for bundle products in a combo. It allows to add checked combo products to cart at once
 * @author alexandrapires
 */
public class ComboFragment extends BaseFragment implements IResponseCallback, OnProductViewHolderClickListener, DialogSimpleListFragment.OnDialogListListener {

    private BundleList mBundleList;
    private String mProductSku;
    private TextView mTotalPrice;
    private ComboGridView mGridView;
    private ProductBundle mBundleWithMultiple;
    private boolean mIsToAddBundleToCart = false;

    /**
     * Empty constructor
     */
    public ComboFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.COMBOS,
                R.layout._def_pdv_combo_fragment,
                R.string.combos_label,
                NO_ADJUST_CONTENT);
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
        // Get data from arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mBundleList = arguments.getParcelable(RestConstants.BUNDLE_PRODUCTS);
            mProductSku = arguments.getString(ConstantsIntentExtra.CONTENT_ID);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //update total price
        mTotalPrice = view.findViewById(R.id.pdv_combo_price);
        mTotalPrice.setText(CurrencyFormatter.formatCurrency(mBundleList.getPrice()));
        mGridView = view.findViewById(R.id.combo_grid_view);
        mGridView.setGridLayoutManager(getResources().getInteger(R.integer.combos_num_columns));
        mGridView.setHasFixedSize(true);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mGridView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mGridView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
        ComboGridAdapter adapter = new ComboGridAdapter(mBundleList.getProducts(), mProductSku);
        adapter.setOnViewHolderClickListener(this);
        mGridView.setAdapter(adapter);
        view.findViewById(R.id.combo_button_buy).setOnClickListener(this);
    }

    /**
     * Click events
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get id
        int id = view.getId();
        // Case sort button
        if (id == R.id.combo_button_buy) {
            addComboToCart();
        }
    }

    /**
     * Add selected combo products to chart
     */
    private void addComboToCart() {
        mIsToAddBundleToCart = true;
        // Validate product simples
        ArrayList<ProductBundle> bundleListProducts = mBundleList.getProducts();
        for (int i = 0; i < bundleListProducts.size(); i++) {
            if (!bundleListProducts.get(i).hasSelectedSimpleVariation() && bundleListProducts.get(i).isChecked()) {
                addToCartWithSelectedSimple(bundleListProducts.get(i));
                return;
            }
        }
        // Add bundle to cart
        triggerContentEventProgress(new GetShoppingCartAddBundleHelper(), GetShoppingCartAddBundleHelper.createBundle(mBundleList), this);
        mIsToAddBundleToCart = false;
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
        try {
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    title,
                    mBundleWithMultiple,
                    this);
            dialog.show(getFragmentManager(), null);
        } catch (NullPointerException e) {
        }
    }

    /**
     * choose the simple and add to cart
     */
    @Override
    public void onDialogListItemSelect(int position) {
        // Update the Combo adapter
        updateComboContainer();
        if(mIsToAddBundleToCart)
            addComboToCart();
    }

    @Override
    public void onDialogListClickView(View view) {

    }

    @Override
    public void onDialogSizeListClickView(int position, CampaignItem item) {

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

            if (!selectedBundle.getSku().equals(mProductSku)) {
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
        else if (view.getId() == R.id.item_variation){
            mIsToAddBundleToCart = false;
            try {
                int positions = (int) view.getTag(R.id.position);
                ProductBundle product = ((ComboGridAdapter) adapter).getItem(positions);
                if(product != null){
                    mBundleWithMultiple = product;
                    onClickSimpleVariationsButton(getString(R.string.product_variance_choose));
                }
            } catch (NullPointerException e) {
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
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            return;
        }
        // Hide dialog progress
        hideActivityProgress();
        super.handleSuccessEvent(baseResponse);
    }


    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            return;
        }
        // Hide dialog progress
        hideActivityProgress();
        // Generic errors
        super.handleErrorEvent(baseResponse);
    }

}
