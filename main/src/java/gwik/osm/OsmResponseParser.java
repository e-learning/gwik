package gwik.osm;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Vladislav Dolbilov (darl@yandex-team.ru)
 * Date: 03.11.11 0:27
 */
public class OsmResponseParser {
    private static final Logger log = Logger.getLogger(OsmResponseParser.class);


    public List<WikiObject> parse(String urlStr) {
        HttpURLConnection urlc = null;
        try {
            URL url = new URL(urlStr);
            urlc = (HttpURLConnection) url.openConnection();
            return parse(urlc.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (urlc != null) {
                urlc.disconnect();
            }
        }
    }

    private List<WikiObject> parse(InputStream is) throws IOException {
        List<WikiObject> result = new ArrayList<WikiObject>();
        try {
            Document doc = parserXML(is);
            XPathFactory f = XPathFactory.newInstance();
            XPath xpath = f.newXPath();

            extractNodes(doc, xpath, result);

            extractWays(doc, xpath, result);

            extractRelations(doc, xpath, result);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            is.close();
        }
        return result;
    }

    private void extractRelations(Document doc, XPath xpath, List<WikiObject> result) throws XPathExpressionException {
        NodeList relations = (NodeList) xpath.evaluate("/osm/relation[tag/@k='wikipedia']", doc, XPathConstants.NODESET);
        for (int i = 0; i < relations.getLength(); ++i) {
            Node relation = relations.item(i);
            double lon = 0;
            double lat = 0;
            int count = 0;

            NodeList relationWays =
                    (NodeList) xpath.evaluate("member[@type = 'way']/@ref", relation, XPathConstants.NODESET);
            log.debug("relation way count " + relationWays.getLength());
            for (int j = 0; j < relationWays.getLength(); ++j) {
                Node way = (Node) xpath.evaluate("/osm/way[@id = '" + relationWays.item(j).getTextContent() + "']", doc, XPathConstants.NODE);
                if (way != null) {
                    NodeList wayNodes = (NodeList) xpath.evaluate("nd/@ref", way, XPathConstants.NODESET);
                    log.debug("way node count " + wayNodes.getLength());
                    for (int h = 0; h < wayNodes.getLength(); ++h) {
                        Node node = (Node) xpath.evaluate("/osm/node[@id = '" + wayNodes.item(h).getTextContent() + "']", doc, XPathConstants.NODE);
                        lon += Double.parseDouble(xpath.evaluate("@lon", node));
                        lat += Double.parseDouble(xpath.evaluate("@lat", node));
                        count++;
                    }
                }
            }
            NodeList relationNodes =
                    (NodeList) xpath.evaluate("member[@type = 'node']/@ref", relation, XPathConstants.NODESET);
            log.debug("relation node count " + relationNodes.getLength());
            for (int j = 0; j < relationNodes.getLength(); ++j) {
                Node node = (Node) xpath.evaluate("/osm/node[@id = '" + relationNodes.item(j).getTextContent() + "']", doc, XPathConstants.NODE);
                lon += Double.parseDouble(xpath.evaluate("@lon", node));
                lat += Double.parseDouble(xpath.evaluate("@lat", node));
                count++;
            }

            if (count > 0) {
                result.add(new WikiObject(
                        xpath.evaluate("tag[@k='wikipedia']/@v", relation),
                        lon / count,
                        lat / count
                ));
            }
        }
    }

    private void extractWays(Document doc, XPath xpath, List<WikiObject> result) throws XPathExpressionException {
        NodeList ways = (NodeList) xpath.evaluate("/osm/way[tag/@k='wikipedia']", doc, XPathConstants.NODESET);
        for (int i = 0; i < ways.getLength(); ++i) {
            Node way = ways.item(i);
            double lon = 0;
            double lat = 0;
            int count = 0;

            NodeList wayNodes = (NodeList) xpath.evaluate("nd/@ref", way, XPathConstants.NODESET);
            log.debug("way node count " + wayNodes.getLength());
            for (int j = 0; j < wayNodes.getLength(); ++j) {
                Node node = (Node) xpath.evaluate("/osm/node[@id = '" + wayNodes.item(j).getTextContent() + "']", doc, XPathConstants.NODE);
                lon += Double.parseDouble(xpath.evaluate("@lon", node));
                lat += Double.parseDouble(xpath.evaluate("@lat", node));
                count++;
            }

            result.add(new WikiObject(
                    xpath.evaluate("tag[@k='wikipedia']/@v", way),
                    lon / count,
                    lat / count
            ));
        }
    }

    private void extractNodes(Document doc, XPath xpath, List<WikiObject> result) throws XPathExpressionException {
        NodeList nodes = (NodeList) xpath.evaluate("/osm/node[tag/@k='wikipedia']", doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            result.add(new WikiObject(
                    xpath.evaluate("tag[@k='wikipedia']/@v", node),
                    Double.parseDouble(xpath.evaluate("@lon", node)),
                    Double.parseDouble(xpath.evaluate("@lat", node))
            ));
        }
    }

    private Document parserXML(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
    }


}
