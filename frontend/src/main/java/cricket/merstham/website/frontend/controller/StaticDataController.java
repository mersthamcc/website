package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.StaticDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class StaticDataController {

    private final StaticDataService service;

    @Autowired
    public StaticDataController(StaticDataService service) {
        this.service = service;
    }

    @GetMapping(value = "/**")
    public ResponseEntity<String> staticData(HttpServletRequest request) {
        var path = request.getRequestURI();
        try {
            var data = service.get(path);
            return ResponseEntity.status(HttpStatus.valueOf(data.getStatusCode()))
                    .contentType(MediaType.valueOf(data.getContentType()))
                    .body(data.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
