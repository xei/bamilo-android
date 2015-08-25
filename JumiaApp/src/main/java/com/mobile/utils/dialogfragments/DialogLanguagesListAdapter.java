package com.mobile.utils.dialogfragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Created by rsoares on 8/25/15.
 */
public class DialogLanguagesListAdapter extends DialogListAdapter{

    public DialogLanguagesListAdapter(Context mActivity, ArrayList<String> mItems) {
        super(mActivity, mItems);
    }

    public DialogLanguagesListAdapter(Context mActivity, ArrayList<String> mItems, ArrayList<String> mItemsAvailable) {
        super(mActivity, mItems, mItemsAvailable);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.dialog_language_list_item, parent, false);
        } else {
            view = convertView;
        }
        TextView textView = (TextView) view.findViewById(R.id.item_text);

        if(mItemsAvailable != null && !mItemsAvailable.contains(mItems.get(position))){
            view.setEnabled(false);
            textView.setVisibility(View.GONE);
        } else {
            view.setEnabled(true);
            textView.setVisibility(View.VISIBLE);
            textView.setText(mItems.get(position));
        }
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_item_checkbox);
        checkBox.setChecked(position == mCheckedPosition);

        return view;
    }
}
