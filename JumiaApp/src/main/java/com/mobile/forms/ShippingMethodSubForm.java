package com.mobile.forms;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.controllers.PickupStationsAdapter;
import com.mobile.newFramework.forms.PickUpStationObject;
import com.mobile.newFramework.objects.checkout.ShippingMethodSubFormHolder;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents an Shipping Method Sub form.
 * 
 * @author Manuel Silva
 * 
 */
public class ShippingMethodSubForm { //implements Parcelable {

	public final static String TAG = ShippingMethodSubForm.class.getSimpleName();

	private int lastID = 0x7f096000;

	//private IcsAdapterView.OnItemSelectedListener spinnerSelectedListener;

    public View dataControl;

    public IcsSpinner icsSpinner;

    public ListView pickupStationsListView;

    public ShippingMethodSubFormHolder shippingMethodSubFormHolder;

    public ShippingMethodSubForm(){}

    /**
     * Gets the next available id for the creation of controls
     * 
     * @return The next ID
     */
    public int getNextId() {
        return ++lastID;
    }

    /**
     * Generate the layout for PUS
     */
    public View generateForm(View parent) {
        Context context = parent.getContext();
        // Case form without options return a dummy view
        if(this.shippingMethodSubFormHolder.options.size() == 0) return this.dataControl = new View(context);
        // Case > 0 inflate a merge layout
        this.dataControl = parent ;
        // Get spinner
        icsSpinner = (IcsSpinner) dataControl.findViewById(android.R.id.custom);
        //icsSpinner.setId(getNextId());
        // Show PUS options
        final HashMap<String, ArrayList<PickUpStationObject>> pickupStationByRegion = new HashMap<>();
        ArrayList<String> mSpinnerOptions = new ArrayList<>();
        for(int j = 0; j< this.shippingMethodSubFormHolder.options.size(); j++) {
            for (int i = 0; i < this.shippingMethodSubFormHolder.options.get(j).getRegions().size(); i++) {
                if (!mSpinnerOptions.contains(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName())) {
                    mSpinnerOptions.add(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName());
                }
                if (!pickupStationByRegion.containsKey(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName())) {
                    pickupStationByRegion.put(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName(), new ArrayList<PickUpStationObject>());
                }
                pickupStationByRegion.get(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName()).add(this.shippingMethodSubFormHolder.options.get(j));
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.form_spinner_item, new ArrayList<>(mSpinnerOptions));
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        icsSpinner.setAdapter(adapter);
        icsSpinner.setPrompt(this.shippingMethodSubFormHolder.label);
        icsSpinner.setVisibility(View.VISIBLE);

        this.dataControl.setVisibility(View.GONE);

        HoloFontLoader.applyDefaultFont(icsSpinner);
        // Listeners

        icsSpinner.setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
                pickupStationsListView = (ListView) dataControl.findViewById(R.id.pickup_stations_list_view);
                if (pickupStationByRegion.get(icsSpinner.getItemAtPosition(position)).size() > 0) {
                    pickupStationsListView.setVisibility(View.VISIBLE);
                    pickupStationsListView.setAdapter(new PickupStationsAdapter(view.getContext(), pickupStationByRegion.get(icsSpinner.getItemAtPosition(position))));
                } else {
                    pickupStationsListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(IcsAdapterView<?> parent) {

            }
        });

        return this.dataControl;
    }


    /**
     * Generate the layout for PUS
     */
    public View generateForm(final Context context, ViewGroup parent) {
        // Case form without options return a dummy view
        if(this.shippingMethodSubFormHolder.options.size() == 0) return this.dataControl = new View(context);
        // Case > 0 inflate a merge layout  
        this.dataControl = View.inflate(context, R.layout.form_icsspinner_shipping, parent);
        // Get spinner
        icsSpinner = (IcsSpinner) dataControl.findViewById(android.R.id.custom);
        //icsSpinner.setId(getNextId());
        // Show PUS options 
        final HashMap<String, ArrayList<PickUpStationObject>> pickupStationByRegion = new HashMap<>();
        ArrayList<String> mSpinnerOptions = new ArrayList<>();
        for(int j = 0; j< this.shippingMethodSubFormHolder.options.size(); j++) {
            for (int i = 0; i < this.shippingMethodSubFormHolder.options.get(j).getRegions().size(); i++) {
                if (!mSpinnerOptions.contains(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName())) {
                    mSpinnerOptions.add(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName());
                }
                if (!pickupStationByRegion.containsKey(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName())) {
                    pickupStationByRegion.put(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName(), new ArrayList<PickUpStationObject>());
                }
                pickupStationByRegion.get(this.shippingMethodSubFormHolder.options.get(j).getRegions().get(i).getName()).add(this.shippingMethodSubFormHolder.options.get(j));
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.form_spinner_item, new ArrayList<>(mSpinnerOptions));
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        icsSpinner.setAdapter(adapter);
        icsSpinner.setPrompt(this.shippingMethodSubFormHolder.label);
        icsSpinner.setVisibility(View.VISIBLE);

        this.dataControl.setVisibility(View.GONE);

        HoloFontLoader.applyDefaultFont(icsSpinner);
        // Listeners
        
        icsSpinner.setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
                pickupStationsListView = (ListView) dataControl.findViewById(R.id.pickup_stations_list_view);
                if (pickupStationByRegion.get(icsSpinner.getItemAtPosition(position)).size() > 0) {
                    pickupStationsListView.setVisibility(View.VISIBLE);
                    pickupStationsListView.setAdapter(new PickupStationsAdapter(context, pickupStationByRegion.get(icsSpinner.getItemAtPosition(position))));
                } else {
                    pickupStationsListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(IcsAdapterView<?> parent) {

            }
        });

        return this.dataControl;
    }


}
