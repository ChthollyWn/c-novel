package xyz.chthollywn.cnovel.websocket;

import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import xyz.chthollywn.cnovel.common.resp.ResponseDTO;
import xyz.chthollywn.cnovel.websocket.hander.FanqieChannelHandler;

import java.io.IOException;

@Slf4j
@Component
@ServerEndpoint("/fanqie/channel/{userId}")
public class FanqieChannel implements ApplicationContextAware {
    @Resource
    private FanqieChannelHandler fanqieChannelHandler;

    private String userId;

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        FanqieChannel.applicationContext = applicationContext;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) throws IOException {
        this.userId = userId;

        this.fanqieChannelHandler = applicationContext.getBean(FanqieChannelHandler.class);

        fanqieChannelHandler.openInit(userId, session);

        log.info("client connect : {}", this.userId);
    }

    @OnClose
    public void onClose() throws IOException {
        fanqieChannelHandler.removeSession(this.userId);
        log.info("client disconnect : {}", this.userId);
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("websocket message: {}", message);
        fanqieChannelHandler.onHandler(this.userId, message);
    }

    @OnError
    public void onError(Throwable t) throws IOException {
        fanqieChannelHandler.sendMessage(this.userId, ResponseDTO.socketError("系统繁忙，请稍后再试"));
        fanqieChannelHandler.removeSession(this.userId);
        log.error("websocket error", t);
    }
}
