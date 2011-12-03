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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: Shulepov
 * Date: 09.11.11
 */
public class ImageInfoParser {
    private static final Logger log = Logger.getLogger(ImageInfoParser.class);

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

    
            public void parse(InputStream is, HashMap<String, String> result) throws IOException {
                try {
                    Document doc = parserXML(is);

                    extractImageInfo(doc, result);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    is.close();
                }
            }

            
            private void extractImageInfo(Document doc, HashMap<String, String> result){
            	NodeList pages = doc.getElementsByTagName("page");

            	for (int page=0; page<pages.getLength(); ++page){
            		Node currentPage = pages.item(page);
                    log.debug("nodeName "+currentPage.getNodeName());
            		
            		//получить значение аттрибута title для данного изображения
            		NamedNodeMap pageAttrs = currentPage.getAttributes();
                    String imageTitle = pageAttrs.getNamedItem("title").getNodeValue();
                    
                    //получить url на изображение
                    Node imageInfo;
                    String imageUrl;
                    if ( ( imageInfo = currentPage.getFirstChild() ) != null ){
                    	Node ii = imageInfo.getFirstChild();
                    	NamedNodeMap iiAttrs = ii.getAttributes();
                        imageUrl = iiAttrs.getNamedItem("url").getNodeValue();
                    }
                    else{
                    	imageUrl = null;
                    }

                    //добавить к результату в форме "Название изображения - url"
                	result.put( imageTitle, imageUrl );
            	}
            }

            
            private Document parserXML(InputStream is) throws SAXException, IOException, ParserConfigurationException {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            }

}
