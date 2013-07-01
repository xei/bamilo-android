package pt.rocket.controllers;

import pt.rocket.view.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This Class is used to create an adapter for the list of account options. It is called by Home Activity.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Sergio Pereira
 * 
 * @version 1.01
 * 
 *          2012/06/19
 * 
 */
public class MyAccountAdapter extends BaseAdapter {

    String[] options;
    Context context;

    /**
     * The constructor for this adapter
     * 
     * @param context
     *            The context from where this adapter is called
     * @param categories
     *            The array containing the categories to display
     */
    public MyAccountAdapter(Context context, String[] options) {
        this.options = options;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.options.length;
    }

    @Override
    public Object getItem(int position) {
        return options[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the Inflate Service
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = null;

        if (convertView != null) {
            v = convertView;
        } else {
            v = inflater.inflate(R.layout.my_account_list_item, parent, false);
        }

        // Get the Category Name
        TextView optionsName = (TextView) v.findViewById(R.id.option_name);
        TextView optionsDescripton = (TextView) v.findViewById(R.id.option_info);
        optionsName.setText(this.options[position]);

        switch (position) {
        case 0:
            optionsDescripton.setText(context.getResources().getString(R.string.option2_description));
            break;

        case 1:
            optionsDescripton.setText(context.getResources().getString(R.string.option3_description));
            break;
        }

        // Return the Category Item View
        return v;
    }

}
