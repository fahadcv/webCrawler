package com.fhd.webcrawler.writer;

import com.fhd.webcrawler.exception.CrawlResultWriteException;
import com.fhd.webcrawler.model.WebLink;
import com.fhd.webcrawler.model.WebPage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

public class CrawlResultJsonWriter implements CrawlResultWriter {

    //    JSONParser parser = new JSONParser();
    JSONObject jsonRoot;
    String fileName;

    public CrawlResultJsonWriter(String jsonFile) throws IOException, ParseException {
        fileName = jsonFile;
//        Object obj = parser.parse(new FileReader(jsonFile + ".json"));
        jsonRoot = new JSONObject();
    }

    public Status writeVisited(WebLink visitedLink, WebPage resultPage) throws CrawlResultWriteException {
        JSONObject result = transform(resultPage);
        JSONObject parent = findNode(jsonRoot, visitedLink);
        if(parent != null) {
            parent.put("result", result);
        } else {
            jsonRoot.put("result", result);
        }
        return Status.SUCCESS;
    }

    public Status writeNonVisited(WebLink nonvisitedLink, int depth) throws CrawlResultWriteException {
        return null;
    }

    public void complete() {
        try (FileWriter file = new FileWriter(fileName)) {

            file.write(jsonRoot.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONObject transform(WebPage resultPage) {
        JSONObject resultNode = new JSONObject();
        if (resultPage.getImages() != null) {
            JSONArray imgArray = new JSONArray();
            for (String image : resultPage.getImages()) {
                imgArray.add(image);
            }
            resultNode.put("images", imgArray);
        }
        if (resultPage.getLinks() != null) {
            JSONArray linksArray = new JSONArray();
            for (WebLink link : resultPage.getLinks()) {
                JSONObject linkObj = new JSONObject();
                linkObj.put("url", link.getHref());
                linksArray.add(linkObj);
            }

            resultNode.put("links", linksArray);
        }
        return resultNode;
    }

    private JSONObject findNode(JSONObject startNode, WebLink webLink) {
        Object url = startNode.get("url");

        if (url != null && url instanceof  String && ((String)url).equalsIgnoreCase(webLink.getHref())) {
            return startNode;
        }

        if (startNode.get("result") != null && ((JSONObject) startNode.get("result")).get("links") != null) {
            JSONArray jsonArray = (JSONArray) ((JSONObject) startNode.get("result")).get("links");
            ListIterator itr = jsonArray.listIterator();
            while ((itr.hasNext())) {
                JSONObject jsonObj = (JSONObject) itr.next();
                JSONObject jsonTmpObj = findNode(jsonObj, webLink);
                if(jsonTmpObj != null) {
                    return jsonTmpObj;
                }
            }
        }
        return null;


    }
}
