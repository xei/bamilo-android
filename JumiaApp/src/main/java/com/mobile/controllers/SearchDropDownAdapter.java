package com.mobile.controllers;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.view.R;

import java.util.List;


/**
 * Array adapter used to show the suggestions
 * @author sergiopereira
 *
 */
public class SearchDropDownAdapter extends ArrayAdapter<Suggestion> implements Filterable {

    public static final String TAG = SearchDropDownAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private String mQuery;

    /**
     * Constructor of adapter for search drop down
     * @param context - the current context
     * @param objects - list of suggestions
     * @param query - the current query
     * @author sergiopereira
     */
    public SearchDropDownAdapter(Context context, List<Suggestion> objects, String query) {
        super(context, 0, 0, objects);
        mInflater = LayoutInflater.from(context);
        mQuery = query;
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getItem(int)
     */
    @Override
    public Suggestion getItem(int position) {
        return super.getItem(position);
    }
    
    
    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        // Validate current view
        if (convertView == null) view = mInflater.inflate(R.layout.search_suggestion_list_item, null);
        else view = convertView;
        // Get current suggestion
        Suggestion sug = getItem(position);
        // Get views
        TextView sugText = (TextView) view.findViewById(R.id.item_text_suggestion);
        TextView sugItems = (TextView) view.findViewById(R.id.item_text_n_items);
        // Set icon
        if(sug.isRecentQuery()) ((ImageView) view.findViewById(R.id.item_img)).setImageResource(R.drawable.ico_recent);
        else ((ImageView) view.findViewById(R.id.item_img)).setImageResource(R.drawable.magnlens);
        // Set suggestion
        setColorOnQuery(sugText, sug.getResult(), mQuery);
        // Set number of suggestions
        int resultValue = sug.getResultValue();
        if (resultValue > 0) {
            sugItems.setText("(" + resultValue + " " + 
                            ((resultValue > 1) 
                            ? getContext().getString(R.string.my_order_items_label) 
                            : getContext().getString(R.string.my_order_item_label)) +
                            ")");
        } else {
            sugItems.setVisibility(View.GONE);
        }

        return view;
    }
    
    
    /**
     * Set the link into a string to order status
     * 
     * @author sergiopereira
     * @see <href=http://www.chrisumbel.com/article/android_textview_rich_text_spannablestring>SpannableString</href>
     */
    private void setColorOnQuery(TextView textView, String mainText, String query) {
        String titleString = mainText;
        int index = titleString.toLowerCase().indexOf(query.toLowerCase());
        if(index != -1) {
            SpannableString title = new SpannableString(titleString);
            title.setSpan(new StyleSpan(Typeface.BOLD), index, index + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(title);
        } else {
            textView.setText(titleString);
        }
    }

}
