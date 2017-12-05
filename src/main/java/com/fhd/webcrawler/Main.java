package com.fhd.webcrawler;

import com.fhd.webcrawler.conf.Configuration;
import com.fhd.webcrawler.exception.CrawlResultWriteException;
import com.fhd.webcrawler.writer.CrawlResultWriterFactory;
import com.fhd.webcrawler.writer.CrawlResultWriter;

import java.net.MalformedURLException;

/**
 * Created by fahad on 04-12-2017.
 */
public class Main {

    public static void main(String a[]) {

        try {
            Configuration conf = new Configuration(a);
            WebCrawler webCrawler = WebCrawlerFactory.newInstance();
            CrawlResultWriter crawlResultWriter = CrawlResultWriterFactory.newInstance(conf.getOutputDestination());
            WebCrawlerEngine webCrawlerEngine = new WebCrawlerEngine(conf, webCrawler, crawlResultWriter);
            webCrawlerEngine.doCrawling();
        } catch (CrawlResultWriteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
