package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.List;

import pt.rocket.components.customfontviews.CheckBox;
import pt.rocket.components.customfontviews.RadioButton;
import pt.rocket.framework.objects.PickUpStationObject;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PickupStationsAdapter extends ArrayAdapter<PickUpStationObject> {

    private PickUpStationObject pickUpStationObject;
    private List<PickUpStationObject> objects;
    private Context context;
    private boolean[] checks;

    static class PickupStationViewHolder {
        TextView pickup_station_address;
        TextView pickup_station_city;
        TextView pickup_station_opening_hours;
        ImageView pickup_station_image;
        RadioButton pickup_station_radio_button;
    }

    public PickupStationsAdapter(Context context, List<PickUpStationObject> objects) {
        super(context, R.layout.pickup_station_layout, objects);
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
            convertView = inflater.inflate(R.layout.pickup_station_layout, parent, false);

            pickupStationViewHolder = new PickupStationViewHolder();

            pickupStationViewHolder.pickup_station_address = (TextView) convertView
                    .findViewById(R.id.pickup_station_address);
            pickupStationViewHolder.pickup_station_city = (TextView) convertView
                    .findViewById(R.id.pickup_station_city);
            pickupStationViewHolder.pickup_station_opening_hours = (TextView) convertView
                    .findViewById(R.id.pickup_station_opening_hours);
            pickupStationViewHolder.pickup_station_image = (ImageView) convertView
                    .findViewById(R.id.pickup_station_image);
            pickupStationViewHolder.pickup_station_radio_button = (RadioButton) convertView
                    .findViewById(R.id.pickup_station_radio_button);

            convertView.setTag(pickupStationViewHolder);
        } else {
            pickupStationViewHolder = (PickupStationViewHolder) convertView.getTag();
        }

        pickupStationViewHolder.pickup_station_radio_button
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        CompoundButton buttonView = (CompoundButton) arg0;

                        if (buttonView.isChecked()) {
                            for (int i = 0; i < checks.length; i++) {
                                checks[i] = false;
                            }
                            checks[position] = true;
                            removeOtherSelections(parent, buttonView);
                            PickupStationsAdapter.this.pickUpStationObject = objects.get(position);
                        }

                    }
                });

        pickupStationViewHolder.pickup_station_radio_button.setChecked(checks[position]);
        setDetails(pickUpStationObject, pickupStationViewHolder);

        RocketImageLoader.instance.loadImage(pickUpStationObject.getImage(),
                pickupStationViewHolder.pickup_station_image, null, R.drawable.no_image_small);

        return convertView;
    }

    private void setDetails(PickUpStationObject pickUpStationObject,
            PickupStationViewHolder pickupStationViewHolder) {
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        final StyleSpan iss = new StyleSpan(android.graphics.Typeface.NORMAL);
        boolean bamilo = context.getResources().getBoolean(R.bool.is_bamilo_specific);

        // Address
        String address = context.getResources().getString(R.string.pickup_station_address);
        String pickupStationAddress = pickUpStationObject.getAddress();
        SpannableStringBuilder sb = new SpannableStringBuilder(address + pickupStationAddress);

        if (bamilo) {
            sb.setSpan(bss, sb.length(), sb.length() - address.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(iss, sb.length() - address.length(), 0, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            sb.setSpan(bss, 0, address.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(iss, address.length(), address.length() + pickupStationAddress.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        pickupStationViewHolder.pickup_station_address.setText(sb);

        // City
        String city = context.getResources().getString(R.string.pickup_station_city);
        String pickupStationCity = pickUpStationObject.getCity();
        sb = new SpannableStringBuilder(city + pickupStationCity);

        if (bamilo) {
            sb.setSpan(bss, sb.length(), sb.length() - city.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(iss, sb.length() - city.length(), 0, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            sb.setSpan(bss, 0, city.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(iss, city.length(), city.length() + pickupStationCity.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        pickupStationViewHolder.pickup_station_city.setText(sb);

        // Opening hours
        String hours = context.getResources().getString(R.string.pickup_station_opening_hours);
        String pickupStationHours = pickUpStationObject.getOpening_hours();
        sb = new SpannableStringBuilder(hours + pickupStationHours);

        if (bamilo) {
            sb.setSpan(bss, sb.length(), sb.length() - hours.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(iss, sb.length() - hours.length(), 0, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            sb.setSpan(bss, 0, hours.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(iss, hours.length(), hours.length() + pickupStationHours.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        pickupStationViewHolder.pickup_station_opening_hours.setText(sb);

    }

    public PickUpStationObject getSelectedPickupStation() {
        return pickUpStationObject;
    }

    private void removeOtherSelections(ViewGroup parent, CompoundButton buttonView) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof RadioButton && v != buttonView) {
                ((RadioButton) v).setChecked(false);
            } else if (v instanceof ViewGroup) {
                removeOtherSelections((ViewGroup) v, buttonView);
            }
        }

    }
}
