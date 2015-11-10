package com.mobile.controllers;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.mobile.components.customfontviews.RadioButton;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.forms.PickUpStationObject;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import java.util.List;

public class PickupStationsAdapter extends ArrayAdapter<PickUpStationObject> {

    private static final String TAG = PickupStationsAdapter.class.getName();
    private PickUpStationObject pickUpStationObject;
    private final List<PickUpStationObject> objects;
    private final Context context;
    private final boolean[] checks;

    static class PickupStationViewHolder {
        public TextView address;
        public TextView city;
        public TextView hours;
        public ImageView image;
        public RadioButton button;
        public TextView fee;
        public View info;
    }

    public PickupStationsAdapter(Context context, List<PickUpStationObject> objects) {
        super(context, R.layout.checkout_shipping_pickup_station, objects);
        this.objects = objects;
        this.context = context;
        this.checks = new boolean[objects.size()];
        this.checks[0] = true;
        this.pickUpStationObject = objects.get(0);
    }

    public PickupStationsAdapter(Context context, List<PickUpStationObject> objects, int selected) {
        super(context, R.layout.checkout_shipping_pickup_station, objects);
        this.objects = objects;
        this.context = context;
        this.checks = new boolean[objects.size()];
        this.checks[selected] = true;
        com.mobile.newFramework.utils.output.Print.i(TAG, "code1pus : before PickupStationsAdapter position " + selected);
        this.pickUpStationObject = objects.get(0);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final PickUpStationObject pickUpStationObject = objects.get(position);
        PickupStationViewHolder pickupStationViewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.checkout_shipping_pickup_station, parent,false);
            pickupStationViewHolder = new PickupStationViewHolder();
            pickupStationViewHolder.address = (TextView) convertView.findViewById(R.id.pickup_station_address);
            pickupStationViewHolder.city = (TextView) convertView.findViewById(R.id.pickup_station_city);
            pickupStationViewHolder.hours = (TextView) convertView.findViewById(R.id.pickup_station_opening_hours);
            pickupStationViewHolder.image = (ImageView) convertView.findViewById(R.id.pickup_station_image);
            pickupStationViewHolder.button = (RadioButton) convertView.findViewById(R.id.pickup_station_radio_button);
            pickupStationViewHolder.fee = (TextView) convertView.findViewById(R.id.pickup_station_shipping_fee);
            pickupStationViewHolder.info = convertView.findViewById(R.id.pickup_station_shipping_fee_info);
            convertView.setTag(pickupStationViewHolder);
        } else {
            pickupStationViewHolder = (PickupStationViewHolder) convertView.getTag();
        }

        pickupStationViewHolder.button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                CompoundButton buttonView = (CompoundButton) arg0;
                if (buttonView.isChecked()) {
                    replaceSelection(parent, position);
                    PickupStationsAdapter.this.pickUpStationObject = objects.get(position);
                }
            }
        });

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceSelection(parent, position);
                PickupStationsAdapter.this.pickUpStationObject = objects.get(position);
            }
        });
        
        pickupStationViewHolder.button.setChecked(checks[position]);
        if (checks[position]) {
            PickupStationsAdapter.this.pickUpStationObject = objects.get(position);
        }

        setDetails(pickUpStationObject, pickupStationViewHolder);

        RocketImageLoader.instance.loadImage(pickUpStationObject.getImage(),
                pickupStationViewHolder.image, null, R.drawable.no_image_small);

        return convertView;
    }

    private void setDetails(PickUpStationObject pickUpStationObject, PickupStationViewHolder pickupStationViewHolder) {
        // Address
        String pickupStationAddress = TextUtils.htmlEncode(pickUpStationObject.getAddress());
        String address = String.format(getContext().getString(R.string.pickup_station_address), pickupStationAddress);
        pickupStationViewHolder.address.setText(Html.fromHtml(address));
        // City
        String pickupStationCity = TextUtils.htmlEncode(pickUpStationObject.getCity());
        String city = String.format(getContext().getString(R.string.pickup_station_city), pickupStationCity);
        pickupStationViewHolder.city.setText(Html.fromHtml(city));
        // Hours
        String pickupStationHours = TextUtils.htmlEncode(pickUpStationObject.getOpeningHours());
        String hours = String.format(getContext().getString(R.string.pickup_station_opening_hours), pickupStationHours);
        pickupStationViewHolder.hours.setText(Html.fromHtml(hours));
        // Shipping Fee
        if (pickUpStationObject.getShippingFee() > 0) {
            String shippingFee = TextUtils.htmlEncode(CurrencyFormatter.formatCurrency(String.valueOf(pickUpStationObject.getShippingFee())));
            String fee = String.format(getContext().getString(R.string.shipping_fee), shippingFee);
            pickupStationViewHolder.fee.setText(Html.fromHtml(fee));
            pickupStationViewHolder.fee.setVisibility(View.VISIBLE);
            pickupStationViewHolder.info.setVisibility(View.VISIBLE);
        }
    }

    public PickUpStationObject getSelectedPickupStation() {
        return pickUpStationObject;
    }

    private void removeOtherSelections(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof RadioButton) {
                ((RadioButton) v).setChecked(false);
            } else if (v instanceof ViewGroup) {
                removeOtherSelections((ViewGroup) v);
            }
        }

    }

    public void setSelection(int position) {
        if (checks.length >= position) {
            checks[position] = true;
        }

    }

    protected void replaceSelection(ViewGroup parent, int position) {
        removeOtherSelections(parent);
        for (int i = 0; i < checks.length; i++) {
            checks[i] = false;
        }
        checks[position] = true;
        notifyDataSetChanged();

    }
}
