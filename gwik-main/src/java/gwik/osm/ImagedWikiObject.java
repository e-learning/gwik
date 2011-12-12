package gwik.osm;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: Shulepov
 * Date: 09.11.11
 */
public class ImagedWikiObject extends WikiObject {
    public ImagedWikiObject(
            String title,
            double lon,
            double lat,
            String imageUrl
            ){
        super(title, lon, lat);
        mImageUrl = imageUrl;
    }

    public ImagedWikiObject(WikiObject wikiObj){
        super( wikiObj.getTitle(), wikiObj.getLon(), wikiObj.getLat() );
    }

    public ImagedWikiObject(WikiObject wikiObj, String imageUrl, String imageTitle){
        super( wikiObj.getTitle(), wikiObj.getLon(), wikiObj.getLat() );
        mImageUrl = imageUrl;
        mImageTitle = imageTitle;
    }

    public ImagedWikiObject(
            String title,
            double lon,
            double lat
            ){
        super(title, lon, lat);
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ImagedWikiObject{" +
                "title='" + super.getTitle() + '\'' +
                ", url='" + super.getUrl() + '\'' +
                ", image='" + mImageTitle + "'" +
                ", imageUrl=" + mImageUrl +
                ", lon=" + super.getLon() +
                ", lat=" + super.getLat() +
                "}\n";
    }

    @Override
    public String toJSONString() {
        try {
            return new JSONObject()
                    .put("title", super.getTitle())
                    .put("url", super.getUrl())
                    .put("imageTitle", mImageTitle)
                    .put("imageUrl", mImageUrl)
                    .put("lon", super.getLon())
                    .put("lat", super.getLat())

                    .toString();
        } catch (JSONException e) {
            return null;
        }
    }

    private String mImageUrl;
    private String mImageTitle;

}
