package com.mobile.interfaces;

import android.content.ContentValues;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Interface used for communication between filters service and parent fragment.
 * @author sergiopereira
 */
public interface OnDialogFilterListener extends Parcelable{

    void onSubmitFilterValues(ContentValues filterValues);

}
