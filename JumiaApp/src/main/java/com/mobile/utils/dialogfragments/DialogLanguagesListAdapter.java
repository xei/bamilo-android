package com.mobile.utils.dialogfragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.configs.Languages;
import com.mobile.view.R;

/**
 * Created by rsoares on 8/25/15.
 */
public class DialogLanguagesListAdapter extends DialogListAdapter{

    public DialogLanguagesListAdapter(Context mActivity, Languages languages) {
        super(mActivity, languages.getLanguageNames());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.dialog_list_item, parent, false);
        } else {
            view = convertView;
        }
        TextView languageName = (TextView) view.findViewById(R.id.item_text);

        if(mItemsAvailable != null && !mItemsAvailable.contains(mItems.get(position))){
            view.setEnabled(false);
            languageName.setVisibility(View.GONE);
        } else {
            view.setEnabled(true);
            languageName.setVisibility(View.VISIBLE);
            languageName.setText(mItems.get(position));
        }

        CheckBox languageCheckBox = (CheckBox) view.findViewById(R.id.dialog_item_checkbox);
        languageCheckBox.setChecked(position == mCheckedPosition);

        return view;
    }
}
