package com.mobile.controllers;

import java.util.List;
import com.mobile.components.customfontviews.RadioButton;
import com.mobile.framework.objects.PickUpStationObject;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;
import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PickupStationsAdapter extends ArrayAdapter<PickUpStationObject> {

    private PickUpStationObject pickUpStationObject;
    private List<PickUpStationObject> objects;
    private Context context;
    private boolean[] checks;

    static class PickupStationViewHolder {
        TextView pickupStationAddress;
        TextView pickupStationCity;
        TextView pickupStationOpeningHours;
        ImageView pickupStationImage;
        RadioButton pickupStationRadioButton;
    }

    public PickupStationsAdapter(Context context, List<PickUpStationObject> objects) {
        super(context, R.layout._def_checkout_shipping_pickup_station, objects);
        this.objects = objects;
        this.context = context;
        this.checks = new boolean[objects.size()];
        this.checks[0] = true;
        this.pickUpStationObject = objects.get(0);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final PickUpStationObject pickUpStationObject = objects.get(position);
        PickupStationViewHolder pickupStationViewHolder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.checkout_shipping_pickup_station, parent,
                    false);


            pickupStationViewHolder = new PickupStationViewHolder();
            pickupStationViewHolder.pickupStationAddress = (TextView) convertView.findViewById(R.id.pickup_station_address);
            pickupStationViewHolder.pickupStationCity = (TextView) convertView.findViewById(R.id.pickup_station_city);
            pickupStationViewHolder.pickupStationOpeningHours = (TextView) convertView.findViewById(R.id.pickup_station_opening_hours);
            pickupStationViewHolder.pickupStationImage = (ImageView) convertView.findViewById(R.id.pickup_station_image);
            pickupStationViewHolder.pickupStationRadioButton = (RadioButton) convertView.findViewById(R.id.pickup_station_radio_button);

            convertView.setTag(pickupStationViewHolder);
        } else {
            pickupStationViewHolder = (PickupStationViewHolder) convertView.getTag();
        }

        pickupStationViewHolder.pickupStationRadioButton.setOnClickListener(new OnClickListener() {
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
        
        pickupStationViewHolder.pickupStationRadioButton.setChecked(checks[position]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setDetailsWithBold(pickUpStationObject, pickupStationViewHolder);
        } else {
            setDetails(pickUpStationObject, pickupStationViewHolder);
        }
        RocketImageLoader.instance.loadImage(pickUpStationObject.getImage(),
                pickupStationViewHolder.pickupStationImage, null, R.drawable.no_image_small);

        return convertView;
    }

    private void setDetails(PickUpStationObject pickUpStationObject, PickupStationViewHolder pickupStationViewHolder) {

        String divider = " ";

        // Address
        String address = context.getResources().getString(R.string.pickup_station_address);
        // String address = "Address:";
        String pickupStationAddress = pickUpStationObject.getAddress();

        pickupStationViewHolder.pickupStationAddress.setText(address + divider + pickupStationAddress);

        // City
        String city = context.getResources().getString(R.string.pickup_station_city);
        String pickupStationCity = pickUpStationObject.getCity();

        pickupStationViewHolder.pickupStationCity.setText(city + divider + pickupStationCity);
        
        // Opening hours
        String hours = context.getResources().getString(R.string.pickup_station_opening_hours);
        String pickupStationHours = pickUpStationObject.getOpening_hours();

        pickupStationViewHolder.pickupStationOpeningHours.setText(hours + divider + pickupStationHours);

    }

    private void setDetailsWithBold(PickUpStationObject pickUpStationObject, PickupStationViewHolder pickupStationViewHolder) {
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        final StyleSpan iss = new StyleSpan(android.graphics.Typeface.NORMAL);
        boolean bamilo = context.getResources().getBoolean(R.bool.is_bamilo_specific);
        String divider = " ";

        // Address
        String address = context.getResources().getString(R.string.pickup_station_address);
        String pickupStationAddress = pickUpStationObject.getAddress();
        SpannableString sb = new SpannableString(address + divider + pickupStationAddress);

        if (bamilo) {
            sb.setSpan(bss, 0, address.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(iss, address.length(), address.length() + pickupStationAddress.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            sb.setSpan(bss, 0, address.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(iss, address.length(), address.length() + pickupStationAddress.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        pickupStationViewHolder.pickupStationAddress.setText(sb);

        // City
        String city = context.getResources().getString(R.string.pickup_station_city);
        String pickupStationCity = pickUpStationObject.getCity();
        sb = new SpannableString(city + divider + pickupStationCity);

        if (bamilo) {
            sb.setSpan(bss, 0, city.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(iss, city.length(), city.length() + pickupStationCity.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            sb.setSpan(bss, 0, city.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(iss, city.length(), city.length() + pickupStationCity.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        pickupStationViewHolder.pickupStationCity.setText(sb);
        //
        // // Opening hours
        String hours = context.getResources().getString(R.string.pickup_station_opening_hours);
        String pickupStationHours = pickUpStationObject.getOpening_hours();
        sb = new SpannableString(hours + divider + pickupStationHours);

        if (bamilo) {
            sb.setSpan(bss, 0, hours.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(iss, hours.length(), hours.length() + pickupStationHours.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            sb.setSpan(bss, 0, hours.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(iss, hours.length(), hours.length() + pickupStationHours.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        pickupStationViewHolder.pickupStationOpeningHours.setText(sb);
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

    protected void replaceSelection(ViewGroup parent, int position) {
        removeOtherSelections(parent);
        for (int i = 0; i < checks.length; i++) {
            checks[i] = false;
        }
        checks[position] = true;
        notifyDataSetChanged();

    }
}
