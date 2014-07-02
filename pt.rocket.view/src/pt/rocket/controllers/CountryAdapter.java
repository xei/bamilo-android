package pt.rocket.controllers;

import org.holoeverywhere.widget.TextView;

import pt.rocket.view.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;

import de.akquinet.android.androlog.Log;

/**
 * This Class is used to create an adapter for the list of countries.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Hugo Matilla
 * 
 * @version 1.00
 * 
 *          2013/04/10
 * 
 */

public class CountryAdapter extends ArrayAdapter<String> {

	private final Context context;
	private String[] values;
	private final String[] flagsList;
	private LayoutInflater mInflater;
	public CountryAdapter(Context context, String[] values, String[] flagsList) {
		super(context, R.layout.change_country_row, values);
		this.context = context;
		this.values = values;
		this.flagsList = flagsList;
		mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void updateValues(String[] values){
	    this.values = values;
	    this.notifyDataSetChanged();
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Country List", "Position: " + position);
        if(mInflater == null){
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        View rowView = convertView;
        if(rowView == null){
            rowView = mInflater.inflate(R.layout.change_country_row, parent, false);    
        }
        
        TextView textView = (TextView) rowView.findViewById(R.id.country_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.flag);
        textView.setText(values[position]);
        if(flagsList != null && flagsList.length > 0){
            AQuery aq = new AQuery(context);
            aq.id(imageView).image(flagsList[position]);    
        }
        
        
        return rowView;
    }
}