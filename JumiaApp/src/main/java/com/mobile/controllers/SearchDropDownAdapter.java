package com.mobile.controllers;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.search.Suggestion;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import java.util.List;


/**
 * Array adapter used to show the suggestions
 * @author sergiopereira
 *
 */
public class SearchDropDownAdapter extends ArrayAdapter<Suggestion> implements Filterable {

    public static final String TAG = SearchDropDownAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;

    /**
     * Constructor of adapter for search drop down
     * @param context - the current context
     * @param objects - list of suggestions
     * @author sergiopereira
     */
    public SearchDropDownAdapter(Context context, List<Suggestion> objects) {
        super(context, 0, 0, objects);
        mInflater = LayoutInflater.from(context);
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

        if(sug.getType() == Suggestion.SUGGESTION_CATEGORY){
            // Set suggestion
            setColorOnSpecialQuery(sugText, String.format(getContext().getString(R.string.search_categories_label), sug.getQuery() ,sug.getResult()), sug.getResult(), sug.getQuery());
        } else if(sug.getType() == Suggestion.SUGGESTION_SHOP_IN_SHOP){
            // Set suggestion
            setColorOnSpecialQuery(sugText,  String.format(getContext().getString(R.string.search_shop_in_shop_label),sug.getResult()), sug.getResult(), sug.getQuery());
        } else {
            // Set suggestion
            setColorOnQuery(sugText, sug.getResult(), sug.getQuery());
        }

        // Set icon
        if(sug.isRecentQuery()) ((ImageView) view.findViewById(R.id.item_img)).setImageResource(R.drawable.ico_recent);
        else ((ImageView) view.findViewById(R.id.item_img)).setImageResource(R.drawable.magnlens);

        return view;
    }
    
    
    /**
     * Set the link into a string to order status
     * 
     * @author sergiopereira
     * @see <href=http://www.chrisumbel.com/article/android_textview_rich_text_spannablestring>SpannableString</href>
     */
    private void setColorOnQuery(TextView textView, String titleString, String query) {
        int index = -1;
        if(TextUtils.isNotEmpty(query)){
            index = titleString.toLowerCase().indexOf(query.toLowerCase());
        }

        if(index != -1) {
            SpannableString title = new SpannableString(titleString);
            title.setSpan(new StyleSpan(Typeface.BOLD), index, index + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(title);
        } else {
            textView.setText(titleString);
        }
    }

    /**
     *
     */
    private void setColorOnSpecialQuery(TextView textView, String titleString, String highlight, String query) {
        int index = titleString.toLowerCase().indexOf(query.toLowerCase());
        int indexHighlitght = titleString.toLowerCase().indexOf(highlight.toLowerCase());
        if(index != -1 && indexHighlitght != -1) {
            SpannableString title = new SpannableString(titleString);
            title.setSpan(new StyleSpan(Typeface.BOLD), index, index + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.blue1)), indexHighlitght, indexHighlitght+highlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(title);
        } else {
            textView.setText(titleString);
        }
    }

}
