package pt.rocket.controllers;

import pt.rocket.view.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
	private final String[] values;
	private final TypedArray flags;

	public CountryAdapter(Context context, String[] values, TypedArray flags) {
		super(context, R.layout.change_country_row, values);
		this.context = context;
		this.values = values;
		this.flags = flags;
	}
	

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Country List", "Position: " + position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.change_country_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.country_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.flag);
        textView.setText(values[position]);
        Drawable flag = flags.getDrawable(position);
        Drawable border = context.getResources().getDrawable(R.drawable.shape_border_flag);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[] { flag, border });
        imageView.setImageDrawable(layerDrawable);
        return rowView;
    }
}