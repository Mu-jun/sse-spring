package com.sindo.techno.monitoring.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class SseEmitters {

    // 이 콜백이 SseEmitter를 관리하는 다른 스레드에서 실행되기 때문에
    // thread-safe한 자료 구조를 사용해야 한다.
    // 사용하지 않으면 ConcurrnetModificationException 에러 발생 가능
    // **CopyOnWriteArrayList**
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    SseEmitter add(SseEmitter emitter) {
        this.emitters.add(emitter);
        log.info("new emitter added added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
        emitter.onCompletion(() -> {
            log.info("SseEmitter Completion");
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.info("SseEmitter Timeout");
            emitter.complete();
        });

        return emitter;
    }
}
