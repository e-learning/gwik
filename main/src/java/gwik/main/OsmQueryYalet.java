package gwik.main;

import gwik.osm.OsmResponseParser;
import gwik.osm.WikiObject;
import net.sf.xfresh.core.InternalRequest;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * @author: Vladislav Dolbilov (darl@yandex-team.ru)
 * Date: 01.11.11 23:34
 */
public class OsmQueryYalet extends AbstractJsonYalet {
    private static final Logger log = Logger.getLogger(OsmQueryYalet.class);

    private static final String API_URL = "http://www.overpass-api.de/api/xapi?*[bbox=%s][wikipedia=*]";

    @Override
    protected Object process(InternalRequest req) throws JSONException {
        final String bbox = req.getParameter("bbox");
        log.info("bbox: " + bbox);

        log.debug("Start requesting overpass api");
        final String url = String.format(API_URL, bbox);
        log.debug("url = " + url);
        List<WikiObject> res = new OsmResponseParser().parse(url);

        log.debug("found:" + res);

        return new JSONObject().put("result", res);
    }
}
