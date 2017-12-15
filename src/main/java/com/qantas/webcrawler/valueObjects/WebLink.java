package com.qantas.webcrawler.valueObjects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.http.HttpStatus;

import java.util.List;

@JsonAutoDetect
public class WebLink {

    String url;
    String title;
    HttpStatus status;
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

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public List<WebLink> getNodes() {
        return nodes;
    }

    public void setNodes(List<WebLink> nodes) {
        this.nodes = nodes;
    }
}
