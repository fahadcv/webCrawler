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
    private final Configuration config;
    WebCrawler webCrawler;
    CrawlResultWriter writer;
    Set<String> visitedLinks;
    Map<String, String> failedLinks;
    Set<String> noRetryFailedLinks;
    int MAX_CRAWL_DEPTH = 64;

    @Inject
    public WebCrawlerEngine(Configuration config, WebCrawler webCrawler, CrawlResultWriter writer) throws CrawlResultWriteException, CrawlException {
        this.config = config;
        this.seedUrl = config.getSeedUrl();
        this.webCrawler = webCrawler;
        this.writer = writer;
        visitedLinks = new HashSet<String>();
        failedLinks = new HashMap<String, String>();
        noRetryFailedLinks = new HashSet<String>();
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
            if(status == CrawlStatus.IN_PROGRESS) {
                status = CrawlStatus.FAILED;
            }
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
                        if (canVisit(normalizedURL) && !noRetryFailedLinks.contains(link.getHref())) {

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
                        if (failedLinks.containsKey(link.getHref()) && e.getMessage().equals(failedLinks.get(link.getHref()))) {
                            noRetryFailedLinks.add(link.getHref());
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
                config.isAllowedDomain(url) &&
                (isRelativeURL(url) || config.isAllowedProtocol(url));
    }

    private boolean isRelativeURL(String url) {
        boolean flag = (!url.contains("://"));
        if (flag)
            System.out.println("Visiting " + url + " isRelativeURL");
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
