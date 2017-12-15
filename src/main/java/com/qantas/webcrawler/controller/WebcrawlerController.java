package com.qantas.webcrawler.controller;

import com.qantas.webcrawler.services.WebcrawlerService;
import org.jsoup.HttpStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.SocketTimeoutException;

@RestController
@RequestMapping(path = "/webcrawler", produces = "application/json")
public class WebcrawlerController {

    @Autowired
    WebcrawlerService webcrawlerService;

    @GetMapping("/findLinks")
    public ResponseEntity findLinks(@RequestParam(value = "url") String url,
                                    @RequestParam(value = "level", defaultValue = "1") int level) {
        ResponseEntity response = null;
        try {
            response = new ResponseEntity(webcrawlerService.findLinks(url, level), HttpStatus.OK);
        } catch (HttpStatusException e) {
            response = new ResponseEntity(null, HttpStatus.NOT_FOUND);
        } catch (SocketTimeoutException e) {
            response = new ResponseEntity(null, HttpStatus.GATEWAY_TIMEOUT);
        } catch (Exception e) {
            response = new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
