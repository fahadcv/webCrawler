package com.fhd.webcrawler;

import com.fhd.webcrawler.conf.Configuration;
import com.fhd.webcrawler.exception.CrawlResultWriteException;
import com.fhd.webcrawler.model.CrawlStatus;
import com.fhd.webcrawler.model.WebLink;
import com.fhd.webcrawler.model.WebPage;
import com.fhd.webcrawler.writer.CrawlResultWriter;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by fahad on 03-12-2017.
 */
public class WebCrawlerEngine {
    private final String seedUrl;
    WebCrawler webCrawler;
    CrawlResultWriter writer;
    Set<String> visitedLinks;
    Map<String, String> failedLinks;
    int MAX_CRAWL_DEPTH = 64;

    @Inject
    public WebCrawlerEngine(Configuration config, WebCrawler webCrawler, CrawlResultWriter writer) throws CrawlResultWriteException {
        this.seedUrl = config.getSeedUrl();
        this.webCrawler = webCrawler;
        this.writer = writer;
        visitedLinks = new HashSet<String>();
        failedLinks = new HashMap();
    }

//    public CompletionStage<CrawlStatus> startCrawling() {
//        return CompletableFuture.supplyAsync(() -> {
//            return new CrawlStatus();
//        });
//    }

    public CrawlStatus doCrawling() {
        CrawlStatus status = CrawlStatus.IN_PROGRESS;
        try {
            WebPage page = webCrawler.crawl(seedUrl);
            visitedLinks.add(seedUrl);
            page.setUrl(seedUrl);
            writer.write(new WebLink().withLink(seedUrl), page);
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
                    if (canVisit(page.getUrl(), link)) {
                        try {
                            WebPage resultPage = webCrawler.crawl(link.getHref());
                            resultPage.setUrl(link.getHref());
                            resultPage.setCrawlDepthtoPage(depth);
                            writer.write(link, resultPage);
                            visitedLinks.add(link.getHref());
                            crawlPage(resultPage, ++depth);
                        } catch (Exception e) {
                            //TODO retry on recoverable error
                            failedLinks.put(link.getHref(), e.getMessage());
                        }
                    }
                }
            } else {

                System.err.println("Exceed the MAX_CRAWL_DEPTH; Skipping the navigation to " + page.getLinks());
            }

        }
    }

    private boolean canVisit(String refere, WebLink webLink) {
        //TODO check for the content type and robots.txt ownering and any other filter configuration
        String url = webLink.getHref();
        return url != null &&
                !visitedLinks.contains(url) &&
                //only visit pages in the same domain.. //TODO may need to support subdomain?
                (url.startsWith(seedUrl)) &&
                !isBookmarkToSamePage(refere, url) &&
                (isRelativeURL(url) || isAllowedProtocol(url));
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

    private boolean isBookmarkToSamePage(String refere, String url) {
        boolean flag = (url.startsWith("#") || url.startsWith(refere + "#"));
        if (flag)
            System.out.println("Can't Visit " + url + " From the page " + refere);
        return flag;
    }
}
