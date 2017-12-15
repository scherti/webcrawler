package com.qantas.webcrawler.services;

import com.qantas.webcrawler.valueObjects.WebLink;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
public class WebcrawlerService {
    private static final Logger logger = LoggerFactory.getLogger(WebcrawlerService.class);
    private static final int TIME_OUT = 4000;
    private int maxLevel = 1;

    /**
     * @param rootURL  - base URL to start from
     * @param maxLevel - maximum number of levels to scan for links
     * @return
     */
    public List<WebLink> findLinks(String rootURL, int maxLevel) {
        this.maxLevel = maxLevel;
        return scanForLinksWorker(0, rootURL);
    }

    /*
    1. Approach: single threaded sequential scanning
    2. Approach: multi threaded scanning with countdown latch
        - max. connection
        - timeout per connection
        - retries if failed
    */
    private List<WebLink> scanForLinksWorker(int level, String url) {
        List<WebLink> links = new ArrayList<>();
        if (level >= maxLevel) {
            return links;
        }
        try {
            Document doc = Jsoup.parse(new URL(url), TIME_OUT);
            logger.info(doc.title());
            Elements webLinks = doc.select("a[href]");
            // filter elements without URL
            List<Element> filteredElements =
                    webLinks.stream().filter(e -> StringUtils.isNotEmpty(e.absUrl("href"))).collect(Collectors.toList());
            final int numberOfLinks = filteredElements.size();
            final CountDownLatch parallelLinkLoadingLatch = new CountDownLatch(numberOfLinks);
            for (Element anchorElement : filteredElements) {
                final String webLinkURL = anchorElement.absUrl("href");
                final String webLinkTitle = anchorElement.text();
                // ignore links without URL's
                if (StringUtils.isNotEmpty(webLinkURL)) {
                    Thread linkLoadingThread = new Thread(() -> {
                        try {
                            WebLink webLink = new WebLink(webLinkURL, webLinkTitle);
                            // try to add next level web links
                            if (StringUtils.isNotEmpty(webLinkURL)) {
                                List<WebLink> nextLevelLinks = scanForLinksWorker(level + 1, webLinkURL);
                                if (nextLevelLinks.size() > 0) {
                                    webLink.setNodes(nextLevelLinks);
                                }
                            }
                            links.add(webLink);
                            logger.debug(webLinkURL);
                        } catch (Exception e) {
                            logger.error(e.getMessage());

                        } finally {
                            parallelLinkLoadingLatch.countDown();
                        }
                    });
                    linkLoadingThread.start();
                }
            }
            parallelLinkLoadingLatch.await();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return links;
    }
}
