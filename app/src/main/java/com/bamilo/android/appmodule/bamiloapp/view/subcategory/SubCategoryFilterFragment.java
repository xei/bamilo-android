package com.bamilo.android.appmodule.bamiloapp.view.subcategory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.view.newfragments.NewBaseFragment;
import com.bamilo.android.framework.service.database.CategoriesTableHelper;
import com.bamilo.android.framework.service.objects.catalog.filters.SubCategory;
import com.bamilo.android.framework.service.pojo.RestConstants;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * A simple {@link NewBaseFragment} subclass. Use the {@link SubCategoryFilterFragment#newInstance}
 * factory method to create an instance of this fragment.
 */
public class SubCategoryFilterFragment extends NewBaseFragment {

    private ArrayList<SubCategory> mSubcategories;
    private SubCategory selectedCategory;

    SubCategoryFilterAdapter mAdapter;

    public SubCategoryFilterFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.FILTERS,
                R.layout.fragment_sub_category_filter,
                R.string.filters_label,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSubcategories = getArguments().getParcelableArrayList(RestConstants.SUB_CATEGORIES);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecyclerView(view);
        bindConfirmButtonClick(view);
    }

    private void bindConfirmButtonClick(View view) {
        view.findViewById(R.id.subcategory_textView_confirm).setOnClickListener(
                v -> {
                    if (mAdapter != null) {
                        goToCatalog(mAdapter.getSelectedItem());
                    }
                });
    }

    private void initRecyclerView(View view) {
        RecyclerView subCatRecycler = view.findViewById(R.id.subcategory_recyclerView_subCatList);
        mAdapter = new SubCategoryFilterAdapter(mSubcategories);
        subCatRecycler.setAdapter(mAdapter);
        subCatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        subCatRecycler.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void goToCatalog(SubCategory category) {
        if (category == null) {
            getBaseActivity().fragmentManagerBackPressed();
            return;
        }

        CategoriesTableHelper.updateCategoryCounter(category.getUrl_slug(), category.getName());
        @TargetLink.Type String link = "catalog_category::" + category.getUrl_slug();

        TargetLink targetLink = new TargetLink(getWeakBaseActivity(), link)
                .addTitle(category.getName())
                .setIsSubCategoryFilter()
                .enableWarningErrorMessage()
                .retainBackStackEntries();

        if (targetLink.run()) {
            setSubCategoryToDefault();
            mAdapter.notifyDataSetChanged();
            mAdapter = null;
            addToGA(category);
        }
    }

    private void setSubCategoryToDefault() {
        for (SubCategory subCategory : mSubcategories) {
            subCategory.setSelected(false);
        }
    }

    private void addToGA(SubCategory category) {
        SimpleEventModel setSubFilterEventModel = new SimpleEventModel();
        setSubFilterEventModel.category = "SubCategoryFilterView";
        setSubFilterEventModel.action = EventActionKeys.TAPPED;
        setSubFilterEventModel.label = category.getName();
        setSubFilterEventModel.value = SimpleEventModel.NO_VALUE;
        TrackerManager
                .trackEvent(getContext(), EventConstants.SearchFiltered, setSubFilterEventModel);
    }
}