package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.configs.Languages;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;
import com.mobile.view.fragments.MyAccountFragment;

/**
 * Created by rsoares on 8/25/15.
 */
public class CountrySettingsAdapter extends BaseAdapter{

    public static class CountryLanguageInformation{
        public String countryName;
        public String countryFlag;
        public Languages languages;
    }

    private CountryLanguageInformation countryObject;
    private LayoutInflater mInflater;

    public CountrySettingsAdapter(Context context, CountryLanguageInformation countryObject){
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.countryObject = countryObject;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
//        return position == 0 ? "Country":"Language";
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            view = mInflater.inflate((position == MyAccountFragment.POSITION_COUNTRY) ? R.layout.country_settings_list_item : R.layout.my_account_list_item, parent, false);
        } else {
            view = convertView;
        }

        TextView country = (TextView) view.findViewById(R.id.option_name);
        TextView info = (TextView) view.findViewById(R.id.option_info);

        if(position == MyAccountFragment.POSITION_COUNTRY){
            country.setText(R.string.country);
            info.setText(countryObject.countryName);
            ImageView flag = (ImageView)view.findViewById(R.id.flag);
            RocketImageLoader.instance.loadImage(countryObject.countryFlag, flag, null, R.drawable.no_image_small);
            view.setEnabled(false);
            if(ShopSelector.isSingleShopCountry()) {
                view.setOnClickListener(null);
            }
        } else {
            country.setText(R.string.language);
            info.setText(countryObject.languages.getSelectedLanguage().getLangName());
            if(countryObject.languages.size() <= 1){
                country.setTextColor(view.getResources().getColor(R.color.black_700));
                info.setTextColor(view.getResources().getColor(R.color.black_700));
                view.setEnabled(false);
                view.setOnClickListener(null);
            }
        }
        return view;
    }
}