package com.fhd.webcrawler.writer;

import com.fhd.webcrawler.exception.CrawlResultWriteException;

import java.io.IOException;

/**
 * Created by fahad on 04-12-2017.
 */
public class CrawlResultWriterFactory {
    public static CrawlResultWriter newInstance(String outArgs) throws CrawlResultWriteException {
        if (outArgs.startsWith("file:")) {
            String fileName = outArgs.substring("file:".length());
            try {
                return new CrawlResultFileWriter(fileName);
            } catch (IOException e) {
                throw new CrawlResultWriteException("Unable to create File : [" + fileName + "]", e.getCause());
            }
        } else {
            //TODO
            throw new RuntimeException("Ïnvalid outArgs -> " + outArgs);
        }
    }

}