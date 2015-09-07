package com.mobile.view.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Created by rsoares on 9/7/15.
 */
public class FilterMainFragment extends BaseFragment{

    public FilterMainFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.Filters,
                R.layout._def_filters_main,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    public static FilterMainFragment getInstance(Bundle bundle) {
        FilterMainFragment filterMainFragment = new FilterMainFragment();
        filterMainFragment.setArguments(bundle);
        return filterMainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView filters_key = (ListView)view.findViewById(R.id.filters_key);
        ListView filter_values = (ListView)view.findViewById(R.id.filter_values);
//        String[] values = new String[] { "Android List View",
//                "Adapter implementation",
//                "Simple List View In Android",
//                "Create List View Android",
//                "Android Example",
//                "List View Source Code",
//                "List View Array Adapter",
//                "Android Example List View",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian",
//                "ansduian"
//        };
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values);
//        filters_key.setAdapter(adapter);
//        filter_values.setAdapter(adapter);
    }
}
