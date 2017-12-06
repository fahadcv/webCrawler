package com.fhd.webcrawler.writer;

import com.fhd.webcrawler.exception.CrawlResultWriteException;
import com.fhd.webcrawler.model.WebLink;
import com.fhd.webcrawler.model.WebPage;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by fahad on 04-12-2017.
 */
public class CrawlResultFileWriter implements CrawlResultWriter {
    private static final String INTENT = " ";
    FileWriter writer;

    public CrawlResultFileWriter(String fileName) throws IOException {
        writer = new FileWriter(fileName);
    }

    public Status writeVisited(WebLink visitedLink, WebPage resultPage) throws CrawlResultWriteException {
        try {
            writer.write(format(resultPage.getCrawlDepthtoPage(), resultPage.getUrl()));
            if(resultPage.getImages() != null) {
                for (String image : resultPage.getImages()) {
                    writer.write(format(resultPage.getCrawlDepthtoPage() + 1, image));
                }
            }
        } catch (IOException e) {
            throw new CrawlResultWriteException(e.getMessage(), e.getCause());
        }
        return Status.SUCCESS;
    }

    public Status writeNonVisited(WebLink nonVisitedLink, int depth) throws CrawlResultWriteException {
        try {
            writer.write(format(depth, nonVisitedLink.getHref()));
        } catch (IOException e) {
            throw new CrawlResultWriteException(e.getMessage(), e.getCause());
        }
        return Status.SUCCESS;
    }

    private String format(int depth, String url) {
        StringBuilder strBldr = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            strBldr.append(INTENT);
        }
        strBldr.append(url);
        strBldr.append("\n");
        return strBldr.toString();
    }

    public void complete() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
