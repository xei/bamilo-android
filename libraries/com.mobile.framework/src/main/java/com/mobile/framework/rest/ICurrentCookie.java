package com.mobile.framework.rest;

/**
 * Created by rsoares on 4/23/15.
 */
public interface ICurrentCookie {
    public void addCookie(String encondedCookie);
    public String getCurrentCookie();
}
