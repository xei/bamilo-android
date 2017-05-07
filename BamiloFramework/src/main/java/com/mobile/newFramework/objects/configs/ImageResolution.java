package com.mobile.newFramework.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author sergiopereira
 *
 */
public class ImageResolution implements Parcelable {
	
	protected final static String TAG = ImageResolution.class.getSimpleName();
	
	// JSON
    public static final String JSON_IDENTIFIER_TAG = "identifier";
    public static final String JSON_WIDTH_TAG = "width";
    public static final String JSON_HEIGHT_TAG = "height";
    public static final String JSON_EXTENSION_TAG = "extension";

	private String identifier;
	private int width;
	private int heigth;
	private String extension;


    /**
     * Constructor
     */
    public ImageResolution(String identifier, int width, int heigth, String extension) {
		this.identifier = identifier;
		this.width = width;
		this.heigth = heigth;
		this.extension = extension;
	}


    /*
     * ############################ GETTERS #################################
     */

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}



	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}



//	/**
//	 * @return the heigth
//	 */
//	public int getHeigth() {
//		return heigth;
//	}



	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

    /*
     * ############################ SETTERS #################################
     */

//	/**
//	 * @param identifier the identifier to set
//	 */
//	public void setIdentifier(String identifier) {
//		this.identifier = identifier;
//	}



	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}



//	/**
//	 * @param heigth the heigth to set
//	 */
//	public void setHeigth(int heigth) {
//		this.heigth = heigth;
//	}
//
//
//
//	/**
//	 * @param extension the extension to set
//	 */
//	public void setExtension(String extension) {
//		this.extension = extension;
//	}

    /*
     * ############################ JSON #################################
     */


    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */
    
    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(identifier);
	    dest.writeInt(width);
	    dest.writeInt(heigth);
	    dest.writeString(extension);
	}
	
	/**
	 * Parcel constructor
	 */
	private ImageResolution(Parcel in) {
		identifier = in.readString();
		width = in.readInt();
		heigth = in.readInt();
		extension = in.readString();
    }
		
	/**
	 * Create parcelable 
	 */
	public static final Parcelable.Creator<ImageResolution> CREATOR = new Parcelable.Creator<ImageResolution>() {
        public ImageResolution createFromParcel(Parcel in) {
            return new ImageResolution(in);
        }

        public ImageResolution[] newArray(int size) {
            return new ImageResolution[size];
        }
    };
	
}
