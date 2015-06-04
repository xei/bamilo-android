package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.output.Print;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.view.R;

import java.util.List;

/**
 * Adapter used on RecentSearchFragment
 * 
 */
public class SearchSuggestionsAdapter extends ArrayAdapter<Suggestion> {

    private LayoutInflater mInflater;

    public SearchSuggestionsAdapter(Context context, List<Suggestion> objects) {
        super(context, 0, 0, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.recentsearch_suggestion_list_item, parent, false);
        } else {
            view = convertView;
        }
        Print.d("ITEM on position " + position);

        TextView tV = (TextView) view.findViewById(R.id.item_text_suggestion);
        tV.setText(getItem(position).getResult());

        return view;
    }
}
