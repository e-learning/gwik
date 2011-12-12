package gwik.osm;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * @author : Shulepov
 * Date: 09.11.11
 */
public class ImagesXmlParser {
    private static final Logger log = Logger.getLogger(ImagesXmlParser.class);

        public void parse(String urlStr, HashMap<String, String> result){
            HttpURLConnection urlc = null;
            try {
                URL url = new URL(urlStr);
                urlc = (HttpURLConnection) url.openConnection();
                parse(urlc.getInputStream(), result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (urlc != null) {
                    urlc.disconnect();
                }
            }
        }


        public void parse(InputStream is, HashMap<String,String> result) throws IOException {

            try {
                Document doc = parserXML(is);

                extractImages(doc, result);

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                is.close();
            }

        }


        private void extractImages(Document doc, HashMap<String, String> founded){
            NodeList pages = doc.getElementsByTagName("page");

            if ( pages.getLength() == 0 ) return;

            for (int page=0; page<pages.getLength(); ++page){
                Node currentPage = pages.item(page);

                //получить title страницы
                NamedNodeMap pageAttrs = currentPage.getAttributes();
                String pageTitle = pageAttrs.getNamedItem("title").getNodeValue();

                //получить название изображения для данной страницы
                Node images;
                String imTitle = null;
                if ( ( images = currentPage.getFirstChild() ) != null ){
                    Node im = images.getFirstChild();
                    NamedNodeMap imAttrs = im.getAttributes();
                    imTitle = imAttrs.getNamedItem("title").getNodeValue();
                } else {
                    imTitle = null;
                }

                //добавить названия страницы и изображения
                founded.put( pageTitle, imTitle);
            }

            //проверить все ли изображения были выведены в данном запросе
            if ( doc.getElementsByTagName("query-continue").getLength() != 0 ){
                founded.put("query-continue", "true");
            }
            else {
                founded.put("query-continue", "false");
            }
        }

        private Document parserXML(InputStream is) throws SAXException, IOException, ParserConfigurationException {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        }

}
