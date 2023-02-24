import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;

public class GrabWikiLinks {

    /**
     * Drives the program
     * @param args not used
     */
    public static void main(String[] args) {
        final String url = "https://en.wikipedia.org/wiki/Kitten";
        int uniqueCount = 0;
        int totalCount = 0;
        HashMap<String, Integer> listOfLinks;


        try {
            final Document document = Jsoup.connect(url).get();
//            System.out.println(document.outerHtml());
            Elements elts = document.select("a");
            System.out.println(elts);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
