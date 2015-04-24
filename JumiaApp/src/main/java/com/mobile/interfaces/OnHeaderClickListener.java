package com.mobile.interfaces;

/**
 * Interface to be used on header of catalog list with RecyclerView.
 * @author Paulo Carvalho
 */
public interface OnHeaderClickListener {

    public void onHeaderClick(String targetType, String url, String title);

}
