package com.mobile.view.productdetail.viewtypes.seller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.addresses.AddressCity;
import com.mobile.service.objects.addresses.AddressRegion;
import com.mobile.view.R;
import java.util.List;

/**
 * Created by Farshid since 6/26/2018. contact farshidabazari@gmail.com
 */
class RegionCitySpinnerAdapter extends ArrayAdapter {
    public RegionCitySpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Object item = getItem(position);
        Integer value = null;
        if (item instanceof AddressRegion) {
            AddressRegion region = (AddressRegion) item;
            value = region.getValue();
        } else if (item instanceof AddressCity){
            AddressCity city = (AddressCity) item;
            value = city.getValue();
        }
        if (value != null) {
            convertView = super.getView(position, convertView, parent);
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            // change first item's color to gray
            tv.setTextColor(
                    ContextCompat.getColor(tv.getContext(), value == -1 ? R.color.black_700 : R.color.black_47));
        }
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) convertView;
        tv.setTextColor(ContextCompat.getColor(tv.getContext(), isEnabled(position) ? R.color.black_47 : R.color.black_700));
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        Object item = getItem(position);
        Integer value = null;
        if (item instanceof AddressRegion) {
            AddressRegion region = (AddressRegion) item;
            value = region.getValue();
        } else if (item instanceof AddressCity){
            AddressCity city = (AddressCity) item;
            value = city.getValue();
        }
        if (value != null) {
            return value != -1;
        }
        return super.isEnabled(position);
    }
}
