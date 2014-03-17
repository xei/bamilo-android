package pt.rocket.framework.objects;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import de.akquinet.android.androlog.Log;

public class SearchSuggestion implements IJSONSerializable, Parcelable {
	private final static String TAG = SearchSuggestion.class.getSimpleName();
	

	private String result;
	private int value;
	
	public SearchSuggestion() {
	
	}
	
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> keys = (Iterator<String>)jsonObject.keys();
			result = (String) keys.next();
			value = jsonObject.getInt(result);
		
		} catch ( JSONException e ) {
			Log.d( TAG, "error parsing json: ", e);
			return false;
		}
		return true;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}
	
	public String getResult() {
		return result;
	}
	
	public int getResultValue() {
		return value;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(result);
		dest.writeInt(value);
	}

	private SearchSuggestion(Parcel in) {
		result = in.readString();
		value = in.readInt();
	}
	
    public static final Parcelable.Creator<SearchSuggestion> CREATOR = new Parcelable.Creator<SearchSuggestion>() {
        public SearchSuggestion createFromParcel(Parcel in) {
            return new SearchSuggestion(in);
        }

        public SearchSuggestion[] newArray(int size) {
            return new SearchSuggestion[size];
        }
    };
	
}
