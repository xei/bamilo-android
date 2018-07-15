package com.mobile.view.productdetail.viewtypes.seller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.addresses.AddressCity;
import com.mobile.view.R;
import com.mobile.view.productdetail.network.model.City;
import com.mobile.view.productdetail.network.model.Region;
import java.util.List;

/**
 * Created by Farshid since 6/26/2018. contact farshidabazari@gmail.com
 */
class RegionCitySpinnerAdapter extends ArrayAdapter {

    @NonNull
    private final Context mContext;
    private final int mResource;
    @NonNull
    private final List items;

    public RegionCitySpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
            @NonNull List regions) {
        super(context, resource, regions);
        mContext = context;
        mResource = resource;
        items = regions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
            @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        Object item = getItem(position);
        Integer value = null;
        if (item instanceof Region) {
            Region region = (Region) item;
            value = region.getValue();
        } else if (item instanceof AddressCity) {
            AddressCity city = (AddressCity) item;
            value = city.getValue();
        }
        if (value != null) {
            return value != -1;
        }
        return super.isEnabled(position);
    }

    public View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(mContext).inflate(mResource, parent, false);

        TextView tv = view.findViewById(R.id.spinnerContent_textView_title);

        tv.setTextColor(ContextCompat.getColor(tv.getContext(),
                isEnabled(position) ? R.color.black_47 : R.color.black_700));

        if (items.get(position) instanceof Region) {
            tv.setText(((Region) items.get(position)).getLabel());
        } else {
            tv.setText(((City) items.get(position)).getLabel());
        }

        return view;
    }
}
