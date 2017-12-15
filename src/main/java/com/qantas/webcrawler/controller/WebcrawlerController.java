package com.qantas.webcrawler.controller;

import com.qantas.webcrawler.services.WebcrawlerService;
import com.qantas.webcrawler.valueObjects.WebLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/webcrawler", produces = "application/json")
public class WebcrawlerController {

    @Autowired
    WebcrawlerService webcrawlerService;

    @GetMapping("/findLinks")
    public ResponseEntity<List<WebLink>> findLinks(@RequestParam(value = "url") String url,
                                                   @RequestParam(value = "level", defaultValue = "1") int level) {
        return new ResponseEntity<>(webcrawlerService.findLinks(url, level), HttpStatus.OK);
    }
}
