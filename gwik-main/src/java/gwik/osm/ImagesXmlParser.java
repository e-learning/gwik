package gwik.osm;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author : Shulepov
 * Date: 09.11.11
 */
public class ImagesXmlParser {
        public String parse(String urlStr){
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

        public String parse(InputStream is) throws IOException {
            String result = new String();
            try {
                Document doc = parserXML(is);

                result = extractImages(doc);

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                is.close();
            }
            return result;
        }

        private String extractImages(Document doc){
            NodeList images = doc.getElementsByTagName("im");
            Node imNode = images.item(0);

            if (imNode == null) {
                return "error";
            }
            Node attrTitle = null;
            try{
               NamedNodeMap attrs = imNode.getAttributes();
               attrTitle = attrs.getNamedItem("title");
            }catch(Exception e){
                throw new RuntimeException(e);
            }

            if ( attrTitle != null) {
                return  ( attrTitle.getNodeValue() );
            }
            else {
                return "error";
            }
        }

        private Document parserXML(InputStream is) throws SAXException, IOException, ParserConfigurationException {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        }

}
