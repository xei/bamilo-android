package pt.rocket.controllers;

import java.util.List;

import pt.rocket.framework.objects.SearchSuggestion;
import pt.rocket.view.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import org.holoeverywhere.widget.TextView;

public class SearchSuggestionsAdapter extends ArrayAdapter<SearchSuggestion> {
    
    private LayoutInflater mInflater;

    public SearchSuggestionsAdapter(Context context, List<SearchSuggestion> objects) {
        super(context, 0, 0, objects);
        mInflater = LayoutInflater.from( context );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if ( convertView == null) {
            view = mInflater.inflate(R.layout.search_suggestion_list_item, parent, false);
        } else {
            view = convertView;
        }
        
        TextView tV = (TextView) view.findViewById(R.id.item_text);
        tV.setText( getItem(position).getResult());
        
        return view;
    }

}
