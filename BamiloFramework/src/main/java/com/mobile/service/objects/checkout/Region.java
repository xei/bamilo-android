package com.mobile.service.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that represents a Region
 * @author manuelsilva
 *
 */
public class Region implements Parcelable {
	private String id;
	private String name;

	/**
	 * Empty Constructor
	 */
	public Region() {

	}

	/**
	 * 
	 * @param id
	 * @param name
	 */
	public Region(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		
	}
	
	   /**
     * Parcel constructor
     * @param in
     */
    private Region(Parcel in) {
    	id = in.readString();
    	name = in.readString();

    }
	
	/**
     * Create parcelable 
     */
    public static final Parcelable.Creator<Region> CREATOR = new Parcelable.Creator<Region>() {
        public Region createFromParcel(Parcel in) {
            return new Region(in);
        }

        public Region[] newArray(int size) {
            return new Region[size];
        }
    };
}
