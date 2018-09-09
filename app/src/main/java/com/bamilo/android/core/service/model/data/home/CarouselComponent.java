package com.bamilo.android.core.service.model.data.home;

import com.bamilo.android.core.service.model.JsonConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 12/20/2017.
 */

public class CarouselComponent extends BaseComponent {
    @Expose
    @SerializedName(JsonConstants.RestConstants.DATA)
    private List<CarouselItem> carouselItems;

    public List<CarouselItem> getCarouselItems() {
        return carouselItems;
    }

    public void setCarouselItems(List<CarouselItem> carouselItems) {
        this.carouselItems = carouselItems;
    }

    public static class CarouselItem {
        @Expose
        @SerializedName(JsonConstants.RestConstants.IMAGE_PORTRAIT)
        private String portraitImage;

        @Expose
        @SerializedName(JsonConstants.RestConstants.IMAGE_LANDSCAPE)
        private String landscapeImage;

        @Expose
        @SerializedName(JsonConstants.RestConstants.TARGET)
        private String target;

        @Expose
        @SerializedName(JsonConstants.RestConstants.TITLE)
        private String title;

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
