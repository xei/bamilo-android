package com.bamilo.android.framework.service.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by msilva on 4/19/16.
 * Class that represents a External Links Section that will be presented in NavigationCategoriesFragment
 */

public class ExternalLinksSection implements IJSONSerializable, Parcelable {

    // The Lable
    private String mLabel;
    // The position of the Section in the categories list
    private int mPosition;
    // The list of external links
    private ArrayList<ExternalLinks> mExternaLinks;

    public ExternalLinksSection(){}


    @Override
    public boolean initialize(JSONObject jsonObject) {

        try {
            mLabel = jsonObject.getString(RestConstants.LABEL);
            mPosition = jsonObject.getInt(RestConstants.POSITION);

            JSONArray externalLinks = jsonObject.getJSONArray(RestConstants.EXTERNAL_LINKS);
            mExternaLinks = new ArrayList<>();
            for (int i = 0; i < externalLinks.length(); i++){
                ExternalLinks externalLink = new ExternalLinks();
                externalLink.initialize(externalLinks.getJSONObject(i));
                mExternaLinks.add(externalLink);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return true;
    }

    public String getLabel() {
        return mLabel;
    }

    public int getPosition() {
        return mPosition;
    }

    public ArrayList<ExternalLinks> getExternaLinks() {
        return mExternaLinks;
    }



    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ExternalLinksSection(Parcel in) {
        mLabel = in.readString();
        mPosition = in.readInt();
        in.readList(mExternaLinks, ExternalLinks.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLabel);
        dest.writeInt(mPosition);
        dest.writeList(mExternaLinks);
    }

    public static final Creator<ExternalLinksSection> CREATOR = new Creator<ExternalLinksSection>() {
        @Override
        public ExternalLinksSection createFromParcel(Parcel in) {
            return new ExternalLinksSection(in);
        }

        @Override
        public ExternalLinksSection[] newArray(int size) {
            return new ExternalLinksSection[size];
        }
    };


}
