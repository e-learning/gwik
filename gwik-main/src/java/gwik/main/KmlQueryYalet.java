package gwik.main;

import gwik.osm.KmlParser;
import gwik.osm.OsmResponseParser;
import gwik.osm.PhotoObject;
import gwik.osm.WikiObject;
import net.sf.xfresh.core.InternalRequest;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author Shulepov Mihail
 * Date: 05.11.11
 */
public class KmlQueryYalet extends AbstractJsonYalet {
    private static final Logger log = Logger.getLogger(OsmQueryYalet.class);

    private static final String API_URL = "http://openstreetview.org/api/photos/locate.kml?bbox=%s";

    @Override
    protected Object process(InternalRequest req) throws JSONException {
        final String bbox = req.getParameter("bbox");
        log.info("bbox: " + bbox);

        log.debug("Start requesting open-street-view api");
        final String url = String.format(API_URL, bbox);
        log.debug("url = " + url);
        List<PhotoObject> res = new KmlParser().parse(url);

        log.debug("found:" + res);

        return new JSONObject().put("result", res);
    }
}