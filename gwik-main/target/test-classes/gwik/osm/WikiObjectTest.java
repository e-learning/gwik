package gwik.osm;

import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author: Vladislav Dolbilov (darl@yandex-team.ru)
 * Date: 14.11.11 23:19
 */
public class WikiObjectTest {


    @Test
    public void testTitleParser() throws Exception {
        assertEquals("тест", new WikiObject("тест", 0, 0).getTitle());
        assertEquals("test2", new WikiObject("en:test2", 0, 0).getTitle());
        assertEquals("test3", new WikiObject("http://tr.wikipedia.org/wiki/test3", 0, 0).getTitle());
        assertEquals("Волга", new WikiObject("http://ru.wikipedia.org/wiki/%C2%EE%EB%E3%E0", 0, 0).getTitle());

        //support of utf-8 in url not yet implemented
        //assertEquals("Волга", new WikiObject("http://ru.wikipedia.org/wiki/%D0%92%D0%BE%D0%BB%D0%B3%D0%B0", 0, 0).getTitle());
    }

    @Test
    public void testUrlParser() throws Exception {
        assertEquals("http://ru.wikipedia.org/wiki/%D0%97%D0%B0%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F_%D1%81%D1%82%D1%80%D0%B0%D0%BD%D0%B8%D1%86%D0%B0",
                new WikiObject("Заглавная страница", 0, 0).getUrl());
        assertEquals("http://ru.wikipedia.org/wiki/%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0",
                new WikiObject("ru:Москва", 0, 0).getUrl());
        assertEquals("http://ru.wikipedia.org/wiki/%D0%9F%D0%B8%D1%82%D0%B5%D1%80",
                new WikiObject("http://ru.wikipedia.org/wiki/%D0%9F%D0%B8%D1%82%D0%B5%D1%80", 0, 0).getUrl());
    }
}
