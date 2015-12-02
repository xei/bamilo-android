package com.mobile.newFramework.objects.product;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductOffer;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents the product offers
 *
 * @author Paulo Carvalho
 *
 */
public class OfferList implements IJSONSerializable, Parcelable {

    protected static final String TAG = OfferList.class.getSimpleName();


    private double minPriceOffer;
    private double minPriceOfferConverted;
    private int totalOffers;
    private ArrayList<ProductOffer> offers;

    /**
     * Complete product empty constructor.
     */
    public OfferList() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            //Offer object
            JSONObject offerJson = jsonObject.getJSONObject(RestConstants.JSON_OFFERS_TAG);
            // Offers
            JSONArray offersArray = offerJson.optJSONArray(RestConstants.JSON_DATA_TAG);
            int size = offersArray.length();
            if(size > 0) {
                offers = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    JSONObject offerObject = offersArray.getJSONObject(i);
                    ProductOffer offer = new ProductOffer(offerObject);
                    offers.add(offer);
                }
            }

            minPriceOffer = offerJson.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_TAG);
            minPriceOfferConverted = offerJson.optDouble(RestConstants.JSON_OFFERS_MIN_PRICE_CONVERTED_TAG);
            totalOffers = offerJson.optInt(RestConstants.JSON_TOTAL_TAG);

        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public ArrayList<ProductOffer> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<ProductOffer> offers) {
        this.offers = offers;
    }

    public double getPriceForTracking() {
        return minPriceOfferConverted;
    }

//    public double getMinPriceOffer() {
//        return minPriceOffer;
//    }

    public int getTotalOffers() {
        return totalOffers;
    }

    /*
     * ############ PARCELABLE ############
     */

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(offers);
        dest.writeDouble(minPriceOffer);
        dest.writeDouble(minPriceOfferConverted);
        dest.writeInt(totalOffers);
    }

    private OfferList(Parcel in) {
        offers = new ArrayList<>();
        in.readList(offers, ProductOffer.class.getClassLoader());
        minPriceOffer = in.readDouble();
        minPriceOfferConverted = in.readDouble();
        totalOffers = in.readInt();
    }

    public static final Parcelable.Creator<OfferList> CREATOR = new Parcelable.Creator<OfferList>() {
        public OfferList createFromParcel(Parcel in) {
            return new OfferList(in);
        }

        public OfferList[] newArray(int size) {
            return new OfferList[size];
        }
    };
}

