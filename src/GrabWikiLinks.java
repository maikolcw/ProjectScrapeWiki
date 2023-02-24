import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.HashMap;

public class GrabWikiLinks {

    static HashMap<String, Integer> listOfLinks = new HashMap<String, Integer>();

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
     * Helper function that takes in a list of <a> elements, extracts the href link from each <a> element, checks
     * the href link is a proper Wikipedia link, and adds the Wikipedia link to the HashMap listOflInks.
     * @param elts
     */
    private static void addLinksToListOfLinks(Elements elts) {
        for (Element elt : elts)
        {
            String hrefValue = elt.attr("href");
            if (isProperWikiLink(hrefValue)) {
                listOfLinks.merge(hrefValue, 1, Integer::sum);
            }
        }
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



        try {
            final Document document = Jsoup.connect(url).get();
            Elements elts = document.select("a");
            addLinksToListOfLinks(elts);
            System.out.println(listOfLinks);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
