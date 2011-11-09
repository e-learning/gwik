package gwik.osm;


import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shulepov Mihail
 * Date: 05.11.11
 */
public class KmlParser {
    public List<PhotoObject> parse(String urlStr){
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

    public List<PhotoObject> parse(InputStream is) throws IOException {
        List<PhotoObject> result = new ArrayList<PhotoObject>();
        try {
            Document doc = parserXML(is);

            extractPlacemarks(doc, result);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            is.close();
        }
        return result;
    }

    private void extractPlacemarks(Document doc, List<PhotoObject> result){
        NodeList placemarks = doc.getElementsByTagName("Placemark");

        for (int i=0; i<placemarks.getLength(); ++i){
        	Element curPlacemark = (Element) placemarks.item(i);

            //поиск узла <href>, содержащего ссылку на иконку
            NodeList href = curPlacemark.getElementsByTagName("href");
           	String iconUrl = href.item(0).getFirstChild().getNodeValue();

            //поиск узла <coordinates>
           	NodeList coordinates = curPlacemark.getElementsByTagName("coordinates");
           	String[] coords = coordinates.item(0).getFirstChild().getNodeValue().split(",");

            result.add(new PhotoObject(
                    "",
            		iconUrl,
            		Double.valueOf(coords[0]),
            		Double.valueOf(coords[1])
                    )
                );
        }
    }

    private Document parserXML(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
    }

}