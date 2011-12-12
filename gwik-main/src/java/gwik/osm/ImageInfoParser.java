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
 * @author: Shulepov
 * Date: 09.11.11
 */
public class ImageInfoParser {
    public void parse(String urlStr, ImagedWikiObject result){
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

            public void parse(InputStream is, ImagedWikiObject result) throws IOException {
                try {
                    Document doc = parserXML(is);

                    extractImageInfo(doc, result);

//                } catch (Exception e) {
//                    throw new RuntimeException(e);
                } catch (SAXException e) {
                    throw new RuntimeException(e);
                } catch (ParserConfigurationException e) {
                    throw new RuntimeException(e);
                } finally {
                    is.close();
                }
            }

            private void extractImageInfo(Document doc, ImagedWikiObject result){
                Node imUrl = doc.getElementsByTagName("ii").item(0);

                if (imUrl == null) {
                    return;
                }
                Node attrTitle = null;
//                try{
                    NamedNodeMap attrs = imUrl.getAttributes();
                    attrTitle = attrs.getNamedItem("url");

//                }catch(Exception e){
//                    throw new RuntimeException(e);
//                }

                //если изображение найдено, установить ссылку на него
                if ( attrTitle != null ){
                    result.setImageUrl( attrTitle.getNodeValue() );
                }

            }

            private Document parserXML(InputStream is) throws SAXException, IOException, ParserConfigurationException {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            }

}
