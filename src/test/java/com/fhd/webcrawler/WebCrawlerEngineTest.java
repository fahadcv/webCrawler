package com.fhd.webcrawler;

import com.fhd.webcrawler.conf.Configuration;
import com.fhd.webcrawler.model.CrawlStatus;
import com.fhd.webcrawler.model.WebLink;
import com.fhd.webcrawler.model.WebPage;
import com.fhd.webcrawler.writer.CrawlResultWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;

/**
 * Created by fahad on 05-12-2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class WebCrawlerEngineTest {

    WebCrawlerEngine target;

    @Mock
    Configuration config;
    @Mock
    WebCrawler webCrawler;
    @Mock
    CrawlResultWriter crawlResultWriter;

    private static final String testDomain = "test.mock";
    private static final String seedUrl = "http://" + testDomain;
    private static final String externalUrl = "http://someother.domain.com";
    private WebPage seedUrlResponsePage;
    private WebPage emptyResponsePage;
    private WebPage test1ResponsePage;
    private WebPage test2ResponsePage;

    private Set<String> seedResultPageImages;
    Set<WebLink> seedResultPageLinks;
    Set<WebLink> seedResultPageExternalLinks;

    @Before
    public void setup() {
        try {
            emptyResponsePage = new WebPage();
            test1ResponsePage = new WebPage();
            test2ResponsePage = new WebPage();
            test1ResponsePage.addImage("test1/img-0");
            test1ResponsePage.addImage("test1/img-1");
            test1ResponsePage.addLink(new WebLink().withLink(seedUrl + "/mock/link-1")); //duplicate

            String newTest1Link1 = seedUrl + "/mock/test1/link-1";
            test1ResponsePage.addLink(new WebLink().withLink(newTest1Link1)); //new
            Mockito.when(webCrawler.crawl(newTest1Link1)).thenReturn(emptyResponsePage);

            String newTest1Link2 = seedUrl + "/mock/test1/link-2";
            test1ResponsePage.addLink(new WebLink().withLink(newTest1Link2)); //new
            Mockito.when(webCrawler.crawl(newTest1Link2)).thenReturn(emptyResponsePage);

            test2ResponsePage.addImage("test2/img-1");
            test2ResponsePage.addLink(new WebLink().withLink(seedUrl + "/mock/link-3")); //duplicate
            String newTest2Link1 = seedUrl + "/mock/test2/link-0";
            test2ResponsePage.addLink(new WebLink().withLink(newTest2Link1)); //new
            Mockito.when(webCrawler.crawl(newTest2Link1)).thenReturn(emptyResponsePage);

            seedResultPageImages = new LinkedHashSet<String>();
            seedResultPageLinks = new LinkedHashSet<WebLink>();
            seedResultPageExternalLinks = new LinkedHashSet<WebLink>();
            for (int i = 0; i < 5; i++) {
                seedResultPageImages.add("mock/img-" + i);

            }
            for (int i = 0; i < 15; i++) {
                String url = seedUrl + "/mock/link-" + i;
                seedResultPageLinks.add(new WebLink().withLink(url));
                if (i == 5) {
                    Mockito.when(webCrawler.crawl(url)).thenReturn(test1ResponsePage);
                } else if (i == 10) {
                    Mockito.when(webCrawler.crawl(url)).thenReturn(test2ResponsePage);
                } else {
                    Mockito.when(webCrawler.crawl(url)).thenReturn(emptyResponsePage);
                }
            }
            for (int i = 0; i < 4; i++) {
                String url = externalUrl + "/mock/link-" + i;
                seedResultPageExternalLinks.add(new WebLink().withLink(url));

            }

            seedUrlResponsePage = new WebPage();
            seedUrlResponsePage.setImages(seedResultPageImages);
            seedUrlResponsePage.setLinks(seedResultPageLinks);
            for (WebLink link : seedResultPageExternalLinks) {
                seedUrlResponsePage.addLink(link);
            }
            seedUrlResponsePage.setUrl(seedUrl);

            Mockito.when(webCrawler.crawl(seedUrl)).thenReturn(seedUrlResponsePage);

            Mockito.when(config.getSeedUrl()).thenReturn(seedUrl);
            Mockito.when(config.getOutputDestination()).thenReturn("file:someFile.out");
            Mockito.when(crawlResultWriter.writeVisited(any(WebLink.class), any(WebPage.class))).thenReturn(CrawlResultWriter.Status.SUCCESS);
            Mockito.when(crawlResultWriter.writeNonVisited(any(WebLink.class), anyInt())).thenReturn(CrawlResultWriter.Status.SUCCESS);

            Mockito.doNothing().when(crawlResultWriter).complete();

            target = new WebCrawlerEngine(config, webCrawler, crawlResultWriter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testDoCrawling() throws Exception {
        CrawlStatus status = target.doCrawling();
        Assert.assertEquals(CrawlStatus.SUCCESS, status);

        Mockito.verify(webCrawler).crawl(seedUrl);

        Set<String> visitedLinks = target.getVisitedLinks();
        Assert.assertEquals(seedResultPageLinks.size() + 1 + 3, visitedLinks.size());
        for (WebLink link : seedResultPageLinks) {
            Assert.assertTrue(visitedLinks.contains(link.getHref()));
        }
        Mockito.verify(crawlResultWriter, Mockito.times(seedResultPageLinks.size() + 1 + 3))
                .writeVisited(any(WebLink.class), any(WebPage.class));
        Mockito.verify(crawlResultWriter, Mockito.times(seedResultPageExternalLinks.size() + 2))
                .writeNonVisited(any(WebLink.class), anyInt());
        //verify no duplicate links are skipped in depth of 1
        Mockito.verify(crawlResultWriter, Mockito.never())
                .writeNonVisited(any(WebLink.class), eq(1));
        //verify 1 duplicate links are skipped in depth of 2
        Mockito.verify(crawlResultWriter, Mockito.times(1))
                .writeNonVisited(any(WebLink.class), eq(2));
        //verify 4 external & 1 duplicate links are skipped in depth of 3
        Mockito.verify(crawlResultWriter, Mockito.times(5))
                .writeNonVisited(any(WebLink.class), eq(3));
        Mockito.verify(crawlResultWriter).complete();
    }

    @Test
    public void isAllowedDomain_false() throws Exception {
        String testUrl = externalUrl+ "/some/extra/path";
        boolean result = target.isAllowedDomain(testUrl);
        Assert.assertFalse(result);
    }

    @Test
    public void isAllowedDomain_true() throws Exception {
        boolean result = target.isAllowedDomain(seedUrl + "/some/extra/path");
        Assert.assertTrue(result);
    }

    @Test
    public void canVisit_external() throws Exception {
        String testUrl = externalUrl+ "/some/extra/path";
        boolean result = target.canVisit(testUrl);
        Assert.assertFalse(result);
    }

    @Test
    public void canVisit_same_domain() throws Exception {
        String testUrl = seedUrl+ "/some/extra/path";
        boolean result = target.canVisit(testUrl);
        Assert.assertTrue(result);
    }

}
