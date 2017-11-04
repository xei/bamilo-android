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
 * Created on 10/21/2017.
 */

public class TileComponent extends BaseComponent {
    private List<Tile> tiles;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        JSONArray tilesObject = jsonObject.optJSONArray(RestConstants.DATA);
        if (tilesObject != null) {
            tiles = new ArrayList<>();
            for (int i = 0; i < tilesObject.length(); i++) {
                Tile temp = new Tile();
                temp.initialize(tilesObject.optJSONObject(i));
                tiles.add(temp);
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

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static class Tile implements IJSONSerializable {
        private String portraitImage;
        private String landscapeImage;
        private String target;

        @Override
        public boolean initialize(JSONObject jsonObject) throws JSONException {
            portraitImage = jsonObject.optString(RestConstants.IMAGE_PORTRAIT);
            landscapeImage = jsonObject.optString(RestConstants.IMAGE_LANDSCAPE);
            target = jsonObject.optString(RestConstants.TARGET);
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
    }
}
