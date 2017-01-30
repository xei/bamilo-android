package com.mobile.newFramework.objects.addresses;

/**
 * Created by Arash on 1/24/2017.
 */

public class AdapterItemSelection {

    public int id;
    private boolean selected = false;
    public boolean isSelected()
    {
     return selected;
    }
    public void setSelected(boolean flg)
    {
        selected = flg;
    }
}
