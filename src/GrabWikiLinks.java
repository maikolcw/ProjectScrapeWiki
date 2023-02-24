import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

import java.util.HashMap;

public class GrabWikiLinks {

    /**
     * Stores a list of Wikipedia links with their number of times occurrence.
     */
    static HashMap<String, Integer> currentListOfFoundLinks = new HashMap<String, Integer>();

    static HashMap<String, Integer> nextListOfFoundLinks = new HashMap<String, Integer>();

    static HashMap<String, Integer> listOfLinksVisited = new HashMap<String, Integer>();


    /**
     * Checks if url is a proper Wikipedia Link. This is assuming a proper Wikipedia link requirement starts
     * with 'https://' only, followed by 2-3 letters for region, followed by wikipedia.org/wiki/, and none or any
     * character afterwards
     * @param url
     * @return boolean
     */
    private static boolean isProperWikiLink(String url) {
        Pattern pattern = Pattern.compile("^https://[a-z]{2,3}\\.wikipedia.org/wiki/\\w*$");
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    /**
     * Helper function that takes in a list of <a> elements, extracts the href link from each <a> element, checks
     * the href link is a proper Wikipedia link, and adds the Wikipedia link to the HashMap listOflInks.
     * @param elts
     */
    private static void addLinksToCurrentListOfFoundLinks(Elements elts) {
        for (Element elt : elts)
        {
            String hrefValue = elt.attr("href");
            if (isProperWikiLink(hrefValue)) {
                currentListOfFoundLinks.merge(hrefValue, 1, Integer::sum);
            }
        }
    }

    private static void addLinksToNextListOfFoundLinks(Elements elts) {
        for (Element elt : elts)
        {
            String hrefValue = elt.attr("href");
            if (isProperWikiLink(hrefValue)) {
                nextListOfFoundLinks.merge(hrefValue, 1, Integer::sum);
            }
        }
    }

    private static void cycleThroughCurrentListOfFoundLinksAndCheckIfVisitedIfNotScrapeLinks() {
        for (int i = 0; i < 3; i++) {
            for (Map.Entry<String,Integer> entry : currentListOfFoundLinks.entrySet()) {
                if (listOfLinksVisited.containsKey(entry.getKey())) {
                    listOfLinksVisited.merge(entry.getKey(), entry.getValue(), Integer::sum);
                } else {
                    try {
                        Document docu = Jsoup.connect(entry.getKey()).get();
                        Elements eles = docu.select("a");
                        listOfLinksVisited.put(entry.getKey(), entry.getValue());
                        addLinksToNextListOfFoundLinks(eles);
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());
                    }

                }
            }
            currentListOfFoundLinks.clear();
            currentListOfFoundLinks = new HashMap<>(nextListOfFoundLinks);
            nextListOfFoundLinks.clear();
        }
    }

    private static void printBeautifully() {
        int totalCount = 0;
        int uniqueCount = listOfLinksVisited.size();
        for (Map.Entry<String,Integer> entry : listOfLinksVisited.entrySet()) {
            System.out.println("Link: " + entry.getKey() + "\t" +
                    "| Number of occurrences: " + entry.getValue());
            totalCount = totalCount + entry.getValue();
        }
        System.out.println("Total number of counts: " + totalCount);
        System.out.println("Number of unique counts: " + uniqueCount);
    }

    /**
     * Drives the program
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("Program is starting, please wait:");
        final String url = "https://en.wikipedia.org/wiki/Middle_English";
        try {
            final Document document = Jsoup.connect(url).get();
            listOfLinksVisited.put(url, 1);
            Elements elts = document.select("a");
            addLinksToCurrentListOfFoundLinks(elts);
            cycleThroughCurrentListOfFoundLinksAndCheckIfVisitedIfNotScrapeLinks();
            printBeautifully();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("404");
        }
    }
}
