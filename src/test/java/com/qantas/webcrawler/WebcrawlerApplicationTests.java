package com.qantas.webcrawler;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.qantas.webcrawler.services.WebcrawlerService;
import com.qantas.webcrawler.valueObjects.WebLink;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebcrawlerApplicationTests {

    private ClientAndServer mockServer;

    @Autowired
    WebcrawlerService webcrawlerService;

    @Before
    public void setUp() {
        mockServer = startClientAndServer(1080);
    }

    @After
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void testWebcrawlerService() {
        try {
            URL url = Resources.getResource("Google.html");
            String google1 = Resources.toString(url, Charsets.UTF_8);
            new MockServerClient("127.0.0.1", 1080)
                    .when(
                            HttpRequest.request()
                                    .withMethod("GET")
                                    .withPath("/p1")
                            , exactly(1)
                    )
                    .respond(
                            response()
                                    .withStatusCode(200)
                                    .withHeaders(
                                            new Header("Content-Type", "text/*; charset=utf-8")
                                    )
                                    .withBody(google1)
                                    .withDelay(new Delay(SECONDS, 1))
                    );

            List<WebLink> links = webcrawlerService.findLinks("http://localhost:1080/p1", 1);

            assertEquals(54, links.size());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
