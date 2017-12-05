package com.fhd.webcrawler;

import com.fhd.webcrawler.model.WebPage;

import java.io.IOException;

/**
 * Created by fahad on 03-12-2017.
 */
public interface WebCrawler {
    WebPage crawl(String url)  throws IOException;
}
