package com.bamilo.apicore.service.model.data;

import com.bamilo.apicore.service.model.JsonConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SliderComponent extends BaseComponent {

    @SerializedName(JsonConstants.RestConstants.DATA)
    @Expose
    private List<Slide> slides;

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    public static class Slide {
        @Expose
        @SerializedName(JsonConstants.RestConstants.TITLE)
        private String title;

        @Expose
        @SerializedName(JsonConstants.RestConstants.IMAGE_PORTRAIT)
        private String portraitImage;

        @Expose
        @SerializedName(JsonConstants.RestConstants.IMAGE_LANDSCAPE)
        private String landscapeImage;

        @Expose
        @SerializedName(JsonConstants.RestConstants.TARGET)
        private String target;

        public Slide() {
        }

        public Slide(String title, String portraitImage, String landscapeImage, String target) {
            this.title = title;
            this.portraitImage = portraitImage;
            this.landscapeImage = landscapeImage;
            this.target = target;
        }

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
    }
}
