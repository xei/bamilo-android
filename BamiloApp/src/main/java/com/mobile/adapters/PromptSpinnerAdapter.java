package com.mobile.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobile.view.R;

import java.util.List;

public class PromptSpinnerAdapter extends ArrayAdapter {
    private String prompt;

    public PromptSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects, String promptText) {
        super(context, resource, objects);
        this.prompt = promptText;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = super.getView(0, convertView, parent);
        }
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        if (position == 0) {
            tv.setText(prompt);
        } else {
            Object item = getItem(position - 1);
            if (item != null) {
                tv.setText(item.toString());
            }
        }
        tv.setTextColor(ContextCompat.getColor(tv.getContext(), position == 0 ? R.color.black_700 : R.color.black_47));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = super.getDropDownView(0, convertView, parent);
        }
        TextView tv = (TextView) convertView;
        if (position == 0) {
            tv.setText(prompt);
        } else {
            Object item = getItem(position - 1);
            if (item != null) {
                tv.setText(item.toString());
            }
        }
        tv.setTextColor(ContextCompat.getColor(tv.getContext(), isEnabled(position) ? R.color.black_47 : R.color.black_700));
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0 && super.isEnabled(position);
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
        notifyDataSetChanged();
    }
}