package com.mobile.service.objects.configs;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/08/26
 *
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
