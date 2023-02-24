import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

import java.util.HashMap;

/**
 * This class utilizes the jsoup library to help with extracting URLs from a URL link. The program accepts a Wikipedia
 * link and throws an error if the link is not a valid Wikipedia link(check isProperWikiLink method for more details).
 * The program will not crash, but instead show the error message and prompt the user for a proper Wikipedia link again.
 * This program gracefully accepts a valid integer, n, between one and twenty. The program will then scrape the link
 * provided of all Wikipedia links embedded in the page, filter for proper Wikipedia links, and store them in a HashMap
 * called currentLiftOfFoundLinks that has the link as a key and an integer as the value for number of occurrences of
 * that link. The program will then repeat the steps of scraping Wikipedia links for all the newly found links. If the
 * link has been visited (is in the HashMap listOfLinksVisited) its counter will be incremented. If not, the link will
 * be visited and scraped then added to listOfLinksVisited. Each time this is done the newly scraped links are put into
 * a HashMap called nextListOfFoundLinks where they will be stored until all links of the currentListOfFoundLinks are
 * cycled through. Once the currentListOfFoundLinks are exhausted it is cleared and the links from the
 * nextListOfFoundLinks are copied to the currentListOfFoundLinks. This process is repeated n times. Once the program
 * has completely cycled through n rotations, the data stored in listOfLinksVisited will be used to show total number of
 * link occurrences, unique number of links, and the address of the links along with their respective occurrences. The
 * data is converted into a csv file.
 */
public class GrabWikiLinks {

    /**
     * Stores a list of Wikipedia links with their number of occurrences. This HashMap will be the main data
     * structure the program will use to cycle through the Wikipedia links.
     */
    static HashMap<String, Integer> currentListOfFoundLinks = new HashMap<String, Integer>();

    /**
     * A HashMap that temporarily stores the extracted Wikipedia links from a link in currentListOfFoundLinks to be used
     * in the next cycle of currentListOfFoundLinks.
     */
    static HashMap<String, Integer> nextListOfFoundLinks = new HashMap<String, Integer>();

    /**
     * The main HashMap data structure to hold all visited Wikipedia links and their number of occurrences.
     */
    static HashMap<String, Integer> listOfLinksVisited = new HashMap<String, Integer>();


    /**
     * Checks if url is a proper Wikipedia Link. This is assuming a proper Wikipedia link requirement starts
     * with 'https://' only, followed by 2-3 letters for region, followed by wikipedia.org/wiki/, and none or any
     * alphanumeric characters afterwards.
     * @param url String
     * @return boolean
     */
    private static boolean isProperWikiLink(String url) {
        Pattern pattern = Pattern.compile("^https://[a-z]{2,3}\\.wikipedia.org/wiki/[^%]*$");
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    /**
     * Helper function that takes in a list of <a> elements, extracts the href link from each <a> element, checks
     * the href link is a proper Wikipedia link, and adds the Wikipedia link to the HashMap currentListOfFoundLinks.
     * @param elts Elements
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

    /**
     * This helper function extracts the href attribute from a list of elements. The href links are then checked
     * if they are proper Wikipedia links. They are then added to the nextListOfFoundLinks HashMap.
     * @param elts Elements
     */
    private static void addLinksToNextListOfFoundLinks(Elements elts) {
        for (Element elt : elts)
        {
            String hrefValue = elt.attr("href");
            if (isProperWikiLink(hrefValue)) {
                nextListOfFoundLinks.merge(hrefValue, 1, Integer::sum);
            }
        }
    }

    /**
     * This helper functions cycles through the pattern of extracting links from currentListOfFoundLinks HashMap.
     * The function then checks if the link is visited, if visited the count for that link is increased in
     * listOfLinksVisited. If the link has not been visited the function scrapes all proper Wikipedia Links from said
     * link and stores them in the nextListOfFoundLinks HashMap. The link is added to listOfLinksVisited HashMap. When
     * the function is done cycling through the currentListOfFoundLinks, the currentListOfFoundLinks gets cleared and
     * the references from nextListOfFoundLinks are shallow copied. The nextListOfFoundLinks are cleared and the
     * pattern repeats itself n times, depending on the user input.
     * @param n int
     */
    private static void cycleThroughCurrentListOfFoundLinksAndCheckIfVisitedIfNotScrapeLinks(int n) {
        for (int i = 0; i < n; i++) {
            for (Map.Entry<String,Integer> entry : currentListOfFoundLinks.entrySet()) {
                if (listOfLinksVisited.containsKey(entry.getKey())) {
                    listOfLinksVisited.merge(entry.getKey(), entry.getValue(), Integer::sum);
                } else {
                    try {
                        Document tempDocument = Jsoup.connect(entry.getKey()).get();
                        Elements tempElements = tempDocument.select("a");
                        listOfLinksVisited.put(entry.getKey(), entry.getValue());
                        addLinksToNextListOfFoundLinks(tempElements);
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

    /**
     * Converts HashMap listOfLinksVisited to a CSV file with PrintWriter. Data is stored in a file called
     * WikipediaLinks.csv under the root folder of the project.
     */
    private static void convertHashMapToCSVFile() {
        System.out.println("Converting HashMap to csv file");
        File csvFile = new File("WikipediaLinks.csv");
        int totalCount = 0;
        int uniqueCount = listOfLinksVisited.size();
        try {
            PrintWriter out = new PrintWriter(csvFile);
            for (Map.Entry<String,Integer> entry : listOfLinksVisited.entrySet()) {
                out.printf("%s, %d\n", entry.getKey(), entry.getValue());
                totalCount = totalCount + entry.getValue();
            }
            out.printf("%s, %d\n", "Total counts", totalCount);
            out.printf("%s, %d\n", "Unique counts", uniqueCount);
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Helper function to print information in a user-friendly way on extracted Wikipedia Links from HashMap
     * listOfLinksVisited. Prints out the total number of links found, number of unique links, and all visited links
     * with their number of occurrences.
     */
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
     * The core function that starts the program and utilizes all the helper functions. The function asks the user for
     * a proper Wikipedia Link and number of times n to cycle through currentListOfFoundLinks to extract Wikipedia
     * Links. Function will throw an error and repeat itself if a proper Wikipedia Link has not been supplied. The
     * function also checks for a proper n parameter (1-20).
     */
    private static void startGrabWikiLinks() {
        try {
            Scanner myScanner = new Scanner(System.in);
            System.out.println("Please enter a proper Wikipedia link: ");
            String wikipediaLink = myScanner.nextLine();
            if (!isProperWikiLink(wikipediaLink)) {
                throw new Exception("");
            }
            System.out.println("Please enter a valid integer n(1-20): ");
            int n = myScanner.nextInt();
            while (n < 1 || n > 20) {
                System.out.println("Integer must be 1-20");
                System.out.println("Please enter a valid integer n(1-20): ");
                n = myScanner.nextInt();
            }
            System.out.println("Beginning link extraction (higher n will take longer)...");
            Document document = Jsoup.connect(wikipediaLink).get();
            listOfLinksVisited.put(wikipediaLink, 0);
            Elements elts = document.select("a");
            addLinksToCurrentListOfFoundLinks(elts);
            cycleThroughCurrentListOfFoundLinksAndCheckIfVisitedIfNotScrapeLinks(n);
            printBeautifully();
            myScanner.close();
            convertHashMapToCSVFile();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            startGrabWikiLinks();
        }
    }

    /**
     * Drives the program.
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("Program is starting...");
        startGrabWikiLinks();
        System.out.println("Program has ended");
    }
}
