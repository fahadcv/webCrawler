package com.fhd.webcrawler;

/**
 * Created by fahad on 03-12-2017.
 */
public class WebCrawlerFactory {
    public static WebCrawler newInstance() {
        return new WebCrawlerImpli();
    }
}
