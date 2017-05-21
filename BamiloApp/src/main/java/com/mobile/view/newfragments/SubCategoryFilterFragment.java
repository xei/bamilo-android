package com.mobile.view.newfragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import com.mobile.adapters.SubCategoryArrayAdapter;
import com.mobile.components.customfontviews.TextView;
import com.mobile.service.database.CategoriesTableHelper;
import com.mobile.service.objects.category.Category;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubCategoryFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubCategoryFilterFragment extends NewBaseFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SUBCATEGORIES = "subcategories";

    private ArrayList<Category> mSubcategories;

    private ListView mListView;
    private TextView mConfirmButton;


    public SubCategoryFilterFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.FILTERS,
                R.layout.fragment_sub_category_filter,
                R.string.filters_label,
                NO_ADJUST_CONTENT);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    /*public static SubCategoryFilterFragment newInstance(Categories categories) {
        SubCategoryFilterFragment fragment = new SubCategoryFilterFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(SUBCATEGORIES, categories);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSubcategories = getArguments().getParcelableArrayList(SUBCATEGORIES);
        }
    }

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_category_filter, container, false);



        return view;
    }
*/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView)view.findViewById(R.id.subcategory_listview);
        SubCategoryArrayAdapter adapter = new SubCategoryArrayAdapter(getBaseActivity(), mSubcategories.get(1).getChildren());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(adapter);

        mConfirmButton = (TextView) view.findViewById(R.id.confirm_button);
        mConfirmButton.setOnClickListener(onConfirmClick);

    }

    View.OnClickListener onConfirmClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getBaseActivity().fragmentManagerBackPressed();
            getBaseActivity().fragmentManagerBackPressed();
            //getBaseActivity().onSwitchFragment(FragmentType.CATALOG, );
            Category selectedCategory = ((SubCategoryArrayAdapter)mListView.getAdapter()).getSelected();
            goToCatalog(selectedCategory);
        }
    };

    private void goToCatalog(Category category) {
        // Update counter for tracking
        CategoriesTableHelper.updateCategoryCounter(category.getUrlKey(), category.getName());
        // Close navigation
        //getBaseActivity().closeNavigationDrawer();
        //mCategory = category;
        @TargetLink.Type String link = category.getTargetLink();

        // Parse target link
        new TargetLink(getWeakBaseActivity(), link)
                .addTitle(category.getName())
                .setIsSubCategoryFilter()
                //.addAppendListener(this)
                .enableWarningErrorMessage()
                .run();
    }


}
