package com.mobile.newFramework.objects.configs;

/**
 * Created by rsoares on 8/26/15.
 */
public class Language {
    private String mLangCode;
    private String mLangName;
    private boolean isDefault;
    private boolean isSelected;

    public String getLangCode() {
        return mLangCode;
    }

    public void setLangCode(String mLangCode) {
        this.mLangCode = mLangCode;
    }

    public String getLangName() {
        return mLangName;
    }

    public void setLangName(String mLangName) {
        this.mLangName = mLangName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
