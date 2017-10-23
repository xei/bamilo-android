package com.mobile.service.objects.home.model;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/22/2017.
 */

public class CarouselComponent extends BaseComponent {
    private List<CarouselItem> carouselItems;
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        JSONArray dataObject = jsonObject.getJSONArray(RestConstants.DATA);
        if (dataObject != null) {
            carouselItems = new ArrayList<>();
            for (int i = 0; i < dataObject.length(); i++) {
                CarouselItem tempItem = new CarouselItem();
                tempItem.initialize(dataObject.getJSONObject(i));
                carouselItems.add(tempItem);
            }
        }
        return false;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.OBJECT_DATA;
    }

    public List<CarouselItem> getCarouselItems() {
        return carouselItems;
    }

    public void setCarouselItems(List<CarouselItem> carouselItems) {
        this.carouselItems = carouselItems;
    }

    public static class CarouselItem implements IJSONSerializable {
        private String portraitImage;
        private String landscapeImage;
        private String target;
        private String title;

        @Override
        public boolean initialize(JSONObject jsonObject) throws JSONException {
            portraitImage = jsonObject.optString(RestConstants.IMAGE_PORTRAIT);
            landscapeImage = jsonObject.optString(RestConstants.IMAGE_LANDSCAPE);
            target = jsonObject.optString(RestConstants.TARGET);
            title = jsonObject.optString(RestConstants.TITLE);
            return false;
        }

        @Override
        public JSONObject toJSON() {
            return null;
        }

        @Override
        public int getRequiredJson() {
            return RequiredJson.NONE;
        }

        public String getPortraitImage() {
            return portraitImage;
        }

        public void setPortraitImage(String portraitImage) {
            this.portraitImage = portraitImage;
        }

        public String getLandscapeImage() {
            return landscapeImage;
        }

        public void setLandscapeImage(String landscapeImage) {
            this.landscapeImage = landscapeImage;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}