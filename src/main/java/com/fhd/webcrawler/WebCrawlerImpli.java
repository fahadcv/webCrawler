package com.fhd.webcrawler;

import com.fhd.webcrawler.model.WebLink;
import com.fhd.webcrawler.model.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by fahad on 03-12-2017.
 */
public class WebCrawlerImpli implements WebCrawler {
    public WebPage crawl(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements linksOnPage = document.select("a[href]");
        WebPage webPage = new WebPage();
        for (Element link : linksOnPage) {
            String href = link.attr("abs:href");
            String caption = link.data();
            WebLink webLink = new WebLink();
            webLink.setCaption(caption);
            webLink.setHref(href);
            webPage.addLink(webLink);
        }
        Elements imagesOnPage = document.select("img[src]");
        for (Element image : imagesOnPage) {
            String img = image.attr("abs:src");
            webPage.addImage(img);
        }
        return webPage;
    }
}
