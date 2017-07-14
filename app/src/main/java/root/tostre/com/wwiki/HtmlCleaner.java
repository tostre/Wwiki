package root.tostre.com.wwiki;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/**
 * Created by Macel on 12.05.17.
 */

public class HtmlCleaner{

    public HtmlCleaner(){

    }

    public String cleanHtmlString(String dirtyHtml){
        Document doc = Jsoup.parse(dirtyHtml);

        Elements elements = doc.getElementsByAttributeValue("role", "note");
        elements.remove();
        elements = doc.getElementsByTag("sup");
        elements.remove();
        elements = doc.getElementsByClass("thumb tright");
        elements.remove();
        elements = doc.getElementsByClass("thumb tleft");
        elements.remove();
        elements = doc.getElementsByClass("reflist columns references-column-width");
        elements.remove();
        elements = doc.getElementsByAttributeValue("role", "note");
        elements.remove();

        String cleanHtml = Jsoup.clean(doc.toString(), new Whitelist().addTags("p", "span", "a", "b", "h1", "h2", "h3", "h4", "h5", "h6", "li", "ol", "ul", "href"));

        return cleanHtml;
    }
}
