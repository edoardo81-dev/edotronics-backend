package com.example.controller;


import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/public")
public class PublicPingController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS).cachePrivate())
                .body(Map.of(
                        "ok", true,
                        "service", "userorder-backend",
                        "at", Instant.now().toString()
                ));
    }

    @RequestMapping(value = "/ping", method = RequestMethod.HEAD)
    public ResponseEntity<Void> pingHead() {
        return ResponseEntity.ok().build();
    }
}

