package com.spring.demo.sse;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SseCronController {

    private final SseEmitters sseEmitters;

    public SseCronController(SseEmitters sseEmitters) {
        this.sseEmitters = sseEmitters;
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void runCronJob() {
        List<SseEmitter> emitters = sseEmitters.getEmitters();
        log.info("cron - emitter list size: {}", emitters.size());
        LocalDateTime cronStartTime = LocalDateTime.now();
        emitters.forEach(emitter -> {
            LocalDateTime sendTime = LocalDateTime.now();
            Map<String, Object> data = new HashMap<>();
            data.put("cronStartTime", cronStartTime);
            data.put("sendTime", sendTime);
            String jsonString = JSONObject.toJSONString(data);

            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(jsonString)
                );
            } catch (IOException e) {
                emitter.complete();
                throw new RuntimeException(e);
            }
        });
    }
}
