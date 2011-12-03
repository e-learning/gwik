package gwik.osm;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author: Vladislav Dolbilov (darl@yandex-team.ru)
 * Date: 14.11.11 22:38
 */
public class OsmResponseParserTest {

    private static final OsmResponseParser osmResponseParser = new OsmResponseParser();

    @Test
    public void testEmptyResponse() throws Exception {
        assertTrue(osmResponseParser.parse(new ByteArrayInputStream("<osm/>".getBytes())).isEmpty());
    }

    @Test
    public void testOneNode() throws Exception {
        List<WikiObject> objects = osmResponseParser.parse(new ByteArrayInputStream(("" +
                "<osm>" +
                "<node id=\"0\" lat=\"51.2553173\" lon=\"7.1525172\">" +
                "<tag k=\"wikipedia\" v=\"ru:test\"/>" +
                "</node>" +
                "</osm>").getBytes()));
        assertEquals(1, objects.size());
        assertEquals("test", objects.get(0).getTitle());
        assertEquals("http://ru.wikipedia.org/wiki/test", objects.get(0).getUrl());
        assertEquals(51.2553173, objects.get(0).getLat(), 0.000001);
        assertEquals(7.1525172, objects.get(0).getLon(), 0.000001);
    }

    @Test
    public void testOneWay() throws Exception {
        List<WikiObject> objects = osmResponseParser.parse(new ByteArrayInputStream(("" +
                "<osm>" +
                "<node id=\"1\" lat=\"51\" lon=\"7\"/>" +
                "<node id=\"2\" lat=\"53\" lon=\"9\"/>" +
                "<way id=\"0\">" +
                "   <nd ref=\"1\"/>" +
                "   <nd ref=\"2\"/>" +
                "   <tag k=\"wikipedia\" v=\"ru:test2\"/>" +
                "</way>" +
                "</osm>").getBytes()));
        assertEquals(1, objects.size());
        assertEquals("test2", objects.get(0).getTitle());
        assertEquals("http://ru.wikipedia.org/wiki/test2", objects.get(0).getUrl());
        assertEquals(52, objects.get(0).getLat(), 0.000001);
        assertEquals(8, objects.get(0).getLon(), 0.000001);
    }

    @Test
    public void testOneRelation() throws Exception {
        List<WikiObject> objects = osmResponseParser.parse(new ByteArrayInputStream(("" +
                "<osm>" +
                "<node id=\"1\" lat=\"1\" lon=\"5\"/>" +
                "<node id=\"2\" lat=\"2\" lon=\"6\"/>" +
                "<node id=\"3\" lat=\"3\" lon=\"7\"/>" +
                "<node id=\"4\" lat=\"5\" lon=\"9\"/>" +
                "<node id=\"5\" lat=\"-1\" lon=\"-2\"/>" +
                "<way id=\"0\">" +
                "   <nd ref=\"1\"/>" +
                "   <nd ref=\"2\"/>" +
                "</way>" +
                "<way id=\"6\">" +
                "   <nd ref=\"3\"/>" +
                "   <nd ref=\"4\"/>" +
                "</way>" +
                "<relation>" +
                "   <member type=\"way\" ref=\"0\"/>" +
                "   <member type=\"way\" ref=\"6\"/>" +
                "   <member type=\"node\" ref=\"5\"/>" +
                "   <tag k=\"wikipedia\" v=\"ru:test3\"/>" +
                "</relation>" +
                "</osm>").getBytes()));
        assertEquals(1, objects.size());
        assertEquals("test3", objects.get(0).getTitle());
        assertEquals("http://ru.wikipedia.org/wiki/test3", objects.get(0).getUrl());
        assertEquals(2, objects.get(0).getLat(), 0.000001);
        assertEquals(5, objects.get(0).getLon(), 0.000001);
    }
}
