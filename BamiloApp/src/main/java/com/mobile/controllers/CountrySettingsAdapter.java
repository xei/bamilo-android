package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.service.utils.shop.ShopSelector;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;
import com.mobile.view.fragments.MyAccountFragment;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/08/25
 *
 * @edit Shahrooz Jahanshah
 * @date 2016/10/31
 */
public class CountrySettingsAdapter extends BaseAdapter {
    public static class CountryLanguageInformation {
        public String countryName;
        public String countryFlag;
    }

    private final CountryLanguageInformation countryObject;
    private final LayoutInflater mInflater;
    private boolean mSingleShop = false;

    public CountrySettingsAdapter(Context context, CountryLanguageInformation countryObject) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.countryObject = countryObject;
        mSingleShop = ShopSelector.isSingleShopCountry();
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {

            view = mInflater.inflate(R.layout.country_settings_list_item, parent, false);
        } else {
            view = convertView;
        }

        TextView country = (TextView) view.findViewById(R.id.option_name);
        TextView info = (TextView) view.findViewById(R.id.option_info);
        ImageView img = (ImageView) view.findViewById(R.id.country_icon);
        img.setImageDrawable(view.getResources().getDrawable(R.drawable.privacy_icons));
        if (!mSingleShop && position == MyAccountFragment.POSITION_COUNTRY) {
            country.setText(R.string.country);
            info.setText(countryObject.countryName);
            ImageView flag = (ImageView) view.findViewById(R.id.flag);
            ImageManager.getInstance().loadImage(countryObject.countryFlag, flag, null, R.drawable.no_image_large, false);
        }
        return view;
    }
}