package com.qantas.webcrawler.valueObjects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect
public class WebLink {

    String url;
    String title;
    List<WebLink> nodes;

    public WebLink(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<WebLink> getNodes() {
        return nodes;
    }

    public void setNodes(List<WebLink> nodes) {
        this.nodes = nodes;
    }
}
