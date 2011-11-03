package gwik.main;

import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;
import net.sf.xfresh.core.Yalet;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.mortbay.jetty.HttpConnection;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * @author: Vladislav Dolbilov (darl@yandex-team.ru)
 * Date: 02.11.11 22:12
 */
public abstract class AbstractJsonYalet implements Yalet {
    private static final Logger log = Logger.getLogger(AbstractJsonYalet.class);



    @Override
    public void process(final InternalRequest req, final InternalResponse res) {
        try {
            final Object result = process(req);

            res.setContentType("application/json");
            res.setProcessed(true);

            final Writer writer = new OutputStreamWriter(res.getOutputStream(), Charset.forName("utf-8"));
            try {
                if (result instanceof JSONString) {
                    writer.write(((JSONString) result).toJSONString());
                } else if (result instanceof JSONObject) {
                    writer.write(result.toString());
                } else if (result == null) {
                    writer.write("null");
                } else {
                    writer.write(new JSONObject(result).toString());
                }
            } finally {
                writer.close();
            }
        } catch (Exception e) {
            log.warn("error", e);
        }
    }

    protected abstract Object process(final InternalRequest req) throws JSONException;


}
