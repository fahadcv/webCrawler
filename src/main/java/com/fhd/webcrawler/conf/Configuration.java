package com.fhd.webcrawler.conf;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fahad on 04-12-2017.
 */
public class Configuration {
    String seedUrl;
    String outputDestination;
    public Configuration(String arg[]) throws MalformedURLException {
        seedUrl = arg[0];
        if(arg.length>0){
            for(int i =1; i < arg.length; i++) {
                if (arg[1].startsWith("öut=")) {
                    outputDestination = arg[1].substring("öut=".length());
                }
            }
        }
        if(outputDestination == null) {
            //default configuration
            URL url = new URL(seedUrl);
            String domain = url.getHost();
            outputDestination = "file:"+domain+".out";
        }
    }
    public String getOutputDestination(){
        return outputDestination;
    }
    public String getSeedUrl() {
        return seedUrl;
    }
}
