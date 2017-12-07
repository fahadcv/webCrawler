package com.fhd.webcrawler.exception;

/**
 * Created by fahad on 07-12-2017.
 */
public class CrawlException extends Exception {
    public CrawlException(String msg) {
        super(msg);
    }
    public CrawlException(String msg, Throwable cause) {
        super(msg, cause);
    }
}