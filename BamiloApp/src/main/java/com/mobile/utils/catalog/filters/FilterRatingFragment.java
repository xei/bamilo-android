package com.mobile.utils.catalog.filters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.FilterOptionArrayAdapter;
import com.mobile.service.objects.catalog.filters.CatalogCheckFilter;
import com.mobile.service.objects.catalog.filters.CatalogRatingFilter;
import com.mobile.service.objects.catalog.filters.CatalogRatingFilterOption;
import com.mobile.service.objects.catalog.filters.MultiFilterOptionInterface;
import com.mobile.service.utils.output.Print;
import com.mobile.view.R;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/10
 *
 * @see <a href="http://tutorials.jenkov.com/java-concurrency/volatile.html">volatile</a>
 */
public class FilterRatingFragment extends FilterCheckFragment {

    private static final String TAG = FilterRatingFragment.class.getSimpleName();

    /**
     * New instance
     */
    public static FilterRatingFragment newInstance(Bundle bundle) {
        Print.d(TAG, "NEW INSTANCE: BRAND");
        FilterRatingFragment frag = new FilterRatingFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    protected FilterOptionArrayAdapter createAdapter() {
        return new FilterRatingOptionArrayAdapter(getActivity(), mFilter);
    }

    private class FilterRatingOptionArrayAdapter extends FilterOptionArrayAdapter {
        public FilterRatingOptionArrayAdapter(Context activity, CatalogCheckFilter mFilter) {
            super(activity,mFilter);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MultiFilterOptionInterface option = getItem(position);
            if (option instanceof CatalogRatingFilterOption) {
                // Validate current view
                if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_sub_item_rating, null);
                RatingBar ratingBar = ((RatingBar) convertView.findViewById(R.id.dialog_item_rating));
                ratingBar.setNumStars(((CatalogRatingFilter) mFilter).getMax());
                ratingBar.setRating(((CatalogRatingFilterOption) option).getAverage());
                // Set check box
                setProductsCount((TextView) convertView.findViewById(R.id.dialog_products_count), option);
                setCheckboxBehavior(((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)), option);
            }
            return convertView;
        }
    }
}
