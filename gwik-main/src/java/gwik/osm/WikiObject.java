package gwik.osm;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Vladislav Dolbilov (darl@yandex-team.ru)
 * Date: 03.11.11 0:17
 */
public class WikiObject implements JSONString {
    private final String title;
    private final String url;

    private final double lon;
    private final double lat;

    private static final Pattern WIKIPEDIA_URL_PATTERN = Pattern.compile("http://(.*).wikipedia.org/wiki/(.*)");

    public WikiObject(String title, double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
        if (title.startsWith("http://")) {
            Matcher m = WIKIPEDIA_URL_PATTERN.matcher(title);
            if (m.matches()) {
                url = title;
                try {
                    this.title = URLDecoder.decode(m.group(2), "cp1251").replaceAll("_", " ");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("invalid wikipedia url: " + title);
            }
        } else {
            String[] parts = title.split(":", 2);
            if (parts.length > 1) {
                try {
                    url = "http://" + parts[0] + ".wikipedia.org/wiki/" + URLEncoder.encode(parts[1].replaceAll(" ", "_"), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                this.title = parts[1];
            } else {
                this.title = title;
                try {
                    this.url = "http://ru.wikipedia.org/wiki/" + URLEncoder.encode(title.replaceAll(" ", "_"), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return "WikiObject{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                "}\n";
    }

    @Override
    public String toJSONString() {
        try {
            return new JSONObject()
                    .put("title", title)
                    .put("url", url)
                    .put("lon", lon)
                    .put("lat", lat)

                    .toString();
        } catch (JSONException e) {
            return null;
        }
    }
}
