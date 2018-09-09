package com.bamilo.android.framework.service.objects.home.model;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/21/2017.
 */

public class SliderComponent extends BaseComponent {
    private List<Slide> slides;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        slides = new ArrayList<>();
        JSONArray slidesJsonArray = jsonObject.optJSONArray(RestConstants.DATA);
        if (slidesJsonArray != null) {
            for (int i = 0; i < slidesJsonArray.length(); i++) {
                JSONObject slideJsonObject = slidesJsonArray.optJSONObject(i);
                Slide tempSlide = new Slide();
                tempSlide.initialize(slideJsonObject);
                slides.add(tempSlide);
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

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    public static class Slide implements IJSONSerializable {
        private String title;
        private String portraitImage;
        private String landscapeImage;
        private String target;
        private int weight;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        @Override
        public boolean initialize(JSONObject jsonObject) throws JSONException {
            title = jsonObject.optString(RestConstants.TITLE);
            landscapeImage = jsonObject.optString(RestConstants.IMAGE_LANDSCAPE);
            portraitImage = jsonObject.optString(RestConstants.IMAGE_PORTRAIT);
            target = jsonObject.optString(RestConstants.TARGET);
            weight = jsonObject.optInt(RestConstants.WEIGHT);
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
    }
}
