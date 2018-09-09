package com.bamilo.android.core.service.model.data.home;

import com.bamilo.android.core.service.model.JsonConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 12/19/2017.
 */

public class TileComponent extends BaseComponent {
    public static final String TEMPLATE_1X = "1x", TEMPLATE_3X = "3x", TEMPLATE_5X = "5x";

    @Expose
    @SerializedName(JsonConstants.RestConstants.TILE_TEMPLATE)
    private String tileTemplate;
    @Expose
    @SerializedName(JsonConstants.RestConstants.DATA)
    private List<Tile> tiles;

    public String getTileTemplate() {
        return tileTemplate;
    }

    public void setTileTemplate(String tileTemplate) {
        this.tileTemplate = tileTemplate;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static class Tile {
        @Expose
        @SerializedName(JsonConstants.RestConstants.IMAGE_PORTRAIT)
        private String portraitImage;
        @Expose
        @SerializedName(JsonConstants.RestConstants.IMAGE_LANDSCAPE)
        private String landscapeImage;
        @Expose
        @SerializedName(JsonConstants.RestConstants.TARGET)
        private String target;

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
