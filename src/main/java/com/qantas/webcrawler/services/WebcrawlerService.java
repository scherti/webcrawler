package com.qantas.webcrawler.services;

import com.qantas.webcrawler.valueObjects.WebLink;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;
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
    public List<WebLink> findLinks(String rootURL, int maxLevel) throws HttpStatusException, SocketTimeoutException {
        this.maxLevel = maxLevel;
        return scanForLinksWorker(0, rootURL);
    }

    private List<WebLink> scanForLinksWorker(int level, String url) throws HttpStatusException, SocketTimeoutException {
        List<WebLink> links = new ArrayList<>();
        if (level < maxLevel) {
            try {
                Document doc = Jsoup.parse(new URL(url), TIME_OUT);
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
                            WebLink webLink = new WebLink(webLinkURL, webLinkTitle);
                            try {
                                // try to add next level web links
                                if (StringUtils.isNotEmpty(webLinkURL)) {
                                    List<WebLink> nextLevelLinks = scanForLinksWorker(level + 1, webLinkURL);
                                    if (nextLevelLinks.size() > 0) {
                                        webLink.setNodes(nextLevelLinks);
                                    }
                                }
                                webLink.setStatus(HttpStatus.OK);
                                logger.debug(webLinkURL);
                            } catch (SocketTimeoutException e) {
                                webLink.setStatus(HttpStatus.GATEWAY_TIMEOUT);
                            } catch (Exception e) {
                                webLink.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                                logger.error(e.getMessage());
                            } finally {
                                links.add(webLink);
                                parallelLinkLoadingLatch.countDown();
                            }
                        });
                        linkLoadingThread.start();
                    }
                }
                parallelLinkLoadingLatch.await();
            } catch (HttpStatusException e) {
                throw e;
            } catch (SocketTimeoutException e) {
                throw e;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return links;
    }
}
