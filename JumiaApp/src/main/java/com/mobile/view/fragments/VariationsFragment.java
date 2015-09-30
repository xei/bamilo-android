package com.mobile.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.interfaces.OnViewHolderClickListener;
import com.mobile.newFramework.objects.product.Variation;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.utils.ui.VariationProductsGridAdapter;
import com.mobile.utils.ui.VariationProductsGridView;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Created by alexandrapires on 9/17/15.
 * This class allows to open a page with a product's variations. It opens the PDV of a variation clicking in an item
 */
public class VariationsFragment extends BaseFragment implements OnViewHolderClickListener {

    private ProductComplete mProductComplete;

    private VariationProductsGridView mGridVariations;

    private VariationProductsGridAdapter mAdapter;

    /**
     * Create and return a new instance.
     *
     * @param bundle - arguments
     */
    public static VariationsFragment getInstance(Bundle bundle) {
        VariationsFragment variationsFragment = new VariationsFragment();
        variationsFragment.setArguments(bundle);
        return variationsFragment;
    }


    /**
     * Empty constructor
     */
    public VariationsFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.product_list_page,   //new layout here
                KeyboardState.NO_ADJUST_CONTENT);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get data from arguments (Home/Categories/Deep link)
        Bundle arguments = getArguments();
        if (arguments != null) {
            Print.i(TAG, "ARGUMENTS: " + arguments.toString());
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
        Print.i(TAG, "ON VIEW CREATED");

        mGridVariations = (VariationProductsGridView) view.findViewById(R.id.gridVariations);
        mAdapter = new VariationProductsGridAdapter(getBaseActivity().getApplicationContext(),mProductComplete.getProductVariations());
        mAdapter.setOnViewHolderClickListener(this);

        mGridVariations.setAdapter(mAdapter);
        mGridVariations.setGridLayoutManager(getBaseActivity().getApplicationContext(), getResources().getInteger(R.integer.variations_num_columns));
        mGridVariations.setHasFixedSize(true);

    }




    @Override
    public void onHeaderClick(String targetType, String url, String title) {

    }

    @Override
    public void onViewHolderClick(RecyclerView.Adapter<?> adapter, int position) {
        // Get item
        Variation product = ((VariationProductsGridAdapter) adapter).getItem(position);
        // Call Product Details
        if (product != null) {
            // Show product
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, product.getSKU());
            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, product.getBrand() + " " + product.getName());
            bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
            bundle.putSerializable(ConstantsIntentExtra.BANNER_TRACKING_TYPE, mGroupType);
            // Goto PDV
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            ToastFactory.ERROR_OCCURRED.show(getBaseActivity());
        }
    }

    @Override
    public void onWishListClick(View view, RecyclerView.Adapter<?> adapter, int position) {

    }
}
