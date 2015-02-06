package com.mobile.controllers;

import java.util.List;

import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.objects.SearchSuggestion;
import com.mobile.view.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import de.akquinet.android.androlog.Log;

/**
 * Adapter used on RecentSearchFragment
 * 
 */
public class SearchSuggestionsAdapter extends ArrayAdapter<SearchSuggestion> {

    private LayoutInflater mInflater;

    public SearchSuggestionsAdapter(Context context, List<SearchSuggestion> objects) {
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
        Log.d("ITEM on position " + position);

        TextView tV = (TextView) view.findViewById(R.id.item_text_suggestion);
        tV.setText(getItem(position).getResult());

        return view;
    }
}
