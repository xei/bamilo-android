package com.mobile.interfaces;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Interface used for Dialog Fragment
 * @author sergiopereira
 */
public interface OnDialogFilterListener extends Serializable{

    public void onSubmitFilterValues(ContentValues filterValues);

}
