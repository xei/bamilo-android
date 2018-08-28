package com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import android.widget.TextView;
import com.bamilo.android.R;

import java.util.ArrayList;

/**
 * Adapter that shows a list of generic items for a dialg
 * <p/>
 * Created by rsoares on 8/25/15.
 *
 * @modified Paulo Carvalho
 */
public class DialogListAdapter extends BaseAdapter {

    protected int mCheckedPosition = -1;

    protected LayoutInflater mInflater;

    protected ArrayList<String> mItems;

    protected ArrayList<String> mItemsAvailable;

    /**
     * Constructor
     */
    public DialogListAdapter(Context context, ArrayList<String> mItems) {
        this(context, mItems, null);
    }

    public DialogListAdapter(Context context, ArrayList<String> mItems, ArrayList<String> mItemsAvailable) {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = mItems;
        this.mItemsAvailable = mItemsAvailable;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.BaseAdapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.dialog_list_item, parent, false);
        } else {
            view = convertView;
        }
        TextView textView = (TextView) view.findViewById(R.id.item_text);
        TextView textViewUnAvailable = (TextView) view.findViewById(R.id.item_text_unavailable);
        if (mItemsAvailable != null && !mItemsAvailable.contains(mItems.get(position))) {
            view.setEnabled(false);
            textView.setVisibility(View.GONE);
            if (textViewUnAvailable != null) {
                textViewUnAvailable.setVisibility(View.VISIBLE);
                textViewUnAvailable.setText(mItems.get(position));
            }
        } else {
            view.setEnabled(true);
            if (textViewUnAvailable != null)
                textViewUnAvailable.setVisibility(View.GONE);

            textView.setVisibility(View.VISIBLE);
            textView.setText(mItems.get(position));
        }
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_item_checkbox);
        checkBox.setChecked(position == mCheckedPosition);

        return view;
    }

    public void setCheckedPosition(int position) {
        mCheckedPosition = position;
    }

    /**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     *
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    public ArrayList<String> getItems() {
        return mItems;
    }

}

