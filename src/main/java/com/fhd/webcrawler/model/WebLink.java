package com.fhd.webcrawler.model;

/**
 * Created by fahad on 03-12-2017.
 */
public class WebLink {
    String href;
    String caption;


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public WebLink withLink(String href) {
        this.href = href;
        return this;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebLink webLink = (WebLink) o;

        if (href != null ? !href.equals(webLink.href) : webLink.href != null) return false;
        return caption != null ? caption.equals(webLink.caption) : webLink.caption == null;

    }

    @Override
    public int hashCode() {
        int result = href != null ? href.hashCode() : 0;
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WebLink{" +
                "href='" + href + '\'' +
                '}';
    }
}
