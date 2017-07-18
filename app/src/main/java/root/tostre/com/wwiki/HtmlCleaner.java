package root.tostre.com.wwiki;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Macel on 12.05.17.
 * Takes a HTML-String and removes unwanted tags
 */

public class HtmlCleaner{

    // Empty
    public HtmlCleaner(){

    }

    // Removes all unwanted tags from a HTML-String
    public String cleanHtmlString(String dirtyHtml){
        Document doc = Jsoup.parse(dirtyHtml);

        Elements elements = doc.getElementsByAttributeValue("id", "bksicon");
        elements.remove();
        // Remove in-line-images
        elements = doc.getElementsByClass("thumb tright");
        elements.remove();
        elements = doc.getElementsByClass("thumb tleft");
        elements.remove();
        elements = doc.getElementsByClass("thumbinner");
        elements.remove();
        // Remove tables
        elements = doc.getElementsByClass("wikitable float-right");
        elements.remove();
        elements = doc.getElementsByAttributeValue("id", "Vorlage_Doppeleintrag");
        elements.remove();
        elements = doc.getElementsByClass("wikitable");
        elements.remove();
        // Remove links to further reading
        elements = doc.getElementsByClass("hauptartikel");
        elements.remove();
        elements = doc.getElementsByAttributeValue("id", "Weblinks");
        elements.remove();
        elements = doc.getElementsByClass("sisterproject");
        elements.remove();
        elements = doc.getElementsByAttributeValue("id", "Vorlage_Gesprochene_Version");
        elements.remove();
        elements = doc.getElementsByAttributeValue("id", "Gesprochene_Version");
        elements.remove();
        elements = doc.getElementsByClass("float-right toccolours");
        elements.remove();

        String cleanHtml = doc.toString();
        //cleanHtml = Jsoup.clean(doc.toString(), new Whitelist().addTags("p", "span", "a", "b", "h1", "h2", "h3", "h4", "h5", "h6", "li", "ol", "ul", "href"));

        return cleanHtml;
    }

}
