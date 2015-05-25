package com.mobile.newFramework.rest;


import com.mobile.newFramework.objects.Campaign;
import com.mobile.newFramework.objects.CompleteProduct;
import com.mobile.newFramework.objects.HomePageObject;
import com.mobile.newFramework.objects.AvailableCountries;
import com.mobile.newFramework.objects.CountryConfigs;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.Sections;

/**
 * Created by rsoares on 5/21/15.
 */
public class DeserializableFactory {

    public IJSONSerializable createObject(String object){
        if(object.equals(CountryConfigs.class.getName())){
            return new CountryConfigs();
        } else if(object.equals(AvailableCountries.class.getName())){
            return new AvailableCountries();
        } else if(object.equals(Sections.class.getName())){
            return new Sections();
        } else if(object.equals(HomePageObject.class.getName())){
            return new HomePageObject();
        } else if(object.equals(CompleteProduct.class.getName())){
            return new CompleteProduct();
        } else if(object.equals(Campaign.class.getName())){
            return new Campaign();
        }

        return null;
    }
}
