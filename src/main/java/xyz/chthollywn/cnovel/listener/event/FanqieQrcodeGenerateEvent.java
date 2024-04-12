package xyz.chthollywn.cnovel.listener.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

public class FanqieQrcodeGenerateEvent extends ApplicationEvent {
    public FanqieQrcodeGenerateEvent(EventData data) {
        super(data);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventData {
        private String token;
        private String userId;
    }
}
