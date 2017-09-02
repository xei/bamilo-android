package com.mobile.forms;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.controllers.PickupStationsAdapter;
import com.mobile.service.forms.PickUpStationObject;
import com.mobile.service.objects.checkout.ShippingFormFieldPUS;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents an Shipping Method Sub form.
 * 
 * @author Manuel Silva
 * 
 */
public class ShippingMethodSubForm {

	public final static String TAG = ShippingMethodSubForm.class.getSimpleName();

    public View dataControl;

    public IcsSpinner icsSpinner;

    public AbsListView pickupStationsListView;

    public ShippingFormFieldPUS shippingMethodSubFormHolder;

    private int currentSelected = 0;
    /**
     * Empty constructor
     */
    public ShippingMethodSubForm(){
        // ...
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
        // Get title
        dataControl.findViewById(R.id.pickup_stations_title).setVisibility(View.VISIBLE);
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
        currentSelected = 0;
        // Listeners
        icsSpinner.setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
                pickupStationsListView = (AbsListView) dataControl.findViewById(R.id.pickup_stations_list_view);
                if (pickupStationByRegion.get(icsSpinner.getItemAtPosition(position)).size() > 0) {
                    pickupStationsListView.setVisibility(View.VISIBLE);
                    pickupStationsListView.setAdapter(new PickupStationsAdapter(view.getContext(), pickupStationByRegion.get(icsSpinner.getItemAtPosition(position)), currentSelected));
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

    public int getSelectedPUS() {
        if (pickupStationsListView.getAdapter() instanceof PickupStationsAdapter) {
            return ((PickupStationsAdapter) pickupStationsListView.getAdapter()).getPosition(((PickupStationsAdapter) pickupStationsListView.getAdapter()).getSelectedPickupStation());

        }
        return 0;
    }

    public void setSelectedPUS(int pos) {
        currentSelected = pos;
        if (pickupStationsListView.getAdapter() instanceof PickupStationsAdapter) {
            ((PickupStationsAdapter) pickupStationsListView.getAdapter()).setSelection(pos);
        }
    }

}
