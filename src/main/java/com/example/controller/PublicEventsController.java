package com.example.controller;

import com.example.realtime.AppEventBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/public/events")
@RequiredArgsConstructor
public class PublicEventsController {

    private final AppEventBroadcaster broadcaster;

    @GetMapping(path = "/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter updates() {
        return broadcaster.subscribe();
    }
}
