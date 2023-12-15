package com.sindo.techno.monitoring.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RestController()
@RequestMapping("sse")
public class SseRestController {

    private final SseEmitters sseEmitter;

    public SseRestController(SseEmitters sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> SeeSubscribe() {
        SseEmitter emitter = new SseEmitter(60 * 1000L);
        sseEmitter.add(emitter);

        // 만료 시간까지 데이터 전송이 없으면
        // 재연결 요청시 503 에러 발생
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!") // 503 에러 방지를 위한 더미 데이터
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(emitter);
    }
}
