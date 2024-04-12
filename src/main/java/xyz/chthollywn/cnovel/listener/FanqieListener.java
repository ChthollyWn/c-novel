package xyz.chthollywn.cnovel.listener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import xyz.chthollywn.cnovel.common.resp.ResponseDTO;
import xyz.chthollywn.cnovel.listener.event.FanqieQrcodeGenerateEvent;
import xyz.chthollywn.cnovel.service.fanqie.FanqieAccountService;
import xyz.chthollywn.cnovel.strategy.cache.CacheService;
import xyz.chthollywn.cnovel.strategy.cache.CacheStrategy;
import xyz.chthollywn.cnovel.websocket.hander.FanqieChannelHandler;

@Slf4j
@Component
public class FanqieListener {
    @Resource
    private FanqieAccountService fanqieAccountService;
    @Resource
    private CacheService cacheService;
    @Resource
    private FanqieChannelHandler fanqieChannelHandler;

    private static final int REQUEST_INTERVAL = 500; // 请求间隔时间，0.5秒
    private static final long TIMEOUT = 60000; // 总超时时间，1分钟
    private CacheStrategy cacheStrategy;

    @PostConstruct
    public void init() {
        this.cacheStrategy = cacheService.getCacheStrategy();
    }

    @Async
    @EventListener
    public void qrcodeGenerateListener(FanqieQrcodeGenerateEvent event) {
        FanqieQrcodeGenerateEvent.EventData eventData = (FanqieQrcodeGenerateEvent.EventData) event.getSource();
        String token = eventData.getToken();
        String userId = eventData.getUserId();
        log.info("监听到二维码生成事件 {}，开始轮询校验二维码结果", token);

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < TIMEOUT) {
            try {
                String sid_tt = fanqieAccountService.getSid_tt(token);
                if (StringUtils.isNotBlank(sid_tt)) {
                    log.info("成功刷新sid_tt {}", sid_tt);
                    cacheStrategy.setFanqieSid_tt(sid_tt);
                    if (StringUtils.isNotBlank(userId)) {
                        fanqieChannelHandler.sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.TEXT, "二维码扫描成功"));
                    }
                    return; // 成功获取到sid_tt后退出循环
                }
                Thread.sleep(REQUEST_INTERVAL); // 保持请求间隔固定为0.5秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 保持良好的中断处理习惯
                log.error("线程被中断", e);
                return;
            } catch (Exception e) {
                log.error("处理过程中发生异常", e);
            }
        }
        if (StringUtils.isNotBlank(userId)) {
            fanqieChannelHandler.sendMessageAndSave(userId, ResponseDTO.socketSuccess(ResponseDTO.TEXT, "二维码扫描超时，请稍后重试"));
        }
        log.error("二维码扫描超时");
    }
}
