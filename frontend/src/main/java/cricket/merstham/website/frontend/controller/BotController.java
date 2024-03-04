package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.configuration.BotConfiguration;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {

    private final BotConfiguration configuration;

    @Autowired
    public BotController(BotConfiguration configuration) {
        this.configuration = configuration;
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN)
    public String robots() {
        return configuration.getRobots();
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML)
    public String sitemap() {
        return configuration.getSitemap();
    }
}
