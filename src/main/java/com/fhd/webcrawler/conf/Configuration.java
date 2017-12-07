package com.fhd.webcrawler.conf;

import com.fhd.webcrawler.exception.CrawlException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fahad on 04-12-2017.
 */
public class Configuration {
    private final String seedUrl;
    private final String seedDomain;
    private String outputDestination;

    public Configuration(String arg[]) throws CrawlException {
        if(arg.length == 0) {
            System.err.println("Seed URL missing ");
            throw new CrawlException("Seed URL missing");
        }
        seedUrl = arg[0];
        try {
            URL url = new URL(seedUrl);
            seedDomain = url.getHost();
        } catch (MalformedURLException e) {
            throw new CrawlException("Invalid seed URL " + seedUrl, e);
        }
        if (arg.length > 0) {
            for (int i = 1; i < arg.length; i++) {
                if (arg[i].startsWith("out=")) {
                    outputDestination = arg[i].substring("out=".length());
                }
            }
        }
        if (outputDestination == null) {
            //default configuration
            outputDestination = "file:" + seedDomain + ".out";
        }
    }

    public String getOutputDestination() {
        return outputDestination;
    }

    public String getSeedUrl() {
        return seedUrl;
    }

    public String getSeedDomain() {
        return seedDomain;
    }

    public boolean isAllowedDomain(String url) throws CrawlException {
        try {
            URL tmp = new URL(url);
            return tmp.getHost().equalsIgnoreCase(seedDomain);
        } catch (MalformedURLException e) {
            throw new CrawlException("Invalid URL found " + url, e);
        }
    }

    public boolean isAllowedProtocol(String url) {
        //Assuming only http & https should be navigated, so that we will not encounter link like mailto:
        boolean flag = (url.startsWith("http://") || url.startsWith("https://"));
        if (!flag)
            System.out.println("Can't Visit " + url + " isNotAllowedProtocol");
        return flag;
    }
}
