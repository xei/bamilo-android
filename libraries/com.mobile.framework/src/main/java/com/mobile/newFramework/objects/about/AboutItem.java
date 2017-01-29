package com.mobile.newFramework.objects.about;

/**
 * Created by shahrooz on 1/28/17.
 */

public class AboutItem {
    String title;
    int Image;
    String appVersion;
    String status;

    public AboutItem(String title, int image, String appVersion, String status) {
        this.title = title;
        Image = image;
        this.appVersion = appVersion;
        this.status = status;
    }

    public AboutItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
