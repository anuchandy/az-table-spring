package com.anuchan.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class WebEndpoint {
    @Autowired
    WebBackendService webBackendService;

    public WebEndpoint() {

    }

    @GetMapping("/fetch")
    public CompletableFuture<String> fetch() {
        return webBackendService.fetch();
    }
}
