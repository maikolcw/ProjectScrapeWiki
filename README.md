# ProjectScrapeWiki
This project accepts a wikipedia link to scrape and return all proper wikipedia links via n iterations, chosen by user.\
\
This program utilizes the jsoup library to help with extracting URLs from a URL link. The program accepts a Wikipedia
link and throws an error if the link is not a valid Wikipedia link (check isProperWikiLink method for more details).
The program will not crash, but instead show the error message and prompt the user for a proper Wikipedia link again.\
\
This program gracefully accepts a valid integer, n, between one and twenty. The program will then scrape the link
provided of all Wikipedia links embedded in the page, filter for proper Wikipedia links, and store them in a HashMap
called currentLiftOfFoundLinks, that has the link as a key and an integer as the value for number of occurrences of
that link. The program will then repeat the steps of scraping Wikipedia links for all the newly found links. If the
link has been visited (is in the HashMap listOfLinksVisited), its counter will be incremented. If not, the link will
be visited and scraped, then added to listOfLinksVisited. Each time this is done the newly scraped links are put into
a HashMap called nextListOfFoundLinks, where they will be stored until all links of the currentListOfFoundLinks are
cycled through. Once the currentListOfFoundLinks are exhausted, it is cleared and the links from the
nextListOfFoundLinks are copied to the currentListOfFoundLinks. This process is repeated n times.\
\
Once the program has completely cycled through n rotations, the data stored in listOfLinksVisited will be used to show total number of
link occurrences, unique number of links, and the address of the links, along with their respective occurrences. The
data is converted into a csv file.
