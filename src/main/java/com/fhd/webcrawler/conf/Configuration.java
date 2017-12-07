package com.fhd.webcrawler.conf;

import com.fhd.webcrawler.exception.CrawlException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fahad on 04-12-2017.
 */
public class Configuration {
    String seedUrl;
    String outputDestination;

    public Configuration(String arg[]) throws CrawlException {
        seedUrl = arg[0];
        if (arg.length > 0) {
            for (int i = 1; i < arg.length; i++) {
                if (arg[1].startsWith("out=")) {
                    outputDestination = arg[1].substring("out=".length());
                }
            }
        }
        if (outputDestination == null) {
            try {
                //default configuration
                URL url = new URL(seedUrl);
                String domain = url.getHost();
                outputDestination = "file:" + domain + ".out";
            } catch (MalformedURLException e) {
                throw new CrawlException("Invalid URL " + seedUrl, e);
            }
        }
    }

    public String getOutputDestination() {
        return outputDestination;
    }

    public String getSeedUrl() {
        return seedUrl;
    }
}
