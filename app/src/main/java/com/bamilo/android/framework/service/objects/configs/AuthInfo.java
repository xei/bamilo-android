package com.bamilo.android.framework.service.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by msilva on 4/4/16.
 */
public class AuthInfo implements IJSONSerializable, Parcelable {

    private static final String TAG = AuthInfo.class.getName();
    private boolean mHasAuthInfo;
    private String mTitle;
    private String mSubtitle;
    private ArrayList<String> mImagesList;

    /**
     * Empty constructor
     */
    public AuthInfo() {
        mHasAuthInfo = false;
        mTitle = "";
        mSubtitle = "";
        mImagesList = null;
    }

    public AuthInfo(Parcel in) {
        mHasAuthInfo = in.readByte() != 0;
        mTitle = in.readString();
        mSubtitle = in.readString();
        mImagesList = new ArrayList<>();
        in.readList(mImagesList, mImagesList.getClass().getClassLoader());
    }

    @Override
    public boolean initialize(JSONObject jsonObject) {
        mHasAuthInfo = true;
        mTitle = jsonObject.optString(RestConstants.TITLE);
        mSubtitle = jsonObject.optString(RestConstants.SUB_TITLE);
        JSONArray imagesJSONArray = jsonObject.optJSONArray(RestConstants.IMAGE_LIST);

        if(CollectionUtils.isNotEmpty(imagesJSONArray)){
            mImagesList = new ArrayList<>();
            for (int i = 0; i < imagesJSONArray.length(); i++) {
                String url = null;
                try {
                    url = imagesJSONArray.getJSONObject(i).getString(RestConstants.URL);
                } catch (JSONException e) {
                    Print.e(TAG, "Error initializing the complete product", e);
                }
                if(TextUtils.isNotEmpty(url)){
                    mImagesList.add(url);
                }
            }
        }

        return false;
    }

    public boolean hasAuthInfo() {
        return mHasAuthInfo;
    }

    /**
     * Get the title retrieved by the API
     * @param title default value case no title came from the API
     * @return the title
     */
    public String getTitle(@Nullable String title) {
        return TextUtils.isNotEmpty(mTitle) ? mTitle : title;
    }

    /**
     * Get the subtitle retrieved by the API
     * @param subTitle default value case no subtitle came from the API
     * @return the title
     */
    public String getSubtitle(@Nullable String subTitle) {
        return TextUtils.isNotEmpty(mSubtitle) ? mSubtitle : subTitle;
    }

    /**
     * Get the list of Images retrieved by the API
     * @return
     */
    public ArrayList<String> getImagesList() {
        return mImagesList;
    }

    @Override
    public JSONObject toJSON(){
        return null;
    }

    @Override
    public int getRequiredJson() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mHasAuthInfo ? 1 : 0));
        dest.writeString(mTitle);
        dest.writeString(mSubtitle);
        dest.writeList(mImagesList);
    }

    public static final Creator<AuthInfo> CREATOR = new Creator<AuthInfo>() {
        @Override
        public AuthInfo createFromParcel(Parcel in) {
            return new AuthInfo(in);
        }

        @Override
        public AuthInfo[] newArray(int size) {
            return new AuthInfo[size];
        }
    };
}
