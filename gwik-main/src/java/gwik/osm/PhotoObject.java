package gwik.osm;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.regex.Pattern;

/**
 * @author Shulepov Mihail
 * Date: 05.11.11
 */
public class PhotoObject implements JSONString {
    public static final int ICON_WIDTH = 75;
    public static final int ICON_HEIGHT = 75;

    public PhotoObject(
            String title,
            String iconImageURL,
            double lon, double lat
            ){
        mTitle = title;
        mIconImageURL = iconImageURL;
        mLon = lon;
        mLat = lat;
    }

    public double getLon(){
        return mLon;
    }

    public double getLat(){
        return mLat;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getIconURL(){
        return mIconImageURL;
    }

    public String getLargeImageURL(){
        return (mIconImageURL.split("-")[0] + "-large.jpg");
    }

    public String getMediumImageURL(){
        return (mIconImageURL.split("-")[0] + "-medium.jpg");
    }

    @Override
    public String toString(){
        return "PhotoObject{" +
                mTitle +
                ", IconURL = " + mIconImageURL +
                ", ImageURL = " + this.getLargeImageURL() +
                ", lon = " + mLon +
                ", lat = " + mLat +
                "}\n";
    }

    @Override
    public String toJSONString(){
                try {
            return new JSONObject()
                    .put("title", mTitle)
                    .put("iconUrl", mIconImageURL)
                    .put("mediumImUrl", this.getMediumImageURL())
                    .put("lon", mLon)
                    .put("lat", mLat)

                    .toString();
        } catch (JSONException e) {
            return null;
        }
    }

    private final String mTitle;
    private final String mIconImageURL;
    private final double mLon;
    private final double mLat;
}