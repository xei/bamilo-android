package com.mobile.newFramework.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by msilva on 4/4/16.
 */
public class AuthInfo implements IJSONSerializable, Parcelable {

    private boolean mHasAuthInfo;
    private String mTitle;
    private String mSubtitle;
    private final ArrayList<String> mImagesList;

    /**
     * Empty constructor
     */
    public AuthInfo() {
        mHasAuthInfo = false;
        mTitle = "";
        mSubtitle = "";
        mImagesList = new ArrayList<>();
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
            for (int i = 0; i < imagesJSONArray.length(); i++) {
                String url = null;
                try {
                    url = imagesJSONArray.getJSONObject(i).getString(RestConstants.URL);
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public static final Parcelable.Creator<AuthInfo> CREATOR = new Parcelable.Creator<AuthInfo>() {
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
