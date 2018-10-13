package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.view.View.OnClickListener;

/**
 * Class used to show the cart info and a navigation container, menu or categories
 *
 * @author sergiopereira
 */
public class DrawerItem {

    public boolean isDivider() {
        return isDivider;
    }

    public void setDivider(boolean divider) {
        isDivider = divider;
    }

    public DrawerItem(boolean isDivider) {
        this.isDivider = isDivider;
    }

    private boolean isDivider;
    private int icon;
    private int name;
    private int description;

    public OnClickListener getLoginListener() {
        return loginListener;
    }

    public void setLoginListener(OnClickListener loginListener) {
        this.loginListener = loginListener;
    }

    private OnClickListener loginListener;

    public OnClickListener getListener() {
        return listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    private OnClickListener listener;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public DrawerItem(String userName, String email, String gender, OnClickListener loginListener) {
        this.userName = userName;
        this.gender = gender;
        this.email = email;
        this.loginListener = loginListener;
    }

    private boolean showBadge;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String gender;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public DrawerItem(int icon, int name, boolean showBadge, int badge, int textColor,
            OnClickListener listener) {
        this.icon = icon;
        this.name = name;
        this.showBadge = showBadge;
        this.badge = badge;
        this.textColor = textColor;
        this.isDivider = false;
        this.listener = listener;

    }

    private int badge;

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    private int textColor;


    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public boolean isShowBadge() {
        return showBadge;
    }

    public void setShowBadge(boolean showBadge) {
        this.showBadge = showBadge;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }


}
