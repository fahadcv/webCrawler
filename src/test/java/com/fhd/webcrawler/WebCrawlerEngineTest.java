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
import java.util.Set;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
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
    private WebPage seedUrlResponsePage;
    private WebPage emptyResponsePage;

    private Set<String> seedResultPageImages;
    Set<WebLink> seedResultPageLinks;

    @Before
    public void setup() {
        try {
            seedResultPageImages = new HashSet<String>();
            seedResultPageLinks = new HashSet<WebLink>();
            for (int i = 0; i < 5; i++) {
                seedResultPageImages.add("mock/img-" + i);
            }
            for (int i = 0; i < 15; i++) {
                String url = seedUrl + "/mock/link-" + i;
                seedResultPageLinks.add(new WebLink().withLink(url));

            }

            seedUrlResponsePage = new WebPage();
            seedUrlResponsePage.setImages(seedResultPageImages);
            seedUrlResponsePage.setLinks(seedResultPageLinks);
            seedUrlResponsePage.setUrl(seedUrl);
            emptyResponsePage = new WebPage();

            Mockito.when(webCrawler.crawl(seedUrl)).thenReturn(seedUrlResponsePage);
            Mockito.when(webCrawler.crawl(not(eq(seedUrl)))).thenReturn(emptyResponsePage);
            Mockito.when(config.getSeedUrl()).thenReturn(seedUrl);
            Mockito.when(config.getOutputDestination()).thenReturn("file:someFile.out");
            Mockito.when(crawlResultWriter.write(any(WebLink.class), any(WebPage.class))).thenReturn(CrawlResultWriter.Status.SUCCESS);
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

        Set<String> vistedLinks = target.getVisitedLinks();
        Assert.assertEquals(seedResultPageLinks.size() + 1, vistedLinks.size());
        for (WebLink link : seedResultPageLinks) {
            Assert.assertTrue(vistedLinks.contains(link.getHref()));
        }
        Mockito.verify(crawlResultWriter, Mockito.times(seedResultPageLinks.size() + 1))
                .write(any(WebLink.class), any(WebPage.class));
    }

}
