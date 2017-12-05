package com.fhd.webcrawler.writer;

import com.fhd.webcrawler.exception.CrawlResultWriteException;
import com.fhd.webcrawler.model.WebLink;
import com.fhd.webcrawler.model.WebPage;

/**
 * Created by fahad on 04-12-2017.
 */
public interface CrawlResultWriter {
    Status write(WebLink visitedLink, WebPage resultPage) throws CrawlResultWriteException;

    void complete();

    enum Status {
        SUCCESS, FAILED;
    }
}
