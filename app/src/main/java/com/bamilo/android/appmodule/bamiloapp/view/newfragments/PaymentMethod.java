package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import com.bamilo.android.framework.service.forms.PaymentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentMethod {
    private String id;
    private String title;
    private String iconUrl;
    private String text;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getTitle()
    {
        return this.title;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setText(String text)
    {
        this.text = text;
    }
    public String getText()
    {
        return this.text.trim();
    }


    public void setMethod(Map.Entry<String, String> entry, HashMap<String, PaymentInfo> infoList)
    {
        setId(entry.getKey());
        setTitle(entry.getValue());
        PaymentInfo info = infoList.get(title);
        setText(info.getText());
        setIconUrl(info.getImage());
    }

}
