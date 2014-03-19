package pt.rocket.framework.objects;

import org.json.JSONObject;

public class WishListItem implements IJSONSerializable{

    @Override
    public boolean initialize(JSONObject jsonObject) {
        return false;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

}
