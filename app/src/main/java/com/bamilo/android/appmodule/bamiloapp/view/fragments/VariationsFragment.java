package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bamilo.android.framework.components.recycler.DividerItemDecoration;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.interfaces.OnProductViewHolderClickListener;
import com.bamilo.android.framework.service.objects.product.Variation;
import com.bamilo.android.framework.service.objects.product.pojo.ProductComplete;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.VariationProductsGridAdapter;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.VariationProductsGridView;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * Created by alexandrapires on 9/17/15.
 * This class allows to open a page with a product's variations. It opens the PDV of a variation clicking in an item
 */
public class VariationsFragment extends BaseFragment implements OnProductViewHolderClickListener {

    protected static final String TAG = VariationsFragment.class.getSimpleName();

    private ProductComplete mProductComplete;

    /**
     * Empty constructor
     */
    public VariationsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.VARIATIONS,
                R.layout.product_list_page,
                R.string.variations,
                NO_ADJUST_CONTENT);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get data from arguments (Home/Categories/Deep link)
        Bundle arguments = getArguments();
        if (arguments != null) {
            mProductComplete = arguments.getParcelable(ConstantsIntentExtra.PRODUCT);
        }


    }


    /*
    * (non-Javadoc)
    * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
    */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VariationProductsGridView mGridVariations = view.findViewById(R.id.gridVariations);
        VariationProductsGridAdapter mAdapter = new VariationProductsGridAdapter(mProductComplete.getProductVariations());
        mAdapter.setOnViewHolderClickListener(this);
        mGridVariations.setAdapter(mAdapter);
        mGridVariations.setGridLayoutManager(getResources().getInteger(R.integer.variations_num_columns));
        mGridVariations.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mGridVariations.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
        mGridVariations.setHasFixedSize(true);
    }

    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {
        // Get item
        Variation product = ((VariationProductsGridAdapter) adapter).getItem(position);
        // Call Product Details
        if (product != null) {
            // Show product
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, product.getSKU());
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrand() + " " + product.getName());
            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
            // Goto PDV
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_occured));
        }
    }



    @Override
    public void onViewHolderItemClick(View view,RecyclerView.Adapter<?> adapter, int position) {

    }

    @Override
    public void onHeaderClick(String target, String title) {
        // ...
    }
}
