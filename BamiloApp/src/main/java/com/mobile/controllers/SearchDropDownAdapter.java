package com.mobile.controllers;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.interfaces.OnProductViewHolderClickListener;
import com.mobile.service.objects.search.Suggestion;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.utils.TextUtils;
import com.mobile.view.R;

import java.util.List;


/**
 * Array adapter used to show the suggestions
 * @author sergiopereira
 *
 */
public class SearchDropDownAdapter extends RecyclerView.Adapter<SearchDropDownAdapter.SuggestionListViewHolder> implements View.OnClickListener {


    public static final String TAG = SearchDropDownAdapter.class.getSimpleName();
    private final Context mContext;
    protected List<Suggestion> mDataSet;
    private OnProductViewHolderClickListener mOnViewHolderClicked;

    /**
     * Constructor of adapter for search drop down
     * @param context - the current context
     * @param objects - list of suggestions
     * @author sergiopereira
     */
    public SearchDropDownAdapter(Context context, List<Suggestion> objects) {
        mContext = context;
        mDataSet = objects;
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getItem(int)
     */
    public Suggestion getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public SuggestionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SuggestionListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SuggestionListViewHolder holder, int position) {
        // Get current suggestion
        Suggestion sug = mDataSet.get(position);
        if (sug.getType() == Suggestion.SUGGESTION_CATEGORY) {
            // Set suggestion
            setColorOnSpecialQuery(holder.suggestionTextView, String.format(mContext.getString(R.string.search_categories_label), sug.getQuery(), sug.getResult()), sug.getResult(), sug.getQuery());
        } else if (sug.getType() == Suggestion.SUGGESTION_SHOP_IN_SHOP) {
            // Set suggestion
            setColorOnSpecialQuery(holder.suggestionTextView, String.format(mContext.getString(R.string.search_shop_in_shop_label), sug.getResult()), sug.getResult(), sug.getQuery());
        } else {
            // Set suggestion
            setColorOnQuery(holder.suggestionTextView, sug.getResult(), sug.getQuery());
        }

        // Set icon
        if (sug.isRecentQuery()) holder.suggestionImageView.setImageResource(R.drawable.ico_recent);
        else holder.suggestionImageView.setVisibility(View.GONE);
        holder.itemView.setTag(R.id.position, position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Set the link into a string to order status
     * 
     * @author sergiopereira
     * @see <href=http://www.chrisumbel.com/article/android_textview_rich_text_spannablestring>SpannableString</href>
     */
    private void setColorOnQuery(TextView textView, String titleString, String query) {
        int index = IntConstants.INVALID_POSITION;
        if(TextUtils.isNotEmpty(query)){
            index = titleString.toLowerCase().indexOf(query.toLowerCase());
        }

        if(index != IntConstants.INVALID_POSITION) {
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
        if(indexHighlitght == 0){
            // To avoid cases where query matches category name
            indexHighlitght = query.length() + titleString.toLowerCase().substring(query.length()).indexOf(highlight.toLowerCase());
        }

        if(index != -1 && indexHighlitght != -1) {
            SpannableString title = new SpannableString(titleString);
            title.setSpan(new StyleSpan(Typeface.BOLD), index, index + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.blue_1)), indexHighlitght, indexHighlitght+highlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(title);
        } else {
            textView.setText(titleString);
        }
    }

    /**
     * Set the listener the click on view holder.
     * @param listener - the listener
     */
    public void setOnViewHolderClickListener(OnProductViewHolderClickListener listener) {
        this.mOnViewHolderClicked = listener;
    }

    @Override
    public void onClick(View view) {
        // Case other sent to listener
        if (mOnViewHolderClicked != null) {
            // position
            int position = (Integer) view.getTag(R.id.position);
            mOnViewHolderClicked.onViewHolderClick(this, position);
        }
    }

    public class SuggestionListViewHolder extends RecyclerView.ViewHolder {

        // Data
        public TextView suggestionTextView;
        public ImageView suggestionImageView;

        /**
         * Constructor
         * @param view -  the view holder
         */
        public SuggestionListViewHolder(View view) {
            super(view);
            suggestionTextView = (TextView) view.findViewById(R.id.item_text_suggestion);
            suggestionImageView = (ImageView) view.findViewById(R.id.item_img);
        }
    }

}
