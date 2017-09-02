package com.mobile.view.newfragments;

import com.mobile.service.forms.PaymentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentMethod {
    private String id;
    private String title;
    private ArrayList<String> imageUrls;
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
    public void setImageUrls(ArrayList<String> imageUrl)
    {
        this.imageUrls = imageUrl;
    }

    public ArrayList<String> getImageUrls()
    {
        return this.imageUrls;
    }
    public void setText(String text)
    {
        this.text = text;
    }
    public String getText()
    {
        return this.text.trim();
    }

    public String getImageUrl()
    {
        if (imageUrls == null || imageUrls.size() == 0) return "";
        return imageUrls.get(0);
    }

    public void setMethod(Map.Entry<String, String> entry, HashMap<String, PaymentInfo> infoList)
    {
        setId(entry.getKey());
        setTitle(entry.getValue());
        PaymentInfo info = infoList.get(title);
        setText(info.getText());
        setImageUrls(info.getImages());
    }

}
