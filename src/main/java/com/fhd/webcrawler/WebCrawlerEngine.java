package com.fhd.webcrawler;

import com.fhd.webcrawler.conf.Configuration;
import com.fhd.webcrawler.exception.CrawlException;
import com.fhd.webcrawler.exception.CrawlResultWriteException;
import com.fhd.webcrawler.model.CrawlStatus;
import com.fhd.webcrawler.model.WebLink;
import com.fhd.webcrawler.model.WebPage;
import com.fhd.webcrawler.writer.CrawlResultWriter;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by fahad on 03-12-2017.
 */
public class WebCrawlerEngine {
    private final String seedUrl;
    private final String seedDomain;
    WebCrawler webCrawler;
    CrawlResultWriter writer;
    Set<String> visitedLinks;
    Map<String, String> failedLinks;
    Map<String, String> noRetryFailedLinks;
    int MAX_CRAWL_DEPTH = 64;

    @Inject
    public WebCrawlerEngine(Configuration config, WebCrawler webCrawler, CrawlResultWriter writer) throws CrawlResultWriteException, CrawlException {
        this.seedUrl = config.getSeedUrl();
        try {
            URL url = new URL(this.seedUrl);
            seedDomain = url.getHost();
        } catch (MalformedURLException e) {
            throw new CrawlException("Invalid seed URL : " + seedUrl, e);
        }
        this.webCrawler = webCrawler;
        this.writer = writer;
        visitedLinks = new HashSet<String>();
        failedLinks = new HashMap<String, String>();
        noRetryFailedLinks = new HashMap<String, String>();
    }

//    public CompletionStage<CrawlStatus> startCrawling() {
//        return CompletableFuture.supplyAsync(() -> {
//            return doCrawling();
//        });
//    }

    public CrawlStatus doCrawling() {
        CrawlStatus status = CrawlStatus.IN_PROGRESS;
        try {
            WebPage page = webCrawler.crawl(seedUrl);
            visitedLinks.add(seedUrl);
            page.setUrl(seedUrl);
            writer.writeVisited(new WebLink().withLink(seedUrl), page);
            crawlPage(page, 1);
            writer.complete();
            if (!failedLinks.isEmpty()) {
                if (visitedLinks.isEmpty()) {
                    status = CrawlStatus.FAILED;
                } else {
                    status = CrawlStatus.PARTIAL_SUCCESS;
                }
                System.err.println(" ****************** failed Links ***** ");
                System.err.println(failedLinks);
            } else {
                status = CrawlStatus.SUCCESS;
            }
        } catch (Exception e) {
            //TODO Error handling
            e.printStackTrace();
        }
        return status;
    }

    public Set<String> getVisitedLinks() {
        return visitedLinks;
    }

    public Map<String, String> getFailedLinks() {
        return failedLinks;
    }

    private void crawlPage(WebPage page, int depth) {
        if (page != null && page.getLinks() != null) {
            if (depth < MAX_CRAWL_DEPTH) {
                for (WebLink link : page.getLinks()) {
                    String normalizedURL = normalize(link.getHref());
                    try {
                        if (canVisit(normalizedURL) && !noRetryFailedLinks.containsKey(link.getHref())) {

                            WebPage resultPage = webCrawler.crawl(link.getHref());
                            resultPage.setUrl(link.getHref());
                            resultPage.setCrawlDepthtoPage(depth);
                            writer.writeVisited(link, resultPage);
                            visitedLinks.add(normalizedURL);
                            if (resultPage.getLinks() != null && resultPage.getLinks().size() > 0) {
                                crawlPage(resultPage, ++depth);
                            }

                        } else {
                            try {
                                writer.writeNonVisited(link, depth);
                            } catch (CrawlResultWriteException e) {
                                failedLinks.put(link.getHref(), e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        //TODO retry on recoverable error
                        e.printStackTrace();
                        if (failedLinks.containsKey(link.getHref())) {
                            noRetryFailedLinks.put(link.getHref(), e.getMessage());
                        } else {
                            failedLinks.put(link.getHref(), e.getMessage());
                        }
                    }
                }
            } else {

                System.err.println("Exceed the MAX_CRAWL_DEPTH; " + MAX_CRAWL_DEPTH + " Skipping the navigation to " + page.getLinks());
            }
        }
    }

    public boolean canVisit(String url) throws CrawlException {
        //TODO check for the content type and robots.txt ownering and any other filter configuration
        return url != null &&
                !visitedLinks.contains(url) &&
                isAllowedDomain(url) &&
                (isRelativeURL(url) || isAllowedProtocol(url));
    }

    public boolean isAllowedDomain(String url) throws CrawlException {
        try {
            URL tmp = new URL(url);
            return tmp.getHost().equalsIgnoreCase(seedDomain);
        } catch (MalformedURLException e) {
            throw new CrawlException("Invalid URL found " + url, e);
        }
    }

    private boolean isRelativeURL(String url) {
        boolean flag = (!url.contains("://"));
        if (flag)
            System.out.println("Visiting " + url + " isRelativeURL");
        return flag;
    }

    private boolean isAllowedProtocol(String url) {
        //Assuming only http & https should be navigated, so that we will not encounter link like mailto:
        boolean flag = (url.startsWith("http://") || url.startsWith("https://"));
        if (!flag)
            System.out.println("Can't Visit " + url + " isNotAllowedProtocol");
        return flag;
    }

    public String normalize(String url) {
        int end;
        if (url != null) {
            if ((end = url.indexOf('#')) > 0) {
                url = url.substring(0, end);
            }
            url = url.trim();
            return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        }
        return url;
    }
}
