package com.fhd.webcrawler;

import com.fhd.webcrawler.conf.Configuration;
import com.fhd.webcrawler.exception.CrawlException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by fahad on 07-12-2017.
 */
public class ConfigurationTest {

    private static final String testDomain = "test.mock";
    private static final String seedUrl = "http://" + testDomain;
    private static final String externalUrl = "http://someother.domain.com";
    Configuration target;

    @Before
    public void setup() throws CrawlException {
        target = new Configuration(new String[] {seedUrl});
    }

    @Test
    public void isAllowedDomain_false() throws Exception {
        Configuration target = new Configuration(new String[] {seedUrl});
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
    public void isAllowedProtocol_false() throws Exception {
        Configuration target = new Configuration(new String[] {seedUrl});
        String testUrl = "mailto:someone@somewhere.com";
        boolean result = target.isAllowedProtocol(testUrl);
        Assert.assertFalse(result);
    }

    @Test
    public void isAllowedProtocol_true() throws Exception {
        boolean result = target.isAllowedProtocol("http://some.domain.com/extra/path");
        Assert.assertTrue(result);
    }

}
