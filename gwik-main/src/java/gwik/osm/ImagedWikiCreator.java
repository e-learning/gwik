package gwik.osm;

import java.util.*;

/**
 * @author Shulepov Mihail
 * Date: 27.11.11
 */
public class ImagedWikiCreator {
    //ссылка на API для получения списка имеющихся в статьях изображений
    private static final String IMAGES_API_URL = "http://ru.wikipedia.org/w/api.php?action=query&titles=%s&prop=images&imlimit=500&format=xml";
    //ссылка на API для получения ссылки на само изображение
    private static final String IMAGE_INFO_API_URL = "http://ru.wikipedia.org/w/api.php?action=query&titles=%s&prop=imageinfo&iiprop=url&format=xml";


    /**
     * Конвертирует список объектов WikiObject в ImagedWikiObject, добавляя
     * ссылки на изображения из соответсвующих статей на википедии
     */
    public List<ImagedWikiObject> convertToImagedWiki(List<WikiObject> src){
        List<ImagedWikiObject> result = new ArrayList<ImagedWikiObject>();
        if ( src.size() == 0 ) return result;
        
        //получить список "статья-изображение"
        HashMap<String, String> fileNames = getImages(src);
        
        //получить список ссылок на изображения ("изображение - url")
        final Collection<String> imageTitles = fileNames.values();
        HashMap<String, String> imageUrls = getImagesURL( imageTitles );

        //добавить полученные ссылки к ImagedWikiObject
        for (int i=0; i<src.size(); ++i){
        	String articleTitle = src.get(i).getTitle();
        	String imageTitle = fileNames.get( articleTitle );
        	String imageUrl = imageUrls.get( imageTitle );
            if (imageUrl == null)
                imageUrl = "http://placehold.it/50&text=NOT+FOUND";
        	result.add( new ImagedWikiObject( src.get(i), imageUrl, imageTitle ) );
        }

        return result;
    }


    /**
     * Получить список "заголовок статьи" - "изображение"
     */
    private HashMap<String, String> getImages(List<WikiObject> src){
        HashMap<String, String> founded = new HashMap<String, String>();
        for (int i=0; i<src.size(); ++i){
            founded.put( src.get(i).getTitle(), null);
        }

        ImagesXmlParser parser = new ImagesXmlParser();
        do{
            String apiRequestUrl = getNotParsedPagesTitles( founded );
            parser.parse( apiRequestUrl, founded );
        } while ( !founded.get("query-continue").equals("false") );


        return founded;
    }


    /**
     * Получает url на изображения из imageTitles
     * возвращает пары "изображение - url"
     */
    private HashMap<String, String> getImagesURL(final Collection<String> imageTitles){
        //сформировать запрос для всех найденных ранее изображений
        String allTitles = null;
        for ( String imTitle : imageTitles ){
            if ( allTitles != null ){
                allTitles += "|" + imTitle;
            }
            else{
                allTitles = imTitle;
            }
        }
        
        allTitles = allTitles.replaceAll(" ", "%20");
        String apiRequestUrl = String.format(IMAGE_INFO_API_URL, allTitles);

        //выполнить запрос
        HashMap<String, String> founded = new HashMap<String, String>();
        ImageInfoParser parser = new ImageInfoParser();
        parser.parse(apiRequestUrl, founded);

        return founded;
    }


    /**
     * Формирование нового запроса для статей, информация об изображениях которых,
     * не уместиласть в предыдущих запросах
     */
    private String getNotParsedPagesTitles(HashMap<String, String> src){
        String titles = null;

        for ( Map.Entry<String, String> entry : src.entrySet() ){
            if ( entry.getValue() == null ){
                if ( titles != null){
                    titles += "|" + entry.getKey();
                }
                else {
                    titles = entry.getKey();
                }
            }
        }

        //заменить пробелы на %20
        titles = titles.replaceAll(" ", "%20");

        return  String.format(IMAGES_API_URL, titles);
    }


}
