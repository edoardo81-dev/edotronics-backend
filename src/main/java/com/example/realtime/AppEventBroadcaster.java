package com.example.realtime;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AppEventBroadcaster {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("at", LocalDateTime.now().toString()), MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void publishInventoryChanged(String reason) {
        Map<String, Object> payload = Map.of(
                "type", "INVENTORY_CHANGED",
                "reason", reason,
                "at", LocalDateTime.now().toString()
        );

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("inventory-changed")
                        .data(payload, MediaType.APPLICATION_JSON));
            } catch (Exception ex) {
                emitters.remove(emitter);
            }
        }
    }
}
