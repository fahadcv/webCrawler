package com.fhd.webcrawler.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fahad on 03-12-2017.
 */
public class WebPage {
    int crawlDepthtoPage;
    String url;
    String title;
    Set<WebLink> links;
    Set<String> images;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addLink(WebLink link) {
        if (links == null) {
            links = new HashSet<WebLink>();
        }
        links.add(link);
    }

    public Set<WebLink> getLinks() {
        return links;
    }

    public void setLinks(Set<WebLink> links) {
        this.links = links;
    }

    public WebPage withLinks(Set<WebLink> links) {
        this.links = links;
        return this;
    }

    public Set<String> getImages() {
        return images;
    }

    public void setImages(Set<String> images) {
        this.images = images;
    }

    public void addImage(String img) {
        if (this.images == null) {
            images = new HashSet<String>();
        }
        images.add(img);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCrawlDepthtoPage() {
        return crawlDepthtoPage;
    }

    public void setCrawlDepthtoPage(int crawlDepthtoPage) {
        this.crawlDepthtoPage = crawlDepthtoPage;
    }

    @Override
    public String toString() {
        return "WebPage{" +
                "title='" + title + '\'' +
                ", links=" + links +
                '}';
    }
}
