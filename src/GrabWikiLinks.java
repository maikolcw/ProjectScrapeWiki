import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.HashMap;

public class GrabWikiLinks {

    /**
     * Checks if url is a proper Wikipedia Link. This is assuming a proper Wikipedia link requirement starts
     * with 'https://' only, followed by 2-3 letters for region, followed by wikipedia.org/wiki/, and none or any
     * character afterwards
     * @param url
     * @return boolean
     */
    private static boolean isProperWikiLink(String url) {
        Pattern pattern = Pattern.compile("^https://[a-z]{2,3}\\.wikipedia.org/wiki/.*$");
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    /**
     * Drives the program
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("Program starts here");
        final String url = "https://en.wikipedia.org/wiki/Kitten";
        int uniqueCount = 0;
        int totalCount = 0;
        HashMap<String, Integer> listOfLinks = new HashMap<String, Integer>();


        try {
//            System.out.println(isProperWikiLink(url));
            final Document document = Jsoup.connect(url).get();
//            System.out.println(document.outerHtml());
            Elements elts = document.select("a");
            for (Element elt : elts)
            {
                String hrefValue = elt.attr("href");
                if (isProperWikiLink(hrefValue)) {
                    listOfLinks.merge(hrefValue, 1, Integer::sum);
                }
            }
            System.out.println(listOfLinks);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
